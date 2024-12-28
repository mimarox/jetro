package net.sf.jetro.transform;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.testng.databinding.text.TextDataSourceConfiguration;
import net.sf.testng.databinding.xml.XMLDataSourceConfiguration;

public class HighLevelJetroIntegrationTestDataBindingConfig {
	public static TextDataSourceConfiguration captureAndEditConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/captureAndEdit/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/captureAndEdit/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration renamingConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/renaming/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/renaming/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration replacingConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/replacing/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/replacing/target.json"));
				
				return urls;
			}
		};
	}
	
	public static XMLDataSourceConfiguration wrappingAndAddingConfig() {
		return new XMLDataSourceConfiguration() {
			
			@Override
			public URL getURL() {
				return toURL("/data/transform/highlevel/wrappingAndAdding/data.xml");
			}
		};
	}
	
	public static TextDataSourceConfiguration replacingWithNullValuesConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source",
						toURL("/data/transform/highlevel/replacingWithNullValues/source.json"));
				urls.put("target",
						toURL("/data/transform/highlevel/replacingWithNullValues/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration keepingJsonPropertyConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/keepingJsonProperty/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/keepingJsonProperty/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration replacingIfWithObjectConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source",
						toURL("/data/transform/highlevel/replacingIfWithObject/source.json"));
				urls.put("target",
						toURL("/data/transform/highlevel/replacingIfWithObject/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration multiCaptureConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/multiCapture/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/multiCapture/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration multiLogAndRemoveConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/multiLogAndRemove/source.json"));
				urls.put("middle", toURL("/data/transform/highlevel/multiLogAndRemove/middle.json"));
				urls.put("target", toURL("/data/transform/highlevel/multiLogAndRemove/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration multiLogAndAddConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/multiLogAndAdd/source.json"));
				urls.put("middle", toURL("/data/transform/highlevel/multiLogAndAdd/middle.json"));
				urls.put("target", toURL("/data/transform/highlevel/multiLogAndAdd/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration multiLogAndReplaceConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/multiLogAndReplace/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/multiLogAndReplace/target.json"));
				
				return urls;
			}
		};
	}
	
	public static TextDataSourceConfiguration shouldHierarchifyConfig() {
		return new TextDataSourceConfiguration() {
			
			@Override
			public Map<String, URL> getURLs() {
				Map<String, URL> urls = new HashMap<>();
				
				urls.put("source", toURL("/data/transform/highlevel/hierarchify/source.json"));
				urls.put("target", toURL("/data/transform/highlevel/hierarchify/target.json"));
				
				return urls;
			}
		};
	}
	
	private static URL toURL(final String name) {
		return HighLevelJetroIntegrationTestDataBindingConfig.class.getResource(name);
	}
}
