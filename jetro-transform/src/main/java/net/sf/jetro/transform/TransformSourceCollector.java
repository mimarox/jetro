package net.sf.jetro.transform;

import java.util.Objects;

import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

/**
 * Part of the transformation API.
 * <p>
 * The methods of this class let you specify the transformation(s) to apply.
 * 
 * @author Matthias Rothe
 * @see Jetro
 */
public class TransformSourceCollector {
	private final VisitingReader source;
	
	TransformSourceCollector(final VisitingReader source) {
		Objects.requireNonNull(source, "source must not be null");	
		this.source = source;
	}

	/**
	 * Specify a {@link ChainedJsonVisitor} to perform the intended transformation.
	 * <p>
	 * This transformer can be either a single instance of any subtype of
	 * ChainedJsonVisitor or a chain of such instances composed by constructor injection
	 * or by calling {@link ChainedJsonVisitor#attachVisitor(JsonVisitor)}.
	 * <p>
	 * Providing an instance of a subtype of ChainedJsonVisitor that doesn't override
	 * any of its methods results in a no-op transformation.
	 * 
	 * @param transformer the ChainedJsonVisitor to perform the transformation
	 * @param <R> the return type of {@link ChainedJsonVisitor#getVisitingResult()}
	 * @return an instance of {@link TransformApplier}
	 * @see #applyingNone()
	 */
	public <R> TransformApplier<R> applying(final ChainedJsonVisitor<R> transformer) {
		Objects.requireNonNull(transformer, "transformer must not be null");
		return new TransformApplier<>(source, transformer);
	}

	/**
	 * Specify a {@link TransformationSpecification} to perform the intended
	 * transformation.
	 * <p>
	 * Providing an instance of an implementation of TransformationSpecification
	 * with an empty {@link TransformationSpecification#specify() specify()} method
	 * will result in a no-op transformation.
	 * 
	 * @param specification the TransformationSpecification to perform the transformation
	 * @return an instance of {@link TransformApplier}
	 */
	public TransformApplier<?> applying(final TransformationSpecification specification) {
		Objects.requireNonNull(specification, "specification must not be null");
		return new TransformApplier<Void>(source, specification);
	}
	
	/**
	 * Use this method if you don't want any transformation to happen.
	 * <p>
	 * Not specifying any transformation might be useful if you use Jetro as a
	 * bean mapper and the beans differ only in data types that are treated equally
	 * in JSON, such as any number types and their wrappers or collection types.
	 * 
	 * @return an instance of {@link TransformApplier}
	 */
	public TransformApplier<?> applyingNone() {
		return applying(new ChainedJsonVisitor<Void>() {});
	}
}
