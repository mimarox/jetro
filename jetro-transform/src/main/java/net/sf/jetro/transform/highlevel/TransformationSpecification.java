package net.sf.jetro.transform.highlevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.transform.TransformApplier;
import net.sf.jetro.transform.TransformSourceCollector;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

/**
 * This class is the entry point to the high level transformation API of Jetro.
 * <p>
 * Subclass this class, implementing the method {@link #specify()} defining a
 * transformation specification. Then call
 * {@link TransformSourceCollector#applying(TransformationSpecification)} of the
 * object returned by any Jetro.transform(..) call providing
 * an instance of your subclass and then call any of the methods of the returned
 * {@link TransformApplier} to perform the transformation.
 * <p>
 * {@link #specify()} lets you define the transformation specification by using an
 * eDSL-like fluent API. All the entry points to such API calls are protected methods
 * of this class and all protected methods of this class with the exception of specify()
 * itself are members of the fluent API. For further documentation see the individual
 * methods.
 * 
 * @author Matthias Rothe
 */
public abstract class TransformationSpecification implements ChainedJsonVisitorSupplier {
	static final JsonPath ROOT_PATH = JsonPath.compile("$");
	
	private static final ChainedJsonVisitor<Void> NOOP_VISITOR =
			new ChainedJsonVisitor<Void>() {};
	
	private TransformationSpecification outerSpecification;
	private Set<ChainedJsonVisitorSupplier> suppliers = new LinkedHashSet<>();
	private Map<String, JsonType> variables = new HashMap<>();
	private boolean specified = false;
	private boolean renderNullValues = false;
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.jetro.transform.highlevel.ChainedJsonVisitorSupplier#toChainedJsonVisitor()
	 */
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

	/**
	 * Entry point to generic path related operations.
	 * <p>
	 * The path must be a valid {@link JsonPath} and may contain wildcards.
	 * To specify the end of a JSON array use the end-of-array specifier 
	 * like so: $[-]. This denotes the end of the root JSON array.
	 * Such a path may only be used to add one or several values to the end of
	 * that array.
	 * <p>
	 * Please make sure that the given path fits the intended operation to be
	 * performed at that path. Adding a property for example will only be possible
	 * at a path resulting in a JSON object. Likewise adding a value will only be
	 * possible at a path resulting in a value or the end of a JSON array.
	 * <p>
	 * Any method adding to a JSON array will add the new value either after the
	 * index given in the path or at the end of the array, if the given index is
	 * the end-of-array specifier.
	 * 
	 * @param path The JSON path at which to perform a transformation.
	 * @return an instance of PathAwareSpecification
	 */
	protected PathAwareSpecification at(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		return at(JsonPath.compile(path));
	}

	/**
	 * Entry point to generic path related operations.
	 * <p>
	 * The path may contain wildcards. To specify the end of a JSON array
	 * use the end-of-array specifier like so: $[-]. This denotes the end of the
	 * root JSON array. Such a path may only be used to add one or several values
	 * to the end of that array.
	 * <p>
	 * Please make sure that the given path fits the intended operation to be
	 * performed at that path. Adding a property for example will only be possible
	 * at a path resulting in a JSON object. Likewise adding a value will only be
	 * possible at a path resulting in a value or the end of a JSON array.
	 * <p>
	 * Any method adding to a JSON array will add the new value either after the
	 * index given in the path or at the end of the array, if the given index is
	 * the end-of-array specifier.
	 * 
	 * @param path The JSON path at which to perform a transformation.
	 * @return an instance of PathAwareSpecification
	 */
	protected PathAwareSpecification at(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		return new PathAwareSpecification(path, this);
	}
	
	/**
	 * Entry point to capturing values and processing them. After capturing
	 * a value, it can be edited and stored as a variable. All variables are
	 * global. So if a nested {@link TransformationSpecification} captures a
	 * variable this is stored in the outermost specification and available
	 * for all specifications on all nesting levels.
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
	 * a value, it can be edited and stored as a variable. All variables are
	 * global. So if a nested {@link TransformationSpecification} captures a
	 * variable this is stored in the outermost specification and available
	 * for all specifications on all nesting levels.
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

	/**
	 * Use this method to keep only the value or property at the given path.
	 * Anything but any parent JSON object or array and the value or property
	 * specified will be removed.
	 * <p>
	 * To keep the child structure of the kept value or property, use the
	 * matching all further JsonPath specifier &quot;:&quot; like so: $.key:.
	 * <p>
	 * The path must be a valid {@link JsonPath} and may contain wildcards.
	 * 
	 * @param path The path to keep
	 */
	protected void keep(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		keep(JsonPath.compile(path));
	}
	
	/**
	 * Use this method to keep only the value or property at the given path.
	 * Anything but any parent JSON object or array and the value or property
	 * specified will be removed.
	 * <p>
	 * To keep the child structure of the kept value or property, use the
	 * matching all further JsonPath specifier &quot;:&quot; like so: $.key:.
	 * <p>
	 * The path may contain wildcards.
	 * 
	 * @param path The path to keep
	 */
	protected void keep(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		addKeepOrRemoveSupplier(currentPath -> 
			currentPath.isParentPathOf(path) || currentPath.matches(path));
	}
	
	/**
	 * Use this method to remove the value or property at the given path.
	 * <p>
	 * The path must be a valid {@link JsonPath} and may contain wildcards.
	 * 
	 * @param path The path at which to remove
	 */
	protected void remove(final String path) {
		Objects.requireNonNull(path, "path must not be null");
		remove(JsonPath.compile(path));
	}
	
	/**
	 * Use this method to remove the value or property at the given path.
	 * <p>
	 * The path may contain wildcards.
	 * 
	 * @param path The path at which to remove
	 */
	protected void remove(final JsonPath path) {
		Objects.requireNonNull(path, "path must not be null");
		addKeepOrRemoveSupplier(currentPath -> !currentPath.matches(path));
	}

	private void addKeepOrRemoveSupplier(final Predicate<JsonPath> passOn) {
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
					return passOn.test(currentPath());
				}
			};
		});
	}
	
	/**
	 * Entry point to renaming property keys. Any key in the entire JSON named
	 * with the given name will be renamed.
	 * 
	 * @param name the name to rename
	 * @return an instance of RenameSpecification
	 */
	protected RenameSpecification renameProperties(final String name) {
		Objects.requireNonNull(name, "name must not be null");
		return new RenameSpecification(name, this);
	}

	/**
	 * Entry point to renaming property keys. Any key in the entire JSON matching
	 * the given regex pattern will be renamed.
	 * 
	 * @param pattern the pattern to match for renaming
	 * @return an instance of RenameSpecification
	 * @see java.util.regex.Pattern
	 * @see String#matches(String)
	 */	
	protected RenameSpecification renamePropertiesMatching(final String pattern) {
		Objects.requireNonNull(pattern, "pattern must not be null");
		return RenameSpecification.matching(pattern, this);
	}

	/**
	 * Entry point to renaming property keys. Any key in the entire JSON named
	 * with the given name will be renamed. This method ignores the case of the
	 * names of property keys.
	 * 
	 * @param name the name to rename
	 * @return an instance of RenameSpecification
	 * @see String#equalsIgnoreCase(String)
	 */
	protected RenameSpecification renamePropertiesIgnoringCase(final String name) {
		Objects.requireNonNull(name, "name must not be null");
		return RenameSpecification.ignoringCase(name, this);
	}
	
	/**
	 * Use this method to apply a custom {@link ChainedJsonVisitor} to this
	 * {@link TransformationSpecification} providing any functionality not
	 * available from the fluent API.
	 * 
	 * @param visitor the visitor to add
	 */
	protected void applyCustomVisitor(final ChainedJsonVisitor<Void> visitor) {
		Objects.requireNonNull(visitor, "visitor must not be null");
		addChainedJsonVisitorSupplier(() -> visitor);
	}
	
	/**
	 * Use this method to apply another {@link TransformationSpecification} making the
	 * specifications composable.
	 * 
	 * @param specification the specification to add
	 */
	protected void applySpecification(final TransformationSpecification specification) {
		Objects.requireNonNull(specification, "specification must not be null");
		specification.outerSpecification = this;
		addChainedJsonVisitorSupplier(specification);
	}
	
	/**
	 * Call this method with a value of <code>true</code> to render any null values
	 * provided. See the individual value supplying methods of the fluent API on how
	 * they treat this setting.
	 * <p>
	 * For nested {@link TransformationSpecification}s the local setting will remain
	 * no matter what the setting is on the nesting specification.
	 * 
	 * @param renderNullValues whether to render null values or not
	 */
	protected void setRenderNullValues(final boolean renderNullValues) {
		this.renderNullValues = renderNullValues;
	}
	
	/**
	 * Implement this method to define the transformation specification using the
	 * various other protected methods of this class. Note that the specification
	 * does not by itself perform the transformation. Rather it is used within a
	 * Jetro.transform(..).applying(TransformationSpecification)-and-outputting-the-
	 * result-in-some-way call chain, where the transformation only ever happens in
	 * the outputting call.
	 */
	protected abstract void specify();
	
	void addChainedJsonVisitorSupplier(final ChainedJsonVisitorSupplier supplier) {
		Objects.requireNonNull(supplier, "supplier must not be null");
		suppliers.add(supplier);
	}

	void putVariable(final String variableName, JsonType value) {
		if (outerSpecification != null) {
			outerSpecification.putVariable(variableName, value);
		} else {
			variables.put(variableName, value);			
		}
	}
	
	JsonType getVariable(final String variableName) {
		if (outerSpecification != null) {
			return outerSpecification.getVariable(variableName);
		} else {
			return variables.get(variableName);
		}
	}

	boolean isRenderNullValues() {
		return renderNullValues;
	}
}
