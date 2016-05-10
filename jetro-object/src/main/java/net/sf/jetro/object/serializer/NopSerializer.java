/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class NopSerializer implements TypeSerializer<Object> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return false; // so this will never be accidentally selected
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		// do nothing
	}
}
