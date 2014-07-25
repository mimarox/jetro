package net.sf.jetro.object.deserializer.beans;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public class TestBean {
	private String name;
	private String trigger;
	private String cause;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}
}
