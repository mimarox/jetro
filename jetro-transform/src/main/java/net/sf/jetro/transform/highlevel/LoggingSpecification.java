package net.sf.jetro.transform.highlevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.transform.logging.LogLevel;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonPrimitive;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.MultiplexingJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

/**
 * This class is part of the {@link TransformationSpecification} fluent API.
 * <p>
 * It provides logging functionality.
 * 
 * @author Matthias Rothe
 */
public class LoggingSpecification {
	private final JsonPath path;
	private final LogLevel logLevel;
	private final TransformationSpecification specification;
	private final List<String> prefaces = new ArrayList<>();
	
	LoggingSpecification(final JsonPath path, final LogLevel logLevel,
			final TransformationSpecification specification) {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(logLevel, "logLevel must not be null");
		Objects.requireNonNull(specification, "specification must not be null");
		
		this.path = path;
		this.logLevel = logLevel;
		this.specification = specification;
	}

	/**
	 * Use this method to add a preface to be put out before the logged JSON.
	 * <p>
	 * If you call this method several times, all specified prefaces will be logged
	 * in the order in which they were specified.
	 * 
	 * @param preface the preface to add
	 * @return this object
	 */
	public LoggingSpecification andPreface(final String preface) {
		Objects.requireNonNull(preface, "preface must not be null");
		
		prefaces.add(preface);
		return this;
	}

	public void using(final Logger logger) {
		Objects.requireNonNull(logger, "logger must not be null");
		
		specification.addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				JsonReturningVisitor jsonReturner = new JsonReturningVisitor(
						new RenderContext().setIndent("\t"));
				
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
				
				private <T2> T2 getMultiplexingJsonVisitor(final Function<
						MultiplexingJsonVisitor<Void>, T2> actualVisitorProvider) {
					MultiplexingJsonVisitor<Void> multiVisitor =
							new MultiplexingJsonVisitor<Void>(getNextVisitor(), jsonReturner);
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
				
				private void handleAfterVisitEnd() {
					if (currentPath().matches(path)) {
						prefaces.forEach(preface -> logLevel.logAt(logger, preface));
						logLevel.logAt(logger, jsonReturner.getVisitingResult());
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
				
				private void handleAfterVisitValue(JsonPrimitive<?> primitive) {
					if (currentPath().matches(path)) {
						prefaces.forEach(preface -> logLevel.logAt(logger, preface));
						
						Object value = primitive.getValue();
						
						if (value != null) {
							logLevel.logAt(logger, value.toString());
						} else {
							logLevel.logAt(logger, "null");
						}
					}
				}

				@Override
				protected void afterVisitNullValue() {
					if (currentPath().matches(path)) {
						prefaces.forEach(preface -> logLevel.logAt(logger, preface));
						logLevel.logAt(logger, "null");						
					}
				}
			};
		});
	}
}
