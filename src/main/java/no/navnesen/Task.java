package no.navnesen;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static no.navnesen.Raise.raise;

/**
 * A quick and easy method for performing synchronous tasks in an asynchronous manner.
 */
public class Task<T> implements Awaitable<T> {

	public static <T> Task<T> complete(T value) {
		return new Task<>(TaskResult.success(value));
	}

	public static <T> Task<T> fail(Exception exception) {
		return new Task<>(TaskResult.failure(exception));
	}

	public static <T> Task<List<T>> all(List<Task<T>> tasks) {
		return new Task<>(() -> tasks.stream().map(Task::await).toList());
	}

	protected final AtomicReference<TaskResult<T>> _result = new AtomicReference<>(null);

	public Task() {
	}

	public Task(TaskResult<T> result) {
		this._result.set(result);
	}

	public Task(TaskAction<T> action) {
		new Thread(() -> {
			synchronized (this) {
				try {
					this.completed(action.run());
				} catch (Exception exception) {
					this.failed(exception);
				}
			}
		}).start();
	}

	public Task(TaskActionResult<T> action) {
		new Thread(() -> {
			synchronized (this) {
				try {
					this.applyResult(action.run());
				} catch (Exception exception) {
					this.failed(exception);
				}
			}
		}).start();
	}

	public T await() {
		final TaskResult<T> result = this.waitForResult();
		if (result.didThrow) {
			raise(result.exception);
		}
		return result.value;
	}

	public <V> Task<V> and(TaskActionAnd<V, T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (result.didThrow) {
				return TaskResult.failure(result.exception);
			}
			return action.run(result.value).waitForResult();
		});
	}

	public <V> Task<V> map(TaskActionMap<V, T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (result.didThrow) {
				throw result.exception;
			}
			return action.run(result.value);
		});
	}

	public Task<T> or(TaskActionOr<T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (!result.didThrow) {
				return result.value;
			}
			return action.run(result.exception);
		});
	}

	public Task<T> orElse(TaskActionOrElse<T> action) {
		return new Task<>(() -> {
			TaskResult<T> result = this.waitForResult();
			if (!result.didThrow) {
				return TaskResult.success(result.value);
			}
			return action.run(result.exception).waitForResult();
		});
	}

	@Override
	public synchronized TaskResult<T> waitForResult() {
		TaskResult<T> result = this._result.get();
		while (result == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			result = this._result.get();
		}
		return result;
	}

	protected void completed(T value) {
		this.applyResult(TaskResult.success(value));
	}

	protected void failed(Exception exception) {
		this.applyResult(TaskResult.failure(exception));
	}

	protected void applyResult(AwaitableResult<T> result) {
		this._result.set(TaskResult.from(result));
		notifyAll();
	}
}
