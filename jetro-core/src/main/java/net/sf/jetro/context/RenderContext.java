package net.sf.jetro.context;

public class RenderContext {
	private String indent;
	private boolean lenient;
	private boolean htmlSafe;
	private boolean serializeNulls = true;

	public String getIndent() {
		return indent;
	}

	public void setIndent(String indent) {
		this.indent = indent;
	}

	public boolean isLenient() {
		return lenient;
	}

	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	public boolean isHtmlSafe() {
		return htmlSafe;
	}

	public void setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
	}

	public boolean isSerializeNulls() {
		return serializeNulls;
	}

	public void setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}
}