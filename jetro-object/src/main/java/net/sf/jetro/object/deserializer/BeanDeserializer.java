package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public class BeanDeserializer implements TypeDeserializer<Object> {
	private DeserializationContext context;

	public BeanDeserializer(DeserializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canDeserialize(TypeToken<Object> typeToken) {
		try {
			BeanInfo info = Introspector.getBeanInfo(typeToken.getClass());
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
	public JsonVisitor<Object> getVisitorFor(TypeToken<Object> typeToken) {
		return new ObjectBuildingVisitor<Object>(context, typeToken);
	}

	private boolean isRealGetter(Method getter) {
		return getter != null && !getter.getName().equals("getClass");
	}
}
