package no.navnesen;

public interface TaskActionOrElse<T> {
	Awaitable<T> run(Exception exception) throws Exception;
}
