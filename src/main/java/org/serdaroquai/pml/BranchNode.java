package org.serdaroquai.pml;

/**
 * A node that holds 16 slots for its children, and a a slot for value if any
 * 
 * @author tr1b6162
 *
 */
public class BranchNode implements Node{

	byte[][] slots = new byte[17][];
	
	public byte[] getHash() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
