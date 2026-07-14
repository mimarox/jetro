package net.sf.jetro.object.deserializer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.testng.databinding.text.TextDataSourceConfiguration;

public class ObjectMapperTestDataConfigurations {

	public static TextDataSourceConfiguration beanWithEnumsConfig() {
		return getConfig("beanWithEnums");
	}
	
	public static TextDataSourceConfiguration beanWithListsConfig() {
		return getConfig("beanWithLists");
	}
	
	public static TextDataSourceConfiguration complexSkippedPropertyConfig() {
		return getConfig("complexSkippedProperty");
	}
	
	public static TextDataSourceConfiguration dateBeanConfig() {
		return getConfig("dateBean");
	}
	
	public static TextDataSourceConfiguration inheritedPropertiesConfig() {
		return getConfig("inheritedProperties");
	}
	
	public static TextDataSourceConfiguration listOfBeansConfig() {
		return getConfig("listOfBeans");
	}
	
	public static TextDataSourceConfiguration listOfListsConfig() {
		return getConfig("listOfLists");
	}
	
	public static TextDataSourceConfiguration listOfMapsStringToBeanConfig() {
		return getConfig("listOfMapsStringToBean");
	}
	
	public static TextDataSourceConfiguration mapEnumToMapDateToBeanConfig() {
		return getConfig("mapEnumToMapDateToBean");
	}
	
	public static TextDataSourceConfiguration mapStringToBeanConfig() {
		return getConfig("mapStringToBean");
	}
	
	public static TextDataSourceConfiguration mapStringToListOfEnumConfig() {
		return getConfig("mapStringToListOfEnum");
	}
	
	public static TextDataSourceConfiguration nestedBeansConfig() {
		return getConfig("nestedBeans");
	}
	
	public static TextDataSourceConfiguration simpleBeanConfig() {
		return getConfig("simpleBean");
	}
	
	private static TextDataSourceConfiguration getConfig(String name) {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("json", getURL(name));
				
				return urls;
			}
		};
	}
	
	private static URL getURL(final String fileName) {
		try {
			return new URI("file:///" + getCurrentBasePath() + "/target/test-classes"
					+ "/data/deserialization/" + "/" + fileName + ".json").toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getCurrentBasePath() {
		return System.getProperty("user.dir");
	}
}
