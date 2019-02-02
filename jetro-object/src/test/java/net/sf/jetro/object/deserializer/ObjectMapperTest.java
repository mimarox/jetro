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

import static org.testng.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.deserializer.DeserializationElement.ElementType;
import net.sf.jetro.object.deserializer.beans.BaseBean;
import net.sf.jetro.object.deserializer.beans.BeanWithEnums;
import net.sf.jetro.object.deserializer.beans.BeanWithLists;
import net.sf.jetro.object.deserializer.beans.BeforeAndAfter;
import net.sf.jetro.object.deserializer.beans.ChildBean;
import net.sf.jetro.object.deserializer.beans.DateBean;
import net.sf.jetro.object.deserializer.beans.LeafBean;
import net.sf.jetro.object.deserializer.beans.RootBean;
import net.sf.jetro.object.deserializer.beans.SimpleBean;
import net.sf.jetro.object.deserializer.beans.SubSubBean;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public class ObjectMapperTest {

	@Test
	@DataBinding(propertiesPrefix = "simpleBean")
	public void testSimpleBeanDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleBean actual = mapper.fromJson(json, SimpleBean.class);
		
		SimpleBean expected = new SimpleBean();
		expected.setString("STRING");
		expected.setBytePrimitive((byte) 1);
		expected.setByteObject((byte) 1);
		expected.setShortPrimitive((short) 1);
		expected.setShortObject((short) 1);
		expected.setIntPrimitive(1);
		expected.setIntegerObject(1);
		expected.setLongPrimitive(1L);
		expected.setLongObject(1L);
		expected.setFloatPrimitive(0.1F);
		expected.setFloatObject(0.1F);
		expected.setDoublePrimitive(0.1);
		expected.setDoubleObject(0.1);
		expected.setBooleanPrimitive(true);
		expected.setBooleanObject(true);
		expected.setNullValue(null);
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "nestedBeans")
	public void testNestedBeansDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		RootBean actual = mapper.fromJson(json, RootBean.class);
		
		LeafBean innerLeafBean = new LeafBean();
		innerLeafBean.setNumber(1);
		
		LeafBean outerLeafBean = new LeafBean();
		outerLeafBean.setNumber(2);
		
		ChildBean childBean = new ChildBean();
		childBean.setHappy(true);
		childBean.setLeaf(innerLeafBean);
		
		RootBean expected = new RootBean();
		expected.setString("STRING");
		expected.setChild(childBean);
		expected.setLeaf(outerLeafBean);
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "listOfBeans")
	public void testListOfBeansDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		List<LeafBean> actual = mapper.fromJson(json, new TypeToken<List<LeafBean>>() {});
		
		List<LeafBean> expected = new ArrayList<>();
		expected.add(new LeafBean(1));
		expected.add(new LeafBean(2));
		expected.add(new LeafBean(3));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "listOfLists")
	public void testListOfListsDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		List<List<String>> actual = mapper.fromJson(json, new TypeToken<List<List<String>>>() {});
		
		List<String> first = new ArrayList<>();
		first.add("1");
		first.add("2");
		first.add("3");
		
		List<String> second = new ArrayList<>();
		second.add("4");
		second.add("5");
		second.add("6");
		
		List<List<String>> expected = new ArrayList<>();
		expected.add(first);
		expected.add(second);
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "beanWithLists")
	public void testBeanWithListsDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		BeanWithLists actual = mapper.fromJson(json, BeanWithLists.class);
		
		List<Double> doubles = new ArrayList<>();
		doubles.add(1.0);
		doubles.add(null);
		doubles.add(0.1);
		
		List<Boolean> booleans = new ArrayList<>();
		booleans.add(null);
		booleans.add(false);
		booleans.add(true);
		booleans.add(null);
		booleans.add(true);
		booleans.add(false);
		
		List<LeafBean> leafs = new ArrayList<>();
		leafs.add(new LeafBean(1));
		leafs.add(null);
		leafs.add(new LeafBean(2));
		
		BeanWithLists expected = new BeanWithLists();
		expected.setDoubles(doubles);
		expected.setBooleans(booleans);
		expected.setLeafs(leafs);
		
		assertEquals(actual, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "complexSkip")
	public void testComplexSkipDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		BeforeAndAfter actual = mapper.fromJson(json, BeforeAndAfter.class);
		
		BeforeAndAfter expected = new BeforeAndAfter();
		expected.setBefore("before");
		expected.setAfter("after");
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "beanWithEnums")
	public void testBeanWithEnumsDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		BeanWithEnums actual = mapper.fromJson(json, BeanWithEnums.class);
		
		BeanWithEnums expected = new BeanWithEnums();
		expected.setElementType(ElementType.OBJECT);
		expected.setElementTypes(Arrays.asList(ElementType.ARRAY, ElementType.OBJECT));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "dateBean")
	public void testDateBeanDeserialization(@TestInput(name = "json") String json) {
		DeserializationContext context = DeserializationContext.getDefault();
		context.addStringDeserializer(TypeToken.of(LocalDateTime.class),
				value -> LocalDateTime.parse(value));
		
		ObjectMapper mapper = new ObjectMapper();
		DateBean actual = mapper.fromJson(json, DateBean.class, context);
		
		DateBean expected = new DateBean();
		expected.setDateTime(LocalDateTime.parse("2019-01-31T20:12:30"));
		expected.setDate(new Date(123456789L));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "inheritedProperties")
	public void testInheritedPropertiesDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		SubSubBean actual = mapper.fromJson(json, SubSubBean.class);
		
		SubSubBean expected = new SubSubBean();
		expected.setBaseString("baseString");
		expected.setSubString("subString");
		expected.setSubSubString("subSubString");
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapStringToBean")
	public void testMapStringToBeanDeserialization(@TestInput(name = "json") String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, BaseBean> actual = mapper.fromJson(json,
				new TypeToken<Map<String, BaseBean>>() {});
		
		Map<String, BaseBean> expected = new HashMap<>();
		expected.put("first",  new BaseBean("first"));
		expected.put("second", new BaseBean("second"));
		expected.put("third",  new BaseBean("third"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapEnumToMapDateToBean")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testMapEnumToMapDateToBeanDeserialization(@TestInput(name = "json") String json) {
		DeserializationContext context = DeserializationContext.getDefault();
		context.addInstanceCreator(TypeToken.of(EnumMap.class), typeToken -> {
			return new EnumMap(typeToken.getTypeParameterTypeToken(0).getRawType());
		});
		context.addStringDeserializer(TypeToken.of(LocalDateTime.class),
				value -> LocalDateTime.parse(value));
		
		ObjectMapper mapper = new ObjectMapper();
		EnumMap<ElementType, Map<LocalDateTime, BaseBean>> actual =
				mapper.fromJson(json,
						new TypeToken<EnumMap<ElementType, Map<LocalDateTime, BaseBean>>>() {},
						context);
				
		Map<LocalDateTime, BaseBean> first = new HashMap<>();
		first.put(LocalDateTime.parse("2019-02-02T00:49:30"), new BaseBean("array"));
		
		Map<LocalDateTime, BaseBean> second = new HashMap<>();
		second.put(LocalDateTime.parse("2019-02-02T00:50:59"), new BaseBean("object"));
		
		EnumMap<ElementType, Map<LocalDateTime, BaseBean>> expected = new EnumMap<>(ElementType.class);
		expected.put(ElementType.ARRAY, first);
		expected.put(ElementType.OBJECT, second);
		
		assertEquals(actual, expected);
	}
}
