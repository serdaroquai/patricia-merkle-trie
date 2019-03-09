package org.serdaroquai.pml;

public enum NodeType {
	BLANK(false),
	LEAF(true),
	EXTENSION(true),
	BRANCH(false);
	
	private boolean isKeyValueType;
	NodeType (boolean isKeyValueType) {
		this.isKeyValueType = isKeyValueType;
	}
	
	public boolean isKeyValueType() {
		return isKeyValueType;
	}
}
