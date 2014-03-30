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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.scene.image.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PictureScanner {
	private static final Logger log = LoggerFactory.getLogger(PictureScanner.class);

	private File directory;

	private List<String> pictureNames = new ArrayList<>();

	private String[] picSuffixes = { ".jpg", ".JPG", ".png", ".PNG", ".gif", ".GIF", ".jpeg", ".JPEG", ".bmp", ".BMP" };

	private Random rand = new Random(System.nanoTime());

	private Lock lock = new ReentrantLock(true);

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		if (!directory.isDirectory()) throw new IllegalArgumentException(directory + " is not a directory");
		this.directory = directory;
		scan();
	}

	public void setDirectory(String directory) {
		setDirectory(new File(directory));
	}

	public void scan() {
		lock.lock();
		try {
			pictureNames.clear();
			
			loadFromDirectory(directory);

			log.debug("Loaded {} picture names", pictureNames.size());
		} finally {
			lock.unlock();
		}
	}
	
	private void loadFromDirectory(File dir) {
		log.debug("Loading from {}", dir);
		String[] names = dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return isPictureFile(name);
			}
		});

		for (String name : names) {
			pictureNames.add(name);
		}
		
		loadFromSubdirs(dir);
	}

	private void loadFromSubdirs(File dir) {
		File[] directories = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		if(directories == null || directories.length == 0) return;
		
		for(File f : directories) {
			loadFromDirectory(f);
		}
	}

	public Image getRandomImage() throws FileNotFoundException {
		lock.lock();
		try {
			int idx = rand.nextInt(pictureNames.size());

			String name = directory.getAbsolutePath() + File.separator + pictureNames.get(idx);
			log.debug("Returning random image {}", name);

			return new Image(new FileInputStream(name));
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return pictureNames.size();
	}

	private boolean isPictureFile(String name) {
		for (String sfx : picSuffixes) {
			if (name.endsWith(sfx)) return true;
		}

		return false;
	}
}
