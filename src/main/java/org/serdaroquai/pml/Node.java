package org.serdaroquai.pml;

public interface Node {

	static Node EMPTY_NODE = new ValueNode("");
	
	/**
	 * Returns 32 byte hash representation of node
	 * 
	 * @return
	 */
	byte[] getHash();
}
