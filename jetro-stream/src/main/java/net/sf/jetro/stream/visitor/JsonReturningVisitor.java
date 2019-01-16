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

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.stream.JsonGenerator;
import net.sf.jetro.util.FastAppendable;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;

import java.nio.CharBuffer;

public final class JsonReturningVisitor extends AbstractJsonWritingVisitor<String> {
	private class JsonReturningObjectVisitor extends AbstractJsonWritingObjectVisitor<String> {
		@Override
		protected JsonObjectVisitor<String> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<String> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonGenerator getGenerator() {
			return JsonReturningVisitor.this.getGenerator();
		}

		@Override
		public String getVisitingResult() {
			return JsonReturningVisitor.this.getVisitingResult();
		}
	}

	private class JsonReturningArrayVisitor extends AbstractJsonWritingArrayVisitor<String> {
		@Override
		protected JsonObjectVisitor<String> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<String> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonGenerator getGenerator() {
			return JsonReturningVisitor.this.getGenerator();
		}

		@Override
		public String getVisitingResult() {
			return JsonReturningVisitor.this.getVisitingResult();
		}
	}

	private StringBuilder buffer;
	private JsonGenerator generator;
	private RenderContext context;

	private JsonObjectVisitor<String> objectVisitor = new JsonReturningObjectVisitor();
	private JsonArrayVisitor<String> arrayVisitor = new JsonReturningArrayVisitor();

	public JsonReturningVisitor() {
		this(new RenderContext());
	}

	public JsonReturningVisitor(final RenderContext context) {
		if (context == null) {
			throw new IllegalArgumentException("context must not be null");
		}

		this.context = context;
		initJsonGenerator();
	}

	@Override
	protected JsonObjectVisitor<String> newJsonObjectVisitor() {
		return objectVisitor;
	}

	@Override
	protected JsonArrayVisitor<String> newJsonArrayVisitor() {
		return arrayVisitor;
	}

	@Override
	protected JsonGenerator getGenerator() {
		return generator;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	@Override
	public String getVisitingResult() {
		String result = buffer.toString();

		buffer.delete(0, buffer.length());
		generator.reset();

		return result;
	}

	private void initJsonGenerator() {
		buffer = new StringBuilder(1024);
		generator = new JsonGenerator(buffer);
		generator.setHtmlSafe(context.isHtmlSafe());
		generator.setIndent(context.getIndent());
		generator.setLenient(context.isLenient());
		generator.setSerializeNulls(context.isSerializeNulls());
	}
}