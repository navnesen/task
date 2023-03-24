package no.navnesen;

public interface TaskActionResult<T> {
	AwaitableResult<T> run() throws Exception;
}