/*
 * #%L
 * Jetro Stream
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
package net.sf.jetro.stream.visitor;

import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;

public final class JsonWritingVisitor extends AbstractJsonWritingVisitor<Void> {
	private class JsonWritingObjectVisitor extends AbstractJsonWritingObjectVisitor<Void> {
		@Override
		protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonWriter getGenerator() {
			return writer;
		}

		@Override
		public Void getVisitingResult() {
			return null;
		}
	}

	private class JsonWritingArrayVisitor extends AbstractJsonWritingArrayVisitor<Void> {
		@Override
		protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonWriter getGenerator() {
			return writer;
		}

		@Override
		public Void getVisitingResult() {
			return null;
		}
	}

	private JsonWriter writer;
	private JsonObjectVisitor<Void> objectVisitor = new JsonWritingObjectVisitor();
	private JsonArrayVisitor<Void> arrayVisitor = new JsonWritingArrayVisitor();

	public JsonWritingVisitor(final JsonWriter writer) {
		if (writer == null) {
			throw new IllegalArgumentException("jsonWriter must not be null");
		}

		this.writer = writer;
	}

	@Override
	protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
		return objectVisitor;
	}

	@Override
	protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
		return arrayVisitor;
	}

	@Override
	protected JsonWriter getGenerator() {
		return writer;
	}

	@Override
	public Void getVisitingResult() {
		return null;
	}
}