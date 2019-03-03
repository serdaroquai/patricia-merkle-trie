package org.serdaroquai.pml;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A node that holds 16 slots for its children, and a a slot for value if any
 * 
 * @author tr1b6162
 *
 */
public class BranchNode extends Node{

	String[] arr; // arr of 17, last being a value
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arr);
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
		BranchNode other = (BranchNode) obj;
		if (!Arrays.equals(arr, other.arr))
			return false;
		return true;
	}

	private BranchNode() {}
	
	private static BranchNode build(Builder b) {
		BranchNode n = new BranchNode();
		n.arr = b.arr;
		return n;
	}
	
	@Override
	public String getHashableString() {
		return Util.serialize(this.arr);
	}
	
	public static class Builder {
		String[] arr = new String[17];
		
		public Builder withElement(int index, String e) {
			arr[index] = e;
			return this;
		}
		
		public Builder withValue(String v) {
			arr[16] = v;
			return this;
		}
		
		public BranchNode build() {
			return BranchNode.build(this);
		}
	}

	@JsonIgnore
	@Override
	public String getValue() {
		return arr[17];
	}
	
	@Override
	public String toString() {
		return getHashableString();
	}

}
