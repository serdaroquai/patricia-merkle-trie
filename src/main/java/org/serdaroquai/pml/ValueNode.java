package org.serdaroquai.pml;

import java.nio.charset.StandardCharsets;

/**
 * A node that consists of a value and its 32-byte hash
 * 
 * hash = ['arbitrary length byte[] representing value']
 * 
 * @author tr1b6162
 *
 */
public class ValueNode implements Node{

	byte[] value;
	
	public ValueNode(String value) {
		//TODO fix me (need encoder)
		this.value = value.getBytes(StandardCharsets.UTF_8);
	}
	
	public String getValue() {
		//TODO fix me (need encoder)
		return new String(value, StandardCharsets.UTF_8);
	}
	
	public byte[] getHash() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
