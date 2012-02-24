package com.rayer.util.drm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class Decryptable {
	/**
	 * AES Encrypt specified function.
	 * @return The specified AESKey, if return null, means no AES Key was used. 
	 */
	public abstract AESEncryptKeys getAESKeys();
	
	public InputStream createDecryptStream(File inFile) throws FileNotFoundException {
		
		if(getAESKeys() == null)
			return new FileInputStream(inFile);
		
		InputStream fis = new FileInputStream(inFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AESEncrypter encrypter = getAESKeys().getEncrypter(inFile.getName());
		encrypter.decrypt(fis, out);
		fis = new ByteArrayInputStream(out.toByteArray());
		return fis;
	}
	
	public InputStream createDecryptStream(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		return createDecryptStream(file);
	}

}
