/*
 * #%L
 * Jetro Core
 * %%
 * Copyright (C) 2013 - 2020 The original author or authors.
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

/**
 * This class stores settings related to JSON rendering and makes them accessible.
 * 
 * @author Matthias Rothe
 */
public class RenderContext {
	private String indent;
	private boolean lenient;
	private boolean htmlSafe;
	private boolean serializeNulls = true;

	/**
	 * Returns the indent.
	 * 
	 * @return the indent
	 */
	public String getIndent() {
		return indent;
	}

	/**
	 * Sets the indent and returns this object.
	 * 
	 * @param indent the indent to set
	 * @return this object
	 */
	public RenderContext setIndent(String indent) {
		this.indent = indent;
		return this;
	}

	/**
	 * Returns the lenient setting.
	 * 
	 * @return the lenient setting
	 */
	public boolean isLenient() {
		return lenient;
	}

	/**
	 * Sets the lenient setting and returns this object.
	 * 
	 * @param lenient the lenient setting to set
	 * @return this object
	 */
	public RenderContext setLenient(boolean lenient) {
		this.lenient = lenient;
		return this;
	}

	/**
	 * Returns the HTML-safe setting.
	 * 
	 * @return the HTML-safe setting
	 */
	public boolean isHtmlSafe() {
		return htmlSafe;
	}

	/**
	 * Sets the HTML-safe setting and returns this object.
	 * 
	 * @param htmlSafe the HTML-safe setting to set
	 * @return this object
	 */
	public RenderContext setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
		return this;
	}

	/**
	 * Returns the serialize nulls setting.
	 * 
	 * @return the serialize nulls setting
	 */
	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	/**
	 * Sets the serialize nulls setting and returns this object.
	 * 
	 * @param serializeNulls the serialize nulls setting to set
	 * @return this object
	 */
	public RenderContext setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
		return this;
	}
}