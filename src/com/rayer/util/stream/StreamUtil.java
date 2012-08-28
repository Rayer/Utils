package com.rayer.util.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * There are some utilities for Streaming.
 * @author rayer
 *
 */
public class StreamUtil {
	
	/**
	 * Code was gotten from http://www.kodejava.org/examples/266.html
	 * @param is InputStream which needed to be converted
	 * @return String convert from InputStream, or "" if error occurs.
	 * @throws IOException throw by BufferedReader.readLine
	 */
	public static String InputStreamToString(InputStream is) throws IOException {
		return InputStreamToString(is, false, true);
	}
	

	/**
	 * Another way to convert Stream to String.
	 * @param is
	 * @param filterLastZeroPadding
	 * @param closeStream
	 * @return
	 * @throws IOException
	 */
	public static String InputStreamToString(InputStream is, boolean filterLastZeroPadding, boolean closeStream) throws IOException {
		/*
		* To convert the InputStream to String we use the BufferedReader.readLine()
		* method. We iterate until the BufferedReader return null which means
		* there's no more data to read. Each line will appended to a StringBuilder
		* and returned as String.
		*/

		if (is != null) {
		StringBuilder sb = new StringBuilder();
		String line;
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			while ((line = reader.readLine()) != null) {
			
				if(filterLastZeroPadding == true) {
					int offset = line.indexOf(0);
					if(offset != -1)
						line = line.substring(0, offset);
				}
				sb.append(line).append("\n");
			}
			

		} finally {
			if(closeStream)
				is.close();
		}
		return sb.toString();
		} 
		else {        
			return "";
		}
	}
	
	/**
	 * Get a input stream and write to multiple output stream, very useful while need T-pipe
	 * @param bufferSize default is 1024
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static final void duplicateStream(int bufferSize, InputStream in, OutputStream... out) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int len;
		
	    while((len = in.read(buffer)) >= 0) {
	    	for(OutputStream o : out)
		      o.write(buffer, 0, len);
	    }
	    
	    for(OutputStream o : out) {
	    	o.flush();
	    	o.close();
	    }
	    
	    in.close();
		
	}
	
	/**
	 * Get a input stream and write to multiple output stream, very useful while need T-pipe
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static final void duplicateStream(InputStream in, OutputStream... out) throws IOException {
		duplicateStream(1024, in, out);
	}
	


	/**
	 * Directly adapt inputstream to outputstream
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		copyInputStream(1024, in, out);
	}
	

	/**
	 * Directly adapt inputstream to outputstream
	 * @param bufferSize
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static final void copyInputStream(int bufferSize, InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[bufferSize];
	    int len;

	    while((len = in.read(buffer)) >= 0)
	      out.write(buffer, 0, len);

	    
	    in.close();
	    out.flush();
	    out.close();
	    
	}


}
