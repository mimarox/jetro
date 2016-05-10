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
package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.deserializer.beans.TestBean;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import org.testng.annotations.Test;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public class ObjectMapperTest {

	@Test
	@DataBinding(propertiesPrefix = "deserialization")
	public void testObjectDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.fromJson(json, TestBean.class);
	}
}
