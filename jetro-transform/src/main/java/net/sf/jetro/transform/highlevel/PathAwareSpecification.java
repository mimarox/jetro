package net.sf.jetro.transform.highlevel;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

public class PathAwareSpecification {
	private final JsonPath path;
	private final TransformationSpecification specification;
	
	PathAwareSpecification(final JsonPath path,
			final TransformationSpecification specification) {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(specification, "specification must not be null");
		
		this.path = path;
		this.specification = specification;
	}

	public void addJsonProperty(final String key, final Object value) {
		addJsonProperty(key, value, new SerializationContext());
	}
	
	public void addJsonProperty(final String key, final Object value,
			final SerializationContext context) {
		addJsonProperty(key, new ObjectVisitingReader(value, context));
	}
	
	public void addJsonProperty(final String key, final JsonType value) {
		addJsonProperty(key, new JsonElementVisitingReader(value));
	}
	
	private void addJsonProperty(final String key, final VisitingReader reader) {
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				
				@Override
				protected boolean doBeforeVisitObjectEnd() {
					JsonVisitor<Void> visitor = getNextVisitor();
					
					if (visitor != null && currentPath().matches(path)) {
						visitor.visitProperty(key);
						reader.accept(visitor);
					}
					
					return true;
				}
			};
		});
	}
	
	public void addJsonProperty(final String key, final Boolean value) {
		addJsonProperty(key, new JsonBoolean(value));
	}
	
	public void addJsonProperty(final String key, final Number value) {
		addJsonProperty(key, new JsonNumber(value));
	}
	
	public void addJsonProperty(final String key, final String value) {
		addJsonProperty(key, new JsonString(value));
	}

	public void addJsonValue(final Object value) {
		addJsonValue(value, new SerializationContext());
	}
	
	public void addJsonValue(final Object value, final SerializationContext context) {
		addJsonValues(val -> new ObjectVisitingReader(val, context),
				() -> Arrays.asList(value));
	}
	
	public void addJsonValue(final JsonType value) {
		addJsonValues(JsonElementVisitingReader::new, () -> Arrays.asList(value));
	}
	
	public void addJsonValue(final Boolean value) {
		addJsonValue(new JsonBoolean(value));
	}
	
	public void addJsonValue(final Number value) {
		addJsonValue(new JsonNumber(value));
	}
	
	public void addJsonValue(final String value) {
		addJsonValue(new JsonString(value));
	}

	public void addAllJsonValues(final Object... values) {
		addAllJsonValues(new SerializationContext(), values);
	}
	
	public void addAllJsonValues(final SerializationContext context, final Object... values) {
		addAllJsonValues(context, Arrays.asList(values));
	}

	public void addAllJsonValues(final Iterable<?> values) {
		addAllJsonValues(new SerializationContext(), values);
	}
	
	public void addAllJsonValues(final SerializationContext context, final Iterable<?> values) {
		addJsonValues(val -> new ObjectVisitingReader(val, context), () -> values);
	}
	
	public void addAllJsonTypes(final JsonType... values) {
		addAllJsonTypes(Arrays.asList(values));
	}
	
	public void addAllJsonTypes(final Iterable<? extends JsonType> values) {
		addJsonValues(JsonElementVisitingReader::new, () -> values);
	}
	
	private <T> void addJsonValues(final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		specification.addChainedJsonVisitorSupplier(() -> {
			if (endsWithArrayWildcard(path)) {
				return getWildcardJsonValueAdder(readerProvider, valuesSupplier);
			} else {
				return getIndexedJsonValueAdder(readerProvider, valuesSupplier);
			}
		});
	}

	private boolean endsWithArrayWildcard(final JsonPath path) {
		return path.hasArrayIndexAt(path.getDepth() - 1) &&
				path.hasWildcardAt(path.getDepth() - 1);
	}
	
	private <T> ChainedJsonVisitor<Void> getWildcardJsonValueAdder(
			final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		JsonPath actualPath = path.removeLastElement();
		
		return new PathAwareJsonVisitor<Void>() {
			
			@Override
			protected boolean doBeforeVisitArrayEnd() {
				JsonVisitor<Void> visitor = getNextVisitor();
				
				if (visitor != null && currentPath().matches(actualPath)) {
					valuesSupplier.get().forEach(value -> {
						if (value != null) {
							readerProvider.apply(value).accept(visitor);							
						} else if (specification.isRenderNullValues()) {
							visitor.visitNullValue();
						}
					});
				}
				
				return true;
			}
		};
	}

	private <T> ChainedJsonVisitor<Void> getIndexedJsonValueAdder(
			final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		return new PathAwareJsonVisitor<Void>() {
			
			@Override
			protected void afterVisitObjectEnd() {
				applyTransformation();
			}
			
			@Override
			protected void afterVisitArrayEnd() {
				applyTransformation();
			}
			
			@Override
			protected void afterVisitValue(final Boolean value) {
				applyTransformation();
			}
			
			@Override
			protected void afterVisitValue(final Number value) {
				applyTransformation();
			}
			
			@Override
			protected void afterVisitValue(final String value) {
				applyTransformation();
			}
			
			private void applyTransformation() {
				JsonVisitor<Void> visitor = getNextVisitor();
				
				if (visitor != null && currentPath().matches(path)) {
					valuesSupplier.get().forEach(value -> {
						if (value != null) {
							readerProvider.apply(value).accept(visitor);							
						} else if (specification.isRenderNullValues()) {
							visitor.visitNullValue();
						}
					});
				}
			}
		};
	}

	public void addFromVariable(final String variableName) {
		addJsonValues(JsonElementVisitingReader::new,
				() -> Arrays.asList(specification.getVariable(variableName)));
	}
	
	public void addAllFromVariable(final String variableName) {
		addJsonValues(JsonElementVisitingReader::new, () -> {
			JsonType value = specification.getVariable(variableName);
			
			if (value instanceof JsonArray) {
				return (JsonArray) value;
			} else {
				return Arrays.asList(value);
			}
		});
	}
}
