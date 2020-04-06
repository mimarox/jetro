package net.sf.jetro.transform;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.transform.beans.Persons;
import net.sf.jetro.transform.beans.WrappingAndAddingSource;
import net.sf.jetro.transform.beans.WrappingAndAddingTarget;
import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.builder.JsonTreeBuilder;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import net.sf.testng.databinding.TestOutput;

public class HighLevelJetroIntegrationTest {
	private static final JsonTreeBuilder BUILDER = new JsonTreeBuilder(true);
	
	@Test
	public void shouldTransformJsonObjectToJsonObject() {
		JsonObject source = new JsonObject();
		JsonElement actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						at("$").addJsonProperty("a", new String[] {"b"});
						at("$.a[0]").addJsonValue("c");
						at("$.a[*]").addJsonValue(2);
						at("$.a[1]").addJsonValue(1);
						at("$.a[1]").addAllJsonValues("d", "e", "f", "g");
						at("$.a[*]").addAllJsonValues(3, 4, 5, 6);
					}
				}).andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", new JsonArray(
				Arrays.asList(new JsonString("b"), new JsonString("c"),
						new JsonString("d"), new JsonString("e"),
						new JsonString("f"), new JsonString("g"),
						new JsonNumber(1), new JsonNumber(2),
						new JsonNumber(3), new JsonNumber(4),
						new JsonNumber(5), new JsonNumber(6)))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "captureAndEdit")
	public void shouldTransformWithCaptureAndEdit(
			@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
				        setRenderNullValues(true);
						
						capture("$.primary_user").edit(primaryUser -> {
				        	if (primaryUser instanceof JsonObject) {
				        		((JsonObject) primaryUser).add(
				        				new JsonProperty("role", "primary"));
				        	}
				        	return primaryUser;
				        }).andSaveAs("primaryUser");

				        remove("$.primary_user");
				        
				        capture("$.secondary_users").editEach(secondaryUser -> {
				        	if (secondaryUser instanceof JsonObject) {
				        		((JsonObject) secondaryUser).add(
				        				new JsonProperty("role", "secondary"));
				        	}
				        	return secondaryUser;
				        }).andSaveAs("secondaryUsers");
				        
				        remove("$.secondary_users");

				        at("$").addJsonProperty("users", new JsonArray());
				        at("$.users[*]").addFromVariable("primaryUser");
				        at("$.users[*]").addAllFromVariable("secondaryUsers");
				        
				        capture("$.users[0].role").andSaveAs("role");
				        at("$.users[0]")
				        	.addJsonPropertyFromVariable("secondRole", "role");
					}
				}).andReturnAsJson();
		
		assertEquals(actual, normalize(target));
	}
	
	@Test
	@DataBinding(propertiesPrefix = "renaming")
	public void shouldTransformRenamingProperties(
			@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
					@Override
					protected void specify() {
						renameProperty("role").to("roles");
						renamePropertiesMatching("([Dd]esc)+").to("text");
						renamePropertyIgnoreCase("TEXT").to("description");
						at("$.name").renamePropertyTo("names");
					}
				}).andReturnAsJson();
		
		assertEquals(actual, normalize(target));
	}

	@Test
	@DataBinding(propertiesPrefix = "replacing")
	public void shouldTransformReplacingValues(
			@TestInput(name = "source") final String source,
			@TestOutput(name = "target") final String target) {
		String actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
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
	public void shouldTransformObjectsWrappingAndAdding(
			@TestInput WrappingAndAddingSource source,
			@TestInput List<Persons> persons,
			@TestOutput WrappingAndAddingTarget target) {
		WrappingAndAddingTarget actual = Jetro.transform(source).applying(
				new TransformationSpecification() {
					
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
				}).andReturnAsObject(WrappingAndAddingTarget.class,
						DeserializationContext.getDefault());
		
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
	
	private static String normalize(final String json) {
		return BUILDER.build(json).toJson();
	}
}
