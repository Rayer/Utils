package com.rayer.util.drm;

import java.security.MessageDigest;

import com.rayer.util.string.StringUtil;

/**
 * Use for {@link ComicView#Initialize(String, String, AESEncryptKeys)} only.<BR>
 * Provide decryption service for ComicView.<BR
 * If you passed null into ComicView.initialize, ComicView will act as no decryption.<BR>
 * @author rayer.tung
 */
public class AESEncryptKeys {
	
	String m_uuid;
	String m_msisdn;
	String m_magic;
	byte[] m_aesIv = new byte[] { 'm', 'j', 'i', 'k', 'y', 'g', 'r', 'f', 'd', 'e', '6', 'y', 'u', '8', 'i', 'n' };
	
	/** 
	 * @param uuid Part of encrypt keys, in android system, use IMEI instead.
	 * @param msisdn Part of encrypt keys, in HamiBook, use login account name instead
	 * @param magic Part of encrypt keys, provided in HamiBook global.
	 */
	public AESEncryptKeys(String uuid, String msisdn, String magic)
	{
		m_uuid = uuid;
		m_msisdn = msisdn;
		m_magic = magic;
	}
	
	//public final static String uuid 	= "12341234";
	//public final static String msisdn 	= "rayer@vista.aero";
	//public final static String magic   	= "0530deserts1981";
	
	/**
	 * For special propose only.
	 * @return AES Decryption key
	 */
	public byte[] genAesKey(String filename) {
		//MD5    md5    = new MD5();
		String key = null;
		try {
			byte[] source = (m_uuid + filename + m_magic + m_msisdn).getBytes();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//digest.digest(source);
			
			byte[] md5Result = digest.digest(source);
			key = StringUtil.asHex(md5Result).toUpperCase().substring(0, 16);
			//md5.Update(m_uuid + filename + m_magic + m_msisdn, null);
			//md5.Update(ComicView.uuid + filename + ComicView.magic + ComicView.msisdn, null);
			//aesKey =  md5.asHex().substring(0, 16).toUpperCase().getBytes();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return key.getBytes();
	}

	/**
	 * For special propose only
	 * @return AES Internal Vector
	 */
	public byte[] getVector() {
		return m_aesIv;
	}
	
	public AESEncrypter getEncrypter(String filename)
	{
		return new AESEncrypter(genAesKey(filename), getVector());
	}
	


}
