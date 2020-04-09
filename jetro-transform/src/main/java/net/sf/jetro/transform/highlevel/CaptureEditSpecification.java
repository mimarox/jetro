package net.sf.jetro.transform.highlevel;

import java.util.Objects;
import java.util.function.Function;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonPrimitive;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.chained.MultiplexingJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

public class CaptureEditSpecification<S extends JsonType, T extends JsonType> {
	private final JsonPath path;
	private final Function<S, T> editor;
	private final TransformationSpecification specification;
	
	CaptureEditSpecification(final JsonPath path,
			final Function<S, T> editor, final TransformationSpecification specification) {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(editor, "editor must not be null");
		Objects.requireNonNull(specification, "specification must not be null");
		
		this.path = path;
		this.editor = editor;
		this.specification = specification;
	}

	public void andSaveAs(final String variableName) {
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				private JsonTreeBuildingVisitor treeBuilder = new JsonTreeBuildingVisitor();
				
				@Override
				protected boolean doBeforeVisitObject() {
					return passOn();
				}
				
				@Override
				protected boolean doBeforeVisitArray() {
					return passOn();
				}
				
				private boolean passOn() {
					if (currentPath().matches(path)) {
						return false;
					} else {
						return true;
					}					
				}
				
				@Override
				protected JsonObjectVisitor<Void> afterVisitObject(
						final JsonObjectVisitor<Void> visitor) {
					JsonObjectVisitor<Void> actualVisitor = visitor;
					
					if (currentPath().matches(path)) {
						actualVisitor = getMultiplexingJsonVisitor(
								multiVisitor -> multiVisitor.visitObject());
					}
					
					return super.afterVisitObject(actualVisitor);
				}
				
				@Override
				protected JsonArrayVisitor<Void> afterVisitArray(
						final JsonArrayVisitor<Void> visitor) {
					JsonArrayVisitor<Void> actualVisitor = visitor;
					
					if (currentPath().matches(path)) {
						actualVisitor = getMultiplexingJsonVisitor(
								multiVisitor -> multiVisitor.visitArray());
					}
					
					return super.afterVisitArray(actualVisitor);
				}
				
				private <T> T getMultiplexingJsonVisitor(final Function<
						MultiplexingJsonVisitor<Void>, T> actualVisitorProvider) {
					MultiplexingJsonVisitor<Void> multiVisitor =
							new MultiplexingJsonVisitor<Void>(getNextVisitor(), treeBuilder);
					return actualVisitorProvider.apply(multiVisitor);
				}
				
				@Override
				protected void afterVisitObjectEnd() {
					handleAfterVisitEnd();
				}
				
				@Override
				protected void afterVisitArrayEnd() {
					handleAfterVisitEnd();
				}
				
				@SuppressWarnings("unchecked")
				private void handleAfterVisitEnd() {
					if (currentPath().matches(path)) {
						JsonType value = (JsonType) treeBuilder.getVisitingResult();
						specification.putVariable(variableName, editor.apply((S) value));
					}
				}
				
				@Override
				protected void afterVisitValue(final Boolean value) {
					handleAfterVisitValue(new JsonBoolean(value));
				}
				
				@Override
				protected void afterVisitValue(final Number value) {
					handleAfterVisitValue(new JsonNumber(value));
				}
				
				@Override
				protected void afterVisitValue(final String value) {
					handleAfterVisitValue(new JsonString(value));
				}

				@SuppressWarnings("unchecked")
				private void handleAfterVisitValue(JsonPrimitive<?> value) {
					if (currentPath().matches(path)) {
						specification.putVariable(variableName, editor.apply((S) value));						
					}
				}
			};
		});
	}
}
