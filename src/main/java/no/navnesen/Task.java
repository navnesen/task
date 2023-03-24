package no.navnesen;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A quick and easy method for performing synchronous tasks in an asynchronous manner.
 */
public class Task<T> {
	public static <T> Task<T> complete(T value) {
		return new Task<>(TaskResult.success(value));
	}

	public static <T> Task<T> fail(Exception exception) {
		return new Task<>(TaskResult.failure(exception));
	}

	public static <T> Task<List<T>> all(List<Task<T>> tasks) {
		throw new RuntimeException("Not implemented");
	}

	protected final AtomicReference<TaskResult<T>> _result = new AtomicReference<>(null);

	public Task(TaskResult<T> result) {
		this._result.set(result);
	}

	public Task(TaskAction<T> action) {
		new Thread(() -> {
			synchronized (this) {
				try {
					this._result.set(TaskResult.success(action.run()));
				} catch (Exception exception) {
					this._result.set(TaskResult.failure(exception));
				} finally {
					notifyAll();
				}
			}
		}).start();
	}

	public T await() {
		throw new RuntimeException("Not implemented");
	}

	public <V> Task<V> and(TaskActionAnd<V, T> action) {
		throw new RuntimeException("Not implemented");
	}

	public <V> Task<V> map(TaskActionMap<V, T> action) {
		throw new RuntimeException("Not implemented");
	}

	public Task<T> or(TaskActionOr<T> action) {
		throw new RuntimeException("Not implemented");
	}
}
