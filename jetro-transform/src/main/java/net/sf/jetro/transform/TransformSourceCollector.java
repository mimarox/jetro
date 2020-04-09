package net.sf.jetro.transform;

import java.util.Objects;

import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

public class TransformSourceCollector {
	private final VisitingReader source;
	
	TransformSourceCollector(final VisitingReader source) {
		Objects.requireNonNull(source, "source must not be null");	
		this.source = source;
	}

	public <R> TransformApplier<R> applying(final ChainedJsonVisitor<R> transformer) {
		Objects.requireNonNull(transformer, "transformer must not be null");
		return new TransformApplier<>(source, transformer);
	}

	public TransformApplier<?> applying(final TransformationSpecification specification) {
		Objects.requireNonNull(specification, "specification must not be null");
		return applying(specification.toChainedJsonVisitor());
	}
}
