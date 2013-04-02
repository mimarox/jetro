package net.sf.jetro.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Implements a LIFO stack. This class offers synchronized as well as unsynchronized methods,
 * so it's up to the developer to decide whether synchronization is necessary or not. Besides,
 * it offers only stack related methods, so there is no chance of accidentally corrupting the stack. 
 * <p>
 * This is in stark contrast to the implementation of the standard Java {@link java.util.Stack Stack}
 * class. 
 * 
 * @author Matthias Rothe
 * @param <E> The type of element held in this instance
 */
public class Stack<E> {
	private List<E> store = new ArrayList<E>();

	/**
	 * Pushes a new element onto the stack.
	 * <p>
	 * This method is unsynchronized and therefore not thread-safe. If you need thread-safe access to this stack,
	 * use {@link #pushSynchronized(Object)} instead.
	 * 
	 * @param element The element to push
	 */
	public void push(E element) {
		store.add(element);
	}

	/**
	 * Pushes a new element onto the stack.
	 * <p>
	 * This method is synchronized and therefore thread-safe. If you don't need thread-safe access to this stack,
	 * use {@link #push(Object)} instead to increase performance.
	 * 
	 * @param element The element to push
	 */
	public synchronized void pushSynchronized(E element) {
		push(element);
	}

	/**
	 * Retrieves the topmost element from this stack, without removing it.
	 * <p>
	 * This method is unsynchronized and therefore not thread-safe. If you need thread-safe access to this stack,
	 * use {@link #peekSynchronized()} instead.
	 * 
	 * @return The topmost element of this stack
	 */
	public E peek() {
		return store.get(getIndex());
	}

	/**
	 * Retrieves the topmost element from this stack, without removing it.
	 * <p>
	 * This method is synchronized and therefore thread-safe. If you don't need thread-safe access to this stack,
	 * use {@link #peek()} instead to increase performance.
	 * 
	 * @return The topmost element of this stack
	 */
	public synchronized E peekSynchronized() {
		return peek();
	}

	/**
	 * Retrieves and removes the topmost element from this stack.
	 * <p>
	 * This method is unsynchronized and therefore not thread-safe. If you need thread-safe access to this stack,
	 * use {@link #popSynchronized()} instead.
	 * 
	 * @return The topmost element of this stack
	 */
	public E pop() {
		return store.remove(getIndex());
	}

	/**
	 * Retrieves and removes the topmost element from this stack.
	 * <p>
	 * This method is synchronized and therefore thread-safe. If you don't need thread-safe access to this stack,
	 * use {@link #pop()} instead to increase performance.
	 * 
	 * @return The topmost element of this stack
	 */
	public synchronized E popSynchronized() {
		return pop();
	}

	/**
	 * Checks whether this stack is empty, e.g. has no elements.
	 * <p>
	 * This method is unsynchronized and therefore not thread-safe. If you need thread-safe access to this stack,
	 * use {@link #isEmptySynchronized()} instead.
	 * 
	 * @return <code>true</code>, if and only if this stack has no elements, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return store.isEmpty();
	}

	/**
	 * Checks whether this stack is empty, e.g. has no elements.
	 * <p>
	 * This method is synchronized and therefore thread-safe. If you don't need thread-safe access to this stack,
	 * use {@link #isEmpty()} instead to increase performance.
	 * 
	 * @return <code>true</code>, if and only if this stack has no elements, <code>false</code> otherwise
	 */
	public synchronized boolean isEmptySynchronized() {
		return isEmpty();
	}

	private int getIndex() {
		int index = store.size() - 1;

		if (index == -1) {
			throw new EmptyStackException();
		}

		return index;
	}
}