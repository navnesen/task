package no.navnesen;

public interface AwaitableResult<T> {
	boolean getDidThrow();

	Exception getException();

	T getValue();
}
