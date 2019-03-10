package org.serdaroquai.pml;

import static org.serdaroquai.pml.Common.BRANCH_NODE_PROTOTYPE;
import static org.serdaroquai.pml.Common.EMPTY_NODE;
import static org.serdaroquai.pml.Common.EMPTY_NODE_BYTES;
import static org.serdaroquai.pml.Common.getNodeType;
import static org.serdaroquai.pml.Common.sha256;
import static org.serdaroquai.pml.Common.toByteString;
import static org.serdaroquai.pml.NibbleString.from;
import static org.serdaroquai.pml.NibbleString.isTerminal;
import static org.serdaroquai.pml.NibbleString.pack;
import static org.serdaroquai.pml.NibbleString.unpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * In oder to introduce transactions, we need to introduce a few changes: 
 * 	1) an encode() and update() method that does not persist, whatever is encoded immediately 
 * but rather stages it until commit. (or introduce some staging methods to store interface)
 *  2) delegate responsibility of setting root hash to a commit method.
 * 
 * - a nice to have would be an update method optimized for initialization, that only keeps relevant nodes 
 * and discards the ones that no longer serve a purpose after initialization.
 * 
 * - a nice to have would be a map (maybe LRU?) that is in sync with trie to serve queries in O(1) time.
 * 
 * @author tr1b6162
 *
 */
public class Trie<K,V>{
	
	private Store store;
	private TrieNode rootNode;
	private ByteString rootHash;
	private Serializer<K> keySerializer;
	private Serializer<V> valueSerializer;
	
	public static Trie<String, String> create() {
		return new Trie<String, String>(EMPTY_NODE_BYTES, new MemoryStore(), Serializer.STRING_UTF8, Serializer.STRING_UTF8);
	}

	public static Trie<String, String> create(Store store) {
		return new Trie<String, String>(EMPTY_NODE_BYTES, store, Serializer.STRING_UTF8, Serializer.STRING_UTF8);
	}
	
	public static Trie<String, String> create(ByteString rootHash, Store store) {
		return new Trie<String, String>(rootHash, store, Serializer.STRING_UTF8, Serializer.STRING_UTF8);
	}
	
	public static <K,V> Trie<K, V> create(ByteString rootHash, Store store, Serializer<K> key, Serializer<V> value) {
		return new Trie<K, V>(EMPTY_NODE_BYTES, store, key, value);
	}
	
	private Trie(
			ByteString rootHash, 
			Store store, 
			Serializer<K> keySerializer, 
			Serializer<V> valueSerializer) {
		
		this.rootHash = rootHash;
		this.rootNode = decodeToNode(rootHash);
		this.keySerializer = keySerializer;
		this.valueSerializer =valueSerializer;
		this.store = store;
	}
	
	public V get(K key) {
		return valueSerializer.deserialize(
				getHelper(rootNode, from(keySerializer.serialize(key))));
	}
	
	public V get(ByteString rootHash, K key) {
		return valueSerializer.deserialize(
				getHelper(decodeToNode(rootHash), from(keySerializer.serialize(key))));
	}

	public ByteString put(K key, V value) {
		return update(keySerializer.serialize(key), valueSerializer.serialize(value));
	}

	public ByteString getRootHash() {
		return this.rootHash;
	}
	
	public Map<K,V> toMap() {
		Map<K,V> results = new HashMap<>();
		toMapHelper(this.rootNode, new ArrayList<Byte>(), results);
		return results;
	}
	
	public Map<K,V> toMap(ByteString rootHash) {
		Map<K,V> results = new HashMap<>();
		toMapHelper(decodeToNode(rootHash), new ArrayList<Byte>(), results);
		return results;
	}
	
	private void toMapHelper(TrieNode node, List<Byte> path, Map<K,V> map) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK) 
			return;
		
		int len = path.size();
		if (type.isKeyValueType()) {
			
			NibbleString key = unpack(node.getItem(0));
			for (Byte b : key) path.add(b);
			
			if (isTerminal(node.getItem(0))) {
				map.put(keySerializer.deserialize(toByteString(path)), 
						valueSerializer.deserialize(node.getItem(1)));
				
			} else {
				toMapHelper(decodeToNode(node.getItem(1)), path, map);
			}
			
			len = path.size() - len; // number of nibbles to remove
			for (int i=0; i<len; i++) path.remove(path.size()-1);
			
		} else if (type == NodeType.BRANCH) {
			
			if (!ByteString.EMPTY.equals(node.getItem(16))) {
				map.put(keySerializer.deserialize(toByteString(path)), 
						valueSerializer.deserialize(node.getItem(16)));
			}
			
			for (int i=0; i<16; i++) {
				if (!ByteString.EMPTY.equals(node.getItem(i))) {
					path.add((byte) i);
					toMapHelper(decodeToNode(node.getItem(i)), path, map);
					path.remove(path.size()-1);
				}
			}
		}
		
	}
	
	private ByteString update(ByteString key, ByteString value) {
		this.rootNode = updateHelper(rootNode, from(key), value);
		this.rootHash = encodeNode(rootNode);
		return this.rootHash;
	}
	
	/*
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
					.addItem(pack(path, true))
					.addItem(value)
					.build();
		
		} else if (type == NodeType.BRANCH) {
			
			TrieNode.Builder builder = TrieNode.newBuilder(node); 
			
			if (path.size() == 0)
				builder.setItem(16, value);
			else {
				int keyIndex = path.nibbleAsByte(0);
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
		NibbleString key = unpack(node.getItem(0));
		
		// find longest common prefix
		int minKeyLength = Math.min(key.size(), path.size());
		int i=0; 
		while (i < minKeyLength && key.nibbleAsChar(i) == path.nibbleAsChar(i)) i++;
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
						.addItem(pack(remainingPath.substring(1), true))
						.addItem(value)
						.build();
				ByteString leafEncoded = encodeNode(leaf);
				
				newNode = TrieNode.newBuilder(BRANCH_NODE_PROTOTYPE)
						.setItem(remainingPath.nibbleAsByte(0), leafEncoded)
						.setItem(16, node.getItem(1))
						.build();
			}
		
		} else {
			TrieNode.Builder builder = TrieNode.newBuilder(BRANCH_NODE_PROTOTYPE);
			
			if (remainingKey.size() == 1 && type == NodeType.EXTENSION) {
				builder.setItem(remainingKey.nibbleAsByte(0), node.getItem(1));
			} else {
				ByteString packedChildKey = pack(
						remainingKey.substring(1), 
						type == NodeType.LEAF);
				
				TrieNode child = TrieNode.newBuilder()
						.addItem(packedChildKey)
						.addItem(node.getItem(1))
						.build();
				
				builder.setItem(remainingKey.nibbleAsByte(0), encodeNode(child));
			}
			
			if (remainingPath.size() == 0) {
				builder.setItem(16, value);
			} else {
				ByteString packedRemainingPath = pack(remainingPath.substring(1), true);
				
				TrieNode leaf = TrieNode.newBuilder()
						.addItem(packedRemainingPath)
						.addItem(value)
						.build();
				
				builder.setItem(remainingPath.nibbleAsByte(0), encodeNode(leaf));
			}
			
			newNode = builder.build();
		}
		
		if (prefixLength > 0) {
			return TrieNode.newBuilder()
					.addItem(pack(key.substring(0, prefixLength), false))
					.addItem(encodeNode(newNode))
					.build();
		} else {
			return newNode;
		}
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
			ByteString hash = sha256(encoded);
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
			
			ByteString subNodeBytes = node.getItem(path.nibbleAsByte(0));
			return getHelper(decodeToNode(subNodeBytes), path.substring(1));
		}
		
		NibbleString key = unpack(node.getItem(0));
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

}
