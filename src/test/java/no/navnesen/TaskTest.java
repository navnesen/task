package no.navnesen;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

	@Test
	public void constructorAcceptsAction() {
		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> {
				Thread.sleep(100);
				return 0;
			});
			assertNull(task._result.get());
			assertEquals(0, task.await());
			assertNotNull(task._result.get());
		});

		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> {
				throw new Exception("hello");
			});
			assertNull(task._result.get());
			assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
			assertNotNull(task._result.get());
		});
	}

	@Test
	public void constructorAcceptsTaskResultAction() {
		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> {
				Thread.sleep(100);
				return TaskResult.success(0);
			});
			assertNull(task._result.get());
			assertEquals(0, task.await());
			assertNotNull(task._result.get());
		});

		assertDoesNotThrow(() -> {
			Task<Integer> task = new Task<>(() -> TaskResult.failure(new Exception("hello")));
			assertNull(task._result.get());
			assertThrows(
				RuntimeException.class,
				task::await,
				"hello"
			);
			assertNotNull(task._result.get());
		});
	}

	@Test
	public void awaitAll() {
		List<Task<Integer>> tasks = new ArrayList<>();
		tasks.add(Task.complete(1));
		tasks.add(Task.complete(2));
		tasks.add(Task.complete(3));

		assertArrayEquals(
			new Integer[]{1, 2, 3},
			Task.all(tasks).await().toArray(new Integer[3])
		);
	}

	@Test
	public void successMap() {
		TaskActionMap<Boolean, Integer> isOne = value -> value == 1;
		assertTrue(Task.complete(1).map(isOne).await());
		assertFalse(Task.complete(2).map(isOne).await());
		assertFalse(Task.complete(0).map(isOne).await());
	}

	@Test
	public void failureMap() {
		Exception e = new Exception("hello");
		Task<Boolean> task = Task.<Integer>fail(e)
			.map(value -> value == 1);
		TaskResult<Boolean> result = task.waitForResult();
		assertTrue(result.didThrow);
		assertNull(result.value);
		assertEquals(e, result.exception);
	}

	@Test
	public void successOr() {
		assertDoesNotThrow(() -> assertEquals(1, Task.complete(1).or(ex -> -1).await()));
	}

	@Test
	public void failureOr() {
		assertDoesNotThrow(() -> assertEquals(-1, Task.fail(new Exception("hello")).or(ex -> {
			assertThrows(
				Exception.class,
				() -> {
					throw ex;
				},
				"hello"
			);
			return -1;
		}).await()));
	}

	@Test
	public void successOrElse() {
		assertDoesNotThrow(() -> assertEquals(1, Task.complete(1).orElse(ex -> Task.complete(-1)).await()));
	}

	@Test
	public void failureOrElse() {
		assertDoesNotThrow(() -> assertEquals(-1, Task.fail(new Exception("hello")).orElse(ex -> {
			assertThrows(
				Exception.class,
				() -> {
					throw ex;
				},
				"hello"
			);
			return Task.complete(-1);
		}).await()));
	}
}