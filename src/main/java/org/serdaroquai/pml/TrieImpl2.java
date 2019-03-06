package org.serdaroquai.pml;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;


/**
 * Thinking out loud: 
 * 
 * A String-like key is decomposed into its hex nibbles
 * For Ex: 'doge' => <64 6f 67 65> => '6', '4', '6', 'f' ...
 * 
 * Given a key can only be odd number of nibbles. so will need a helper to
 * like a ByteString but can work with odd number of nibbles. It also should support
 * concatenation and substring operations without copying the underlying bytes
 * 
 * NibbleString.from(ByteString)
 * 
 * While traversing / updating nodes however, we will need to store some flags in 
 * nodes key. 
 * 
 * Therefore we will need to 
 * 		ByteString encode(NibbleString n, boolean isTerminal) 
 * 		: if it is even # of nibbles put 0b0000 and treat it as odd
 * 		: if it is odd # of nibbles put 0b0001 so it is even
 * 
 * 		after which, 
 * 		: if key belongs to a terminal node | the first nibble with 0b0010
 * 
 * when a key is encoded, we will use it for storage only so ByteString would do
 * 
 * - An encoded ByteString should reveal the information of terminality
 * and should be decoded back to a nibbleString
 * 		boolean isTerminal(ByteString encoded);
 * 		NibbleString decode(ByteString encoded);
 * 
 * - an extension node never points to another extension node
 * 		- if it is not terminal, it must point to a branch node
 * 		- it it is terminal it must point to a value (<32) or hash of value (>=32)
 * 		- since the underlying key value store will also be storing values as well as nodes, 
 * it should be of type <ByteString, ByteString> and not worry about types.
 * 		- the retrieving code will parse it as a Node based on the terminal information of flag
 * 
 * - a branch node stores only values.
 * 
 * @author tr1b6162
 *
 */
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
	 * @param node rootNode bytes
	 * @param path bytes of key (Not compact)
	 * @return
	 */
	public ByteString get(ByteString node, ByteString bytes) {
		return getHelper(node, NibbleString.from(bytes));
	}
	
	public ByteString getHelper(ByteString nodeBytes, NibbleString path) {
		if (path.size() == 0 || nodeBytes == EMPTY_NODE_BYTES) return nodeBytes;
		
		try {
			TreeNode node = nodeBytes.size() < 32 
					? NodeProto.TreeNode.parseFrom(nodeBytes) 
					: store.get(nodeBytes);
					
			if (node.getItemCount() == 2) {
				NibbleString key = NibbleString.compactDecode(node.getItem(0));
				if (key.equals(path.substring(0, key.size()))) {
					return getHelper(node.getItem(1), path.substring(key.size()));
				} else {
					return EMPTY_NODE_BYTES;
				}
			
			} else if (node.getItemCount() == 17) {
				char key = path.nibbleAt(0);
				return getHelper(node.getItem(ByteUtils.nibbleToIndex(key)), path.substring(1));
			}
			
		} catch (InvalidProtocolBufferException e) {
			throw new AssertionError("Should never happen");
		}
		throw new AssertionError("Should never happen");
	}

}
