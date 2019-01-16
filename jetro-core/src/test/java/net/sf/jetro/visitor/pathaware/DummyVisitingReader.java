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
package net.sf.jetro.visitor.pathaware;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

public class DummyVisitingReader implements VisitingReader {

	@Override
	public void accept(JsonVisitor<?> visitor) {
		// {
		JsonObjectVisitor<?> rootVisitor = visitor.visitObject();

		// {"key":
		rootVisitor.visitProperty("key");

		// {"key":[
		JsonArrayVisitor<?> keyVisitor = rootVisitor.visitArray();

		// {"key":[null
		keyVisitor.visitNullValue();

		// {"key":[null,2.0
		keyVisitor.visitValue(2.0);

		// {"key":[null,2.0,"<&>"
		keyVisitor.visitValue("<&>");

		// {"key":[null,2.0,"<&>",true
		keyVisitor.visitValue(true);

		// {"key":[null,2.0,"<&>",true,[
		JsonArrayVisitor<?> key_4_Visitor = keyVisitor.visitArray();

		// {"key":[null,2.0,"<&>",true,[">&<"
		key_4_Visitor.visitValue(">&<");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"
		key_4_Visitor.visitValue("bar");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"]
		key_4_Visitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{
		JsonObjectVisitor<?> key_5_Visitor = keyVisitor.visitObject();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":
		key_5_Visitor.visitProperty("key");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"
		key_5_Visitor.visitValue("äöü");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}
		key_5_Visitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}]
		keyVisitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo"
		rootVisitor.visitProperty("foo");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo":"bar"
		rootVisitor.visitValue("bar");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo":"bar"}
		rootVisitor.visitEnd();

		// finalize visiting
		visitor.visitEnd();
	}
}