package no.navnesen;

public interface TaskActionMap<V, T> {
	V run(T value) throws Exception;
}
