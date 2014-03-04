package net.sf.jetro.context;

public class RenderContext {
	private String indent;
	private boolean lenient;
	private boolean htmlSafe;
	private boolean serializeNulls = true;

	public String getIndent() {
		return indent;
	}

	public RenderContext setIndent(String indent) {
		this.indent = indent;
		return this;
	}

	public boolean isLenient() {
		return lenient;
	}

	public RenderContext setLenient(boolean lenient) {
		this.lenient = lenient;
		return this;
	}

	public boolean isHtmlSafe() {
		return htmlSafe;
	}

	public RenderContext setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
		return this;
	}

	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	public RenderContext setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
		return this;
	}
}