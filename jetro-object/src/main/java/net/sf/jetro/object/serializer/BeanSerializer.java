/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object.serializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.jetro.object.annotations.JsonAlias;
import net.sf.jetro.object.annotations.JsonIgnore;
import net.sf.jetro.object.annotations.JsonIgnoreProperties;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class BeanSerializer implements TypeSerializer<Object> {
	private SerializationContext context;

	public BeanSerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		try {
			BeanInfo info = Introspector.getBeanInfo(toSerialize.getClass());
			PropertyDescriptor[] properties = info.getPropertyDescriptors();

			if (properties.length > 1) { // first is always getClass()
				for (PropertyDescriptor property : properties) {
					Method getter = property.getReadMethod();

					if (isRealGetter(getter)) {
						return true;
					}
				}
			}
		} catch (IntrospectionException e) {}

		return false;
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		if (toSerialize == null) {
			recipient.visitNullValue();
			return;
		}

		try {
			JsonObjectVisitor<?> objectVisitor = recipient.visitObject();
			
			Set<String> propertiesToIgnore = getPropertiesToIgnore(toSerialize);
			
			BeanInfo info = Introspector.getBeanInfo(toSerialize.getClass());
			PropertyDescriptor[] properties = info.getPropertyDescriptors();

			for (PropertyDescriptor property : properties) {
				Method getter = property.getReadMethod();

				if (isRealGetter(getter) && !isIgnoredProperty(propertiesToIgnore, property)) {
					Object value = getter.invoke(toSerialize);
					TypeSerializer<Object> serializer = context.getTypeSerializer(value);

					objectVisitor.visitProperty(alias(toSerialize, property));
					serializer.serialize(value, objectVisitor);
				}
			}

			objectVisitor.visitEnd();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Set<String> getPropertiesToIgnore(Object toSerialize) {
		Set<String> propertiesToIgnore = new HashSet<>();
		
		populatePropertiesToIgnoreFromType(propertiesToIgnore, toSerialize);
		populatePropertiesToIgnoreFromFields(propertiesToIgnore, toSerialize);
		
		return propertiesToIgnore;
	}

	private void populatePropertiesToIgnoreFromType(Set<String> propertiesToIgnore, Object toSerialize) {
		JsonIgnoreProperties jsonIgnoreProperties = 
				toSerialize.getClass().getAnnotation(JsonIgnoreProperties.class);
		
		if (jsonIgnoreProperties != null) {
			propertiesToIgnore.addAll(Arrays.asList(jsonIgnoreProperties.properties()));
		}
		
		Class<?> superClass = toSerialize.getClass().getSuperclass();
		
		if (superClass != null) {
			populatePropertiesToIgnoreFromSuperclass(propertiesToIgnore, superClass);
		}
	}
	
	private void populatePropertiesToIgnoreFromSuperclass(Set<String> propertiesToIgnore,
			Class<?> superClass) {
		JsonIgnoreProperties jsonIgnoreProperties = 
				superClass.getAnnotation(JsonIgnoreProperties.class);
		
		if (jsonIgnoreProperties != null) {
			propertiesToIgnore.addAll(Arrays.asList(jsonIgnoreProperties.properties()));
		}
		
		Class<?> superSuperClass = superClass.getSuperclass();
		
		if (superSuperClass != null) {
			populatePropertiesToIgnoreFromSuperclass(propertiesToIgnore, superSuperClass);
		}
	}

	private void populatePropertiesToIgnoreFromFields(Set<String> propertiesToIgnore, Object toSerialize) {
		Field[] fields = toSerialize.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
			
			if (jsonIgnore != null) {
				propertiesToIgnore.add(field.getName());
			}
		}
		
		Class<?> superClass = toSerialize.getClass().getSuperclass();
		
		if (superClass != null) {
			populatePropertiesToIgnoreFromFieldsOfSuperclass(propertiesToIgnore, superClass);
		}		
	}

	private void populatePropertiesToIgnoreFromFieldsOfSuperclass(Set<String> propertiesToIgnore,
			Class<?> superClass) {
		Field[] fields = superClass.getDeclaredFields();
		
		for (Field field : fields) {
			JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
			
			if (jsonIgnore != null) {
				propertiesToIgnore.add(field.getName());
			}
		}
		
		Class<?> superSuperClass = superClass.getSuperclass();
		
		if (superSuperClass != null) {
			populatePropertiesToIgnoreFromFieldsOfSuperclass(propertiesToIgnore, superSuperClass);
		}		
	}

	private boolean isRealGetter(Method getter) {
		return getter != null && !getter.getName().equals("getClass");
	}

	private boolean isIgnoredProperty(Set<String> propertiesToIgnore, PropertyDescriptor property) {
		return propertiesToIgnore.contains(property.getName());
	}

	private String alias(Object toSerialize, PropertyDescriptor property) {
		String propertyName = null;
		
		try {
			Field field = toSerialize.getClass().getDeclaredField(property.getName());
			JsonAlias jsonAlias = field.getAnnotation(JsonAlias.class);
			
			if (jsonAlias != null) {
				propertyName = jsonAlias.newKey();
			} else {
				propertyName = property.getName();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			propertyName = property.getName();
		}
		
		return propertyName;
	}
}
