package net.sf.jetro.object.deserializer;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.testng.annotations.Test;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonString;

public class DeserializationTest {

	@Test
	public void deserializeJsonArrayToSet() {
		DeserializationContext context = DeserializationContext.getDefault();
		context.addInstanceCreator(TypeToken.of(Set.class), typeToken -> new HashSet<>());
		
		JsonArray jsonArray = new JsonArray(Arrays.asList(
				new JsonString("abc"), new JsonString("def")));
		
		ObjectBuildingVisitor<Set<String>> visitor = new ObjectBuildingVisitor<>(
				new TypeToken<Set<String>>() {}, context);
		jsonArray.mergeInto(visitor);
		
		Set<String> set = visitor.getVisitingResult();
		
		assertEquals(set.size(), 2);
		assertEquals(set, new HashSet<String>(Arrays.asList("abc", "def")));
	}
	
	@Test
	public void deserializeJsonArrayToPriorityQueue() {
		DeserializationContext context = DeserializationContext.getDefault();
		context.addInstanceCreator(TypeToken.of(PriorityQueue.class), typeToken -> new PriorityQueue<>());
		
		JsonArray jsonArray = new JsonArray(Arrays.asList(
				new JsonString("abc"), new JsonString("def")));
		
		ObjectBuildingVisitor<PriorityQueue<String>> visitor = new ObjectBuildingVisitor<>(
				new TypeToken<PriorityQueue<String>>() {}, context);
		jsonArray.mergeInto(visitor);
		
		PriorityQueue<String> queue = visitor.getVisitingResult();
		
		assertEquals(queue.size(), 2);
		assertEquals(queue, new PriorityQueue<String>(Arrays.asList("abc", "def")));
	}	
}
