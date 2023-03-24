package no.navnesen;

public interface TaskActionAnd<V, T> {
	Task<V> run(T value) throws Exception;
}
