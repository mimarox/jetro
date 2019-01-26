/*
 * #%L
 * Jetro Object
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(cause, name, trigger);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestBean other = (TestBean) obj;
		return Objects.equals(cause, other.cause) && Objects.equals(name, other.name)
				&& Objects.equals(trigger, other.trigger);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestBean [name=").append(name).append(", trigger=").append(trigger).append(", cause=")
				.append(cause).append("]");
		return builder.toString();
	}
}
