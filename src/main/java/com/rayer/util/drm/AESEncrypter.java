package com.rayer.util.drm;


import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class AESEncrypter implements EncrypterBase
{
	
	public enum DRM_TYPE {
		USER_DRM,
		PRELOAD_DRM,
		EPUB_COMPONENT_DRM,
		NO_DRM
	}

	Cipher ecipher;
	Cipher dcipher;
	
	SecretKey key;
	byte[] keyBytes;
	
	public AESEncrypter(byte[] keyBytes, byte[] ivBytes)
	{
		key = new SecretKeySpec(keyBytes, "AES");
		this.keyBytes = keyBytes;
		
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivBytes);
		try
		{
			ecipher = Cipher.getInstance("AES/CBC/NoPadding");
			dcipher = Cipher.getInstance("AES/CBC/NoPadding");
			
			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
		
	private byte[] asBin(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
		int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
		int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);

		encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	} 
	
	private String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if ((buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString(buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }
	
	/* (non-Javadoc)
	 * @see com.she.hami.reader.encryption.Encrypter#encrypt(java.lang.String)
	 */
	@Override
	public String encrypt(String plainText) {
		byte[] cipherText = null;
		try {
			cipherText = ecipher.doFinal(plainText.getBytes()); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(cipherText == null)
			return null;
		else
			return asHex(cipherText);
	}
	
	/* (non-Javadoc)
	 * @see com.she.hami.reader.encryption.Encrypter#decrypt(java.lang.String)
	 */
	@Override
	public String decrypt(String encryptedText) {
		byte[] plainText = null;
		try {
			plainText = dcipher.doFinal(asBin(encryptedText));
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(plainText == null)
			return null;
		else
			return new String(plainText);
	}
	
	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[1024];
	
	/* (non-Javadoc)
	 * @see com.she.hami.reader.encryption.Encrypter#encrypt(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void encrypt(InputStream in, OutputStream out)
	{
		try
		{
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, ecipher);
			
			// Read in the cleartext bytes and write to out to encrypt
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0)
			{
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e)
		{
		}
	}
	
	/* (non-Javadoc)
	 * @see com.she.hami.reader.encryption.Encrypter#decrypt(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void decrypt(InputStream in, OutputStream out)
	{

		try
		{
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);
			
			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0)
			{
			
				out.write(buf, 0, numRead);
			}
			
			out.write(dcipher.doFinal());
			out.close();
		}
		catch (OutOfMemoryError ome)
		{
			ome.printStackTrace();
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}


}