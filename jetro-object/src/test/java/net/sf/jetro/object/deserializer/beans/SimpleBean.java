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
 * Created by matthias.rothe on 27.01.19.
 */
public class SimpleBean {
	private String string;
	private byte bytePrimitive;
	private Byte byteObject;
	private short shortPrimitive;
	private Short shortObject;
	private int intPrimitive;
	private Integer integerObject;
	private long longPrimitive;
	private Long longObject;
	private float floatPrimitive;
	private Float floatObject;
	private double doublePrimitive;
	private Double doubleObject;
	private boolean booleanPrimitive;
	private Boolean booleanObject;
	private Object nullValue = new Object();
	
	public String getString() {
		return string;
	}
	
	public void setString(String string) {
		this.string = string;
	}
	
	public byte getBytePrimitive() {
		return bytePrimitive;
	}
	
	public void setBytePrimitive(byte bytePrimitive) {
		this.bytePrimitive = bytePrimitive;
	}
	
	public Byte getByteObject() {
		return byteObject;
	}
	
	public void setByteObject(Byte byteObject) {
		this.byteObject = byteObject;
	}
	
	public short getShortPrimitive() {
		return shortPrimitive;
	}
	
	public void setShortPrimitive(short shortPrimitive) {
		this.shortPrimitive = shortPrimitive;
	}
	
	public Short getShortObject() {
		return shortObject;
	}
	
	public void setShortObject(Short shortObject) {
		this.shortObject = shortObject;
	}
	
	public int getIntPrimitive() {
		return intPrimitive;
	}
	
	public void setIntPrimitive(int intPrimitive) {
		this.intPrimitive = intPrimitive;
	}
	
	public Integer getIntegerObject() {
		return integerObject;
	}
	
	public void setIntegerObject(Integer integerObject) {
		this.integerObject = integerObject;
	}
	
	public long getLongPrimitive() {
		return longPrimitive;
	}
	
	public void setLongPrimitive(long longPrimitive) {
		this.longPrimitive = longPrimitive;
	}
	
	public Long getLongObject() {
		return longObject;
	}
	
	public void setLongObject(Long longObject) {
		this.longObject = longObject;
	}
	
	public float getFloatPrimitive() {
		return floatPrimitive;
	}
	
	public void setFloatPrimitive(float floatPrimitive) {
		this.floatPrimitive = floatPrimitive;
	}
	
	public Float getFloatObject() {
		return floatObject;
	}
	
	public void setFloatObject(Float floatObject) {
		this.floatObject = floatObject;
	}
	
	public double getDoublePrimitive() {
		return doublePrimitive;
	}
	
	public void setDoublePrimitive(double doublePrimitive) {
		this.doublePrimitive = doublePrimitive;
	}
	
	public Double getDoubleObject() {
		return doubleObject;
	}
	
	public void setDoubleObject(Double doubleObject) {
		this.doubleObject = doubleObject;
	}
	
	public boolean isBooleanPrimitive() {
		return booleanPrimitive;
	}
	
	public void setBooleanPrimitive(boolean booleanPrimitive) {
		this.booleanPrimitive = booleanPrimitive;
	}
	
	public Boolean getBooleanObject() {
		return booleanObject;
	}
	
	public void setBooleanObject(Boolean booleanObject) {
		this.booleanObject = booleanObject;
	}
	
	public Object getNullValue() {
		return nullValue;
	}
	
	public void setNullValue(Object nullValue) {
		this.nullValue = nullValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanObject, booleanPrimitive, byteObject,
				bytePrimitive, doubleObject, doublePrimitive,
				floatObject, floatPrimitive, intPrimitive, integerObject,
				longObject, longPrimitive, nullValue,
				shortObject, shortPrimitive, string);
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
		SimpleBean other = (SimpleBean) obj;
		return Objects.equals(booleanObject, other.booleanObject)
				&& booleanPrimitive == other.booleanPrimitive
				&& Objects.equals(byteObject, other.byteObject)
				&& bytePrimitive == other.bytePrimitive
				&& Objects.equals(doubleObject, other.doubleObject)
				&& Double.doubleToLongBits(doublePrimitive) == 
				Double.doubleToLongBits(other.doublePrimitive)
				&& Objects.equals(floatObject, other.floatObject)
				&& Float.floatToIntBits(floatPrimitive) == 
				Float.floatToIntBits(other.floatPrimitive)
				&& intPrimitive == other.intPrimitive
				&& Objects.equals(integerObject, other.integerObject)
				&& Objects.equals(longObject, other.longObject)
				&& longPrimitive == other.longPrimitive
				&& Objects.equals(nullValue, other.nullValue)
				&& Objects.equals(shortObject, other.shortObject)
				&& shortPrimitive == other.shortPrimitive
				&& Objects.equals(string, other.string);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleBean [string=").append(string).append(", bytePrimitive=")
				.append(bytePrimitive).append(", byteObject=").append(byteObject)
				.append(", shortPrimitive=").append(shortPrimitive).append(", shortObject=")
				.append(shortObject).append(", intPrimitive=").append(intPrimitive)
				.append(", integerObject=").append(integerObject).append(", longPrimitive=")
				.append(longPrimitive).append(", longObject=").append(longObject)
				.append(", floatPrimitive=").append(floatPrimitive).append(", floatObject=")
				.append(floatObject).append(", doublePrimitive=").append(doublePrimitive)
				.append(", doubleObject=").append(doubleObject).append(", booleanPrimitive=")
				.append(booleanPrimitive).append(", booleanObject=").append(booleanObject)
				.append(", nullValue=").append(nullValue).append("]");
		return builder.toString();
	}
}
