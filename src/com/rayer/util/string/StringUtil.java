package com.rayer.util.string;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.rayer.util.stream.StreamUtil;

public class StringUtil {

	public static String formStringByToken(String inFormat, Object... objList) {
		
		//Actually, use %s only, as token of String.
		//As long as only %s was used, use object.toString() is sufficient.
		
		StringBuilder sb = processStringToken(inFormat, objList);
		
		return sb.toString();
	}

	private static StringBuilder processStringToken(String inFormat,
			Object... objList) {
		String[] spilted = inFormat.split("%s");
		StringBuilder sb = new StringBuilder();
		
		for(int counter = 0; counter < objList.length; ++counter) {
			sb.append(spilted[counter]);
			sb.append(objList[counter].toString());
		}
		return sb;
	}

	/**
	 * @deprecated
	 * @param filePath
	 * @param content
	 * @throws IOException 
	 * @throws  
	 */
	@Deprecated
	public static void stringToFile(String filePath, String content) throws IOException {
		stringToFile(new File(filePath), content);
	}
	
	/**
	 * @deprecated
	 * @param filePath
	 * @param content
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void stringToFile(File file, String content) throws FileNotFoundException, IOException {
		stringToStream(content, new FileOutputStream(file));
	}
	
	public static void stringToStream(String content, FileOutputStream fout) throws IOException {
		DataOutputStream dataout = new DataOutputStream(fout);
		byte[] data1 = content.getBytes("UTF-8");
		dataout.write(data1);
		fout.flush();
		fout.close();

	}
	
	public static String fromFile(String path) throws IOException {
		File file = new File(path);
		if(file.exists() == false)
			return null;
		
		return fromFile(file);
	}

	public static String fromFile(File file) throws IOException {
		return fromStream(new FileInputStream(file));
	}
	

	public static String fromStream(InputStream fis) throws IOException {
		return StreamUtil.InputStreamToString(fis);
	}
	
	public static String asHex (byte buf[]) 
	{
	      StringBuffer strbuf = new StringBuffer(buf.length * 2);
	      int i;

	      for (i = 0; i < buf.length; i++) 
	      {
	    	  if ((buf[i] & 0xff) < 0x10)
	    		  strbuf.append("0");

	    	  strbuf.append(Long.toString(buf[i] & 0xff, 16));
	      }

	      return strbuf.toString();
	}
	
	public static String getMD5(String input) {
		String res = "";
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(input.getBytes());
			byte[] md5 = algorithm.digest();
			String tmp = "";
			for (int i = 0; i < md5.length; i++) {
				tmp = (Integer.toHexString(0xFF & md5[i]));
				if (tmp.length() == 1) {
					res += "0" + tmp;
				} else {
					res += tmp;
				}
			}
		} catch (NoSuchAlgorithmException ex) {
		}
		return res;
	}




	

}
