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

	private final InputStream inputStream;
	
	public DosBinaryFileReader(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean readBoolean() throws IOException {
		return readUnsignedByte() != 0;
	}

    public short readInt() throws IOException {
        byte[] bits16 = new byte[2];
        inputStream.read(bits16);
        return ByteBuffer.wrap(bits16).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
	
	public int readUnsignedByte() throws IOException {
		byte[] bytes = new byte[1];
		readBytes(bytes);
		return Byte.toUnsignedInt(bytes[0]);
	}

	public int readWord() throws IOException {
		byte[] bytes = new byte[2];
		readBytes(bytes);
		return (Byte.toUnsignedInt(bytes[1]) << 8) |
				Byte.toUnsignedInt(bytes[0]);
	}
	
	public long readDoubleWord() throws IOException {
		byte[] bytes = new byte[4];
		readBytes(bytes);
		return ((long)Byte.toUnsignedInt(bytes[3]) << 24) |
				(Byte.toUnsignedInt(bytes[2]) << 16) |
				(Byte.toUnsignedInt(bytes[1]) << 8) |
				Byte.toUnsignedInt(bytes[0]);
	}

    public float readFloat() throws IOException {
        byte[] floatBytes = new byte[4];
        inputStream.read(floatBytes);
        return ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
	
	public String readZeroTerminatedAsciiString(int len) throws IOException {
		byte[] bytes = new byte[len];
		readBytes(bytes);
		
		int zeroByteIndex = -1;
		for (int i = 0; i < len; i++) {
			if (bytes[i] == 0) {
				zeroByteIndex = i;
				break;
			}
		}
		
		if (zeroByteIndex < 0) {
			throw new IllegalStateException("No zero byte encountered in zero-terminated string");
		}
		
		return new String(Arrays.copyOf(bytes, zeroByteIndex), DOS_CHARSET);
	}
	
	public String readAsciiString(int len) throws IOException {
		byte[] bytes = new byte[len];
		readBytes(bytes);
		return new String(bytes, DOS_CHARSET);
	}
	
	public void skipBytes(long value) throws IOException {
		// Work around issues in InputStream.skip() method that does not always skip the requested 
		// amount of bytes. 
		long bytesRead = 0;
		
		while(bytesRead < value) {
			long diff = value - bytesRead;
			int block = diff > 1024 ? 1024 : (int)diff;
			
			byte[] bytes = new byte[block];
			inputStream.read(bytes);
			
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
