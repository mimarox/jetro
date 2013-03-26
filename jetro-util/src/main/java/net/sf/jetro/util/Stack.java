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
 * @author matthias.rothe
 * @param <E> The type of element held in this instance
 */
public class Stack<E> {
	private List<E> store = new ArrayList<E>();

	public void push(E element) {
		store.add(element);
	}

	public synchronized void pushSynchronized(E element) {
		push(element);
	}

	public E peek() {
		return store.get(getIndex());
	}

	public synchronized E peekSynchronized() {
		return peek();
	}

	public E pop() {
		return store.remove(getIndex());
	}

	public synchronized E popSynchronized() {
		return pop();
	}

	public boolean isEmpty() {
		return store.isEmpty();
	}

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