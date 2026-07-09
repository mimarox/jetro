package net.sf.jetro.transform.dataconfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.testng.databinding.text.TextDataSourceConfiguration;
import net.sf.testng.databinding.xml.XMLDataSourceConfiguration;

public class DataConfigurations {
	public static TextDataSourceConfiguration captureAndEditConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("captureAndEdit"));
				urls.put("target", getTargetURL("captureAndEdit"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration renamingConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("renaming"));
				urls.put("target", getTargetURL("renaming"));
				
				return urls;
			}
		};		
	}
	
	public static TextDataSourceConfiguration replacingConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("replacing"));
				urls.put("target", getTargetURL("replacing"));
				
				return urls;
			}
		};
	}
	
	public static XMLDataSourceConfiguration wrappingAndAddingConfig() {
		return new XMLDataSourceConfiguration() {
			
			@Override
			public URL getURL() {
				try {
					return new URI("file:///" + getCurrentBasePath() + "/target/test-classes" +
							"/data/transform/highlevel/wrappingAndAdding/data.xml").toURL();
				} catch (MalformedURLException | URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public static TextDataSourceConfiguration replacingWithNullValuesConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("replacingWithNullValues"));
				urls.put("target", getTargetURL("replacingWithNullValues"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration keepingJsonPropertyConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("keepingJsonProperty"));
				urls.put("target", getTargetURL("keepingJsonProperty"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration replacingIfWithObjectConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<String, URL>();
				
				urls.put("source", getSourceURL("replacingIfWithObject"));
				urls.put("target", getTargetURL("replacingIfWithObject"));
				
				return urls;
			}
		};
	}
	
	private static URL getSourceURL(final String testSegment) {
		try {
			return new URI("file:///" + getCurrentBasePath() + "/target/test-classes"
					+ "/data/transform/highlevel/" +
					testSegment + "/source.json").toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL getTargetURL(final String testSegment) {
		try {
			return new URI("file:///" + getCurrentBasePath() + "/target/test-classes"
					+ "/data/transform/highlevel/" +
					testSegment + "/target.json").toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getCurrentBasePath() {
		return System.getProperty("user.dir");
	}
}
