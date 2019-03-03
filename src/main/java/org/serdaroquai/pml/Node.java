package org.serdaroquai.pml;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Node {

	static final Node EMPTY_NODE = new Node() {
		private static final String EMPTY_NODE_TOSTRING = "44136FA355B3678A1146AD16F7E8649E94FB4FC21FE77E8310C060F61CAAFF8A, {}";

		@Override
		public String toString() {return EMPTY_NODE_TOSTRING;}
		@Override
		public String getHashableString() { return "{}";}
		@Override
		public String getValue() {return null;}
	};

	@JsonIgnore
	abstract String getHashableString();

	abstract public String getValue();
}
