/*
 * Thumbnailator - a thumbnail generation library
 *
 * Copyright (c) 2008-2020 Chris Kroells
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

package net.evonit.thumbnailator2.tasks.io;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.Thumbnails;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class Issue69FileImageSourceTest {

	@TempDir
	public File tempFolder;
	public File tempFile;
	public static int SIZE = 8000;

	@BeforeEach
	public void prepareSource() throws IOException {
		tempFile = new File(tempFolder, "temp.jpg");

		BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setPaint(new GradientPaint(0, 0, Color.blue, SIZE, SIZE, Color.red));
		g.dispose();
		ImageIO.write(img, "jpg", tempFile);

		System.setProperty("thumbnailator.conserveMemoryWorkaround", "");
	}

	@Test
	public void fromFileBySizeWorkaroundDisabled() throws IOException {
		// given
		ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
		FileImageSource source = new FileImageSource(tempFile);
		source.setThumbnailParameter(param);

		// when
		BufferedImage img = source.read();

		// then
		assertEquals(SIZE, img.getWidth());
		assertEquals(SIZE, img.getHeight());
	}

	@Test
	public void fromFileBySizeWorkaroundEnabled() throws IOException {
		// given
		System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");

		ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
		FileImageSource source = new FileImageSource(tempFile);
		source.setThumbnailParameter(param);

		// when
		BufferedImage img = source.read();

		// then
		assertTrue(img.getWidth() < SIZE);
		assertTrue(img.getWidth() >= 600);
		assertTrue(img.getHeight() < SIZE);
		assertTrue(img.getHeight() >= 600);
	}

	@Test
	public void fromFileByScaleWorkaroundDisabled() throws IOException {
		// given
		ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
		FileImageSource source = new FileImageSource(tempFile);
		source.setThumbnailParameter(param);

		// when
		BufferedImage img = source.read();

		// then
		assertEquals(SIZE, img.getWidth());
		assertEquals(SIZE, img.getHeight());
	}

	@Test
	public void fromFileByScaleWorkaroundEnabled() throws IOException {
		// given
		System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");

		ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
		FileImageSource source = new FileImageSource(tempFile);
		source.setThumbnailParameter(param);

		// when
		BufferedImage img = source.read();

		// then
		assertTrue(img.getWidth() < SIZE);
		assertTrue(img.getWidth() >= 600);
		assertTrue(img.getHeight() < SIZE);
		assertTrue(img.getHeight() >= 600);
	}

	// Reproduces Issue 161.
	// https://github.com/coobird/thumbnailator/issues/161
	@Test
	public void usingThumbnailsWidthWorkaroundEnabled() throws IOException {
		// given
		System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");

		// when
		BufferedImage img = Thumbnails.of(tempFile).width(600).asBufferedImage();

		// then
		assertTrue(img.getWidth() < SIZE);
		assertTrue(img.getWidth() >= 600);
		assertTrue(img.getHeight() < SIZE);
		assertTrue(img.getHeight() >= 600);
	}

	// Reproduces Issue 161.
	// https://github.com/coobird/thumbnailator/issues/161
	@Test
	public void usingThumbnailsHeightWorkaroundEnabled() throws IOException {
		// given
		System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");

		// when
		BufferedImage img = Thumbnails.of(tempFile).height(600).asBufferedImage();

		// then
		assertTrue(img.getWidth() < SIZE);
		assertTrue(img.getWidth() >= 600);
		assertTrue(img.getHeight() < SIZE);
		assertTrue(img.getHeight() >= 600);
	}
}