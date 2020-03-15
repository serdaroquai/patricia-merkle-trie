package org.serdaroquai.pml;

import com.google.protobuf.ByteString;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class TrieNodeTest {

    /**
     * Simple test demonstrating protobuff nodes equality
     */
    @Test
    public void testNodeEquality() {
        NodeProto.TrieNode node1 = NodeProto.TrieNode.newBuilder().addItem(ByteString.copyFrom(new byte[]{1})).build();
        NodeProto.TrieNode node1prime = NodeProto.TrieNode.newBuilder().addItem(ByteString.copyFrom(new byte[]{1})).build();
        NodeProto.TrieNode node2 = NodeProto.TrieNode.newBuilder().addItem(ByteString.copyFrom(new byte[]{2})).build();

        assertNotEquals(node1, node2);
        assertEquals(node1, node1prime);
    }


    /**
     * Simple test demonstrating ByteBuffer equality.
     * In other words node equality when nodes are encoded.
     */
    @Test
    public void testNodeEqualityEncoded() {
        ByteBuffer b1 = ByteBuffer.wrap(new byte[]{1});
        ByteBuffer b1Prime = ByteBuffer.wrap(new byte[]{1});
        ByteBuffer b2 = ByteBuffer.wrap(new byte[]{2});

        assertNotEquals(b1, b2);
        assertEquals(b1, b1Prime);
    }
}
