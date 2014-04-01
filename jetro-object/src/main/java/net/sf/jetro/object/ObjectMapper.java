package net.sf.jetro.object;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;

import java.io.InputStream;
import java.io.StringReader;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectMapper {
	public ObjectMerger merge(Object toMerge) {
		return new ObjectMerger(toMerge);
	}

	public String toJson(Object object) {
		JsonReturningVisitor receiver = new JsonReturningVisitor();
		merge(object).into(receiver);
		return receiver.getVisitingResult();
	}

	public <T> T fromJson(String json, Class<T> targetClass) {
		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));
		ObjectBuildingVisitor visitor = new ObjectBuildingVisitor(null, TypeToken.of(targetClass));
		reader.accept(visitor);
		return targetClass.cast(visitor.getVisitingResult());
	}

	public <T> T fromJson(InputStream in, Class<T> clazz) {
		return null;
	}
}
