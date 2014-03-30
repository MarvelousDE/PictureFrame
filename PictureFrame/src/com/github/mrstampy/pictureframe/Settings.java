/*
 * PictureFrame - a simple picture viewer written in JavaFX 
 *
 * Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.pictureframe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {

	private static final Logger log = LoggerFactory.getLogger(Settings.class);

	private static final String PV_PROPERTIES = "pf.properties";
	private static final String WORK_DIR = System.getProperty("user.home") + File.separator + ".pictureframe";

	private static final String PICTURE_DURATION_KEY = "picture.duration";
	private static final String PICTURE_TRANSITION_KEY = "picture.transition";
	private static final String PICTURE_DIR_KEY = "picture.dir";
	private static final String FULL_SCREEN_KEY = "full.screen";
	private static final String WIDTH_KEY = "width";
	private static final String HEIGHT_KEY = "height";
	private static final String X_KEY = "x";
	private static final String Y_KEY = "y";

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
		return getLongProperty(PICTURE_DURATION_KEY, 5000);
	}

	public void setDuration(long duration) {
		properties.setProperty(PICTURE_DURATION_KEY, Long.toString(duration));
		store();
	}
	
	public long getTransition() {
		return getLongProperty(PICTURE_TRANSITION_KEY, 5000);
	}
	
	public void setTransition(long transition) {
		properties.setProperty(PICTURE_TRANSITION_KEY, Long.toString(transition));
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
	
	public double getX() {
		return getDoubleProperty(X_KEY, 0);
	}
	
	public void setX(double x) {
		properties.setProperty(X_KEY, Double.toString(x));
		store();
	}
	
	public double getY() {
		return getDoubleProperty(Y_KEY, 0);
	}
	
	public void setY(double y) {
		properties.setProperty(Y_KEY, Double.toString(y));
		store();
	}

	private double getDoubleProperty(String key, double dflt) {
		String s = properties.getProperty(key);

		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			properties.setProperty(key, Double.toString(dflt));
			store();
		}

		return dflt;
	}

	private long getLongProperty(String key, long dflt) {
		String s = properties.getProperty(key);

		try {
			return Long.parseLong(s);
		} catch (Exception e) {
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
