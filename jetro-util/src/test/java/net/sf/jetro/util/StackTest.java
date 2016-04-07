/*
 * #%L
 * Jetro Utilities
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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