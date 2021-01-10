package nl.vincentvanderleun.dos.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DosBinaryFileReader {
	private final static Charset DOS_CHARSET = StandardCharsets.US_ASCII;
	private final static int MAX_BLOCK_SIZE = 1024 * 16;
	
	private final InputStream inputStream;
	
	public DosBinaryFileReader(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean readBoolean() throws IOException {
		return readUnsignedByte() != 0;
	}

    public short readInt() throws IOException {
        byte[] signedIntBytes = new byte[2];

        inputStream.read(signedIntBytes);
        
        return ByteBuffer.wrap(signedIntBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
	
	public int readUnsignedByte() throws IOException {
		byte[] unsignedByte = new byte[1];
		
		readBytes(unsignedByte);
		
		return Byte.toUnsignedInt(unsignedByte[0]);
	}

	public int readWord() throws IOException {
		byte[] unsignedIntBytes = new byte[2];
		
		readBytes(unsignedIntBytes);
		
		return (Byte.toUnsignedInt(unsignedIntBytes[1]) << 8) |
				Byte.toUnsignedInt(unsignedIntBytes[0]);
	}
	
	public long readDoubleWord() throws IOException {
		byte[] doubleWordBytes = new byte[4];
		
		readBytes(doubleWordBytes);
		
		return ((long)Byte.toUnsignedInt(doubleWordBytes[3]) << 24) |
				(Byte.toUnsignedInt(doubleWordBytes[2]) << 16) |
				(Byte.toUnsignedInt(doubleWordBytes[1]) << 8) |
				Byte.toUnsignedInt(doubleWordBytes[0]);
	}

    public float readFloat() throws IOException {
        byte[] floatBytes = new byte[4];
        
        inputStream.read(floatBytes);
        
        return ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
	
	public String readZeroTerminatedAsciiString(int len) throws IOException {
		byte[] stringBytes = new byte[len];
		
		readBytes(stringBytes);
		
		int zeroByteIndex = -1;
		for (int i = 0; i < len; i++) {
			if (stringBytes[i] == 0) {
				zeroByteIndex = i;
				break;
			}
		}
		
		if (zeroByteIndex < 0) {
			throw new IllegalStateException("No zero byte encountered in zero-terminated string");
		}
		
		return new String(Arrays.copyOf(stringBytes, zeroByteIndex), DOS_CHARSET);
	}
	
	public String readAsciiString(int len) throws IOException {
		byte[] stringBytes = new byte[len];
		
		readBytes(stringBytes);
		
		return new String(stringBytes, DOS_CHARSET);
	}
	
	public void skipBytes(long value) throws IOException {
		// Work around issues in InputStream.skip() method that does not always skip the 
		// exact requested amount of bytes. 
		long bytesRead = 0;

		byte[] blockBytes = new byte[0];
		
		while(bytesRead < value) {
			final long diff = value - bytesRead;
			final int block = diff > MAX_BLOCK_SIZE ? MAX_BLOCK_SIZE : (int)diff;
			
			if(blockBytes.length != block) {
				blockBytes = new byte[block];
			}
			readBytes(blockBytes);
			
			bytesRead += block;
		}
	}
	
	private void readBytes(byte[] bytes) throws IOException {
		int value = inputStream.read(bytes);
		
		if(value < 0) {
			throw new IOException("Reached EOF");
		}
	}
}
