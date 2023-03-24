package no.navnesen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

	@Test
	public void constructorAcceptsTaskResult() {
		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(TaskResult.success(0));
			assertNotNull(task._result.get());
			assertEquals(0, task.await());
		});

		assertDoesNotThrow(() -> {
			Task<Integer> task = Task.complete(0);
			assertNotNull(task._result.get());
			assertEquals(0, task.await());
		});

		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(TaskResult.failure(new Exception("hello")));
			assertNotNull(task._result.get());
			assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
		});

		assertDoesNotThrow(() -> {
			Task<Integer> task = Task.fail(new Exception("hello"));
			assertNotNull(task._result.get());
			assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
		});
	}

}