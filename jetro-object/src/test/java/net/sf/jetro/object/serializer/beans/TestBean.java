package net.sf.jetro.object.serializer.beans;

import java.util.List;
import java.util.Map;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class TestBean {
	private boolean visible;
	private char character;
	private String string;
	private int integer;
	private long[] array;
	private List<String> list;
	private Map<String, String> map;
	private NestedTestBean bean;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public char getCharacter() {
		return character;
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public long[] getArray() {
		return array;
	}

	public void setArray(long[] array) {
		this.array = array;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public NestedTestBean getBean() {
		return bean;
	}

	public void setBean(NestedTestBean bean) {
		this.bean = bean;
	}
}
