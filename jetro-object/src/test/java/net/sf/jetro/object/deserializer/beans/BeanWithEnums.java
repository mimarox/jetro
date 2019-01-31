package net.sf.jetro.object.deserializer.beans;

import java.util.List;
import java.util.Objects;

import net.sf.jetro.object.deserializer.DeserializationElement.ElementType;

public class BeanWithEnums {
	private ElementType elementType;
	private List<ElementType> elementTypes;
	
	public ElementType getElementType() {
		return elementType;
	}
	
	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}
	
	public List<ElementType> getElementTypes() {
		return elementTypes;
	}
	
	public void setElementTypes(List<ElementType> elementTypes) {
		this.elementTypes = elementTypes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementType, elementTypes);
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
		BeanWithEnums other = (BeanWithEnums) obj;
		return elementType == other.elementType &&
				Objects.equals(elementTypes, other.elementTypes);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BeanWithEnums [elementType=").append(elementType)
			.append(", elementTypes=").append(elementTypes).append("]");
		return builder.toString();
	}
}
