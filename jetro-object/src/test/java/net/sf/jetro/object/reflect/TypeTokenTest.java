package net.sf.jetro.object.reflect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by matthias.rothe on 26.03.14.
 */
public class TypeTokenTest {

	@Test(expectedExceptions = RuntimeException.class)
	public void testNoSubClassTypeToken() {
		TypeToken<String> token = new TypeToken<String>();
	}

	@Test
	public void testNonGenericTypeToken() {
		TypeToken<String> token = new TypeToken<String>(){};
		assertEquals(String.class, token.getType());
		assertEquals(String.class, token.getRawType());
	}

	@Test
	public void testSimpleGenericTypeToken() {
		TypeToken<List<String>> token = new TypeToken<List<String>>(){};

		Type type = token.getType();
		assertTrue(type instanceof ParameterizedType, "expected ParameterizedType");

		ParameterizedType parameterizedType = (ParameterizedType) type;
		assertEquals(List.class, parameterizedType.getRawType());
		assertEquals(String.class, parameterizedType.getActualTypeArguments()[0]);

		assertEquals(List.class, token.getRawType());
	}

	@Test
	public void testWildcardGenericTypeToken() {
		TypeToken<List<? super Integer>> token = new TypeToken<List<? super Integer>>(){};
	}

	@Test
	public <T> void testTypeVariableTypeToken() {
		TypeToken<List<T>> token = new TypeToken<List<T>>(){};
	}
}
