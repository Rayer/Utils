package com.rayer.util.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class StreamUtil {
	
	/**
	 * Code was gotten from http://www.kodejava.org/examples/266.html
	 * @param is InputStream which needed to be converted
	 * @return String convert from InputStream, or "" if error occurs.
	 * @throws IOException throw by BufferedReader.readLine
	 */
	public static String InputStreamToString(InputStream is) throws IOException {
		return InputStreamToString(is, false);
	}
	

	public static String InputStreamToString(InputStream is, boolean filterLastZeroPadding) throws IOException {
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
			is.close();
		}
		return sb.toString();
		} 
		else {        
			return "";
		}
	}
	

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		copyInputStream(in, out, 1024);
	}
	
	public static final void copyInputStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
	    int len;

	    while((len = in.read(buffer)) >= 0)
	      out.write(buffer, 0, len);

	    in.close();
	    out.close();
	    
	}
	
//	public static File InputStreamToFile(InputStream is, File f) {
//		FileOutputStream
//		return null;
//	}

}
