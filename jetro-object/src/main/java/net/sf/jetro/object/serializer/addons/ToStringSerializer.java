package net.sf.jetro.object.serializer.addons;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.visitor.JsonVisitor;

public class ToStringSerializer implements TypeSerializer<Object> {
	private Set<Class<?>> applicableClasses = new LinkedHashSet<>();
	
	public ToStringSerializer(Class<?>... applicableClasses) {
		this.applicableClasses.addAll(Arrays.asList(applicableClasses));
	}
	
	public ToStringSerializer(Set<Class<?>> applicableClasses) {
		this.applicableClasses.addAll(applicableClasses);
	}
	
	@Override
	public boolean canSerialize(Object toSerialize) {
		return applicableClasses.parallelStream()
				.filter(clazz -> clazz.equals(toSerialize.getClass())).findFirst().isPresent();
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize.toString());
	}
}
