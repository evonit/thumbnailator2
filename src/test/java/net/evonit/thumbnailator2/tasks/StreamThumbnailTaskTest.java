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

package net.evonit.thumbnailator2.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.evonit.thumbnailator2.TestUtils;
import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.resizers.Resizers;
import net.evonit.thumbnailator2.test.BufferedImageComparer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StreamThumbnailTaskTest {

	@TempDir
	File temporaryFolder;

	@Test
	public void nullParameter() {
		// given
		InputStream is = mock(InputStream.class);
		OutputStream os = mock(OutputStream.class);

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			// when
			new StreamThumbnailTask(null, is, os);
		});

		// then
		assertEquals("The parameter is null.", exception.getMessage());
		verifyNoInteractions(is);
		verifyNoInteractions(os);
	}

	@Test
	public void testRead_CorrectUsage() throws IOException {
		ThumbnailParameter param = new ThumbnailParameter(
				new Dimension(50, 50),
				null,
				true,
				"png",
				ThumbnailParameter.DEFAULT_FORMAT_TYPE,
				ThumbnailParameter.DEFAULT_QUALITY,
				BufferedImage.TYPE_INT_ARGB,
				null,
				Resizers.PROGRESSIVE,
				true,
				true
		);
		
		File inputFile = TestUtils.copyResourceToTemporaryFile(
				"Thumbnailator/grid.jpg", temporaryFolder
		);
		File outputFile = new File(temporaryFolder, "output.png");

		InputStream spyIs = spy(new FileInputStream(inputFile));
		OutputStream spyOs = spy(new FileOutputStream(outputFile));
		
		StreamThumbnailTask task = new StreamThumbnailTask(param, spyIs, spyOs);
		BufferedImage img = task.read();

		assertTrue(BufferedImageComparer.isSame(img, ImageIO.read(inputFile)));
		
		verify(spyIs, never()).close();
		verifyNoInteractions(spyOs);
	}
	
	@Test
	public void testWrite_CorrectUsage() throws IOException {
		ThumbnailParameter param = new ThumbnailParameter(
				new Dimension(50, 50),
				null,
				true,
				"png",
				ThumbnailParameter.DEFAULT_FORMAT_TYPE,
				ThumbnailParameter.DEFAULT_QUALITY,
				BufferedImage.TYPE_INT_ARGB,
				null,
				Resizers.PROGRESSIVE,
				true,
				true
		);

		File inputFile = TestUtils.copyResourceToTemporaryFile(
				"Thumbnailator/grid.jpg", temporaryFolder
		);
		File outputFile = new File(temporaryFolder, "output.png");
		
		InputStream spyIs = spy(new FileInputStream(inputFile));
		OutputStream spyOs = spy(new FileOutputStream(outputFile));
		
		StreamThumbnailTask task = new StreamThumbnailTask(param, spyIs, spyOs);
		BufferedImage img = new BufferedImageBuilder(50, 50).build();
		
		task.write(img);
		
		verifyNoInteractions(spyIs);
		verify(spyOs, never()).close();
		
		BufferedImage outputImage = ImageIO.read(outputFile);
		assertTrue(BufferedImageComparer.isRGBSimilar(img, outputImage));
	}

	@Test
	public void testGetParam() {
		ThumbnailParameter param = new ThumbnailParameter(
				new Dimension(50, 50),
				null,
				true,
				"png",
				ThumbnailParameter.DEFAULT_FORMAT_TYPE,
				ThumbnailParameter.DEFAULT_QUALITY,
				BufferedImage.TYPE_INT_ARGB,
				null,
				Resizers.PROGRESSIVE,
				true,
				true
		);
		
		InputStream is = mock(InputStream.class);
		OutputStream os = mock(OutputStream.class);
		
		StreamThumbnailTask task = new StreamThumbnailTask(param, is, os);

		assertEquals(param, task.getParam());

		verifyNoInteractions(is);
		verifyNoInteractions(os);
	}
}