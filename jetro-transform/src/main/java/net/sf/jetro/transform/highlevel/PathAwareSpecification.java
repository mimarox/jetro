package net.sf.jetro.transform.highlevel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonPrimitive;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

/**
 * This class is part of the {@link TransformationSpecification} fluent API.
 * <p>
 * It provides methods operating at a given {@link JsonPath}.
 * 
 * @author Matthias Rothe
 */
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

	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * The value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the default {@link SerializationContext}.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 */
	public void addJsonProperty(final String key, final Object value) {
		addJsonProperty(key, value, new SerializationContext());
	}
	
	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * The value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the given {@link SerializationContext}.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 * @param context the context to be used for serialization of the value
	 */
	public void addJsonProperty(final String key, final Object value,
			final SerializationContext context) {
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(context, "context must not be null");
		addJsonProperty(key, val -> new ObjectVisitingReader(val, context), () -> value);
	}
	
	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * The value must be a {@link JsonType}, either a {@link JsonObject}, a
	 * {@link JsonArray} or any one of the {@link JsonPrimitive}s.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 */
	public void addJsonProperty(final String key, final JsonType value) {
		Objects.requireNonNull(key, "key must not be null");
		addJsonProperty(key, JsonElementVisitingReader::new, () -> value);
	}

	/**
	 * Use this method to add a JSON property with the given key and the value
	 * saved with the given variable name to a JSON object.
	 * <p>
	 * If a value of <code>null</code> is retrieved for the given variable name,
	 * it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param variableName the variable name of the value of the new property
	 */
	public void addJsonPropertyFromVariable(final String key,
			final String variableName) {
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
	
	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 */
	public void addJsonProperty(final String key, final Boolean value) {
		addJsonProperty(key, value != null ? new JsonBoolean(value) : null);
	}
	
	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 */
	public void addJsonProperty(final String key, final Number value) {
		addJsonProperty(key, value != null ? new JsonNumber(value) : null);
	}
	
	/**
	 * Use this method to add a JSON property with the given key and value
	 * to a JSON object.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 *  
	 * @param key the key or name of the new property
	 * @param value the value of the new property
	 */
	public void addJsonProperty(final String key, final String value) {
		addJsonProperty(key, value != null ? new JsonString(value) : null);
	}

	/**
	 * Use this method to add all JSON properties set on the given
	 * {@link JsonObject} to a JSON object.
	 * <p>
	 * If a value of a property is <code>null</code> it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>, otherwise it is omitted.
	 * 
	 * @param jsonObject the JsonObject to add the properties of
	 */
	public void addAllJsonProperties(final JsonObject jsonObject) {
		Objects.requireNonNull(jsonObject, "jsonObject must not be null");
		addAllJsonProperties(JsonElementVisitingReader::new, () -> jsonObject.asMap());
	}
	
	/**
	 * Use this method to add all keys and values of the given map of properties
	 * as JSON properties to a JSON object.
	 * <p>
	 * The values should be Java Beans, but don't have to be {@link Serializable}.
	 * They will be serialized to JSON using the default {@link SerializationContext}.
	 * <p>
	 * If a value of a key value pair is <code>null</code> it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise it is omitted.
	 * 
	 * @param properties the map of key value pairs to be added as properties
	 * @param <T> the common parent type of the values, may be {@link Object}
	 */
	public <T> void addAllJsonProperties(final Map<String, T> properties) {
		addAllJsonProperties(properties, new SerializationContext());
	}
	
	/**
	 * Use this method to add all keys and values of the given map of properties
	 * as JSON properties to a JSON object.
	 * <p>
	 * The values should be Java Beans, but don't have to be {@link Serializable}.
	 * They will be serialized to JSON using the given {@link SerializationContext}.
	 * <p>
	 * If a value of a key value pair is <code>null</code> it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise it is omitted.
	 * 
	 * @param properties the map of key value pairs to be added as properties
	 * @param context the context to be used for serialization of the value
	 * @param <T> the common parent type of the values, may be {@link Object}
	 */
	public <T> void addAllJsonProperties(final Map<String, T> properties,
			final SerializationContext context) {
		Objects.requireNonNull(properties, "properties must not be null");
		Objects.requireNonNull(context, "context must not be null");
		
		addAllJsonProperties(value -> new ObjectVisitingReader(value, context),
				() -> properties);
	}
	
	/**
	 * Use this method to add all JSON properties of the {@link JsonObject}
	 * saved with the given variable name to a JSON object.
	 * <p>
	 * If a value of <code>null</code> is retrieved as the value of a property of
	 * the JsonObject, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the property is omitted.
	 * 
	 * @param variableName the variable name of the JsonObject to add the properties of
	 * @throws NoSuchElementException if no value could be found for the given
	 * variable name
	 * @throws ClassCastException if the value found for the given variable name is
	 * not a {@link JsonObject}
	 */
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

	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * The value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the default {@link SerializationContext}.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 */
	public void addJsonValue(final Object value) {
		addJsonValue(value, new SerializationContext());
	}
	
	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * The value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the given {@link SerializationContext}.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 * @param context the context to be used for serialization of the value
	 */
	public void addJsonValue(final Object value, final SerializationContext context) {
		Objects.requireNonNull(context, "context must not be null");
		addJsonValues(val -> new ObjectVisitingReader(val, context),
				() -> Arrays.asList(value));
	}
	
	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 */
	public void addJsonValue(final JsonType value) {
		addJsonValues(JsonElementVisitingReader::new, () -> Arrays.asList(value));
	}
	
	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 */
	public void addJsonValue(final Boolean value) {
		addJsonValue(value != null ? new JsonBoolean(value) : null);
	}
	
	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 */
	public void addJsonValue(final Number value) {
		addJsonValue(value != null ? new JsonNumber(value) : null);
	}
	
	/**
	 * Use this method to add the given value to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is given, it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 * 
	 * @param value the value to add
	 */
	public void addJsonValue(final String value) {
		addJsonValue(value != null ? new JsonString(value) : null);
	}

	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * Each value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the default {@link SerializationContext}.
	 * <p>
	 * If <code>null</code> values are given, they are rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 */
	public void addAllJsonValues(final Object... values) {
		addAllJsonValues(new SerializationContext(), values);
	}
	
	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * Each value should be a Java Bean, but doesn't have to be {@link Serializable}.
	 * It will be serialized to JSON using the given {@link SerializationContext}.
	 * <p>
	 * If <code>null</code> values are given, they are rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 * @param context the context to be used for serialization of the values
	 */
	public void addAllJsonValues(final SerializationContext context, final Object... values) {
		Objects.requireNonNull(context, "context must not be null");
		addAllJsonValues(context, Arrays.asList(values));
	}

	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * Each element of values should be a Java Bean, but doesn't have to be
	 * {@link Serializable}. It will be serialized to JSON using the default
	 * {@link SerializationContext}.
	 * <p>
	 * If the values iterable contains <code>null</code> values, they are rendered
	 * if {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 */
	public void addAllJsonValues(final Iterable<?> values) {
		Objects.requireNonNull(values, "values must not be null");		
		addAllJsonValues(new SerializationContext(), values);
	}
	
	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * Each element of values should be a Java Bean, but doesn't have to be
	 * {@link Serializable}. It will be serialized to JSON using the given
	 * {@link SerializationContext}.
	 * <p>
	 * If the values iterable contains <code>null</code> values, they are rendered
	 * if {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 * @param context the context to be used for serialization of the values
	 */
	public void addAllJsonValues(final SerializationContext context, final Iterable<?> values) {
		Objects.requireNonNull(context, "context must not be null");
		Objects.requireNonNull(values, "values must not be null");

		addJsonValues(val -> new ObjectVisitingReader(val, context), () -> values);
	}
	
	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * If <code>null</code> values are given, they are rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 */
	public void addAllJsonTypes(final JsonType... values) {
		addAllJsonTypes(Arrays.asList(values));
	}
	
	/**
	 * Use this method to add all of the given values to a JSON array.
	 * <p>
	 * If the values iterable contains <code>null</code> values, they are rendered
	 * if {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise they are omitted.
	 * 
	 * @param values the values to add
	 */
	public void addAllJsonTypes(final Iterable<? extends JsonType> values) {
		Objects.requireNonNull(values, "values must not be null");
		addJsonValues(JsonElementVisitingReader::new, () -> values);
	}

	/**
	 * Use this method to add the value saved with the given variable name
	 * to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is retrieved for the given variable name,
	 * it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 *
	 * @param variableName the variable name of the value
	 */
	public void addFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		addJsonValues(JsonElementVisitingReader::new,
				() -> Arrays.asList(specification.getVariable(variableName)));
	}
	
	/**
	 * Use this method to add all the values of the {@link JsonArray} or a single
	 * other value saved with the given variable name to a JSON array.
	 * <p>
	 * If a value of <code>null</code> is retrieved for the given variable name,
	 * it is rendered if
	 * {@link TransformationSpecification#setRenderNullValues(boolean)}
	 * is set to <code>true</code>. Otherwise the value is omitted.
	 *
	 * @param variableName the variable name of the value
	 */
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
			if (endsWithEndOfArray(path)) {
				return getEndOfArrayJsonValueAdder(readerProvider, valuesSupplier);
			} else {
				return getIndexedJsonValueAdder(readerProvider, valuesSupplier, false);
			}
		});
	}

	private boolean endsWithEndOfArray(final JsonPath path) {
		return !path.isRootPath() && path.hasEndOfArrayAt(path.getDepth() - 1);
	}
	
	private <T> ChainedJsonVisitor<Void> getEndOfArrayJsonValueAdder(
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

	/**
	 * Use this method to rename a property of a JSON object to new name.
	 * 
	 * @param newName the new name of the property
	 * @throws NullPointerException if newName is <code>null</code>
	 * @throws IllegalArgumentException if the {@link #path} doesn't end
	 * in a property name
	 */
	public void renamePropertyTo(final String newName) {
		Objects.requireNonNull(newName, "newName must not be null");
		
		if (!canRenameAt(path)) {
			throw new IllegalArgumentException(
					"path must end in a property name to be renamed");
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
		return !path.isRootPath() && path.hasPropertyNameAt(path.getDepth() - 1);
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 */
	public void replaceWith(final Object value) {
		replaceWith(value, new SerializationContext());
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value
	 * using the given {@link SerializationContext} to serialize the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 * @param context The SerializationContext to use
	 */
	public void replaceWith(final Object value, final SerializationContext context) {
		Objects.requireNonNull(context, "context must not be null");
		replaceWith(val -> new ObjectVisitingReader(val, context),
				() -> Arrays.asList(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 */
	public void replaceWith(final JsonType value) {
		replaceWith(JsonElementVisitingReader::new, () -> Arrays.asList(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 */
	public void replaceWith(final Boolean value) {
		replaceWith(new JsonBoolean(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 */
	public void replaceWith(final Number value) {
		replaceWith(new JsonNumber(value));
	}
	
	/**
	 * Use this method to replace the value at the given path with the given value.
	 * <p>
	 * Note: The given value may be <code>null</code>. In this case null is always
	 * rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param value The value to replace with
	 */
	public void replaceWith(final String value) {
		replaceWith(new JsonString(value));
	}

	/**
	 * Use this method to replace the value at the given path with the value
	 * referenced by the given variable name.
	 * <p>
	 * Note: The value referenced by the variable name may be <code>null</code>.
	 * In this case null is always rendered to the resulting JSON.
	 * {@link TransformationSpecification#isRenderNullValues()} is not considered by
	 * this method.
	 * 
	 * @param variableName the name of the variable to replace with
	 */
	public void replaceWithFromVariable(final String variableName) {
		Objects.requireNonNull(variableName, "variableName must not be null");
		
		replaceWith(JsonElementVisitingReader::new, () -> {
			return Arrays.asList(specification.getVariable(variableName));
		});
	}
	
	private <T> void replaceWith(final Function<T, VisitingReader> readerProvider,
			final Supplier<Iterable<T>> valuesSupplier) {
		specification.addChainedJsonVisitorSupplier(() ->
		getIndexedJsonValueAdder(readerProvider, valuesSupplier, true));
	}

	/**
	 * Use this method to replace the value at the given path, if and only if
	 * the given predicate evaluates to <code>true</code>. This method can be
	 * used to replace values only if the current values satisfy certain criteria.
	 * 
	 * @param predicate the predicate to evaluate
	 * @return an instance of {@link ReplaceIfSpecification}
	 */
	public ReplaceIfSpecification replaceIf(final Predicate<JsonType> predicate) {
		Objects.requireNonNull(predicate, "predicate must not be null");
		return new ReplaceIfSpecification(path, predicate, specification);
	}
}
