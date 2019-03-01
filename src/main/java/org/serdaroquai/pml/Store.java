package org.serdaroquai.pml;

public interface Store {

	Node retrieve(byte[] hash);
	void store(Node n);
}
