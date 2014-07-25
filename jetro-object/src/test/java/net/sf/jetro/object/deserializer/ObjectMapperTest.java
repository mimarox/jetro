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
