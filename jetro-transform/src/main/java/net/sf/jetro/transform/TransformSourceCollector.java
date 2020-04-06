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
		return new TransformApplier<>(source, transformer);
	}

	public TransformApplier<?> applying(final TransformationSpecification specification) {
		return applying(specification.toChainedJsonVisitor());
	}
}
