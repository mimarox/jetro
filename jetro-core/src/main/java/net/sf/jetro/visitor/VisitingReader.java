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
package net.sf.jetro.visitor;

/**
 * Generic interface for a class reading a representation of JSON and calling
 * the methods of the {@link JsonVisitor} given to the {@link #accept(JsonVisitor)}
 * method accordingly.
 * 
 * @author matthias.rothe
 */
public interface VisitingReader {

	/**
	 * Accepts a visitor to call its visit methods according to the structure of the read
	 * JSON representation.
	 * 
	 * @param visitor The accepted visitor
	 */
	void accept(JsonVisitor<?> visitor);
}