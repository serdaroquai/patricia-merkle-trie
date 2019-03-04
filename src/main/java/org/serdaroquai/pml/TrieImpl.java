package org.serdaroquai.pml;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.io.BaseEncoding;

public class TrieImpl implements Trie{

	Node root;
	byte[] rootHash;
	Store store;
	
	public TrieImpl(Store store) {
		this.store = store;
		this.root = Node.EMPTY_NODE;
		this.rootHash = Util.sha256(root);
	}
	
	public TrieImpl(Store store, byte[] rootHash) {
		this.store = store;
		this.rootHash = rootHash;
		this.root = store.get(rootHash);
	}

	private static class Key {
		private String hexKey;
		private int pos;
		public Key(byte[] bytes) {
			this.hexKey = BaseEncoding.base16().encode(bytes);
			this.pos = 0;
		}
		
		boolean didResolve() { return pos >= hexKey.length();}
		char currentPos() { return hexKey.charAt(pos);}
		int currentPosAsIndex() {
			char c = hexKey.charAt(pos);
			if (Character.isDigit(c)) return c - '0';
			else return c - 55; // A == 10
		}
		boolean increment() { pos++; return didResolve(); }
	}
	
	private static class Result {
		Deque<Node> route = new ArrayDeque<>();
		String result;
		
		public void trace() {
			while (!route.isEmpty()) System.out.println(route.pollFirst());
			System.out.println("Result: " + result);
		}
	}
	
	@Override
	public String get(byte[] key) {
		Result r = new Result();
		internalGet(new Key(key), root, r);
		r.trace();
		return r.result;
	}
	
	private void internalGet(Key k, Node n, Result r) {
		if (n == null) return;
		r.route.offerLast(n);
		if (k.didResolve()) {
			r.result = n.getValue();
			return;
		}
		
		if (n instanceof BranchNode) {
			BranchNode branch = (BranchNode) n;
			int c = k.currentPosAsIndex();
			k.increment();
			if (branch.arr[c] == null) return; // not found
			else if (branch.arr[c].length() < 32 && k.didResolve()) {
				r.result = branch.arr[c];
				return;
			} else {
				internalGet(k, store.get(BaseEncoding.base16().decode(branch.arr[c])), r);
				return;
			}
		} else if (n instanceof ExtensionNode) {
			ExtensionNode ext = (ExtensionNode) n;
			int p = 0;
			while (!k.didResolve() && p < ext.partialKey.length()) {
				if (k.currentPos() != ext.partialKey.charAt(p)) break;
				k.increment(); p++;
			}
			if (k.didResolve() && p == ext.partialKey.length()) {
				// match
				if (n.getValue().length() < 32) {
					r.result = n.getValue();					
				} else {
					internalGet(k, store.get(BaseEncoding.base16().decode(n.getValue())), r);
				}
				return;
			} else if (k.didResolve() && p < ext.partialKey.length()) {
				// key is shorter (aka not found)
				return;
			} else if (p == ext.partialKey.length()) {
				// next node pls
				internalGet(k, store.get(BaseEncoding.base16().decode(n.getValue())), r);
			}
		}
	}

	@Override
	public byte[] put(byte[] key, String value) {
		String hexKey = BaseEncoding.base16().encode(key);
		
		if (root == Node.EMPTY_NODE) {
			root = new ExtensionNode.Builder().withPartialKey(hexKey).withValue(value).build();
			rootHash = Util.sha256(root);
			return rootHash;
		}
		
		char[] newKey = hexKey.toCharArray();
		int i=0; // index on new key
		
		Node node = root;
		
		while (node != null) {
			if (node instanceof ExtensionNode) {
				int p=0;
				String partial = ((ExtensionNode) node).getPartialKey();
				while (i < newKey.length && p < partial.length()) {
					if (newKey[i] == partial.charAt(p)) {
						i++; p++;
					}
				}
				
				if (i == newKey.length && p == partial.length()) {
					/* 
					 *  perfect match, three possibilities
					 *  1) value is < 32 bytes (just replace it)
					 *  2) value is >= 32 bytes (it is a reference)
					 *  	2.1) reference to a value node (just create a value node and replace it)
					 *  	2.2) reference to a branch node ( go replace branch nodes value)
					 */
				}
				else if (i == newKey.length && p != partial.length() ) {
					/*
					 *  we need to convert this extension node into a shorter one
					 *  create a branch node at i 
					 */
				}
					
			}
		}
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean remove(byte[] key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] getRootHash() {
		return rootHash;
	}
	
}
