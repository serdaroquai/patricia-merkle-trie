package org.serdaroquai.pml;

import com.google.protobuf.ByteString;

/**
 *  Serialization interface to define how to convert a given T type into ByteString.
 *  There are some built in common implementations for convenience.
 *  
 *  TODO: ByteString introduces an unnecessary level of byte[] copying.
 *  
 * @author tr1b6162
 *
 * @param <T>
 */
public interface Serializer<T> {
	
	ByteString serialize(T obj);
	T deserialize(ByteString bytes);
	
	public static final Serializer<String> STRING_UTF8 = new Serializer<String>() {
		@Override
		public ByteString serialize(String obj) { return ByteString.copyFromUtf8(obj);}
		@Override
		public String deserialize(ByteString bytes) { return bytes.toStringUtf8();}	
	};
	
	/**
	 * TODO, test me
	 */
	public static final Serializer<Boolean> BOOLEAN = new Serializer<Boolean>() {
		private final ByteString FALSE = ByteString.copyFrom(new byte[] {(byte) 0x00});
		private final ByteString TRUE = ByteString.copyFrom(new byte[] {(byte) 0xFF});
		@Override
		public ByteString serialize(Boolean obj) { return obj ? TRUE : FALSE;}
		@Override
		public Boolean deserialize(ByteString bytes) { return TRUE.equals(bytes) ? true : false;}	
	};
	
	/**
	 * TODO, test me
	 */
	public static final Serializer<Long> INT64 = new Serializer<Long>() {
		@Override
		public ByteString serialize(Long l) { 
			if (l == null) throw new AssertionError("Does not allow null values");
			byte[] result = new byte[Long.BYTES];
		    for (int i = 7; i >= 0; i--) {
		        result[i] = (byte)(l & 0xFF);
		        l >>= 8;
		    }
		    return ByteString.copyFrom(result);
		}
		@Override
		public Long deserialize(ByteString bytes) {
			if (ByteString.EMPTY.equals(bytes)) throw new AssertionError("Does not allow null values");
			long result = 0;
		    for (int i = 0; i < 8; i++) {
		        result <<= 8;
		        result |= (bytes.byteAt(i) & 0xFF);
		    }
		    return result;
		}	
	};
	
}
