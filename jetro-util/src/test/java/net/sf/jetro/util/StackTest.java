package net.sf.jetro.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EmptyStackException;

import org.testng.annotations.Test;

public class StackTest {
	/*+*
	 * As the synchronized stack methods are synchronized by modifier, checking whether the
	 * synchronization works, would be unit testing the JVM. Therefore these methods are
	 * called without actually checking the effect of synchronization.
	 */

	private Stack<String> stack = new Stack<String>();

	@Test
	public void shouldAlwaysReturnTopmostElement() {
		assertTrue(stack.isEmpty(), "Expected stack to be empty");

		stack.push("a");
		assertEquals(stack.peek(), "a");
		assertFalse(stack.isEmptySynchronized(), "Expected stack to not be empty");

		stack.pushSynchronized("b");
		assertEquals(stack.peek(), "b");
		assertFalse(stack.isEmpty(), "Expected stack to not be empty");

		stack.pushSynchronized("c");
		assertEquals(stack.peekSynchronized(), "c");
		assertFalse(stack.isEmpty(), "Expected stack to not be empty");
	}

	@Test(dependsOnMethods = "shouldAlwaysReturnTopmostElement")
	public void shouldAlwaysRemoveTopmostElement() {
		assertEquals(stack.popSynchronized(), "c");
		assertFalse(stack.isEmptySynchronized(), "Expected stack to not be empty");

		assertEquals(stack.popSynchronized(), "b");
		assertFalse(stack.isEmpty(), "Expected stack to not be empty");

		assertEquals(stack.pop(), "a");
		assertTrue(stack.isEmptySynchronized(), "Expected stack to be empty");
	}

	@Test(dependsOnMethods = "shouldAlwaysRemoveTopmostElement", expectedExceptions = EmptyStackException.class)
	public void shouldThrowWhenPeekOnEmptyStack() {
		stack.peek();
	}

	@Test(dependsOnMethods = "shouldAlwaysRemoveTopmostElement", expectedExceptions = EmptyStackException.class)
	public void shouldThrowWhenPopOnEmptyStack() {
		stack.pop();
	}

	@Test
	public void testMethodsAreSynchronized() throws Exception {
		Method pushSynchronized = Stack.class.getDeclaredMethod("pushSynchronized", Object.class);
		assertTrue(Modifier.isSynchronized(pushSynchronized.getModifiers()),
			"Expected pushSynchronized to be synchronized");

		Method peekSynchronized = Stack.class.getDeclaredMethod("peekSynchronized");
		assertTrue(Modifier.isSynchronized(peekSynchronized.getModifiers()),
			"Expected peekSynchronized to be synchronized");

		Method popSynchronized = Stack.class.getDeclaredMethod("popSynchronized");
		assertTrue(Modifier.isSynchronized(popSynchronized.getModifiers()),
			"Expected popSynchronized to be synchronized");

		Method isEmptySynchronized = Stack.class.getDeclaredMethod("isEmptySynchronized");
		assertTrue(Modifier.isSynchronized(isEmptySynchronized.getModifiers()),
			"Expected isEmptySynchronized to be synchronized");
	}
}