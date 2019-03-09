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
	TrieNode rootNode;
	ByteString rootHash;
	
	public TrieImpl(Store store) {
		this.store = store;
		this.rootNode = EMPTY_NODE;
		this.rootHash = EMPTY_NODE_BYTES;
	}
	
	//TODO swap this out when initializing with a serializer
	@Deprecated
	public void update(String key, String value) {
		update(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value));
	}
	
	public void update(ByteString key, ByteString value) {
		this.rootNode = updateHelper(rootNode, NibbleString.from(key), value);
		this.rootHash = encodeNode(rootNode);
	}
	
	/**
	 * Key point to keep in mind with this helper is that 
	 * new nodes returned from self-recursion are persisted by current invocation
	 * but returned nodes are persisted by the parent.
	 * 
	 * @param node. TrieNode format
	 * @param path. NibbleString (not compact)
	 * @param value. Value to be inserted
	 * 
	 * @return the new version of self node
	 */
	private TrieNode updateHelper(TrieNode node, NibbleString path, ByteString value) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK) {
			return TrieNode.newBuilder()
					.addItem(NibbleString.pack(path, true))
					.addItem(value)
					.build();
		
		} else if (type == NodeType.BRANCH) {
			
			TrieNode.Builder builder = TrieNode.newBuilder(node); 
			
			if (path.size() == 0)
				builder.setItem(16, value);
			else {
				int keyIndex = ByteUtils.nibbleToIndex(path.nibbleAt(0));
				TrieNode newNode = decodeToNode(node.getItem(keyIndex));
				newNode = updateHelper(newNode, path.substring(1), value);
				builder.setItem(keyIndex, encodeNode(newNode));
			}
			return builder.build();
			
		} else if (type.isKeyValueType()) {
			return updateKeyValueHelper(node, path, value);
		}
		
		throw new AssertionError("Not possible");
	}
	
	private TrieNode updateKeyValueHelper(TrieNode node, NibbleString path, ByteString value) {
		NodeType type = getNodeType(node);
		NibbleString key = NibbleString.unpack(node.getItem(0));
		
		// find longest common prefix
		int minKeyLength = Math.min(key.size(), path.size());
		int i=0; 
		while (i < minKeyLength && key.nibbleAt(i) == path.nibbleAt(i)) i++;
		int prefixLength = i;
		
		NibbleString remainingPath = path.substring(prefixLength);
		NibbleString remainingKey = key.substring(prefixLength);
		
		TrieNode newNode;
		if (remainingPath.size() == 0 && remainingKey.size() == 0) {
			if (type == NodeType.LEAF) {
				return TrieNode.newBuilder(node).setItem(1, value).build();
			} else {
				newNode = updateHelper(decodeToNode(node.getItem(1)), remainingPath, value);
			}
		
		
		} else if (remainingKey.size() == 0) {
			if (type == NodeType.EXTENSION) {
				newNode = updateHelper(decodeToNode(node.getItem(1)), remainingPath, value);
			} else {
				
				TrieNode leaf = TrieNode.newBuilder()
						.addItem(NibbleString.pack(remainingPath.substring(1), true))
						.addItem(value)
						.build();
				ByteString leafEncoded = encodeNode(leaf);
				
				int index = ByteUtils.nibbleToIndex(remainingPath.nibbleAt(0));
				newNode = TrieNode.newBuilder(Util.BRANCH_NODE_PROTOTYPE)
						.setItem(index, leafEncoded)
						.setItem(16, node.getItem(1))
						.build();
			}
		
		} else {
			TrieNode.Builder builder = TrieNode.newBuilder(Util.BRANCH_NODE_PROTOTYPE);
			
			if (remainingKey.size() == 1 && type == NodeType.EXTENSION) {
				builder.setItem(ByteUtils.nibbleToIndex(remainingKey.nibbleAt(0)), node.getItem(1));
			} else {
				ByteString packedChildKey = NibbleString.pack(
						remainingKey.substring(1), 
						type == NodeType.LEAF);
				
				TrieNode child = TrieNode.newBuilder()
						.addItem(packedChildKey)
						.addItem(node.getItem(1))
						.build();
				
				builder.setItem(ByteUtils.nibbleToIndex(remainingKey.nibbleAt(0)), encodeNode(child));
			}
			
			if (remainingPath.size() == 0) {
				builder.setItem(16, value);
			} else {
				ByteString packedRemainingPath = NibbleString.pack(remainingPath.substring(1), true);
				
				TrieNode leaf = TrieNode.newBuilder()
						.addItem(packedRemainingPath)
						.addItem(value)
						.build();
				
				builder.setItem(ByteUtils.nibbleToIndex(remainingPath.nibbleAt(0)), encodeNode(leaf));
			}
			
			newNode = builder.build();
		}
		
		if (prefixLength > 0) {
			return TrieNode.newBuilder()
					.addItem(NibbleString.pack(key.substring(0, prefixLength), false))
					.addItem(encodeNode(newNode))
					.build();
		} else {
			return newNode;
		}
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
		return getHelper(decodeToNode(nodeBytes), NibbleString.from(bytes));
	}
	
	// swap this out when implementing serializers
	@Deprecated
	public String get (String key) {
		return get(ByteString.copyFromUtf8(key)).toStringUtf8();
	}
	
	public ByteString get(ByteString key) {
		return getHelper(rootNode, NibbleString.from(key));
	}
	
	private boolean isTerminal(ByteString bs) {
		return (bs.byteAt(0) & NibbleString.TERMINAL) == NibbleString.TERMINAL;
	}
	
	/**
	 * Encodes a given node into a ByteString using Protocol Buffers. 
	 * returns the resulting ByteString if length <= 34, else stores it and returns 
	 * its hash.
	 * 
	 * Only exception to this rule is, if the node to be encoded is the root node, 
	 * a hash is generated regardless of length.
	 * 
	 * Returned ByteString length is always <= 34.
	 * 
	 * @param node
	 * @return
	 */
	private ByteString encodeNode(TrieNode node) {
		if (EMPTY_NODE.equals(node)) return EMPTY_NODE_BYTES;
		ByteString encoded = node.toByteString();
		if (encoded.size() < 34 && node != this.rootNode) return encoded;
		else {
			ByteString hash = Util.sha256(encoded);
			ByteString hashNode = TrieNode.newBuilder().addItem(hash).build().toByteString();
			store.put(hashNode, encoded);
			return hashNode;
		}
	}

	/**
	 * Decodes an encoded TrieNode protobuff bytestring back into a TrieNode object.
	 * 
	 * If the given bytes are of type TrieNode.HASH, then actual node representing 
	 * the hash is fetched from the store and returned.
	 * 
	 * Avoiding casting by exceptions here to increase performance, therefore introduced 
	 * a single element TrieNode of type HASH that holds a single 32 byte hash value.
	 * 
	 * Hence the total byte length became 34 instead of 32.
	 * 
	 * TODO take a look at https://developers.google.com/protocol-buffers/docs/encoding
	 * 
	 * @param bytes
	 * @return
	 */
	private TrieNode decodeToNode(ByteString bytes) {
		
		if (EMPTY_NODE_BYTES.equals(bytes)) return EMPTY_NODE;
		
		try {
			TrieNode node = TrieNode.parseFrom(bytes);
			if (NodeType.HASH == getNodeType(node)) 
				return TrieNode.parseFrom(store.get(bytes));
			else
				return node;
			
		} catch (InvalidProtocolBufferException e) {
			throw new AssertionError("Invalid TrieNode bytes. This should never happen");
		}
		
	}
	/**
	 * Returns the stored value stored in given path
	 * 
	 * @param node TrieNode
	 * @param path path of value relative to the given TrieNode
	 * 
	 * @return value
	 */
	private ByteString getHelper(TrieNode node, NibbleString path) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK)
			return EMPTY_NODE_BYTES;
		
		if (type == NodeType.BRANCH) {
			if (path.size() == 0)
				return node.getItem(16);
			
			ByteString subNodeBytes = node.getItem(ByteUtils.nibbleToIndex(path.nibbleAt(0)));
			return getHelper(decodeToNode(subNodeBytes), path.substring(1));
		}
		
		NibbleString key = NibbleString.unpack(node.getItem(0));
		if (type == NodeType.LEAF) {
			return path.equals(key) ? node.getItem(1) : EMPTY_NODE_BYTES;
		}
		
		if (type == NodeType.EXTENSION) {
			if (key.equals(path.substring(0, key.size())))
				return getHelper(decodeToNode(node.getItem(1)), path.substring(key.size()));
			else
				return EMPTY_NODE_BYTES;
		}
		
		throw new AssertionError("Not possible");
	}
	
	private NodeType getNodeType(TrieNode node) {
		if (EMPTY_NODE.equals(node)) return NodeType.BLANK;
		
		switch(node.getItemCount()) {
		case 1: 
			return NodeType.HASH;
		case 2: 
			ByteString key = node.getItem(0);
			return isTerminal(key) ? NodeType.LEAF : NodeType.EXTENSION;
		case 17: 
			return NodeType.BRANCH;
		default:
			throw new AssertionError("Unrecognized encoded Node format");
		}
	}

}
