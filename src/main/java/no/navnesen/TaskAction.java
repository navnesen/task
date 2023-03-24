package no.navnesen;

public interface TaskAction<T> {
	T run() throws Exception;
}
