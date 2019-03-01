package org.serdaroquai.pml;

/**
 * A node that holds a partial key, and one of the two below
 *   1) a hash of another node
 *   2) a byte[] of length < 32 representing a value
 * 	
 * @author tr1b6162
 *
 */
public class ExtensionNode implements Node {

	String partialKey;
	byte[] valueOrNode;
	
	public byte[] getHash() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
