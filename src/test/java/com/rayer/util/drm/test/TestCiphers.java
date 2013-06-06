package com.rayer.util.drm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.rayer.util.drm.AESEncrypter;
import com.rayer.util.drm.EncrypterBase;
import com.rayer.util.drm.RSAEncrypter;
import com.rayer.util.string.StringUtil;

public class TestCiphers {

	static final String TEST_STRING[] = {
		"This is a basic alphabet test",
		"這是一個中文測試文字",
		"This is 中文夾雜英文 Without control test",
		"This is acccdisk",
		"This is a 16 pad"
	};
	
	static final byte[] AES_IV = new byte[] { 'm', 'j', 'i', 'k', 'y', 'g', 'r', 'f', 'd', 'e', '6', 'y', 'u', '8', 'i', 'n' };

	
	@Test
	public void testAESEncrypt() {
		byte[] key = (StringUtil.asHex(new String("5k4g4u ek7AEShk4g4m/42k7KEY").getBytes()).toUpperCase().substring(0, 16)).getBytes();

		EncrypterBase encrypter = new AESEncrypter(key, AES_IV);
		doEncryptTest(encrypter);
		
	}
	
	@Test
	public void testRSAEncrypt() {
		EncrypterBase encrypter = RSAEncrypter.createRandomRSAEncrypter();
		doEncryptTest(encrypter);
	}


	private void doEncryptTest(EncrypterBase encrypter) {
		String[] encrypt_result = new String[TEST_STRING.length];
		String[] test_result = new String[TEST_STRING.length];
		
		System.out.println("START AES ENCRYPTER TEST");
		for(int i = 0; i < TEST_STRING.length; ++i) {
			encrypt_result[i] = new String(encrypter.encrypt(TEST_STRING[i].getBytes()));
		}
		
		for(int i = 0; i < TEST_STRING.length; ++i) {
			test_result[i] = new String(encrypter.decrypt(encrypt_result[i].getBytes()));
			System.out.println(new String(test_result[i]));
		}
		
		for(int i = 0; i < TEST_STRING.length; ++i) {
			if(TEST_STRING[i].equals(test_result[i]))
				continue;
			else
				fail();
		}
	}

}
