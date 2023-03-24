package no.navnesen;

public interface TaskActionOr<T> {
	T run(Exception exception) throws Exception;
}
