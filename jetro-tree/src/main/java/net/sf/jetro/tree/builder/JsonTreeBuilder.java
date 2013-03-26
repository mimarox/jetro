package net.sf.jetro.tree.builder;

import java.io.Reader;
import java.io.StringReader;

import net.sf.jeson.stream.JsonReader;
import net.sf.jeson.tree.JsonElement;
import net.sf.jeson.tree.VirtualJsonRoot;
import net.sf.jeson.visitor.io.JsonVisitingReader;

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

		JsonVisitingReader visitingReader = new JsonVisitingReader(reader);
		JsonTreeBuildingVisitor builder = new JsonTreeBuildingVisitor();

		visitingReader.accept(builder);
		return builder.getVisitingResult();
	}
}