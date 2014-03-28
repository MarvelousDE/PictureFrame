package com.github.mrstampy.pictureframe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {
	private static final Logger log = LoggerFactory.getLogger(Settings.class);

	private static final String PV_PROPERTIES = "pv.properties";
	private static final String WORK_DIR = System.getProperty("user.home") + File.separator + ".pictureview";
	
	private static final String PICTURE_DURATION = "picture.duration";
	private static final String PICTURE_DIR_KEY = "picture.dir";

	private Properties properties = new Properties();
	private File userfile = new File(WORK_DIR, PV_PROPERTIES);

	public Settings() {
		loadProperties();
	}

	public String getDirectory() {
		return properties.getProperty(PICTURE_DIR_KEY);
	}

	public void setDirectory(String dir) {
		properties.setProperty(PICTURE_DIR_KEY, dir);
		store();
	}
	
	public long getDuration() {
		String ds = properties.getProperty(PICTURE_DURATION);
		
		try {
			return Long.parseLong(ds);
		} catch(Exception e) {
			setDuration(5000);
			return 5000;
		}
	}
	
	public void setDuration(long duration) {
		properties.setProperty(PICTURE_DURATION, Long.toString(duration));
		store();
	}

	private void store() {
		try {
			properties.store(new FileWriter(userfile), "PictureFrame properties");
		} catch (Exception e) {
			log.error("Could not store properties", e);
		}
	}

	private void loadProperties() {
		if (!properties.isEmpty()) return;

		if (!userfile.exists()) {
			File userdir = new File(WORK_DIR);
			userdir.mkdirs();
			setDirectory(".");
		}

		try {
			properties.load(new FileInputStream(userfile));
		} catch (Exception e) {
			log.error("Could not load properties", e);
		}
	}

}
