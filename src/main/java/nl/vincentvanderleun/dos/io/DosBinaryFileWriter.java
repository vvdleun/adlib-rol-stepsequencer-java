package nl.vincentvanderleun.dos.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DosBinaryFileWriter {
	private final static Charset DOS_CHARSET = StandardCharsets.US_ASCII;

	private final OutputStream outputStream;
	
	public DosBinaryFileWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeBoolean(boolean value) throws IOException {
		byte bytes = value ? Byte.MIN_VALUE : 0;

		outputStream.write(bytes);
	}

    public void writeInt(int value) throws IOException {
    	writeInt((short)value);
    }
	
    public void writeInt(short value) throws IOException {
		byte[] bytes = new byte[2];

		bytes[0] = (byte)(value & 0xFF);
		bytes[1] = (byte)((value >> 8) & 0xFF);
		
		outputStream.write(bytes);
    }
	
	public void writeUnsignedByte(int value) throws IOException {
		byte[] bytes = new byte[1];

		bytes[0] = (byte)(value & 0xFF);
		
		outputStream.write(bytes);
	}

	public void writeWord(int value) throws IOException {
		byte[] bytes = new byte[2];

		bytes[0] = (byte)(value & 0xFF);
		bytes[1] = (byte)((value >> 8) & 0xFF);
		
		outputStream.write(bytes);
	}
	
	public void writeDoubleWord(long value) throws IOException {
		byte[] bytes = new byte[4];

		bytes[0] = (byte)(value & 0xFF);
		bytes[1] = (byte)((value >> 8) & 0xFF);
		bytes[2] = (byte)((value >> 16) & 0xFF);
		bytes[3] = (byte)((value >> 24) & 0xFF);

		outputStream.write(bytes);
	}

    public void writeFloat(float value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4)
        		.order(ByteOrder.LITTLE_ENDIAN)
        		.putFloat(value).array();

        outputStream.write(bytes);
    }
	
	public void writeZeroTerminatedAsciiString(String value, int len) throws IOException {
		if (value.length() >= len) {
			throw new IllegalStateException("String '" + value + "' is too long for ASCII zero-terminated string of " + len + " bytes");
		}
		
		byte[] sourceBytes = value.getBytes(DOS_CHARSET);
		byte[] destBytes = new byte[len];
		System.arraycopy(sourceBytes, 0, destBytes, 0, sourceBytes.length);
		
        outputStream.write(destBytes);
	}
	
	public void writeAsciiString(String value) throws IOException {
		byte[] bytes = value.getBytes(DOS_CHARSET);

		outputStream.write(bytes);
	}
	
	public void writeEmptyBytes(int len) throws IOException {
		// Work around issues in InputStream.skip() method that does not always skip the requested 
		// amount of bytes. This implementation wastes memory though, so only use it on smaller amount 
		// of bytes.
		byte[] bytes = new byte[len];

		outputStream.write(bytes);
	}
}
