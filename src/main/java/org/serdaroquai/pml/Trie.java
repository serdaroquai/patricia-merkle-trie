package org.serdaroquai.pml;

import static org.serdaroquai.pml.Common.*;
import static org.serdaroquai.pml.NibbleString.from;
import static org.serdaroquai.pml.NibbleString.isTerminal;
import static org.serdaroquai.pml.NibbleString.pack;
import static org.serdaroquai.pml.NibbleString.unpack;

import java.nio.ByteBuffer;
import java.util.*;

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
	private ByteBuffer rootHash;
	private Serializer<K> keySerializer;
	private Serializer<V> valueSerializer;
		
	public static class TrieBuilder<K,V> {
		
		ByteBuffer rootHash = EMPTY_NODE_BYTES;
		Store store = new MemoryStore(); 
		Map<K,V> initialValues = new HashMap<>();
		Serializer<K> keySerializer; 
		Serializer<V> valueSerializer;
		
		public TrieBuilder() {};
		
		public TrieBuilder<K,V> rootHash(ByteBuffer rootHash) {
			this.rootHash = rootHash;
			return this;
		}
		
		public TrieBuilder<K,V> store(Store store) {
			this.store = store;
			return this;
		}
		
		public TrieBuilder<K,V> keySerializer(Serializer<K> keySerializer) {
			this.keySerializer = keySerializer;
			return this;
		}
		
		public TrieBuilder<K,V> valueSerializer(Serializer<V> valueSerializer) {
			this.valueSerializer = valueSerializer;
			return this;
		}
		
		public TrieBuilder<K,V> from(Map<K,V> values) {
			this.initialValues = values;
			return this;
		}
		
		public Trie<K,V> build() {
			if (keySerializer == null || valueSerializer == null) 
				throw new AssertionError("Need to set serializers");
			
			if (rootHash != EMPTY_NODE_BYTES && !initialValues.isEmpty())
				throw new AssertionError("Can not have initial values in non-empty trie");
			
			Trie<K,V> trie = new Trie<K,V>(rootHash, store, keySerializer, valueSerializer);
			if (rootHash != EMPTY_NODE_BYTES) return trie;
			
			// populate initial values
			Trie<K,V> temp = new Trie<K,V>(rootHash, new MemoryStore(), keySerializer, valueSerializer);
			for (Map.Entry<K, V> e : initialValues.entrySet()) {
				temp.put(e.getKey(), e.getValue());
			}
			ByteBuffer rootHash = temp.getRootHash();
			TrieNode rootNode = temp.decodeToNode(rootHash, true);
			
			List<TrieNode> nodes = temp.nodes();
			for (TrieNode node : nodes) 
				trie.encodeNode(node, rootNode.equals(node));
			
			if (trie.store.commit()) {
				trie.rootHash = rootHash;
				trie.rootNode = rootNode;
				return trie;
			}
			
			throw new AssertionError("Could not commit initial values");
			
		}
	}
	
	private Trie(
			ByteBuffer rootHash, 
			Store store, 
			Serializer<K> keySerializer, 
			Serializer<V> valueSerializer) {
		
		this.store = store; // decodeToNode needs store to be initialized first
		this.rootHash = rootHash;
		this.rootNode = decodeToNode(rootHash, true);
		this.keySerializer = keySerializer;
		this.valueSerializer =valueSerializer;
	}
	
	public V get(K key) {
		return valueSerializer.deserialize(
				getHelper(rootNode, from(keySerializer.serialize(key))));
	}
	
	public V get(ByteBuffer rootHash, K key) {
		return valueSerializer.deserialize(
				getHelper(decodeToNode(rootHash, true), from(keySerializer.serialize(key))));
	}

	public ByteBuffer put(K key, V value) {
		return update(keySerializer.serialize(key), valueSerializer.serialize(value));
	}

	public ByteBuffer getRootHash() {
		return this.rootHash;
	}
	
	protected Store getStore() {
		return this.store;
	}
	
	protected List<TrieNode> nodes() {
		return nodes(this.rootHash);
	}
	
	private List<TrieNode> nodes(ByteBuffer bytes) {
		List<TrieNode> results = new ArrayList<>();
		nodesHelper(decodeToNode(bytes, true), new ArrayList<Byte>(), results);
		return results;
	}
	
	/**
	 * Retrieve a list of all reachable nodes from the given starting point
	 * 
	 * @param node stating root node
	 * @param path current path
	 * @param list result list
	 */
	private void nodesHelper(TrieNode node, List<Byte> path, List<TrieNode> list) {
		
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK) 
			return;
		
		list.add(node);
		
		int len = path.size();
		if (type.isKeyValueType() && !isTerminal(node.getItem(0).asReadOnlyByteBuffer())) {
			
			NibbleString key = unpack(node.getItem(0).asReadOnlyByteBuffer());
			for (Byte b : key) path.add(b);
			
			nodesHelper(decodeToNode(node.getItem(1).asReadOnlyByteBuffer()), path, list);
			
			len = path.size() - len; // number of nibbles to remove
			for (int i=0; i<len; i++) path.remove(path.size()-1);
			
		} else if (type == NodeType.BRANCH) {
			
			for (int i=0; i<16; i++) {
				ByteBuffer bytes = node.getItem(i).asReadOnlyByteBuffer();
				if (EMPTY.equals(bytes)) continue;
				
				try {
					// get rid of nested nodes in branch nodes
					TrieNode childNode = TrieNode.parseFrom(bytes);
					if (NodeType.HASH != getNodeType(childNode)) continue; 
					
					path.add((byte) i);
					nodesHelper(decodeToNode(node.getItem(i).asReadOnlyByteBuffer()), path, list);
					path.remove(path.size()-1);
					
				} catch (InvalidProtocolBufferException e) {
					throw new AssertionError("Should never be here");
				}
			}
		}
	}

	public Map<K,V> toMap() {
		return toMap(this.rootHash);
	}
	
	public Map<K,V> toMap(ByteBuffer rootHash) {
		Map<K,V> results = new HashMap<>();
		toMapHelper(decodeToNode(rootHash, true), new ArrayList<Byte>(), results);
		return results;
	}
	
	private void toMapHelper(TrieNode node, List<Byte> path, Map<K,V> map) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK) 
			return;
		
		int len = path.size();
		if (type.isKeyValueType()) {
			
			NibbleString key = unpack(node.getItem(0).asReadOnlyByteBuffer());
			for (Byte b : key) path.add(b);
			
			if (isTerminal(node.getItem(0).asReadOnlyByteBuffer())) {
				map.put(keySerializer.deserialize(toByteBuffer(path)), 
						valueSerializer.deserialize(node.getItem(1).asReadOnlyByteBuffer()));
				
			} else {
				toMapHelper(decodeToNode(node.getItem(1).asReadOnlyByteBuffer()), path, map);
			}
			
			len = path.size() - len; // number of nibbles to remove
			for (int i=0; i<len; i++) path.remove(path.size()-1);
			
		} else if (type == NodeType.BRANCH) {
			
			if (!EMPTY.equals(node.getItem(16).asReadOnlyByteBuffer())) {
				map.put(keySerializer.deserialize(toByteBuffer(path)), 
						valueSerializer.deserialize(node.getItem(16).asReadOnlyByteBuffer()));
			}
			
			for (int i=0; i<16; i++) {
				if (!EMPTY.equals(node.getItem(i).asReadOnlyByteBuffer())) {
					path.add((byte) i);
					toMapHelper(decodeToNode(node.getItem(i).asReadOnlyByteBuffer()), path, map);
					path.remove(path.size()-1);
				}
			}
		}
		
	}
	
	private ByteBuffer update(ByteBuffer key, ByteBuffer value) {
		TrieNode newRootNode = updateHelper(rootNode, from(key), value);
		ByteBuffer newRootHash = encodeNode(newRootNode, true);
		
		if (store.commit()) {
			this.rootNode = newRootNode;
			this.rootHash = newRootHash;
		} else {
			store.rollback();
		}
		
		return this.rootHash;
	}
	
	/**
	 * Key point to keep in mind with this helper is that 
	 * new nodes returned from self-recursion are persisted by current invocation
	 * but returned nodes are persisted by the parent.
	 * 
	 * @param node TrieNode format
	 * @param path NibbleString (not compact)
	 * @param value Value to be inserted
	 * 
	 * @return the new version of self node
	 */
	private TrieNode updateHelper(TrieNode node, NibbleString path, ByteBuffer value) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK) {
			return TrieNode.newBuilder()
					.addItem(ByteString.copyFrom(pack(path, true)))
					.addItem(ByteString.copyFrom(value))
					.build();
		
		} else if (type == NodeType.BRANCH) {
			
			TrieNode.Builder builder = TrieNode.newBuilder(node); 
			
			if (path.size() == 0)
				builder.setItem(16, ByteString.copyFrom(value));
			else {
				int keyIndex = path.nibbleAsByte(0);
				TrieNode newNode = decodeToNode(node.getItem(keyIndex).asReadOnlyByteBuffer());
				newNode = updateHelper(newNode, path.substring(1), value);
				builder.setItem(keyIndex, ByteString.copyFrom(encodeNode(newNode)));
			}
			return builder.build();
			
		} else if (type.isKeyValueType()) {
			return updateKeyValueHelper(node, path, value);
		}
		
		throw new AssertionError("Not possible");
	}
	
	private TrieNode updateKeyValueHelper(TrieNode node, NibbleString path, ByteBuffer value) {
		NodeType type = getNodeType(node);
		NibbleString key = unpack(node.getItem(0).asReadOnlyByteBuffer());
		
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
				return TrieNode.newBuilder(node).setItem(1, ByteString.copyFrom(value)).build();
			} else {
				newNode = updateHelper(decodeToNode(node.getItem(1).asReadOnlyByteBuffer()), remainingPath, value);
			}
		
		
		} else if (remainingKey.size() == 0) {
			if (type == NodeType.EXTENSION) {
				newNode = updateHelper(decodeToNode(node.getItem(1).asReadOnlyByteBuffer()), remainingPath, value);
			} else {
				
				TrieNode leaf = TrieNode.newBuilder()
						.addItem(ByteString.copyFrom(pack(remainingPath.substring(1), true)))
						.addItem(ByteString.copyFrom(value))
						.build();
				ByteBuffer leafEncoded = encodeNode(leaf);
				
				newNode = TrieNode.newBuilder(BRANCH_NODE_PROTOTYPE)
						.setItem(remainingPath.nibbleAsByte(0), ByteString.copyFrom(leafEncoded))
						.setItem(16, node.getItem(1))
						.build();
			}
		
		} else {
			TrieNode.Builder builder = TrieNode.newBuilder(BRANCH_NODE_PROTOTYPE);
			
			if (remainingKey.size() == 1 && type == NodeType.EXTENSION) {
				builder.setItem(remainingKey.nibbleAsByte(0), node.getItem(1));
			} else {
				ByteBuffer packedChildKey = pack(
						remainingKey.substring(1), 
						type == NodeType.LEAF);
				
				TrieNode child = TrieNode.newBuilder()
						.addItem(ByteString.copyFrom(packedChildKey))
						.addItem(node.getItem(1))
						.build();
				
				builder.setItem(remainingKey.nibbleAsByte(0), ByteString.copyFrom(encodeNode(child)));
			}
			
			if (remainingPath.size() == 0) {
				builder.setItem(16, ByteString.copyFrom(value));
			} else {
				ByteBuffer packedRemainingPath = pack(remainingPath.substring(1), true);
				
				TrieNode leaf = TrieNode.newBuilder()
						.addItem(ByteString.copyFrom(packedRemainingPath))
						.addItem(ByteString.copyFrom(value))
						.build();
				
				builder.setItem(remainingPath.nibbleAsByte(0), ByteString.copyFrom(encodeNode(leaf)));
			}
			
			newNode = builder.build();
		}
		
		if (prefixLength > 0) {
			return TrieNode.newBuilder()
					.addItem(ByteString.copyFrom(pack(key.substring(0, prefixLength), false)))
					.addItem(ByteString.copyFrom(encodeNode(newNode)))
					.build();
		} else {
			return newNode;
		}
	}
	
	private ByteBuffer encodeNode(TrieNode node) {
		return encodeNode(node, false);
	}
	
	/**
	 * Encodes a given node into a ByteString using Protocol Buffers. 
	 * returns the resulting ByteString if length <= 34, else stores it and returns 
	 * its hash encoded in a hash node.
	 * 
	 * Only exception to this rule is, if the node to be encoded is the root node, 
	 * in which case, a raw hash is generated regardless of length
	 * 
	 * Returned ByteBuffer limit is always <= 34.
	 * 
	 * @param node
	 * @return
	 */
	private ByteBuffer encodeNode(TrieNode node, boolean hash32Bytes) {
		
		if (EMPTY_NODE.equals(node)) return EMPTY_NODE_BYTES;
		ByteBuffer encoded = ByteBuffer.wrap(node.toByteArray());
		if (encoded.limit() < 34 && !hash32Bytes) return encoded;
		else {
			ByteBuffer hash = sha256(encoded);
			ByteBuffer hashNode = ByteBuffer.wrap(
					TrieNode.newBuilder()
						// careful copyFrom changes buffer position hence the use of .array()
						.addItem(ByteString.copyFrom(hash.array())) 
						.build()
						.toByteArray());
			store.put(hash, encoded);
			
			return hash32Bytes ? hash : hashNode;
		}
	}
	
	private TrieNode decodeToNode(ByteBuffer bytes) {
		return decodeToNode(bytes, false);
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
	 * Note that even though HashNodes themselves are 34 bytes long, every node is still 
	 * keyed with actual hash (32 bytes)
	 * 
	 * take a look at https://developers.google.com/protocol-buffers/docs/encoding
	 * 
	 * @param bytes
	 * @return
	 */
	private TrieNode decodeToNode(ByteBuffer bytes, boolean hash32Bytes) {
		
		if (EMPTY_NODE_BYTES.equals(bytes)) return EMPTY_NODE;
		
		try {
			if (hash32Bytes) 
				return TrieNode.parseFrom(store.get(bytes));
			
			TrieNode node = TrieNode.parseFrom(bytes);
			if (NodeType.HASH == getNodeType(node)) 
				return TrieNode.parseFrom(store.get(node.getItem(0).asReadOnlyByteBuffer()));
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
	private ByteBuffer getHelper(TrieNode node, NibbleString path) {
		NodeType type = getNodeType(node);
		
		if (type == NodeType.BLANK)
			return EMPTY_NODE_BYTES;
		
		if (type == NodeType.BRANCH) {
			if (path.size() == 0)
				return node.getItem(16).asReadOnlyByteBuffer();
			
			ByteBuffer subNodeBytes = node.getItem(path.nibbleAsByte(0)).asReadOnlyByteBuffer();
			return getHelper(decodeToNode(subNodeBytes), path.substring(1));
		}
		
		NibbleString key = unpack(node.getItem(0).asReadOnlyByteBuffer());
		if (type == NodeType.LEAF) {
			return path.equals(key) ? node.getItem(1).asReadOnlyByteBuffer() : EMPTY_NODE_BYTES;
		}
		
		if (type == NodeType.EXTENSION) {
			if (key.equals(path.substring(0, key.size())))
				return getHelper(decodeToNode(node.getItem(1).asReadOnlyByteBuffer()), path.substring(key.size()));
			else
				return EMPTY_NODE_BYTES;
		}
		
		throw new AssertionError("Not possible");
	}

	/**
	 * Given an old root hash, finds the differences between the two states
	 *
	 * @param oldRoot an older rootHash
	 * @param remove keys that have been changed and their old values
	 * @param update keys in currentRoot that either don't exist in oldRoot, or has a different value in currentRoot.
	 */
	public void difference(ByteBuffer oldRoot, Map<K,V> remove, Map<K,V> update) {
		PriorityQueue<Pair> pqOld = new PriorityQueue<>();
		PriorityQueue<Pair> pqNew = new PriorityQueue<>();

		pqOld.offer(new Pair(Collections.emptyList(), decodeToNode(oldRoot, true)));
		pqNew.offer(new Pair(Collections.emptyList(), decodeToNode(this.rootHash, true)));

		while (!pqOld.isEmpty() || !pqNew.isEmpty()) {
			Pair pOld = pqOld.isEmpty() ? Pair.DUMMY : pqOld.poll();
			Pair pNew = pqNew.isEmpty() ? Pair.DUMMY : pqNew.poll();

			int compare = pOld.compareTo(pNew);
			if (compare < 0) {
				if (pNew != Pair.DUMMY) pqNew.offer(pNew);

				ByteBuffer value = getImmediateValueOfNode(pOld.node);
				if (!EMPTY.equals(value)) {
					remove.put(keySerializer.deserialize(toByteBuffer(pOld.path)),
							valueSerializer.deserialize(value));
				}
				enqueueChildren(pqNew, pNew.path, pNew.node);

			} else  if (compare > 0) {
				if (pOld != Pair.DUMMY) pqOld.offer(pOld);

				ByteBuffer value = getImmediateValueOfNode(pNew.node);
				if (!EMPTY.equals(value)) {
					update.put(keySerializer.deserialize(toByteBuffer(pNew.path)),
							valueSerializer.deserialize(value));
				}
				enqueueChildren(pqNew, pNew.path, pNew.node);

			} else { // pOld.equals(pNew)
				if (pOld.node.equals(pNew.node)) continue; // both path and nodes are same (best case)

				ByteBuffer value1 = getImmediateValueOfNode(pOld.node);
				ByteBuffer value2 = getImmediateValueOfNode(pNew.node);

				if (!value1.equals(value2)) {
					if (!EMPTY.equals(value1)) {
						remove.put(keySerializer.deserialize(toByteBuffer(pOld.path)), valueSerializer.deserialize(value1));
					}
					if (!EMPTY.equals(value2)) {
						update.put(keySerializer.deserialize(toByteBuffer(pNew.path)), valueSerializer.deserialize(value2));
					}
				}

				// enqueue children
				enqueueChildren(pqOld, pOld.path, pOld.node);
				enqueueChildren(pqNew, pNew.path, pNew.node);
			}
		}
	}

	private void enqueueChildren(PriorityQueue<Pair> pq, List<Byte> path, TrieNode node) {
		NodeType nodeType = getNodeType(node);
		if (nodeType.isKeyValueType()) {
			NibbleString key = NibbleString.unpack(node.getItem(0).asReadOnlyByteBuffer());
			ByteBuffer value = node.getItem(1).asReadOnlyByteBuffer();

			if (isTerminal(node.getItem(0).asReadOnlyByteBuffer())) {
				if (!EMPTY_NIBBLE.equals(key)) {
					// a terminal node with some additional key, normalize and requeue
					List<Byte> newPath = new ArrayList<>(path);
					for (Byte b : key) newPath.add(b);

					TrieNode normalizedNode = TrieNode.newBuilder()
							.addItem(ByteString.copyFrom(pack(EMPTY_NIBBLE, true)))
							.addItem(ByteString.copyFrom(value))
							.build();

					pq.offer(new Pair(newPath, normalizedNode));
				}
			} else {
				// extension node (add key to path and queue child node)
				List<Byte> newPath = new ArrayList<>(path);
				for (Byte b : key) newPath.add(b);
				pq.offer(new Pair(newPath, decodeToNode(value)));
			}
		} else if (nodeType == NodeType.BRANCH) {
			// branch node
			// traverse and queue children
			for (int i = 0; i < 16; i++) {
				if (EMPTY.equals(node.getItem(i).asReadOnlyByteBuffer())) continue;
				List<Byte> newPath = new ArrayList<>(path);
				newPath.add((byte) i);
				pq.offer(new Pair(newPath, decodeToNode(node.getItem(i).asReadOnlyByteBuffer())));
			}
		}
	}

	/**
	 * Returns the immediate value associated with given node if
	 *  * node is a leaf node with no key extension (key: 20)
	 *  * node is a branch node with a value
	 *
	 * @param node
	 * @return empty bytebuffer otherwise
	 */
	private ByteBuffer getImmediateValueOfNode(TrieNode node) {
		NodeType nodeType = getNodeType(node);
		if (nodeType.isKeyValueType()) { // leaf node with no key extension
			NibbleString key = NibbleString.unpack(node.getItem(0).asReadOnlyByteBuffer());
			if (isTerminal(node.getItem(0).asReadOnlyByteBuffer()) && EMPTY_NIBBLE.equals(key)) {
				return node.getItem(1).asReadOnlyByteBuffer();
			}
		} else if (nodeType == NodeType.BRANCH) {
			return node.getItem(16).asReadOnlyByteBuffer();
		}
		return EMPTY;
	}


}
