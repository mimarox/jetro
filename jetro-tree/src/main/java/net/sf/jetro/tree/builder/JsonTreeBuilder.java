/*
 * #%L
 * Jetro Tree
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
package net.sf.jetro.tree.builder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.VirtualJsonRoot;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

public class JsonTreeBuilder {
	private boolean lenient;
	
	/**
	 * Creates a JsonTreeBuilder with lenient set to false
	 */
	public JsonTreeBuilder() {
		this(false);
	}

	public JsonTreeBuilder(final boolean lenient) {
		this.lenient = lenient;
	}

	public JsonElement build(final String json) {
		return build(json, (ChainedJsonVisitor<?>[]) null);
	}

	public JsonElement build(final String json, ChainedJsonVisitor<?>... transformers) {
		JsonElement root;

		if (json == null) {
			throw new IllegalArgumentException("json must not be null");
		} else if (json.equals("")) {
			root = new VirtualJsonRoot();
		} else {
			root = build(new StringReader(json), transformers);
		}

		return root;
	}
	
	public JsonElement build(final Reader in) {
		return build(in, (ChainedJsonVisitor<?>[]) null);
	}
	
	public JsonElement build(final Reader in, ChainedJsonVisitor<?>... transformers) {
		JsonReader reader = new JsonReader(in);
		reader.setLenient(lenient);

		StreamVisitingReader visitingReader = new StreamVisitingReader(reader);
		JsonTreeBuildingVisitor treeBuildingVisitor = new JsonTreeBuildingVisitor();
		JsonVisitor<?> visitor = buildTransformerChain(transformers, treeBuildingVisitor);

		visitingReader.accept(visitor);
		try {
			visitingReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return treeBuildingVisitor.getVisitingResult();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JsonVisitor<?> buildTransformerChain(final ChainedJsonVisitor<?>[] transformers,
			final JsonTreeBuildingVisitor jsonTreeBuildingVisitor) {
		if (transformers == null) {
			return jsonTreeBuildingVisitor;
		}
		
		ChainedJsonVisitor first = transformers[0];
		
		int i = 1;
		while (i < transformers.length) {
			first.attachVisitor(transformers[i++]);
		}
		
		first.attachVisitor(jsonTreeBuildingVisitor);
		
		return first;
		
	}
}