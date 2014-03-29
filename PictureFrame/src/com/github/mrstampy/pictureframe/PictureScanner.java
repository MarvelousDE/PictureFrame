package com.github.mrstampy.pictureframe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
			String[] names = directory.list();

			for (String name : names) {
				if (isPictureFile(name)) {
					log.trace("Including picture {}", name);
					pictureNames.add(name);
				}
			}

			log.debug("Loaded {} picture names", pictureNames.size());
		} finally {
			lock.unlock();
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
