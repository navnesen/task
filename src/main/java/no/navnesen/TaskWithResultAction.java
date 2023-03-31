package no.navnesen;

public interface TaskWithResultAction<T> {
	void run(T result);
}
