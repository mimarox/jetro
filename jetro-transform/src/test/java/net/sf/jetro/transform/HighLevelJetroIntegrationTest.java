package net.sf.jetro.transform;

import static org.testng.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.transform.beans.Persons;
import net.sf.jetro.transform.beans.SourceObject;
import net.sf.jetro.transform.beans.WrappingAndAddingSource;
import net.sf.jetro.transform.beans.WrappingAndAddingTarget;
import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.builder.JsonTreeBuilder;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import net.sf.testng.databinding.TestOutput;

public class HighLevelJetroIntegrationTest {
	private static final JsonTreeBuilder BUILDER = new JsonTreeBuilder(true);

	@Test
	public void shouldTransformJsonObjectToJsonObject() {
		JsonObject source = new JsonObject();
		JsonElement actual = Jetro.transform(source).applying(new TransformationSpecification() {

			@Override
			protected void specify() {
				at("$").addJsonProperty("a", new String[] { "b" });
				at("$.a[0]").addJsonValue("c");
				at("$.a[-]").addJsonValue(2);
				at("$.a[1]").addJsonValue(1);
				at("$.a[1]").addAllJsonValues("d", "e", "f", "g");
				at("$.a[-]").addAllJsonValues(3, 4, 5, 6);
			}
		}).andReturnAsJsonElement();

		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", new JsonArray(Arrays.asList(new JsonString("b"), new JsonString("c"),
				new JsonString("d"), new JsonString("e"), new JsonString("f"), new JsonString("g"), new JsonNumber(1),
				new JsonNumber(2), new JsonNumber(3), new JsonNumber(4), new JsonNumber(5), new JsonNumber(6)))));

		assertEquals(actual, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "captureAndEdit")
	public void shouldTransformWithCaptureAndEdit(@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(new TransformationSpecification() {

			@Override
			protected void specify() {
				setRenderNullValues(true);

				capture("$.primary_user").edit(primaryUser -> {
					if (primaryUser instanceof JsonObject) {
						((JsonObject) primaryUser).add(new JsonProperty("role", "primary"));
					}
					return primaryUser;
				}).andSaveAs("primaryUser");

				remove("$.primary_user");

				capture("$.secondary_users").editEach(secondaryUser -> {
					if (secondaryUser instanceof JsonObject) {
						((JsonObject) secondaryUser).add(new JsonProperty("role", "secondary"));
					}
					return secondaryUser;
				}).andSaveAs("secondaryUsers");

				remove("$.secondary_users");

				at("$").addJsonProperty("users", new JsonArray());
				at("$.users[-]").addFromVariable("primaryUser");
				at("$.users[-]").addAllFromVariable("secondaryUsers");

				capture("$.users[0].role").andSaveAs("role");
				at("$.users[0]").addJsonPropertyFromVariable("secondRole", "role");
			}
		}).andReturnAsJson();

		assertEquals(actual, normalize(target));
	}

	@Test
	@DataBinding(propertiesPrefix = "renaming")
	public void shouldTransformRenamingProperties(@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(new TransformationSpecification() {

			@Override
			protected void specify() {
				renameProperties("role").to("roles");
				renamePropertiesMatching("([Dd]esc)+").to("text");
				renamePropertiesIgnoringCase("TEXT").to("description");
				at("$.name").renamePropertyTo("names");
			}
		}).andReturnAsJson();

		assertEquals(actual, normalize(target));
	}

	@Test
	@DataBinding(propertiesPrefix = "replacing")
	public void shouldTransformReplacingValues(@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(new TransformationSpecification() {

			@Override
			protected void specify() {
				at("$.object").replaceWith(new JsonArray());

				capture("$.array").editAndReplace(array -> {
					JsonArray arrays = new JsonArray();

					arrays.add(array.deepCopy());
					arrays.add(array);

					return arrays;
				});

				at("$.array").renamePropertyTo("arrays");
				at("$.object").renamePropertyTo("array");
			}
		}).andReturnAsJson();

		assertEquals(actual, normalize(target));
	}

	@Test
	@DataBinding(propertiesPrefix = "wrappingAndAdding")
	public void shouldTransformObjectsWrappingAndAdding(@TestInput WrappingAndAddingSource source,
			@TestInput List<Persons> persons, @TestOutput WrappingAndAddingTarget target) {
		WrappingAndAddingTarget actual = Jetro.transform(source).applying(new TransformationSpecification() {

			@Override
			protected void specify() {
				capture("$.person").editAndReplace(person -> {
					JsonArray persons = new JsonArray();
					persons.add(person);
					return persons;
				});

				at("$.person[*]").addAllJsonValues(persons);

				at("$.person").renamePropertyTo("persons");
			}
		}).andReturnAsObject(WrappingAndAddingTarget.class, DeserializationContext.getDefault());

		assertEquals(actual, target);
	}

	@Test
	public void shouldTransformJsonObjectToJsonArray() {
		String actual = Jetro.transform("{}").applying(
				new TransformationSpecification() {

					@Override
					protected void specify() {
						capture("$").editAndReplace(root -> new JsonArray());
					}
		}).andReturnAsJson(new RenderContext());

		assertEquals(actual, "[]");
	}

	@Test
	public void shouldTransformAddingBoolean() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
			
					@Override
					protected void specify() {
						at("$[-]").addJsonValue(true);
					}
		}).andReturnAsJson();
		
		assertEquals(actual, "[true]");
	}

	@Test
	public void shouldTransformAddingNumber() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
			
					@Override
					protected void specify() {
						at("$[-]").addJsonValue(Long.MAX_VALUE);
					}
		}).andReturnAsJson();
		
		assertEquals(actual, "[" + Long.MAX_VALUE + "]");
	}
	
	@Test
	public void shouldTransformAddingString() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
			
					@Override
					protected void specify() {
						at("$[-]").addJsonValue("value");
					}
		}).andReturnAsJson();
		
		assertEquals(actual, "[\"value\"]");
	}
	
	@Test
	public void shouldTransformAddingListOfPrimitives() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
			
					@Override
					protected void specify() {
						at("$[-]").addJsonValue(Arrays.asList("value", 1, false));
					}
		}).andReturnAsJson();
		
		assertEquals(actual, "[[\"value\",1,false]]");
	}
	
	@Test
	public void shouldTransformAddingLocalDateTime() {
		LocalDateTime dateTime = LocalDateTime.of(2020, 4, 7, 12, 11);
		
		SerializationContext context = new SerializationContext();
		context.addTypeSerializer(new TypeSerializer<LocalDateTime>() {

			@Override
			public boolean canSerialize(Object toSerialize) {
				return toSerialize != null && toSerialize instanceof LocalDateTime;
			}

			@Override
			public void serialize(LocalDateTime toSerialize, JsonVisitor<?> recipient) {
				recipient.visitValue(toSerialize.get(ChronoField.YEAR));
			}
		});
				
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
			
					@Override
					protected void specify() {
						at("$[-]").addJsonValue(dateTime, context);
					}
		}).andReturnAsJson();
		
		assertEquals(actual, "[" + dateTime.get(ChronoField.YEAR) + "]");
	}
	
	@Test
	public void shouldTransformAddingAllJsonTypesVarargs() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$[-]").addAllJsonTypes(new JsonString("value"),
								new JsonNumber(1), new JsonBoolean(false));
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[\"value\",1,false]");
	}
	
	@Test
	public void shouldTransformAddingAllJsonTypesIterable() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$[-]").addAllJsonTypes(new JsonArray(Arrays.asList(
								new JsonString("value"), new JsonNumber(1),
								new JsonBoolean(false))));
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[\"value\",1,false]");		
	}
	
	@Test
	public void shouldTransformAddingJsonPropertiesWithMap() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						Map<String, Persons> properties = new HashMap<>();
						
						properties.put("personA", new Persons("Martha Best", 21));
						properties.put("personB", new Persons("Evan Longer", 45));
						
						at("$").addAllJsonProperties(properties);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = getPersonsAAndB();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingJsonPropertiesWithJsonObject() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$").addAllJsonProperties(getPersonsAAndB());
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = getPersonsAAndB();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingJsonPropertiesFromVariable() {
		JsonObject actual = (JsonObject) Jetro.transform(getPersonsAAndB())
				.applying(new TransformationSpecification() {
					
					@Override
					protected void specify() {
						capture("$.personA").edit(personA -> {
							JsonObject person = new JsonObject();
							person.add(new JsonProperty("personA", personA));
							return person;
						}).andSaveAs("person");
						
						remove("$.personA");

						at("$").addAllJsonPropertiesFromVariable("person");
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = getPersonsAAndB();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformCapturingRoot() {
		String actual = Jetro.transform(getPersonsAAndB()).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						capture("$").edit(value -> {
							assertEquals(value, getPersonsAAndB());
							return value;
						}).andSaveAs("persons");
					}
				})
				.andReturnAsJson();
		
		String expected = getPersonsAAndB().toJson();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformCopyingJsonValue() {
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", true));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						final String varName = "boolean";
						capture("$.a").andSaveAs(varName);
						at("$").addJsonPropertyFromVariable("b", varName);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = source.deepCopy();
		expected.add(new JsonProperty("b", true));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingBooleanJsonProperty() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$").addJsonProperty("a", false);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", false));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingNumberJsonProperty() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$").addJsonProperty("a", 1);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", 1));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingStringJsonProperty() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$").addJsonProperty("a", "value");
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", "value"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingWithBoolean() {
		JsonPath aPath = JsonPath.compile("$.a");
		
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", false));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at(aPath).replaceWith(true);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(aPath, true);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingWithNumber() {
		JsonPath aPath = JsonPath.compile("$.a");
		
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", false));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at(aPath).replaceWith(1);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(aPath, 1);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingWithString() {
		JsonPath aPath = JsonPath.compile("$.a");
		
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", false));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at(aPath).replaceWith("value");
						at("$.b").replaceWith("non-existent");
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(aPath, "value");
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingWithListOfStrings() {
		JsonPath aPath = JsonPath.compile("$.a");
		
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", false));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at(aPath).replaceWith(Arrays.asList("value-a", "value-b"));
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(aPath, new JsonArray(Arrays.asList(
				new JsonString("value-a"), new JsonString("value-b"))));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldTransformAddingNullValues() {
		JsonArray actual = (JsonArray) Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						
						at("$[-]").addJsonValue((Boolean) null);
						at("$[-]").addJsonValue((Number) null);
						at("$[-]").addJsonValue((String) null);
						at("$[-]").addJsonValue((JsonType) null);
						at("$[-]").addJsonValue((Object) null);
					}
				}).andReturnAsJsonElement();
		
		JsonArray expected = new JsonArray(Arrays.asList(new JsonNull(), new JsonNull(),
				new JsonNull(), new JsonNull(), new JsonNull()));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldNotTransformNotAddingNullValues() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						
						at("$[*]").addJsonValue((Boolean) null);
						at("$[*]").addJsonValue((Number) null);
						at("$[*]").addJsonValue((String) null);
						at("$[*]").addJsonValue((JsonType) null);
						at("$[*]").addJsonValue((Object) null);
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[]");
	}
	
	@Test(groups = "negativeTests")
	public void shouldTransformAddingAllNullValues() {
		JsonArray actual = (JsonArray) Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						
						at("$[-]").addAllJsonValues((Object) null, null, null);
						at("$[-]").addAllJsonTypes((JsonType) null, null);
					}
				}).andReturnAsJsonElement();
		
		JsonArray expected = new JsonArray(Arrays.asList(new JsonNull(), new JsonNull(),
				new JsonNull(), new JsonNull(), new JsonNull()));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldNotTransformNotAddingAllNullValues() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						
						at("$[*]").addAllJsonValues((Object) null, null, null);
						at("$[*]").addAllJsonTypes((JsonType) null, null);
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[]");
	}
	
	@Test(groups = "negativeTests")
	public void shouldTransformAddingJsonPropertyNullValue() {
		JsonObject actual = (JsonObject) Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						
						at("$").addJsonProperty("boolean", (Boolean) null);
						at("$").addJsonProperty("number", (Number) null);
						at("$").addJsonProperty("string", (String) null);
						at("$").addJsonProperty("jsonType", (JsonType) null);
						at("$").addJsonProperty("object", (Object) null);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("boolean", new JsonNull()));
		expected.add(new JsonProperty("number", new JsonNull()));
		expected.add(new JsonProperty("string", new JsonNull()));
		expected.add(new JsonProperty("jsonType", new JsonNull()));
		expected.add(new JsonProperty("object", new JsonNull()));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldNotTransformNotRenderingJsonPropertyNullValue() {
		String actual = Jetro.transform("{}").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						
						at("$").addJsonProperty("boolean", (Boolean) null);
						at("$").addJsonProperty("number", (Number) null);
						at("$").addJsonProperty("string", (String) null);
						at("$").addJsonProperty("jsonType", (JsonType) null);
						at("$").addJsonProperty("object", (Object) null);
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "{}");
	}

	@Test(groups = "negativeTests")
	public void shouldTransformAddingAllJsonTypesNullValues() {
		JsonArray actual = (JsonArray) Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						at("$[-]").addAllJsonTypes(null, null);
					}
				}).andReturnAsJsonElement();
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNull(), new JsonNull()));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldNotTransformNotRenderingAddingAllJsonTypesNullValues() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						at("$[*]").addAllJsonTypes(null, null);
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[]");
	}
	
	@Test(groups = "negativeTests")
	public void shouldTransformAddingAllJsonValuesNullValues() {
		JsonArray actual = (JsonArray) Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						at("$[-]").addAllJsonValues((Object) null, null, null);
					}
				}).andReturnAsJsonElement();
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNull(), new JsonNull(), new JsonNull()));
		
		assertEquals(actual, expected);
	}
	
	@Test(groups = "negativeTests")
	public void shouldNotTransformNotRenderingAddingAllJsonValuesNullValues() {
		String actual = Jetro.transform("[]").applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						at("$[*]").addAllJsonValues((Object) null, null);
					}
				}).andReturnAsJson();
		
		assertEquals(actual, "[]");
	}
	
	@Test(groups = "negativeTests")
	@DataBinding(propertiesPrefix = "replacingWithNullValues")
	public void shouldTransformReplacingWithNullValuesRenderingTrue(
			@TestInput(name = "source") String source,
			@TestOutput(name = "target") String target) {
		JsonElement actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(true);
						
						at("$.boolean").replaceWith((Boolean) null);
						at("$.number").replaceWith((Number) null);
						at("$.string").replaceWith((String) null);
						at("$.jsonType").replaceWith((JsonType) null);
						at("$.object").replaceWith((Object) null);
						at("$.object2").replaceWith(null, new SerializationContext());
					}
				}).andReturnAsJsonElement();
		
		assertEquals(actual, BUILDER.build(target));
	}
	
	@Test(groups = "negativeTests")
	@DataBinding(propertiesPrefix = "replacingWithNullValues")
	public void shouldTransformReplacingWithNullValuesRenderingFalse(
			@TestInput(name = "source") String source,
			@TestOutput(name = "target") String target) {
		JsonElement actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						setRenderNullValues(false);
						
						at("$.boolean").replaceWith((Boolean) null);
						at("$.number").replaceWith((Number) null);
						at("$.string").replaceWith((String) null);
						at("$.jsonType").replaceWith((JsonType) null);
						at("$.object").replaceWith((Object) null);
						at("$.object2").replaceWith(null, new SerializationContext());
					}
				}).andReturnAsJsonElement();
		
		assertEquals(actual, BUILDER.build(target));
	}
	
	@Test
	public void shouldTransformKeepingJsonValue() {
		JsonArray source = new JsonArray(Arrays.asList(
				new JsonNumber(1), new JsonNumber(2), new JsonNumber(3)));
		
		JsonArray actual = (JsonArray) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						keep("$[1]");
					}
				}).andReturnAsJsonElement();
		
		JsonArray expected = new JsonArray(Arrays.asList(new JsonNumber(2)));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "keepingJsonProperty")
	public void shouldTransformKeepingJsonProperty(
			@TestInput(name = "source") String source,
			@TestOutput(name = "target") String target) {
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						keep("$.foo0.bar1.baz0:");
					}
				}).andReturnAsJson();
		
		assertEquals(actual, normalize(target));
	}
	
	@Test
	public void shouldTransformApplyingCustomVisitor() {
		JsonObject source = new JsonObject();
		source.add(new JsonProperty("a", false));
		
		JsonObject actual = (JsonObject) Jetro.transform(source).applying(
				new TransformationSpecification() {
			
			@Override
			protected void specify() {
				renameProperties("a").to("b");
				
				applyCustomVisitor(new UniformChainedJsonVisitor<Void>() {
					
					@Override
					protected String beforeVisitProperty(final String name) {
						if ("b".equals(name)) {
							return "c";
						} else {
							return name;
						}
					}
					
					@Override
					protected Boolean beforeVisitValue(final boolean value) {
						visitValue("" + value);
						return null;
					}
				});
				
				at("$.c").replaceWith(true);
			}
		}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("c", true));
		
		assertEquals(actual, expected);
	}

	@Test
	@DataBinding(propertiesPrefix = "captureAndEdit")
	public void shouldTransformApplyingSpecifications(
			@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {

			@Override
			protected void specify() {
				setRenderNullValues(true);

				applySpecification(new TransformationSpecification() {
					
					@Override
					protected void specify() {
						capture("$.primary_user").edit(primaryUser -> {
							if (primaryUser instanceof JsonObject) {
								((JsonObject) primaryUser).add(new JsonProperty("role", "primary"));
							}
							return primaryUser;
						}).andSaveAs("primaryUser");

						remove("$.primary_user");
					}
				});

				applySpecification(new TransformationSpecification() {
					
					@Override
					protected void specify() {
						capture("$.secondary_users").editEach(secondaryUser -> {
							if (secondaryUser instanceof JsonObject) {
								((JsonObject) secondaryUser).add(new JsonProperty("role", "secondary"));
							}
							return secondaryUser;
						}).andSaveAs("secondaryUsers");

						remove("$.secondary_users");
					}
				});

				at("$").addJsonProperty("users", new JsonArray());
				at("$.users[-]").addFromVariable("primaryUser");
				at("$.users[-]").addAllFromVariable("secondaryUsers");

				capture("$.users[0].role").andSaveAs("role");
				at("$.users[0]").addJsonPropertyFromVariable("secondRole", "role");
			}
		}).andReturnAsJson();

		assertEquals(actual, normalize(target));
	}
	
	@Test
	@DataBinding(propertiesPrefix = "replacingIfWithObject")
	public void shouldTransformReplacingIfWithObject(
			@TestInput(name = "source") String source,
			@TestOutput(name = "target") String target) {
		SourceObject replacement = new SourceObject();
		
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$.*").replaceIf(this::determineReplacement).with(replacement);
						at("$.objectToBeReplaced").renamePropertyTo("replacedObject");
					}
					
					private boolean determineReplacement(final JsonType value) {
						if (value instanceof JsonObject) {
							return ((JsonObject) value).getElementAt(
									JsonPath.compile("$.present")).isPresent();
						} else {
							return false;
						}
					}
				}).andReturnAsJson();
		
		assertEquals(actual, normalize(target));
	}
	
	@Test
	@DataBinding(propertiesPrefix = "replacingIfWithObject")
	public void shouldTransformReplacingIfWithJsonObject(
			@TestInput(name = "source") String source,
			@TestOutput(name = "target") String target) {
		JsonObject replacement = new JsonObject();
		replacement.add(new JsonProperty("foo", "bar"));
		
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$.*").replaceIf(this::determineReplacement).with(replacement);
						at("$.objectToBeReplaced").renamePropertyTo("replacedObject");
					}
					
					private boolean determineReplacement(final JsonType value) {
						if (value instanceof JsonObject) {
							return ((JsonObject) value).getElementAt(
									JsonPath.compile("$.present")).isPresent();
						} else {
							return false;
						}
					}
				}).andReturnAsJson();
		
		assertEquals(actual, normalize(target));
	}
	
	@Test
	public void shouldTransformReplacingIfWithBoolean() {
		JsonArray source = new JsonArray(Arrays.asList(
				new JsonBoolean(true), new JsonBoolean(false)));
		
		JsonArray actual = (JsonArray) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
			@Override
			protected void specify() {
				at("$[*]").replaceIf(value -> {
					return value instanceof JsonBoolean &&
							((JsonBoolean) value).getValue();
				}).with(false);
			}
		}).andReturnAsJsonElement();
		
		JsonArray expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(JsonPath.compile("$[0]"), false);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingIfWithNumber() {
		JsonArray source = new JsonArray(Arrays.asList(
				new JsonBoolean(true), new JsonBoolean(false)));
		
		JsonArray actual = (JsonArray) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
			@Override
			protected void specify() {
				at("$[*]").replaceIf(value -> {
					return value instanceof JsonBoolean &&
							((JsonBoolean) value).getValue();
				}).with(1);
			}
		}).andReturnAsJsonElement();
		
		JsonArray expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(JsonPath.compile("$[0]"), 1);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformReplacingIfWithString() {
		JsonArray source = new JsonArray(Arrays.asList(
				new JsonBoolean(true), new JsonBoolean(false)));
		
		JsonArray actual = (JsonArray) Jetro.transform(source).applying(
				new TransformationSpecification() {
					
			@Override
			protected void specify() {
				at("$[*]").replaceIf(value -> {
					return value instanceof JsonBoolean &&
							((JsonBoolean) value).getValue();
				}).with("gotcha");
			}
		}).andReturnAsJsonElement();
		
		JsonArray expected = source.deepCopy();
		expected.recalculateTreePaths();
		expected.replaceElementAt(JsonPath.compile("$[0]"), "gotcha");
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformAddingToEachArrayIndex() {
		String source = "[1,2,3]";
		
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
			
			@Override
			protected void specify() {
				at("$[*]").addJsonValue("a");
			}
		}).andReturnAsJson();
		
		String expected = "[1,\"a\",2,\"a\",3,\"a\"]";
		
		assertEquals(actual, expected);
	}
	
	private static String normalize(final String json) {
		return BUILDER.build(json).toJson();
	}
	
	private static JsonObject getPersonsAAndB() {
		JsonObject personA = new JsonObject();
		personA.add(new JsonProperty("name", "Martha Best"));
		personA.add(new JsonProperty("age", 21));
		
		JsonObject personB = new JsonObject();
		personB.add(new JsonProperty("name", "Evan Longer"));
		personB.add(new JsonProperty("age", 45));
		
		JsonObject persons = new JsonObject();
		persons.add(new JsonProperty("personA", personA));
		persons.add(new JsonProperty("personB", personB));
		
		return persons;
	}
}
