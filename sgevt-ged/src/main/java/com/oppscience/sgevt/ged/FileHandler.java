package com.oppscience.sgevt.ged;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.tomcat.util.http.fileupload.FileUtils;

public class FileHandler {
	
	
	public static void downloadUsingStream(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, Integer.MAX_VALUE)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	public static void downloadUsingNIO(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}
	
	public static void createDirectoryIfNotExist(String basePath) throws IOException {
		File f=new File(basePath);
		if(!f.exists()) {
			FileUtils.forceMkdir(f);
		}
	}
	
	public static void deleteDirectoryIfExist(String basePath) throws IOException {
		File f=new File(basePath);
		if(f.exists() && f.isDirectory()) {
			FileUtils.deleteDirectory(f);
		}
	}

}
