package net.sf.jetro.visitor.chained;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;

public class MultiplexingJsonVisitorTest {

	@Test
	public void shouldMultiplex() {
		JsonTreeBuildingVisitor masterVisitor = new JsonTreeBuildingVisitor();
		JsonTreeBuildingVisitor slaveVisitor1 = new JsonTreeBuildingVisitor();
		JsonTreeBuildingVisitor slaveVisitor2 = new JsonTreeBuildingVisitor();
		
		MultiplexingJsonVisitor<JsonElement> multiplexingVisitor = 
				new MultiplexingJsonVisitor<JsonElement>(masterVisitor,
						slaveVisitor1, slaveVisitor2);
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("key0", "value"));
		expected.add(new JsonProperty("key1", true));
		expected.add(new JsonProperty("key2", 2));
		expected.add(new JsonProperty("key3"));
		expected.add(new JsonProperty("key4", new JsonArray(Arrays.asList(new JsonType[]{
				new JsonString("value"), new JsonNumber(2), new JsonBoolean(true)}))));
		
		JsonElementVisitingReader reader = new JsonElementVisitingReader(expected);
		reader.accept(multiplexingVisitor);
		
		JsonElement actualFromMaster = masterVisitor.getVisitingResult();
		JsonElement actualFromSlave1 = slaveVisitor1.getVisitingResult();
		JsonElement actualFromSlave2 = slaveVisitor2.getVisitingResult();
		
		assertEquals(actualFromMaster, expected);
		assertEquals(actualFromSlave1, expected);
		assertEquals(actualFromSlave2, expected);	}
}
