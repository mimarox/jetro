package net.sf.jetro.patch;

import java.util.Objects;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonType;

public class ReplacePatchOperation extends ValuePatchOperation {
	public ReplacePatchOperation(JsonObject patchDefinition) {
		super(patchDefinition);
	}

	@Override
	public JsonType applyPatch(final JsonType source) throws JsonPatchException {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		if (!(source instanceof JsonCollection)) {
			throw new JsonPatchException(new IllegalArgumentException("source must either be "
					+ "a JsonArray or a JsonObject"));
		}
		
		final JsonCollection target = (JsonCollection) source.deepCopy();
		final JsonPath jsonPath = path.toJsonPath();
		
		if (target.hasElementAt(jsonPath)) {
			target.replaceElementAt(jsonPath, value);
			return handleTarget(target);
		} else {
			throw new JsonPatchException("Cannot replace a non-existent value");
		}
	}
}
