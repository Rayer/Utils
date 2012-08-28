package com.rayer.util.drm;

import java.io.InputStream;
import java.io.OutputStream;

public interface EncrypterBase {

	public abstract String encrypt(String plainText);
	public abstract String decrypt(String encryptedText);
	public abstract void encrypt(InputStream in, OutputStream out);
	public abstract void decrypt(InputStream in, OutputStream out);


}