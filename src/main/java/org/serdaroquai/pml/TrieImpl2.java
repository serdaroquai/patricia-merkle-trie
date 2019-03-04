package org.serdaroquai.pml;

import java.util.Iterator;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class TrieImpl2 {
	
	public static final ByteString EMPTY_NODE_BYTES = ByteString.EMPTY;
	
	Store2 store;
	
	public TrieImpl2(Store2 store) {
		this.store = store;
	}
	
	/**
	 * A String-like key is decomposed into its hex nibbles
	 * For Ex: 'doge' => <64 6f 67 65> => '6', '4', '6', 'f' ...
	 * 
	 * @param node
	 * @param path
	 * @return
	 */
	public ByteString get(ByteString node, ByteString path) {
		Iterator<Character> it = ByteUtils.NibbleIterator.from(path);
		return getHelper(node, it);
	}

	/*
	 *  TODO passing an iterator as parameter probably wont work here since, 
	 *  when it does not match there is no way to uniterate..
	 *  
	 *  ByteString also fails since it does not allow a single nibble
	 *  
	 *  Perhaps the best option would be an immutable array of nibbles encoded as hex chars
	 *  in which a substring will return a comparable view without actually copying
	 *  underlying nibble array.
	 *  
	 *  NibbleString
	 */
	private ByteString getHelper(ByteString node, Iterator<Character> it) {
		if (!it.hasNext() || node == EMPTY_NODE_BYTES) return node;
		
		try {
			TreeNode currentNode = node.size() < 32 
					? NodeProto.TreeNode.parseFrom(node) 
					: store.get(node);
					
			if (currentNode.getArrayCount() == 2) {
				String key = ByteUtils.compactEncode(currentNode.getArray(0));
				int i = 0;
				while (it.hasNext() && i < key.length() && key.charAt(i) == it.next()) i++;
				if (i == key.length()) {
					return getHelper(currentNode.getArray(1), it);
				} else {
					return EMPTY_NODE_BYTES;
				}
			} else if (currentNode.getArrayCount() == 17){
				return getHelper(currentNode.getArray(ByteUtils.hexToNibble(it.next(), false)), it);
			}
		} catch (InvalidProtocolBufferException e) {
			throw new AssertionError("Should never happen");
		}
		
		
		return null;
	}

}
