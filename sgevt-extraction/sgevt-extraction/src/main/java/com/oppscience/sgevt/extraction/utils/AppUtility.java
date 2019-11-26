package com.oppscience.sgevt.extraction.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppUtility {

	private Environment environment;

	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public String getEnvironmentValue(String key) {
		return environment.getProperty(key);
	}

	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		} else if (str != null && str.length() == 0) {
			return true;
		} else {
			return false;
		}

	}

	public static String getFileNameWithoutExtension(File file) {
		if (file != null) {
			String fileName = file.getName();
			int pos = fileName.lastIndexOf(".");
			if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
				fileName = fileName.substring(0, pos);
			}
			return fileName;
		} else {
			return null;
		}

	}

}