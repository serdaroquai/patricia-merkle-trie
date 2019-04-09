package org.serdaroquai.pml;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

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
	
	ByteBuffer serialize(T obj);
	T deserialize(ByteBuffer bytes);
	
	public static final Serializer<String> STRING_UTF8 = new Serializer<String>() {
		@Override
		public ByteBuffer serialize(String obj) { return ByteBuffer.wrap(obj.getBytes(StandardCharsets.UTF_8));}
		@Override
		public String deserialize(ByteBuffer bytes) {
			if (bytes.hasArray()) {
				return new String(bytes.array(), StandardCharsets.UTF_8);
			} else {
				// take the long way since bytebuffer is read only
				CharBuffer cb = StandardCharsets.UTF_8.decode(bytes);
				return cb.toString();
			}
		}	
	};
	
	/**
	 * TODO, test me
	 */
	public static final Serializer<Boolean> BOOLEAN = new Serializer<Boolean>() {
		private final ByteBuffer FALSE = ByteBuffer.wrap(new byte[] {(byte) 0x00});
		private final ByteBuffer TRUE = ByteBuffer.wrap(new byte[] {(byte) 0xFF});
		@Override
		public ByteBuffer serialize(Boolean obj) { return obj ? TRUE : FALSE;}
		@Override
		public Boolean deserialize(ByteBuffer bytes) { return TRUE.equals(bytes) ? true : false;}	
	};
	
	/**
	 * TODO, test me
	 */
	public static final Serializer<Long> INT64 = new Serializer<Long>() {
		@Override
		public ByteBuffer serialize(Long l) { 
			if (l == null) throw new AssertionError("Does not allow null values");
			byte[] result = new byte[Long.BYTES];
		    for (int i = 7; i >= 0; i--) {
		        result[i] = (byte)(l & 0xFF);
		        l >>= 8;
		    }
		    return ByteBuffer.wrap(result);
		}
		@Override
		public Long deserialize(ByteBuffer bytes) {
			if (Common.EMPTY.equals(bytes)) throw new AssertionError("Does not allow null values");
			long result = 0;
		    for (int i = 0; i < 8; i++) {
		        result <<= 8;
		        result |= (bytes.get(i) & 0xFF);
		    }
		    return result;
		}	
	};
	
}
