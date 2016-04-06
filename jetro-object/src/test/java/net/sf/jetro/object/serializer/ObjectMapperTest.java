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

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.serializer.beans.NestedTestBean;
import net.sf.jetro.object.serializer.beans.TestBean;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthias.rothe on 27.02.14.
 */
public class ObjectMapperTest {

	@Test
	public void testBeanSerialization() {
		TestBean bean = buildTestBean();
		JsonReturningVisitor receiver = new JsonReturningVisitor();

		ObjectMapper mapper = new ObjectMapper();
		mapper.merge(bean).into(receiver);

		System.out.println(receiver.getVisitingResult());
	}

	private TestBean buildTestBean() {
		NestedTestBean nested = new NestedTestBean();
		nested.setFoo("value");
		nested.setBar((byte) 3);

		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");

		TestBean bean = new TestBean();
		bean.setArray(new long[] { 1, 2, 3 });
		bean.setBean(nested);
		bean.setCharacter('c');
		bean.setInteger(5);
		bean.setList(Arrays.asList(new String[] { null, "foo", "bar" }));
		bean.setMap(map);
		bean.setString("some string");
		bean.setVisible(true);

		return bean;
	}
}
