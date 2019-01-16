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
package net.sf.jetro.object.visitor;

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

import java.lang.reflect.Type;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class ObjectBuildingVisitor<R> extends UniformChainedJsonVisitor<R> {
	private DeserializationContext context;
	private TypeToken<R> targetTypeToken;
	private R resultObject;

	private JsonVisitor<?> buildingVisitor;

	public ObjectBuildingVisitor(DeserializationContext context, TypeToken<R> targetTypeToken) {
		this.context = context;
		this.targetTypeToken = targetTypeToken;
	}

	@Override
	protected boolean beforeVisitObject() {
		/*
			Prerequisite: A property has been visited and the *property name stored*

			1. Get the type of the field for the current property if present on the *result object*
			2. Get the *JsonVisitor* for that type from the deserialization registry
			3. Store this JsonVisitor
			4. Forward all calls until visitObjectEnd to JsonVisitor
			5. In visitObjectEnd
				a. call visitObjectEnd of JsonVisitor
				b. call getVisitingResult of JsonVisitor
				c. drop JsonVisitor
				d. set field identified by current property of result object to visiting result
				e. drop the current property

			We don't need a stack, but have a potentially very long call chain

			What about arrays?


			What about primitives?
				- if the result object is of primitive type (separate JsonVisitor implementation handling primitives)
					- visitObject, visitArray and visitProperty each throw an exception
					- visitValue throws an exception if called more then once
					- the value is stored as the result object

				- if the result object is of generic bean type
					- the value is set on the field identified by the current property if applicable
					- the current property is dropped

				- if the result object is of a specialized bean type
					- a separate JsonVisitor implementation decides how to handle the value

				- if the result object is of an array type
					- the value is added to the array

				- if the result object is of a map type
				    - the value is put to the map with the current property if the type of the value fits the expected type
				    - the current property is dropped
		 */

		if (buildingVisitor == null) {
			buildingVisitor = context.getObjectVisitorFor(targetTypeToken);
		} else {
			buildingVisitor.visitObject();
		}

		return true;
	}

	@Override
	protected void afterVisitObjectEnd() {
		if (buildingVisitor != null) {
			buildingVisitor.visitEnd();
		}
	}

	@Override
	protected boolean beforeVisitArray() {
		if (buildingVisitor == null) {
			buildingVisitor = context.getArrayVisitorFor(targetTypeToken);
		} else {
			buildingVisitor.visitArray();
		}

		return true;
	}

	@Override
	protected void afterVisitArrayEnd() {
		if (buildingVisitor != null) {
			buildingVisitor.visitEnd();
		}
	}

	@Override
	protected void afterVisitProperty(String name) {
		if (buildingVisitor != null) {
			buildingVisitor.visitProperty(name);
		}
	}

	@Override
	protected void afterVisitValue(String value) {
		if (buildingVisitor != null) {
			buildingVisitor.visitValue(value);
		} else if (targetTypeToken.isAssignableFrom(String.class)) {
			resultObject = (R) value;
		}
	}

	@Override
	protected void afterVisitValue(Number value) {
		if (buildingVisitor != null) {
			buildingVisitor.visitValue(value);
		} else if (targetTypeToken.isAssignableFrom(String.class)) {
			resultObject = (R) value;
		}
	}

	@Override
	protected void afterVisitValue(Boolean value) {
		if (buildingVisitor != null) {
			buildingVisitor.visitValue(value);
		} else if (targetTypeToken.isAssignableFrom(String.class)) {
			resultObject = (R) value;
		}
	}

	@Override
	protected void afterVisitNullValue() {
		if (buildingVisitor != null) {
			buildingVisitor.visitNullValue();
		}
	}

	@Override
	protected R afterGetVisitingResult(R result) {
		if (buildingVisitor != null) {
			resultObject = (R) buildingVisitor.getVisitingResult();
		}

		return resultObject;
	}
}
