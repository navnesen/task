package no.navnesen;

@SuppressWarnings("ClassCanBeRecord")
public class TaskResult<T> implements AwaitableResult<T> {
	public static <T> TaskResult<T> success(T value) {
		return new TaskResult<>(false, null, value);
	}

	public static <T> TaskResult<T> failure(Exception exception) {
		return new TaskResult<>(true, exception, null);
	}

	public static <T> TaskResult<T> from(AwaitableResult<T> result) {
		if (result.getDidThrow()) {
			return TaskResult.failure(result.getException());
		} else {
			return TaskResult.success(result.getValue());
		}
	}

	public final boolean didThrow;
	public final Exception exception;
	public final T value;

	public TaskResult(boolean didThrow, Exception exception, T value) {
		if (didThrow && exception == null) {
			throw new NullPointerException("Could not instantiate TaskResult: the thrown exception cannot be null!");
		} else if (!didThrow && value == null) {
			throw new NullPointerException("Could not instantiate TaskResult: The returned action value cannot be null!");
		}

		this.didThrow = didThrow;
		this.exception = exception;
		this.value = value;
	}

	@Override
	public boolean getDidThrow() {
		return this.didThrow;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public Exception getException() {
		return this.exception;
	}
}