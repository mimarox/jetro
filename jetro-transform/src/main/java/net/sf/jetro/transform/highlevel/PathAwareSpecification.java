package net.sf.jetro.transform.highlevel;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
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
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(context, "context must not be null");
		addJsonProperty(key, val -> new ObjectVisitingReader(val, context), () -> value);
	}
	
	public void addJsonProperty(final String key, final JsonType value) {
		Objects.requireNonNull(key, "key must not be null");
		addJsonProperty(key, JsonElementVisitingReader::new, () -> value);
	}

	public void addJsonPropertyFromVariable(String key, String variableName) {
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(variableName, "variableName must not be null");
		addJsonProperty(key, JsonElementVisitingReader::new,
				() -> specification.getVariable(variableName));
	}
	
	private <T> void addJsonProperty(final String key,
			final Function<T, VisitingReader> readerProvider,
			final Supplier<T> valueSupplier) {
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				
				@Override
				protected boolean doBeforeVisitObjectEnd() {
					JsonVisitor<Void> visitor = getNextVisitor();
					
					if (visitor != null && currentPath().matches(path)) {
						T value = valueSupplier.get();
						
						if (value != null) {
							visitor.visitProperty(key);
							readerProvider.apply(value).accept(visitor);							
						} else if (specification.isRenderNullValues()) {
							visitor.visitProperty(key);
							visitor.visitNullValue();
						}
					}
					
					return true;
				}
			};
		});
	}
	
	public void addJsonProperty(final String key, final Boolean value) {
		addJsonProperty(key, value != null ? new JsonBoolean(value) : null);
	}
	
	public void addJsonProperty(final String key, final Number value) {
		addJsonProperty(key, value != null ? new JsonNumber(value) : null);
	}
	
	public void addJsonProperty(final String key, final String value) {
		addJsonProperty(key, value != null ? new JsonString(value) : null);
	}

	public void addAllJsonProperties(final JsonObject jsonObject) {
		Objects.requireNonNull(jsonObject, "jsonObject must not be null");
		addAllJsonProperties(JsonElementVisitingReader::new, () -> jsonObject.asMap());
	}
	
	public <T> void addAllJsonProperties(final Map<String, T> properties) {
		addAllJsonProperties(properties, new SerializationContext());
	}
	
	public <T> void addAllJsonProperties(final Map<String, T> properties,
			final SerializationContext context) {
		Objects.requireNonNull(properties, "properties must not be null");
		Objects.requireNonNull(context, "context must not be null");
		
		addAllJsonProperties(value -> new ObjectVisitingReader(value, context),
				() -> properties);
	}
	
	public void addAllJsonPropertiesFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		addAllJsonProperties(JsonElementVisitingReader::new, () -> {
			JsonType value = specification.getVariable(variableName);
			
			if (value != null && value instanceof JsonObject) {
				return ((JsonObject) value).asMap();
			} else if (value == null) {
				throw new NoSuchElementException("No value found for variable name " +
						variableName + ".");
			} else {
				throw new ClassCastException("The variable value with name " +
						variableName + " is not a JsonObject.");
			}
		});
	}
	
	private <T> void addAllJsonProperties(
			final Function<T, VisitingReader> readerProvider,
			final Supplier<Map<String, T>> valueSupplier) {
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				
				@Override
				protected boolean doBeforeVisitObjectEnd() {
					JsonVisitor<Void> visitor = getNextVisitor();
					
					if (visitor != null && currentPath().matches(path)) {
						valueSupplier.get().entrySet().forEach(entry -> {
							String key = entry.getKey();
							T value = entry.getValue();
							
							if (value != null) {
								visitor.visitProperty(key);
								readerProvider.apply(value).accept(visitor);							
							} else if (specification.isRenderNullValues()) {
								visitor.visitProperty(key);
								visitor.visitNullValue();
							}
						});
					}
					
					return true;
				}
			};
		});
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
		addJsonValue(value != null ? new JsonBoolean(value) : null);
	}
	
	public void addJsonValue(final Number value) {
		addJsonValue(value != null ? new JsonNumber(value) : null);
	}
	
	public void addJsonValue(final String value) {
		addJsonValue(value != null ? new JsonString(value) : null);
	}

	public void addAllJsonValues(final Object... values) {
		addAllJsonValues(new SerializationContext(), values);
	}
	
	public void addAllJsonValues(final SerializationContext context, final Object... values) {
		Objects.requireNonNull(context, "context must not be null");
		addAllJsonValues(context, Arrays.asList(values));
	}

	public void addAllJsonValues(final Iterable<?> values) {
		Objects.requireNonNull(values, "values must not be null");		
		addAllJsonValues(new SerializationContext(), values);
	}
	
	public void addAllJsonValues(final SerializationContext context, final Iterable<?> values) {
		Objects.requireNonNull(context, "context must not be null");
		Objects.requireNonNull(values, "values must not be null");

		addJsonValues(val -> new ObjectVisitingReader(val, context), () -> values);
	}
	
	public void addAllJsonTypes(final JsonType... values) {
		addAllJsonTypes(Arrays.asList(values));
	}
	
	public void addAllJsonTypes(final Iterable<? extends JsonType> values) {
		Objects.requireNonNull(values, "values must not be null");
		addJsonValues(JsonElementVisitingReader::new, () -> values);
	}

	public void addFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		addJsonValues(JsonElementVisitingReader::new,
				() -> Arrays.asList(specification.getVariable(variableName)));
	}
	
	public void addAllFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		
		addJsonValues(JsonElementVisitingReader::new, () -> {
			JsonType value = specification.getVariable(variableName);
			
			if (value instanceof JsonArray) {
				return (JsonArray) value;
			} else {
				return Arrays.asList(value);
			}
		});
	}
	
	private <T> void addJsonValues(final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		specification.addChainedJsonVisitorSupplier(() -> {
			if (endsWithArrayWildcard(path)) {
				return getWildcardJsonValueAdder(readerProvider, valuesSupplier);
			} else {
				return getIndexedJsonValueAdder(readerProvider, valuesSupplier, false);
			}
		});
	}

	private boolean endsWithArrayWildcard(final JsonPath path) {
		return !path.isRootPath() && path.hasArrayIndexAt(path.getDepth() - 1) &&
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
			final Supplier<Iterable<T>> valuesSupplier,
			final boolean replace) {
		return new PathAwareJsonVisitor<Void>() {
			
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
				return passOn() ? value : null;
			}
			
			@Override
			protected Number doBeforeVisitValue(final Number value) {
				return passOn() ? value : null;
			}
			
			@Override
			protected String doBeforeVisitValue(final String value) {
				return passOn() ? value : null;
			}
			
			@Override
			protected boolean doBeforeVisitNullValue() {
				return passOn();
			}
			
			private boolean passOn() {
				if (replace && currentPath().matches(path)) {
					return false;
				} else {
					return true;
				}
			}
			
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
						} else if (replace || specification.isRenderNullValues()) {
							visitor.visitNullValue();
						}
					});
				}
			}
		};
	}

	public void renamePropertyTo(final String newName) {
		Objects.requireNonNull(newName, "newName must not be null");
		
		if (!canRenameAt(path)) {
			throw new IllegalArgumentException("path must end in a property name to be renamed");
		}
		
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				
				@Override
				protected String doBeforeVisitProperty(final String name) {
					if (currentPath().matches(path)) {
						return newName;
					} else {
						return name;
					}
				}
			};
		});
	}

	private boolean canRenameAt(final JsonPath path) {
		return path.hasPropertyNameAt(path.getDepth() - 1);
	}

	public void replaceWith(final Object value) {
		replaceWith(value, new SerializationContext());
	}
	
	public void replaceWith(final Object value, final SerializationContext context) {
		replaceWith(val -> new ObjectVisitingReader(value, context),
				() -> Arrays.asList(value));
	}
	
	public void replaceWith(final JsonType value) {
		replaceWith(JsonElementVisitingReader::new, () -> Arrays.asList(value));
	}
	
	public void replaceWith(final Boolean value) {
		replaceWith(new JsonBoolean(value));
	}
	
	public void replaceWith(final Number value) {
		replaceWith(new JsonNumber(value));
	}
	
	public void replaceWith(final String value) {
		replaceWith(new JsonString(value));
	}
	
	private <T> void replaceWith(final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		specification.addChainedJsonVisitorSupplier(() ->
		getIndexedJsonValueAdder(readerProvider, valuesSupplier, true));
	}
}
