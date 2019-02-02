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

import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

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

			BeanInfo info = Introspector.getBeanInfo(toSerialize.getClass());
			PropertyDescriptor[] properties = info.getPropertyDescriptors();

			for (PropertyDescriptor property : properties) {
				Method getter = property.getReadMethod();

				if (isRealGetter(getter)) {
					Object value = getter.invoke(toSerialize);
					TypeSerializer<Object> serializer = context.getTypeSerializer(value);

					objectVisitor.visitProperty(property.getName());
					serializer.serialize(value, objectVisitor);
				}
			}

			objectVisitor.visitEnd();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean isRealGetter(Method getter) {
		return getter != null && !getter.getName().equals("getClass");
	}
}
