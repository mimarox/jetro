package net.sf.jetro.path;

class MatchesAllFurtherPathElement extends JsonPathElement {
	private static final long serialVersionUID = 3355362025443127736L;

	MatchesAllFurtherPathElement() {
		super(false);
	}

	@Override
	public String toString() {
		return ":";
	}
}