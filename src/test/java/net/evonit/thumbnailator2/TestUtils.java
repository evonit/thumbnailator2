/*
 * Thumbnailator - a thumbnail generation library
 *
 * Copyright (c) 2008-2022 Chris Kroells
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.evonit.thumbnailator2;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class TestUtils {
	/**
	 * Copies a specified length of the source file.
	 * 
	 * @param sourceFile		The source file.
	 * @param destFile			The destination file.
	 * @param length			Length of source file to copy.
	 * @throws IOException		If an IOException is thrown.
	 */
	public static void copyFile(File sourceFile, File destFile, long length) throws IOException {
		FileInputStream fis = new FileInputStream(sourceFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		fis.getChannel().transferTo(0, length, fos.getChannel());
		fis.close();
		fos.close();
	}

	/**
	 * Copies a file.
	 *
	 * @param sourceFile		The source file.
	 * @param destFile			The destination file.
	 * @throws IOException		If an IOException is thrown.
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		copyFile(sourceFile, destFile, sourceFile.length());
	}

	/**
	 * Returns the format of an image which is read through the {@link InputStream}.
	 * 
	 * @param is			The {@link InputStream} to an image.
	 * @return				File format of the image.
	 * @throws IOException
	 */
	public static String getFormatName(InputStream is) throws IOException {
		return ImageIO.getImageReaders(
				ImageIO.createImageInputStream(is)
		).next().getFormatName();
	}

	public static URL getResource(String resourceName) throws IOException {
		URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
		if (url == null) {
			throw new IOException("Resource not found: " + resourceName);
		}
		return url;
	}

	public static InputStream getResourceStream(String resourceName) throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			throw new IOException("Resource not found: " + resourceName);
		}
		return is;
	}

	public static File copyResourceToFile(String resourceName, File destination) throws IOException {
		// create parent directory
		File parentDir = destination.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (parentDir.mkdirs()) {
				//System.out.println("created directory: " + parentDir.getAbsolutePath());
			} else {
				//System.out.println("failed to create directory: " + parentDir.getAbsolutePath());
			}
		}

		InputStream is = getResourceStream(resourceName);
		FileOutputStream fos = new FileOutputStream(destination);

		byte[] buffer;
		int bytesAvailable;
		while ((bytesAvailable = is.available()) != 0) {
			buffer = new byte[bytesAvailable];
			int bytesRead = is.read(buffer, 0, buffer.length);
			fos.write(buffer, 0, bytesRead);
		}
		is.close();
		fos.close();

		return destination;
	}

	public static File copyResourceToTemporaryFile(String resourceName, File folder) throws IOException {
		String name;
		if (resourceName.contains("/")) {
			name = resourceName.substring(resourceName.lastIndexOf("/") + 1);
		} else {
			name = resourceName;
		}
		File destination = new File(folder, name);

		return copyResourceToFile(resourceName, destination);
	}

	public static File copyResourceToTemporaryFile(String resourceName, String namedAs, File folder) throws IOException {
		return copyResourceToFile(resourceName, new File(folder, namedAs));
	}

	public static BufferedImage getImageFromResource(String resourceName) throws IOException {
		InputStream is = getResourceStream(resourceName);
		try {
			return ImageIO.read(is);
		} finally {
			is.close();
		}
	}
}
