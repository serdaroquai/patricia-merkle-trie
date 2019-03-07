package org.serdaroquai.pml;

import org.serdaroquai.pml.NodeProto.TrieNode;

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
public class TrieImpl {
	
	public static final TrieNode EMPTY_NODE = TrieNode.newBuilder().build();
	public static final ByteString EMPTY_NODE_BYTES = EMPTY_NODE.toByteString();
	
	Store store;
	
	public TrieImpl(Store store) {
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
	public ByteString get(ByteString nodeBytes, ByteString bytes) {
		return getHelper2(decodeToNode(nodeBytes), NibbleString.from(bytes));
	}
	
	private boolean isTerminal(ByteString bs) {
		return (bs.byteAt(0) & 0b0010_0000) == 0b0010_0000;
	}
	
	/* 
	 * returns the ByteString of node OR stores the node and returns the hash
	 * returned ByteString is always <= 32 so you can store it in parent node 
	 */
	private ByteString encodeNode(TrieNode node) {
		if (EMPTY_NODE.equals(node)) return EMPTY_NODE_BYTES;
		ByteString encoded = node.toByteString();
		if (encoded.size() < 32) return encoded;
		else {
			ByteString hash = Util.sha256(encoded);
			store.put(hash, encoded);
			return hash;
		}
	}
	

	/*
	 * Avoiding exceptions here to increase performance, hence introduced a single 
	 * element TrieNode that only holds a hash value.
	 * 
	 * byte length becomes 34 per hash instead of 32.
	 * 
	 * TODO take a look at https://developers.google.com/protocol-buffers/docs/encoding
	 * 
	 */
	private TrieNode decodeToNode(ByteString bytes) {
		
		if (EMPTY_NODE_BYTES.equals(bytes)) return EMPTY_NODE;
		
		try {
			TrieNode node = TrieNode.parseFrom(bytes);
			if (node.getItemCount() == 1) // This is a hash node
				return TrieNode.parseFrom(store.get(bytes));
			else
				return node; 
			
		} catch (InvalidProtocolBufferException e) {}
		
		throw new AssertionError("Never happen");
	}
	
	public ByteString getHelper2(TrieNode node, NibbleString path) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK)
			return EMPTY_NODE_BYTES;
		
		if (type == NodeType.BRANCH) {
			if (path.size() == 0)
				return node.getItem(16);
			
			ByteString subNodeBytes = node.getItem(ByteUtils.nibbleToIndex(path.nibbleAt(0)));
			return getHelper2(decodeToNode(subNodeBytes), path.substring(1));
		}
		
		NibbleString key = NibbleString.compactDecode(node.getItem(0));
		if (type == NodeType.LEAF) {
			return path.equals(key) ? node.getItem(1) : EMPTY_NODE_BYTES;
		}
		
		if (type == NodeType.EXTENSION) {
			if (key.equals(path.substring(0, key.size())))
				return getHelper2(decodeToNode(node.getItem(1)), path.substring(key.size()));
			else
				return EMPTY_NODE_BYTES;
		}
		
		throw new AssertionError("Not possible");
	}
	
	private NodeType getNodeType(TrieNode node) {
		if (EMPTY_NODE.equals(node)) 
			return NodeType.BLANK;
		
		if (node.getItemCount() == 2) {
			
			ByteString key = node.getItem(0);
			if (isTerminal(key)) 
				return NodeType.LEAF;
			else 
				return NodeType.EXTENSION;
		}
		
		if (node.getItemCount() == 17) {
			return NodeType.BRANCH;
		}
		
		throw new AssertionError("Impossible");
	}

}
