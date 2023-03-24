package no.navnesen;

public interface Awaitable<T> {
	AwaitableResult<T> waitForResult();
}
