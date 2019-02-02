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
import net.sf.jetro.object.deserializer.beans.BaseBean;
import net.sf.jetro.object.deserializer.beans.BeanWithEnums;
import net.sf.jetro.object.deserializer.beans.BeanWithLists;
import net.sf.jetro.object.deserializer.beans.BeforeAndAfter;
import net.sf.jetro.object.deserializer.beans.ChildBean;
import net.sf.jetro.object.deserializer.beans.DateBean;
import net.sf.jetro.object.deserializer.beans.ElementType;
import net.sf.jetro.object.deserializer.beans.LeafBean;
import net.sf.jetro.object.deserializer.beans.RootBean;
import net.sf.jetro.object.deserializer.beans.SimpleBean;
import net.sf.jetro.object.deserializer.beans.SubSubBean;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.addons.DateSerializer;
import net.sf.jetro.object.serializer.addons.ToStringSerializer;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public class ObjectMapperTest {
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test
	@DataBinding(propertiesPrefix = "simpleBean")
	public void testSimpleBeanDeserialization(@TestInput(name = "json") String json) {
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
		
		SimpleBean actual2 = roundTrip(actual, SimpleBean.class);
		assertEquals(actual2, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "nestedBeans")
	public void testNestedBeansDeserialization(@TestInput(name = "json") String json) {
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
		
		RootBean actual2 = roundTrip(actual, RootBean.class);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "listOfBeans")
	public void testListOfBeansDeserialization(@TestInput(name = "json") String json) {
		TypeToken<List<LeafBean>> typeToken = new TypeToken<List<LeafBean>>() {};
		List<LeafBean> actual = mapper.fromJson(json, typeToken);
		
		List<LeafBean> expected = new ArrayList<>();
		expected.add(new LeafBean(1));
		expected.add(new LeafBean(2));
		expected.add(new LeafBean(3));
		
		assertEquals(actual, expected);
		
		List<LeafBean> actual2 = roundTrip(actual, typeToken);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "listOfLists")
	public void testListOfListsDeserialization(@TestInput(name = "json") String json) {
		TypeToken<List<List<String>>> typeToken = new TypeToken<List<List<String>>>() {};
		List<List<String>> actual = mapper.fromJson(json, typeToken);
		
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
		
		List<List<String>> actual2 = roundTrip(actual, typeToken);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "beanWithLists")
	public void testBeanWithListsDeserialization(@TestInput(name = "json") String json) {
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
		
		BeanWithLists actual2 = roundTrip(actual, BeanWithLists.class);
		assertEquals(actual2, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "complexSkip")
	public void testComplexSkipDeserialization(@TestInput(name = "json") String json) {
		BeforeAndAfter actual = mapper.fromJson(json, BeforeAndAfter.class);
		
		BeforeAndAfter expected = new BeforeAndAfter();
		expected.setBefore("before");
		expected.setAfter("after");
		
		assertEquals(actual, expected);
		
		//no round trip since that wouldn't cause skipping
	}
	
	@Test
	@DataBinding(propertiesPrefix = "beanWithEnums")
	public void testBeanWithEnumsDeserialization(@TestInput(name = "json") String json) {
		BeanWithEnums actual = mapper.fromJson(json, BeanWithEnums.class);
		
		BeanWithEnums expected = new BeanWithEnums();
		expected.setElementType(ElementType.OBJECT);
		expected.setElementTypes(Arrays.asList(ElementType.ARRAY, ElementType.OBJECT));
		
		assertEquals(actual, expected);
		
		BeanWithEnums actual2 = roundTrip(actual, BeanWithEnums.class);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "dateBean")
	public void testDateBeanDeserialization(@TestInput(name = "json") String json) {
		DeserializationContext deserializationContext = DeserializationContext.getDefault();
		deserializationContext.addStringDeserializer(TypeToken.of(LocalDateTime.class),
				value -> LocalDateTime.parse(value));
		
		DateBean actual = mapper.fromJson(json, DateBean.class, deserializationContext);
		
		DateBean expected = new DateBean();
		expected.setDateTime(LocalDateTime.parse("2019-01-31T20:12:30"));
		expected.setDate(new Date(123456789L));
		
		assertEquals(actual, expected);
		
		SerializationContext serializationContext = new SerializationContext();
		serializationContext.addTypeSerializer(new DateSerializer())
				.addTypeSerializer(new ToStringSerializer(LocalDateTime.class));
		
		DateBean actual2 = roundTrip(actual, DateBean.class, serializationContext,
				deserializationContext);
		assertEquals(actual2, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "inheritedProperties")
	public void testInheritedPropertiesDeserialization(@TestInput(name = "json") String json) {
		SubSubBean actual = mapper.fromJson(json, SubSubBean.class);
		
		SubSubBean expected = new SubSubBean();
		expected.setBaseString("baseString");
		expected.setSubString("subString");
		expected.setSubSubString("subSubString");
		
		assertEquals(actual, expected);
		
		SubSubBean actual2 = roundTrip(actual, SubSubBean.class);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapStringToBean")
	public void testMapStringToBeanDeserialization(@TestInput(name = "json") String json) {
		TypeToken<Map<String, BaseBean>> typeToken = new TypeToken<Map<String, BaseBean>>() {};
		
		Map<String, BaseBean> actual = mapper.fromJson(json, typeToken);
		
		Map<String, BaseBean> expected = new HashMap<>();
		expected.put("first",  new BaseBean("first"));
		expected.put("second", new BaseBean("second"));
		expected.put("third",  new BaseBean("third"));
		
		assertEquals(actual, expected);
		
		Map<String, BaseBean> actual2 = roundTrip(actual, typeToken);
		assertEquals(actual2, expected);
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

		TypeToken<EnumMap<ElementType, Map<LocalDateTime, BaseBean>>> typeToken =
				new TypeToken<EnumMap<ElementType, Map<LocalDateTime, BaseBean>>>() {};
		
		EnumMap<ElementType, Map<LocalDateTime, BaseBean>> actual =
				mapper.fromJson(json, typeToken, context);
				
		Map<LocalDateTime, BaseBean> first = new HashMap<>();
		first.put(LocalDateTime.parse("2019-02-02T00:49:30"), new BaseBean("array"));
		
		Map<LocalDateTime, BaseBean> second = new HashMap<>();
		second.put(LocalDateTime.parse("2019-02-02T00:50:59"), new BaseBean("object"));
		
		EnumMap<ElementType, Map<LocalDateTime, BaseBean>> expected = new EnumMap<>(ElementType.class);
		expected.put(ElementType.ARRAY, first);
		expected.put(ElementType.OBJECT, second);
		
		assertEquals(actual, expected);
		
		EnumMap<ElementType, Map<LocalDateTime, BaseBean>> actual2 = roundTrip(actual,
				typeToken, context);
		assertEquals(actual2, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "listOfMapsStringToBean")
	public void testListOfMapsStringToBeanDeserialization(@TestInput(name = "json") String json) {
		TypeToken<List<Map<String, BaseBean>>> typeToken =
				new TypeToken<List<Map<String, BaseBean>>>() {};
		
		List<Map<String, BaseBean>> actual = mapper.fromJson(json, typeToken);
		
		Map<String, BaseBean> first = new HashMap<>();
		first.put("first",  new BaseBean("first"));
		first.put("second", new BaseBean("second"));
		first.put("third",  new BaseBean("third"));
		
		Map<String, BaseBean> second = new HashMap<>();
		second.put("fourth", new BaseBean("fourth"));
		second.put("fifth",  new BaseBean("fifth"));
		second.put("sixth",  new BaseBean("sixth"));
		
		List<Map<String, BaseBean>> expected = new ArrayList<>();
		expected.add(first);
		expected.add(second);
		
		assertEquals(actual, expected);
		
		List<Map<String, BaseBean>> actual2 = roundTrip(actual, typeToken);
		assertEquals(actual2, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapStringToListOfEnum")
	public void testMapStringToListOfEnumDeserialization(@TestInput(name = "json") String json) {
		TypeToken<Map<String, List<ElementType>>> typeToken =
				new TypeToken<Map<String, List<ElementType>>>() {};
		
		Map<String, List<ElementType>> actual = mapper.fromJson(json, typeToken);
		
		List<ElementType> first = new ArrayList<>();
		first.add(ElementType.ARRAY);
		first.add(ElementType.OBJECT);
		first.add(ElementType.PRIMITIVE);
		
		List<ElementType> second = new ArrayList<>();
		second.add(ElementType.PRIMITIVE);
		second.add(ElementType.ARRAY);
		second.add(ElementType.OBJECT);
		
		Map<String, List<ElementType>> expected = new HashMap<>();
		expected.put("first", first);
		expected.put("second", second);
		
		assertEquals(actual, expected);
		
		Map<String, List<ElementType>> actual2 = roundTrip(actual, typeToken);
		assertEquals(actual2, expected);
	}	
	
	private <T> T roundTrip(T from, Class<T> clazz) {
		return roundTrip(from, TypeToken.of(clazz));
	}
	
	private <T> T roundTrip(T from, Class<T> clazz, SerializationContext serializationContext,
			DeserializationContext deserializationContext) {
		TypeToken<T> typeToken = TypeToken.of(clazz); 
		
		ObjectVisitingReader reader = new ObjectVisitingReader(from, serializationContext);
		ObjectBuildingVisitor<T> visitor =
				new ObjectBuildingVisitor<>(typeToken, deserializationContext);
		reader.accept(visitor);
		
		return visitor.getVisitingResult();
	}
	
	private <T> T roundTrip(T from, TypeToken<T> typeToken) {
		return roundTrip(from, typeToken, DeserializationContext.getDefault());
	}
	
	private <T> T roundTrip(T from, TypeToken<T> typeToken, DeserializationContext context) {
		ObjectVisitingReader reader = new ObjectVisitingReader(from, new SerializationContext());
		ObjectBuildingVisitor<T> visitor = new ObjectBuildingVisitor<>(typeToken, context);
		reader.accept(visitor);
		return visitor.getVisitingResult();
	}
}
