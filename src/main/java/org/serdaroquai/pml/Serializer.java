package org.serdaroquai.pml;

import com.google.protobuf.ByteString;

public interface Serializer<T> {
	
	ByteString serialize(T obj);
	T deserialize(ByteString bytes);
	
	public static final Serializer<String> STRING_UTF8 = new Serializer<String>() {
		@Override
		public ByteString serialize(String obj) { return ByteString.copyFromUtf8(obj);}
		@Override
		public String deserialize(ByteString bytes) { return bytes.toStringUtf8();}	
	};
	
}
