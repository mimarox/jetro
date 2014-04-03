package net.sf.jetro.visitor.chained;

import com.sun.org.apache.regexp.internal.RE;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class multiplexes all calls to all subsequent visitors.
 *
 * @author matthias.rothe
 * @since 13.03.14.
 */
public final class MultiplexingJsonVisitor<R> extends UniformChainedJsonVisitor<R> {
	private List<Stack<JsonVisitor<?>>> visitorStacks;

	public MultiplexingJsonVisitor(JsonVisitor<R> masterVisitor, JsonVisitor<?>... slaveVisitors) {
		super(masterVisitor);

		if (slaveVisitors != null && slaveVisitors.length > 0) {
			visitorStacks = new ArrayList<Stack<JsonVisitor<?>>>();

			for (JsonVisitor<?> visitor : slaveVisitors) {
				Stack<JsonVisitor<?>> stack = new Stack<JsonVisitor<?>>();
				stack.push(visitor);
				visitorStacks.add(stack);
			}
		}
	}

	@Override
	protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> visitor) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.push(stack.peek().visitObject());
		}

		return super.afterVisitObject(visitor);
	}

	@Override
	protected void afterVisitObjectEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			JsonVisitor<?> topVisitor = stack.peek();

			if (topVisitor instanceof JsonObjectVisitor) {
				topVisitor.visitEnd();
				stack.pop();
			}
		}
	}

	@Override
	protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> visitor) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.push(stack.peek().visitArray());
		}

		return super.afterVisitArray(visitor);
	}

	@Override
	protected void afterVisitArrayEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			JsonVisitor<?> topVisitor = stack.peek();

			if (topVisitor instanceof JsonArrayVisitor) {
				topVisitor.visitEnd();
				stack.pop();
			}
		}
	}

	@Override
	protected void afterVisitProperty(String name) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitProperty(name);
		}
	}

	@Override
	protected void afterVisitValue(Boolean value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitValue(Number value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitValue(String value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitNullValue() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitNullValue();
		}
	}

	@Override
	protected void afterVisitEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.pop().visitEnd();
		}
	}
}
