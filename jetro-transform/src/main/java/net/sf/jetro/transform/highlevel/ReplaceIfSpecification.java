package net.sf.jetro.transform.highlevel;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonPrimitive;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

/**
 * This class is part of the {@link TransformationSpecification} fluent API.
 * <p>
 * It provides several methods called <code>with</code> and one method
 * {@link #withFromVariable(String)}.
 * 
 * @author Matthias Rothe
 */
public class ReplaceIfSpecification {
	private final class ReplaceTransformer<T> extends PathAwareJsonVisitor<Void> {
		private final Function<T, VisitingReader> readerProvider;
		private final Supplier<T> valueSupplier;
		private JsonTreeBuildingVisitor treeBuilder = new JsonTreeBuildingVisitor();
		private JsonPrimitive<?> primitive;

		private ReplaceTransformer(Function<T, VisitingReader> readerProvider, Supplier<T> valueSupplier) {
			this.readerProvider = readerProvider;
			this.valueSupplier = valueSupplier;
		}

		@Override
		protected boolean doBeforeVisitObject() {
			return passOn();
		}

		@Override
		protected boolean doBeforeVisitArray() {
			return passOn();
		}

		@Override
		protected Boolean doBeforeVisitValue(final boolean value) {
			if (passOn()) {
				return value;
			} else {
				primitive = new JsonBoolean(value);
				return null;
			}
		}

		@Override
		protected Number doBeforeVisitValue(final Number value) {
			if (passOn()) {
				return value;
			} else {
				primitive = new JsonNumber(value);
				return null;
			}
		}

		@Override
		protected String doBeforeVisitValue(final String value) {
			if (passOn()) {
				return value;
			} else {
				primitive = new JsonString(value);
				return null;
			}
		}

		@Override
		protected boolean doBeforeVisitNullValue() {
			if (passOn()) {
				return true;
			} else {
				primitive = new JsonNull();
				return false;
			}
		}

		private boolean passOn() {
			if (currentPath().matches(path)) {
				return false;
			} else {
				return true;
			}					
		}

		@Override
		@SuppressWarnings({ "unchecked" })
		protected JsonObjectVisitor<Void> afterVisitObject(
				final JsonObjectVisitor<Void> visitor) {
			JsonObjectVisitor<Void> actualVisitor = visitor;
			
			if (currentPath().matches(path)) {
				actualVisitor = (JsonObjectVisitor)
						treeBuilder.visitObject();
			}
			
			return super.afterVisitObject(actualVisitor);
		}

		@Override
		@SuppressWarnings({ "unchecked" })
		protected JsonArrayVisitor<Void> afterVisitArray(
				final JsonArrayVisitor<Void> visitor) {
			JsonArrayVisitor<Void> actualVisitor = visitor;
			
			if (currentPath().matches(path)) {
				actualVisitor = (JsonArrayVisitor) treeBuilder.visitArray();
			}
			
			return super.afterVisitArray(actualVisitor);
		}

		@Override
		protected void afterVisitObjectEnd() {
			applyTransformation(false);
		}

		@Override
		protected void afterVisitArrayEnd() {
			applyTransformation(false);
		}

		@Override
		protected void afterVisitValue(final Boolean value) {
			applyTransformation(true);
		}

		@Override
		protected void afterVisitValue(final Number value) {
			applyTransformation(true);
		}

		@Override
		protected void afterVisitValue(final String value) {
			applyTransformation(true);
		}

		@Override
		protected void afterVisitNullValue() {
			applyTransformation(true);
		}

		private void applyTransformation(final boolean fromPrimitive) {
			JsonVisitor<Void> visitor = getNextVisitor();
			
			if (visitor != null && currentPath().matches(path)) {
				JsonType capturedValue = getCapturedValue(fromPrimitive);
				
				if (predicate.test(capturedValue)) {
					T value = valueSupplier.get();
					
					if (value != null) {
						readerProvider.apply(value).accept(visitor);
					} else {
						visitor.visitNullValue();
					}
				} else {
					capturedValue.mergeInto(visitor);
				}
			}
		}

		private JsonType getCapturedValue(boolean fromPrimitive) {
			if (fromPrimitive) {
				return primitive;
			} else {
				treeBuilder.visitEnd();
				return (JsonType) treeBuilder.getVisitingResult();
			}
		}
	}

	private final JsonPath path;
	private final Predicate<JsonType> predicate;
	private final TransformationSpecification specification;
	
	ReplaceIfSpecification(final JsonPath path, final Predicate<JsonType> predicate,
			final TransformationSpecification specification) {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(predicate, "predicate must not be null");
		Objects.requireNonNull(specification, "specification must not be null");
		
		this.path = path;
		this.predicate = predicate;
		this.specification = specification;
	}

	/**
	 * Use this method to replace the value at the given path with the given value
	 * if and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value the value to replace with
	 */
	public void with(final Object value) {
		with(value, new SerializationContext());
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * using the given {@link SerializationContext} to serialize the given value if
	 * and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 * @param context The SerializationContext to use
	 */
	public void with(final Object value, final SerializationContext context) {
		Objects.requireNonNull(context, "context must not be null");
		replace(val -> new ObjectVisitingReader(val, context), () -> value);
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * if and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value the value to replace with
	 */
	public void with(final JsonType value) {
		replace(JsonElementVisitingReader::new, () -> value);
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * if and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value the value to replace with
	 */
	public void with(final Boolean value) {
		with(new JsonBoolean(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * if and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value the value to replace with
	 */
	public void with(final Number value) {
		with(new JsonNumber(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * if and only if the given predicate evaluated to <code>true</code>.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value the value to replace with
	 */
	public void with(final String value) {
		with(new JsonString(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the value
	 * referenced by the given variable name if and only if the given predicate
	 * evaluated to <code>true</code>.
	 * <p>
	 * Note: The value referenced by the variable name may be <code>null</code>.
	 * In this case null is always rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param variableName the name of the variable to replace with
	 */
	public void withFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		replace(JsonElementVisitingReader::new, () -> {
			return specification.getVariable(variableName);
		});
	}
	
	@SuppressWarnings("rawtypes")
	private <T> void replace(final Function<T, VisitingReader> readerProvider,
			final Supplier<T> valueSupplier) {
		specification.addChainedJsonVisitorSupplier(() ->
				new ReplaceTransformer<T>(readerProvider, valueSupplier));
	}
}
