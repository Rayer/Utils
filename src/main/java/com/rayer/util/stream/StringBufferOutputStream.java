package com.rayer.util.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Not finished yet
 * @deprecated
 * @author rayer
 *
 */
public class StringBufferOutputStream extends BufferedOutputStream {

	public StringBufferOutputStream(OutputStream arg0) {
		super(arg0);
	}
	
	public StringBufferOutputStream(OutputStream arg0, int arg1) {
		super(arg0, arg1);
	}
	
	public void setEncoding(String encoding) {
		_encoding = encoding;
	}

	@Override
	public synchronized void write(byte[] b, int off, int len)
			throws IOException {
		_sb.append(new String(b, _encoding));
		
		super.write(b, off, len);
	}

	@Override
	public synchronized void write(int b) throws IOException {
		//.........how to?
		//hopefully it will write to buffer, and we can capture it at wrhte(byte[] b, int off, int len)
		super.write(b);
	}

	StringBuffer _sb = new StringBuffer();
	String _encoding = "UTF-8";
	
	StringBuffer getStreamBuffer() {
		return _sb;
	}

}
