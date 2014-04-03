package net.sf.jetro.visitor.pathaware;

import net.sf.jetro.path.ArrayIndexPathElement;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.path.PropertyNamePathElement;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

public abstract class PathAwareJsonVisitor<R> extends UniformChainedJsonVisitor<R> {
	private abstract class ParseState {
		protected boolean justEntered;
	}

	private class ObjectState extends ParseState {
	}

	private class ArrayState extends ParseState {
		private int index;
	}

	private JsonPath currentPath = new JsonPath();
	private Stack<ParseState> stateStack = new Stack<ParseState>();

	public PathAwareJsonVisitor() {
	}

	public PathAwareJsonVisitor(final JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}

	protected JsonPath currentPath() {
		return currentPath;
	}

	@Override
	protected final boolean beforeVisitObject() {
		handleVisitValue();
		ObjectState state = new ObjectState();
		state.justEntered = true;
		stateStack.push(state);
		return doBeforeVisitObject();
	}

	protected boolean doBeforeVisitObject() {
		return true;
	}

	@Override
	protected final boolean beforeVisitArray() {
		handleVisitValue();
		ArrayState state = new ArrayState();
		state.justEntered = true;
		stateStack.push(state);
		return doBeforeVisitArray();
	}

	protected boolean doBeforeVisitArray() {
		return true;
	}

	@Override
	protected final String beforeVisitProperty(final String name) {
		if (!stateStack.isEmpty()) {
			ParseState state = stateStack.peek();

			if (state instanceof PathAwareJsonVisitor.ObjectState) {
				PropertyNamePathElement pathElement = new PropertyNamePathElement(name);

				if (state.justEntered) {
					state.justEntered = false;
					currentPath = currentPath.append(pathElement);
				} else {
					currentPath = currentPath.replaceLastElementWith(pathElement);
				}
			}
		}

		return doBeforeVisitProperty(name);
	}

	protected String doBeforeVisitProperty(final String name) {
		return name;
	}

	@Override
	protected final Boolean beforeVisitValue(final boolean value) {
		handleVisitValue();
		return doBeforeVisitValue(value);
	}

	protected boolean doBeforeVisitValue(final boolean value) {
		return value;
	}

	@Override
	protected final Number beforeVisitValue(final Number value) {
		handleVisitValue();
		return doBeforeVisitValue(value);
	}

	protected Number doBeforeVisitValue(final Number value) {
		return value;
	}

	@Override
	protected final String beforeVisitValue(final String value) {
		handleVisitValue();
		return doBeforeVisitValue(value);
	}

	protected String doBeforeVisitValue(final String value) {
		return value;
	}

	@Override
	protected final boolean beforeVisitNullValue() {
		handleVisitValue();
		return doBeforeVisitNullValue();
	}

	protected boolean doBeforeVisitNullValue() {
		return true;
	}

	private void handleVisitValue() {
		if (!stateStack.isEmpty()) {
			ParseState state = stateStack.peek();

			if (state instanceof PathAwareJsonVisitor.ArrayState) {
				@SuppressWarnings("unchecked")
				ArrayIndexPathElement pathElement = new ArrayIndexPathElement(((ArrayState) state).index++);

				if (state.justEntered) {
					state.justEntered = false;
					currentPath = currentPath.append(pathElement);
				} else {
					currentPath = currentPath.replaceLastElementWith(pathElement);
				}
			}
		}
	}

	@Override
	protected final boolean beforeVisitObjectEnd() {
		handleVisitEnd();
		return doBeforeVisitObjectEnd();
	}

	protected boolean doBeforeVisitObjectEnd() {
		return true;
	}

	@Override
	protected final boolean beforeVisitArrayEnd() {
		handleVisitEnd();
		return doBeforeVisitArrayEnd();
	}

	protected boolean doBeforeVisitArrayEnd() {
		return true;
	}

	@Override
	protected final boolean beforeVisitEnd() {
		handleVisitEnd();
		return doBeforeVisitEnd();
	}

	protected boolean doBeforeVisitEnd() {
		return true;
	}

	private void handleVisitEnd() {
		if (!stateStack.isEmpty()) {
			ParseState state = stateStack.pop();

			if (!state.justEntered) {
				currentPath = currentPath.removeLastElement();
			}
		}
	}
}