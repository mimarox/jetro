package net.sf.jetro.transform.highlevel;

import java.util.Objects;

import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

/**
 * This class is part of the {@link TransformationSpecification} fluent API.
 * <p>
 * It provides the method {@link #to(String)}.
 * 
 * @author Matthias Rothe
 */
public class RenameSpecification {
	private final String name;
	private final TransformationSpecification specification;
	private boolean matching;
	private boolean ignoreCase;
	
	RenameSpecification(final String name,
			final TransformationSpecification specification) {
		Objects.requireNonNull(name, "name must not be null");
		Objects.requireNonNull(specification, "specification must not be null");

		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}
		
		this.name = name;
		this.specification = specification;
	}

	/**
	 * Use this method to rename a property to the given new name.
	 * 
	 * @param newName The new name to rename to
	 */
	public void to(final String newName) {
		Objects.requireNonNull(newName, "newName must not be null");
		
		if (newName.trim().isEmpty()) {
			throw new IllegalArgumentException("newName must not be empty");
		}
		
		specification.addChainedJsonVisitorSupplier(() -> {
			return new UniformChainedJsonVisitor<Void>() {
				
				@Override
				protected String beforeVisitProperty(final String key) {
					if ((matching && key.matches(name)) ||
						(ignoreCase && key.equalsIgnoreCase(name)) ||
						key.equals(name)) {
						return newName;
					} else {
						return key;
					}
				}
			};
		});
	}

	static RenameSpecification matching(final String pattern,
			final TransformationSpecification specification) {
		Objects.requireNonNull(pattern, "pattern must not be null");
		Objects.requireNonNull(specification, "specification must not be null");

		if (pattern.trim().isEmpty()) {
			throw new IllegalArgumentException("pattern must not be empty");
		}

		RenameSpecification renameSpec = new RenameSpecification(pattern, specification);
		renameSpec.matching = true;
		return renameSpec;
	}
	
	static RenameSpecification ignoringCase(final String name,
			final TransformationSpecification specification) {
		Objects.requireNonNull(name, "name must not be null");
		Objects.requireNonNull(specification, "specification must not be null");

		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}

		RenameSpecification renameSpec = new RenameSpecification(name, specification);
		renameSpec.ignoreCase = true;
		return renameSpec;
	}
}
