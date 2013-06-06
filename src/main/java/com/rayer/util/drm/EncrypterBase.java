package com.rayer.util.drm;

import java.io.InputStream;
import java.io.OutputStream;

public interface EncrypterBase {

	public abstract byte[] encrypt(byte[] plain);
	public abstract byte[] decrypt(byte[] encrypted);
	public abstract void encrypt(InputStream in, OutputStream out);
	public abstract void decrypt(InputStream in, OutputStream out);


}