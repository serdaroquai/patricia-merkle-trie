package org.serdaroquai.pml;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A node that holds a partial key, and one of the two below
 *   1) a hash of another node
 *   2) a byte[] of length < 32 representing a value
 * 	
 * @author tr1b6162
 *
 */
public class ExtensionNode extends Node {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((partialKey == null) ? 0 : partialKey.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtensionNode other = (ExtensionNode) obj;
		if (partialKey == null) {
			if (other.partialKey != null)
				return false;
		} else if (!partialKey.equals(other.partialKey))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	String partialKey;
	String value;
	
	private ExtensionNode() {};
	
	@JsonProperty("k")
	public String getPartialKey() {
		return partialKey;
	}
	
	@JsonProperty("v")
	public String getValue() {
		return value;
	}
	
	@Override
	public String getHashableString() {
		return Util.serialize(this);
	}
	
	@Override
	public String toString() {
		return getHashableString();
	}
	
	private static ExtensionNode build(Builder b) {
		ExtensionNode n = new ExtensionNode();
		n.partialKey = b.partialKey;
		n.value = b.value;
		return n;
	}
	
	public static class Builder {
		String partialKey;
		String value;
		
		public Builder withPartialKey(String key) {
			this.partialKey = key;
			return this;
		}
		
		public Builder withValue(String value) {
			this.value = value;
			return this;
		}
		
		public ExtensionNode build() {
			return ExtensionNode.build(this);
		}
	}

}
