package org.serdaroquai.pml;

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
