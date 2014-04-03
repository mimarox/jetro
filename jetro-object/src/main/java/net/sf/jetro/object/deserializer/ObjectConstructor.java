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
