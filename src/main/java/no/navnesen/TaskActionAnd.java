package no.navnesen;

public interface TaskActionAnd<V, T> {
	Awaitable<V> run(T value) throws Exception;
}
