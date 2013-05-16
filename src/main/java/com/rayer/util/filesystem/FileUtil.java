package com.rayer.util.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.rayer.util.drm.AESEncrypter;

/**
 * File Utilities
 * @author rayer
 *
 */
public class FileUtil {

	/**
	 * Write any stream into file
	 * @param is target InputStream
	 * @param filename
	 */
	public static void writeStreamToFile(InputStream is, String filename) {
		File f = new File(filename);
		writeStreamToFile(is, f);
	}

	/**
	 * Write any stream into file
	 * @param is target Inputstream;
	 * @param f File handler
	 */
	public static void writeStreamToFile(InputStream is, File f) {
		if (f.exists() == true)
			f.delete();

		try {
			FileOutputStream fos = new FileOutputStream(f);
			byte[] buf = new byte[8192];
			int len;
			while ((len = is.read(buf)) > 0)
				fos.write(buf, 0, len);

			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public static void decryptStreamToFile(InputStream is, AESEncrypter aes,
			String string) {

	}

	/**
	 * A simple way to copy a file
	 * @param source source file path
	 * @param dest destination file path
	 * @return
	 */
	public static boolean copyFile(String source, String dest) {
		try {
			File f1 = new File(source);
			File f2 = new File(dest);
			InputStream in = new FileInputStream(f1);

			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);

			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;

	}

	/**
	 * A simple way to perform rm -rf
	 * @param filePath target dictionary
	 * @return
	 */
	public static boolean deleteTree(String filePath) {
		return deleteTree(new File(filePath));
	}

	/**
	 * A simply way to perform rm -rf
	 * @param path
	 * @return
	 */
	public static boolean deleteTree(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteTree(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	// public static long getExternalStorageSpace() {
	//
	// long space = 0;
	// try {
	// StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
	// .getAbsolutePath());
	// space = (long) stat.getAvailableBlocks()
	// * (long) stat.getBlockSize();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return space;
	// }
	//
	// public static boolean isExternalStorageExist() {
	// return Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED) ? true : false;
	// }
	//
	// public static float getLocalStorageSpace() {
	// float space = 0;
	// try {
	// StatFs stat = new StatFs("/data/");
	// space = stat.getAvailableBlocks() * (float) stat.getBlockSize();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return space;
	// }

	/**
	 * A simple way to perform cp -r
	 * @param src source directory
	 * @param dest destination directory
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.getParentFile().mkdirs();
				dest.mkdir();
				// System.out.println("Directory copied from "
				// + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			// System.out.println("File copied from " + src + " to " + dest);
		}
	}

	/**
	 * A easy way to create checksum of the file, this is NOT MD5!
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static byte[] createChecksum(File f) throws Exception {

		InputStream fis = new FileInputStream(f);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}
	
	/**
	 * A easy way to create checksum of the file, this is NOT MD5!
	 * @param filename
	 * @return
	 * @throws Exception
	 */

	public static byte[] createChecksum(String filename) throws Exception {
		return createChecksum(new File(filename));
	}


	/**
	 * A easy way to create MD5 of the file
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Checksum(File f) throws Exception {
		byte[] b = createChecksum(f);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * A easy way to create MD5 of the file
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Checksum(String filename) throws Exception {
		return getMD5Checksum(new File(filename));
	}

	/**
	 * Recursively walk a directory tree and return a List of all Files found;
	 * the List is sorted using File.compareTo().
	 * 
	 * @param aStartingDir
	 *            is a valid directory, which can be read.
	 */
	static public List<File> getFileListing(File aStartingDir)
			throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	// PRIVATE //
	static private List<File> getFileListingNoSort(File aStartingDir)
			throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			if (file.getName().startsWith("."))
				continue;

			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be
	 * read.
	 */
	static private void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}

	/**
	 * A easy way to save any bean implements Serialable to file
	 * @param cachePath
	 * @param item
	 * @throws IOException
	 */
	public void saveSerializedItem(String cachePath, Serializable item) throws IOException {
		saveSerializedItem(new File(cachePath), item);
	}
	
	/**
	 * A easy way to save any bean implements Serialable to file
	 * @param cacheFile
	 * @param item
	 * @throws IOException
	 */
	public void saveSerializedItem(File cacheFile, Serializable item) throws IOException {
		cacheFile.createNewFile();

		ObjectOutputStream obj_out = new ObjectOutputStream(
				new FileOutputStream(cacheFile));
		obj_out.writeObject(item);
		obj_out.flush();
		obj_out.close();
		
	}

	/**
	 * A easy way to load any bean implements Serialable from file
	 * @param cachePath
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public <T extends Serializable> T getSerializedItem(String cachePath) throws IOException, ClassNotFoundException {
		return getSerializedItem(new File(cachePath));
	}
	
	/**
	 * A easy way to load any bean implements Serialable from file
	 * @param cacheFile
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getSerializedItem(File cacheFile) throws IOException, ClassNotFoundException {
		T ret = null;

		//File inputFile = new File(cachePath);
		ObjectInputStream obj_in = new ObjectInputStream(
				new FileInputStream(cacheFile));
		ret = (T) obj_in.readObject();
		obj_in.close();
		

		return ret;
	}

}
