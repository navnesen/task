package no.navnesen;

public interface TaskActionOrElse<T> {
	Task<T> run(Exception exception) throws Exception;
}
