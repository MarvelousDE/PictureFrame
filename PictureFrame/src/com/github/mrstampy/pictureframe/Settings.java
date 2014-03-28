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
	
	private static final String PICTURE_DURATION_KEY = "picture.duration";
	private static final String PICTURE_DIR_KEY = "picture.dir";
	private static final String FULL_SCREEN_KEY = "full.screen";
	private static final String WIDTH_KEY = "width";
	private static final String HEIGHT_KEY = "height";

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
		String ds = properties.getProperty(PICTURE_DURATION_KEY);
		
		try {
			return Long.parseLong(ds);
		} catch(Exception e) {
			setDuration(5000);
			return 5000;
		}
	}
	
	public void setDuration(long duration) {
		properties.setProperty(PICTURE_DURATION_KEY, Long.toString(duration));
		store();
	}
	
	public boolean isFullScreen() {
		return Boolean.parseBoolean(properties.getProperty(FULL_SCREEN_KEY));
	}
	
	public void setFullScreen(boolean fullScreen) {
		properties.setProperty(FULL_SCREEN_KEY, Boolean.toString(fullScreen));
		store();
	}
	
	public double getWidth() {
		return getDoubleProperty(WIDTH_KEY, 1000);
	}
	
	public double getHeight() {
		return getDoubleProperty(HEIGHT_KEY, 1000);
	}
	
	public void setWidth(double width) {
		properties.setProperty(WIDTH_KEY, Double.toString(width));
		store();
	}
	
	public void setHeight(double height) {
		properties.setProperty(HEIGHT_KEY, Double.toString(height));
		store();
	}

	private double getDoubleProperty(String key, double dflt) {
		String s = properties.getProperty(key);
		
		try {
			return Double.parseDouble(s);
		} catch(Exception e) {
			properties.setProperty(key, Double.toString(dflt));
			store();
		}
		
		return dflt;
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
