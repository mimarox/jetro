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
package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class ObjectConstructor {
	private Map<TypeToken<?>, InstanceCreator<?>> instanceCreatorMap = new HashMap<TypeToken<?>, InstanceCreator<?>>();

	public <T> T constructFrom(Class<T> clazz) {
		return (T) constructFrom(TypeToken.of(clazz));
	}

	public Object constructFrom(Type type) {
		return constructFrom(TypeToken.of(type));
	}

	public <T> T constructFrom(TypeToken<T> typeToken) {
		InstanceCreator<T> instanceCreator = getInstanceCreator(typeToken);

		if (instanceCreator != null) {
			return instanceCreator.createInstance(typeToken);
		} else {
			try {
				return typeToken.getRawType().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private <T> InstanceCreator<T> getInstanceCreator(TypeToken<T> typeToken) {
		if (instanceCreatorMap.containsKey(typeToken)) {
			return (InstanceCreator<T>) instanceCreatorMap.get(typeToken);
		} else {
			return null;
		}
	}

	public <T> void addInstanceCreator(TypeToken<T> typeToken, InstanceCreator<T> instanceCreator) {
		instanceCreatorMap.put(typeToken, instanceCreator);
	}
}
