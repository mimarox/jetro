package net.sf.jetro.tree.builder;

import java.io.Reader;
import java.io.StringReader;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.VirtualJsonRoot;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;

public class JsonTreeBuilder {
	private boolean lenient;

	public JsonTreeBuilder() {
		this(false);
	}

	public JsonTreeBuilder(final boolean lenient) {
		this.lenient = lenient;
	}

	public JsonElement build(final String json) {
		JsonElement root;

		if (json == null) {
			throw new IllegalArgumentException("json must not be null");
		} else if (json.equals("")) {
			root = new VirtualJsonRoot();
		} else {
			root = build(new StringReader(json));
		}

		return root;
	}

	public JsonElement build(final Reader in) {
		JsonReader reader = new JsonReader(in);
		reader.setLenient(lenient);

		StreamVisitingReader visitingReader = new StreamVisitingReader(reader);
		JsonTreeBuildingVisitor builder = new JsonTreeBuildingVisitor();

		visitingReader.accept(builder);
		return builder.getVisitingResult();
	}
}