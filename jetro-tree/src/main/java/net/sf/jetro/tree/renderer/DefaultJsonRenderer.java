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
package net.sf.jetro.tree.renderer;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.VirtualJsonRoot;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;

public class DefaultJsonRenderer implements JsonRenderer {
	private RenderContext context;

	public DefaultJsonRenderer() {
		this(new RenderContext());
	}

	public DefaultJsonRenderer(final RenderContext context) {
		if (context == null) {
			throw new IllegalArgumentException("context must not be null");
		}

		this.context = context;
	}

	@Override
	public String render(final JsonElement element) {
		if (element instanceof VirtualJsonRoot) {
			VirtualJsonRoot rootElement = (VirtualJsonRoot) element;

			StringBuilder renderingResult = new StringBuilder();
			String separator = shouldPrettyPrint() ? ",\n" : ",";

			for (JsonElement childElement : rootElement) {
				renderingResult.append(renderInternal(childElement)).append(separator);
			}

			return renderingResult.substring(0, renderingResult.length() - 1);
		} else {
			return renderInternal(element);
		}
	}

	private String renderInternal(final JsonElement element) {
		JsonElementVisitingReader visitingReader = new JsonElementVisitingReader(element);
		JsonReturningVisitor returningVisitor = new JsonReturningVisitor(context);
		visitingReader.accept(returningVisitor);
		return returningVisitor.getVisitingResult();
	}

	private boolean shouldPrettyPrint() {
		return context.getIndent() != null && !context.getIndent().equals("");
	}
}