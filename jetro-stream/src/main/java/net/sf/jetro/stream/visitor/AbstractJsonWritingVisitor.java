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

import java.io.IOException;

import net.sf.jetro.stream.JsonGenerator;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class AbstractJsonWritingVisitor<R> implements JsonVisitor<R> {
	@Override
	public JsonObjectVisitor<R> visitObject() {
		try {
			getGenerator().beginObject();
			return newJsonObjectVisitor();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonObjectVisitor<R> newJsonObjectVisitor();

	@Override
	public JsonArrayVisitor<R> visitArray() {
		try {
			getGenerator().beginArray();
			return newJsonArrayVisitor();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonArrayVisitor<R> newJsonArrayVisitor();

	@Override
	public void visitProperty(String name) {
		try {
			getGenerator().name(name);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(boolean value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(Number value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(String value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitNullValue() {
		try {
			getGenerator().nullValue();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitEnd() {
		try {
			getGenerator().flush();
			getGenerator().close();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonGenerator getGenerator();
}