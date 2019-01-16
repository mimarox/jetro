/*
 * #%L
 * Jetro Core
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
package net.sf.jetro.context;

public class RenderContext {
	private String indent;
	private boolean lenient;
	private boolean htmlSafe;
	private boolean serializeNulls = true;

	public String getIndent() {
		return indent;
	}

	public RenderContext setIndent(String indent) {
		this.indent = indent;
		return this;
	}

	public boolean isLenient() {
		return lenient;
	}

	public RenderContext setLenient(boolean lenient) {
		this.lenient = lenient;
		return this;
	}

	public boolean isHtmlSafe() {
		return htmlSafe;
	}

	public RenderContext setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
		return this;
	}

	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	public RenderContext setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
		return this;
	}
}