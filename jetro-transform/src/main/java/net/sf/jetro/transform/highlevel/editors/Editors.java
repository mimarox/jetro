package net.sf.jetro.transform.highlevel.editors;

import java.util.Objects;

/**
 * This class contains standard editors and entry points to such editors to
 * be used with <code>TransformationSpecification.capture().&lt;any
 * edit function&gt;</code>.
 * 
 * @author Matthias Rothe
 */
public class Editors {
	
	/**
	 * Private constructor to make the class uninstantiable.
	 */
	private Editors() {}
	
	public static HierarchifyingEditor hierarchifyMapping(final String foreignKey) {
		Objects.requireNonNull(foreignKey, "foreignKey must not be null");
		return new HierarchifyingEditor(foreignKey);
	}
}
