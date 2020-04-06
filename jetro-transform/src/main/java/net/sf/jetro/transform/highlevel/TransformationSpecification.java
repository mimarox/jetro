package net.sf.jetro.transform.highlevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

public abstract class TransformationSpecification implements ChainedJsonVisitorSupplier {
	static final JsonPath ROOT_PATH = JsonPath.compile("$");
	
	private static final ChainedJsonVisitor<Void> NOOP_VISITOR =
			new ChainedJsonVisitor<Void>() {};
	
	private Set<ChainedJsonVisitorSupplier> suppliers = new LinkedHashSet<>();
	private Map<String, JsonType> variables = new HashMap<>();
	private boolean specified = false;
	private boolean renderNullValues = false;
	
	@Override
	public ChainedJsonVisitor<Void> toChainedJsonVisitor() {
		if (!specified) {
			specify();
			specified = true;
		}
		
		ChainedJsonVisitor<Void> visitor = null;
		Iterator<ChainedJsonVisitorSupplier> iterator = suppliers.iterator();
		
		if (iterator.hasNext()) {
			visitor = iterator.next().toChainedJsonVisitor();
		}
		
		while (iterator.hasNext()) {
			visitor.attachVisitor(iterator.next().toChainedJsonVisitor());
		}
		
		return visitor != null ? visitor : NOOP_VISITOR;
	}

	protected PathAwareSpecification at(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		return at(JsonPath.compile(path));
	}

	protected PathAwareSpecification at(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		return new PathAwareSpecification(path, this);
	}
	
	/**
	 * Entry point to capturing values and processing them. After capturing
	 * a value, it can be edited and stored as a variable.
	 * <p>
	 * The path must be a valid {@link JsonPath} and must not contain any wildcards.
	 * 
	 * @param path The JSON path to capture the value from.
	 * @return an instance of CaptureSpecification
	 */
	protected CaptureSpecification capture(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		return capture(JsonPath.compile(path));
	}
	
	/**
	 * Entry point to capturing values and processing them. After capturing
	 * a value, it can be edited and stored as a variable.
	 * <p>
	 * The {@link JsonPath path} must not contain any wildcards.
	 * 
	 * @param path The JsonPath to capture the value from.
	 * @return an instance of CaptureSpecification
	 */
	protected CaptureSpecification capture(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		
		if (path.containsOptionals()) {
			throw new IllegalArgumentException("path must not contain any wildcards");
		} else {
			return new CaptureSpecification(path, this);
		}
	}

	protected void remove(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		remove(JsonPath.compile(path));
	}
	
	protected void remove(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		addChainedJsonVisitorSupplier(() -> {
			return new PathAwareJsonVisitor<Void>() {
				
				@Override
				protected boolean doBeforeVisitObject() {
					return passOn();
				}
				
				@Override
				protected boolean doBeforeVisitArray() {
					return passOn();
				}
				
				@Override
				protected String doBeforeVisitProperty(final String name) {
					return passOn() ? name : null;
				}
				
				@Override
				protected Boolean doBeforeVisitValue(final boolean value) {
					return passOn() ? value : null;
				}
				
				@Override
				protected Number doBeforeVisitValue(final Number value) {
					return passOn() ? value : null;
				}
				
				@Override
				protected String doBeforeVisitValue(final String value) {
					return passOn() ? value : null;
				}
				
				@Override
				protected boolean doBeforeVisitNullValue() {
					return passOn();
				}
				
				private boolean passOn() {
					if (currentPath().matches(path)) {
						return false;
					} else {
						return true;
					}
				}
			};
		});
	}

	protected RenameSpecification renameProperty(final String name) {
		Objects.requireNonNull(name, "name must not be null");
		return new RenameSpecification(name, this);
	}

	protected RenameSpecification renamePropertiesMatching(final String pattern) {
		Objects.requireNonNull(pattern, "pattern must not be null");
		return RenameSpecification.matching(pattern, this);
	}

	protected RenameSpecification renamePropertyIgnoreCase(final String name) {
		Objects.requireNonNull(name, "name must not be null");
		return RenameSpecification.ignoringCase(name, this);
	}
	
	protected void setRenderNullValues(final boolean renderNullValues) {
		this.renderNullValues = renderNullValues;
	}
	
	protected abstract void specify();
	
	void addChainedJsonVisitorSupplier(final ChainedJsonVisitorSupplier supplier) {
		Objects.requireNonNull(supplier, "supplier must not be null");
		suppliers.add(supplier);
	}

	void putVariable(final String variableName, JsonType value) {
		variables.put(variableName, value);
	}
	
	JsonType getVariable(final String variableName) {
		return variables.get(variableName);
	}

	boolean isRenderNullValues() {
		return renderNullValues;
	}
}
