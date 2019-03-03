package org.serdaroquai.pml;

public interface Store {

	Node get(byte[] hash);
	void store(Node n);
}
