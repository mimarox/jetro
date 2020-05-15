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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Objects;

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
	 * Creates a JsonTreeBuilder with lenient set to false.
	 */
	public JsonTreeBuilder() {
		this(false);
	}

	public JsonTreeBuilder(final boolean lenient) {
		this.lenient = lenient;
	}

	/**
	 * @deprecated Use {@link #buildFrom(String)} instead
	 * @param json the JSON to build
	 * @return a JsonElement
	 */
	@Deprecated
	public JsonElement build(final String json) {
		return buildFrom(json);
	}

	public JsonElement buildFrom(final String json) {
		return buildFrom(json, (ChainedJsonVisitor<?>[]) null);
	}

	/**
	 * @deprecated Use {@link #buildFrom(String,ChainedJsonVisitor<?>...)} instead
	 * @param json the JSON to build
	 * @param transformers the transformers to use
	 * @return a JsonElement
	 */
	@Deprecated
	public JsonElement build(final String json, ChainedJsonVisitor<?>... transformers) {
		return buildFrom(json, transformers);
	}

	public JsonElement buildFrom(final String json, ChainedJsonVisitor<?>... transformers) {
		JsonElement root;

		if (json == null) {
			throw new IllegalArgumentException("json must not be null");
		} else if (json.equals("")) {
			root = new VirtualJsonRoot();
		} else {
			try {
				root = buildFrom(new StringReader(json), transformers);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return root;
	}
	
	/**
	 * Builds a {@link JsonElement} from a URL pointing to a JSON document
	 * using UTF-8 to read from the stream.
	 *  
	 * @param url the URL pointing to a JSON document
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be opened or closed
	 */
	public JsonElement buildFrom(final URL url) throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		return buildFrom(url.openStream());
	}
	
	/**
	 * Builds a {@link JsonElement} from a URL pointing to a JSON document using
	 * the named charset to read the stream.
	 *  
	 * @param url the URL pointing to a JSON document
	 * @param charsetName the name of the charset to use
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be opened or closed, or the given
	 * named charset is not supported
	 */
	public JsonElement buildFrom(final URL url, final String charsetName)
			throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");

		return buildFrom(url.openStream(), charsetName);
	}
	
	/**
	 * Builds a {@link JsonElement} from a URL pointing to a JSON document
	 *  using UTF-8 to read from the stream and applying the given transformers.
	 *  
	 * @param url the URL pointing to a JSON document
	 * @param transformers the transformers to apply
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be opened or closed
	 */
	public JsonElement buildFrom(final URL url,
			final ChainedJsonVisitor<?>... transformers) throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		return buildFrom(url.openStream(), transformers);
	}
	
	/**
	 * Builds a {@link JsonElement} from a URL pointing to a JSON document using
	 * the named charset to read the stream and applying the given transformers.
	 *  
	 * @param url the URL pointing to a JSON document
	 * @param charsetName the name of the charset to use
	 * @param transformers the transformers to apply
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be opened or closed, or the given
	 * named charset cannot be used
	 */
	public JsonElement buildFrom(final URL url, final String charsetName,
			final ChainedJsonVisitor<?>... transformers)
					throws IOException {
		Objects.requireNonNull(url, "url must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");

		return buildFrom(url.openStream(), charsetName, transformers);		
	}
	
	/**
	 * Builds a {@link JsonElement} from an {@link InputStream} containing a
	 * JSON document using UTF-8 to read from the stream.
	 *  
	 * @param in the InputStream containing a JSON document
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be closed
	 * @throws RuntimeException if the executing system doesn't support UTF-8
	 */
	public JsonElement buildFrom(final InputStream in) throws IOException {
		Objects.requireNonNull(in, "in must not be null");

		try {
			return buildFrom(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Builds a {@link JsonElement} from an {@link InputStream} containing a
	 * JSON document using the named charset to read the stream.
	 *  
	 * @param in the InputStream containing a JSON document
	 * @param charsetName the name of the charset to use
	 * @return the built JsonElement
	 * @throws IOException if the stream cannot be closed, or the given
	 * named charset is not supported
	 */
	public JsonElement buildFrom(final InputStream in, final String charsetName)
			throws IOException {
		Objects.requireNonNull(in, "in must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		return buildFrom(new InputStreamReader(in, charsetName));
	}
	
	public JsonElement buildFrom(final InputStream in,
			final ChainedJsonVisitor<?>... transformers) throws IOException {
		Objects.requireNonNull(in, "in must not be null");

		try {
			return buildFrom(new InputStreamReader(in, "UTF-8"), transformers);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JsonElement buildFrom(final InputStream in, final String charsetName,
			final ChainedJsonVisitor<?>... transformers)
					throws IOException {
		Objects.requireNonNull(in, "in must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		return buildFrom(new InputStreamReader(in, charsetName), transformers);		
	}
	
	/**
	 * @deprecated Use {@link #buildFrom(Reader)} instead
	 * @param in the reader to read the JSON from
	 * @return a JsonElement
	 * @throws RuntimeException if the reader can't be closed
	 */
	@Deprecated
	public JsonElement build(final Reader in) {
		try {
			return buildFrom(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public JsonElement buildFrom(final Reader in) throws IOException {
		return buildFrom(in, (ChainedJsonVisitor<?>[]) null);
	}
	
	/**
	 * @deprecated Use {@link #buildFrom(Reader,ChainedJsonVisitor<?>...)} instead
	 * @param in the reader to read the JSON from
	 * @param transformers the transformers to use
	 * @return a JsonElement
	 * @throws RuntimeException if the reader can't be closed
	 */
	@Deprecated
	public JsonElement build(final Reader in,
			final ChainedJsonVisitor<?>... transformers) {
		try {
			return buildFrom(in, transformers);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public JsonElement buildFrom(final Reader in,
			final ChainedJsonVisitor<?>... transformers) throws IOException {
		JsonReader reader = new JsonReader(in);
		reader.setLenient(lenient);

		try (StreamVisitingReader visitingReader = new StreamVisitingReader(reader)) {
			JsonTreeBuildingVisitor treeBuildingVisitor = new JsonTreeBuildingVisitor();
			JsonVisitor<?> visitor = buildTransformerChain(transformers, treeBuildingVisitor);

			visitingReader.accept(visitor);
			return treeBuildingVisitor.getVisitingResult();
		}
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