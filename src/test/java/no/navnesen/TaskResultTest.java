package no.navnesen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskResultTest {
	@Test
	public void validSuccessResult() {
		assertDoesNotThrow(() -> {
			TaskResult<Integer> result = new TaskResult<>(false, null, 0);
			assertFalse(result.didThrow);
			assertNull(result.exception);
			assertEquals(0, result.value);
		});
		assertDoesNotThrow(() -> {
			TaskResult<Integer> result = TaskResult.success(0);
			assertFalse(result.didThrow);
			assertNull(result.exception);
			assertEquals(0, result.value);
		});
	}

	@Test
	public void validFailureResult() {
		assertDoesNotThrow(() -> {
			TaskResult<Integer> result = new TaskResult<>(true, new Exception("hello"), null);
			assertTrue(result.didThrow);
			assertNull(result.value);
			assertThrows(
				Exception.class,
				() -> {
					throw result.exception;
				},
				"hello"
			);
		});
		assertDoesNotThrow(() -> {
			TaskResult<Integer> result = TaskResult.failure(new Exception("hello"));
			assertTrue(result.didThrow);
			assertNull(result.value);
			assertThrows(
				Exception.class,
				() -> {
					throw result.exception;
				},
				"hello"
			);
		});
	}

	@Test
	public void didNotThrowValueIsNull() {
		assertThrows(
			NullPointerException.class,
			() -> TaskResult.success(null),
			"Could not instantiate TaskResult: The returned action value cannot be null!"
		);
	}

	@Test
	public void didThrowExceptionIsNull() {
		assertThrows(
			NullPointerException.class,
			() -> new TaskResult<Integer>(true, null, null),
			"Could not instantiate TaskResult: the thrown exception cannot be null!"
		);
	}
}