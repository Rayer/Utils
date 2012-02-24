package com.rayer.util.filesystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.rayer.util.stream.StreamUtil;


public class ZipUtil {
	
	public static final boolean VERBOSE = true;
	public static final String DEBUGTITLE = "hamibook2";
	public static final int ZIP_OUTPUTSTREAM_BUFFER_SIZE = 8192;
	
	public static final void unzip(String filepath, String destinationDir) throws IOException {
		Enumeration<?> entries;
		ZipFile zipFile;
		
		zipFile = new ZipFile(filepath);
		entries = zipFile.entries();
		
		if(destinationDir.endsWith("/") == false)
			destinationDir += "/";
		
		File f = new File(destinationDir);
		if(f.exists() == false)
			f.mkdirs();
		
//		if(VERBOSE) {
//		Log.d(DEBUGTITLE, "Attemping to unzip : " + filepath);
//			Log.d(DEBUGTITLE, "Extract to : " + destinationDir);
//		}
		while(entries.hasMoreElements()) {
			ZipEntry z = (ZipEntry) entries.nextElement();
			
			//Log.d(DEBUGTITLE, "Unzipping to : " + destinationDir + z.getName());
			//Log.d(DEBUGTITLE, "zip entry : " + z.getName() + " " +z.isDirectory());

			if(z.isDirectory()) {
				File directory = new File(destinationDir + z.getName());
				directory.mkdirs();
				continue;
			}
			
			String totalFile = destinationDir + z.getName();
			String targetPath = totalFile.substring(0, totalFile.lastIndexOf("/"));
			
			File fd = new File(targetPath);
			fd.mkdirs();
			
			
			StreamUtil.copyInputStream(zipFile.getInputStream(z), new BufferedOutputStream(new FileOutputStream(destinationDir + z.getName()), ZIP_OUTPUTSTREAM_BUFFER_SIZE));
		}
	}

	
	public static final void unzip(InputStream is, File destination)
			throws IOException {
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry zentry = null;
		while ((zentry = zis.getNextEntry()) != null) {

			if (zentry.isDirectory()) {
				File directory = new File(destination.getPath() + "/" + zentry.getName());
				directory.mkdirs();
				continue;
			}

			String totalFile = destination.getPath() + "/" + zentry.getName();
			String targetPath = totalFile.substring(0, totalFile.lastIndexOf("/"));

			File fd = new File(targetPath);
			fd.mkdirs();

			FileOutputStream fout = new FileOutputStream(new File(destination.getPath() + "/" + zentry.getName()));
			for (int c = zis.read(); c != -1; c = zis.read())
				fout.write(c);
			//StreamUtil.copyInputStream(zis.getInputStream(zentry), new BufferedOutputStream(new FileOutputStream(destinationDir + z.getName()),ZIP_OUTPUTSTREAM_BUFFER_SIZE));

		}
	}

}
