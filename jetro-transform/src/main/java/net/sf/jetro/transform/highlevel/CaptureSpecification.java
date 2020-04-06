package net.sf.jetro.transform.highlevel;

import java.util.Objects;
import java.util.function.Function;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonType;

public class CaptureSpecification {
	private final JsonPath path;
	private final TransformationSpecification specification;
	
	CaptureSpecification(final JsonPath path,
			final TransformationSpecification specification) {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(specification, "specification must not be null");
		
		this.path = path;
		this.specification = specification;
	}

	public <S extends JsonType, T extends JsonType> CaptureEditSpecification<S, T>
	edit(final Function<S, T> editor) {
		Objects.requireNonNull(editor, "editor must not be null");
		return new CaptureEditSpecification<>(path, editor, specification);
	}
	
	public <S extends JsonType, T extends JsonType>
	CaptureEditSpecification<JsonArray, JsonArray>
	editEach(final Function<S, T> editor) {
		Objects.requireNonNull(editor, "editor must not be null");
		
		@SuppressWarnings("unchecked")
		Function<JsonArray, JsonArray> eachEditor = array -> {
			JsonArray result = new JsonArray();
			array.forEach(value -> result.add(editor.apply((S) value)));
			return result;
		};
		
		return new CaptureEditSpecification<>(path, eachEditor, specification);
	}
	
	public void andSaveAs(final String variableName) {
		new CaptureEditSpecification<>(path, Function.identity(), specification)
		.andSaveAs(variableName);
	}
}
