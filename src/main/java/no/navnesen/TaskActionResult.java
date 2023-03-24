package no.navnesen;

public interface TaskActionResult<T> {
	TaskResult<T> run() throws Exception;
}