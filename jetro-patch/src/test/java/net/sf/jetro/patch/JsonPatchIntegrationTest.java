package net.sf.jetro.patch;

import static net.sf.jetro.patch.JsonPatch.patch;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.sf.jetro.tree.builder.JsonTreeBuilder;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import net.sf.testng.databinding.TestOutput;

@Test(dependsOnGroups = "individualTests")
public class JsonPatchIntegrationTest {
	private static final JsonTreeBuilder BUILDER = new JsonTreeBuilder(true); 
	
	@Test
	@DataBinding
	public void shouldPatch(@TestInput(name = "source") final String source,
							@TestInput(name = "patchOperations") final String patchOperations,
							@TestOutput(name = "target") final String target)
									throws JsonPatchException {
		String actual = patch(source).applying(patchOperations).andReturnAsJson();
		String expected = normalize(target);
		
		assertEquals(actual, expected);
	}

	private String normalize(String target) {
		return BUILDER.build(target).toJson();
	}
}
