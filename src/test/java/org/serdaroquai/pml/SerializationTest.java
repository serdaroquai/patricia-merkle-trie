package org.serdaroquai.pml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SerializationTest {

	
//	@Test
//	public void protobuff() throws InvalidProtocolBufferException {
////		System.out.println(ByteString.copyFromUtf8("BB").);
//		ExtensionNode e = NodeProto.ExtensionNode.newBuilder().setKey("AA").setValue(ByteString.copyFromUtf8("BB")).build();
//		Node node = NodeProto.Node.newBuilder().setE(e).build();
//		byte[] byteArray = node.toByteArray();
////		System.out.println(byteArray.length);
////		System.out.println("{\"k\":\"AA\",\"v\":\"BB\"}".getBytes().length);
//		Node node2 = NodeProto.Node.parseFrom(byteArray);
//		assertEquals(node, node2);
//		
//	}
	@Test
	public void testExtensionNodeSerialization() {
		ExtensionNode n = new ExtensionNode.Builder().withPartialKey("AA").withValue("BB").build();
		assertEquals("{\"k\":\"AA\",\"v\":\"BB\"}", n.getHashableString());
	}
	
	@Test
	public void testExtensionNodeDeserialization() {
		Node expected = Util.deserialize("{\"k\":\"AA\",\"v\":\"BB\"}");
		ExtensionNode n = new ExtensionNode.Builder().withPartialKey("AA").withValue("BB").build();
		assertEquals(expected, n);
	}
	
	@Test
	public void testExtensionNodeDeserializationNullValues() {
		Node expected = Util.deserialize("{\"k\":\"AA\",\"v\":null}");
		ExtensionNode n = new ExtensionNode.Builder().withPartialKey("AA").build();
		assertEquals(expected, n);
	}
	
	@Test
	public void testBranchNodeSerialization() {
		BranchNode n = new BranchNode.Builder()
				.withElement(0,"0").withElement(1,"1").withElement(3, "3")
				.withValue("V").build();
		assertEquals("[\"0\",\"1\",null,\"3\",null,null,null,null,null,null,null,null,null,null,null,null,\"V\"]", n.getHashableString());
	}
	
	@Test
	public void testBranchNodeDeserialization() {
		Node expected = Util.deserialize("[\"0\",\"1\",null,\"3\",null,null,null,null,null,null,null,null,null,null,null,null,\"V\"]");
		BranchNode n = new BranchNode.Builder()
				.withElement(0, "0").withElement(1, "1").withElement(3, "3")
				.withValue("V").build();
		assertEquals(expected, n);
	}
	
	@Test
	public void testEmptyNodeSerialize() {
		assertEquals("{}", Node.EMPTY_NODE.getHashableString());
	}
	
	@Test
	public void testEmptyNodeDeserialize() {
		Node expected = Node.EMPTY_NODE;
		Node empty = Util.deserialize("{}");
		assertEquals(expected, empty);
	}
}
