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

package net.evonit.thumbnailator2.resizers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.evonit.thumbnailator2.test.BufferedImageComparer;

import net.evonit.thumbnailator2.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NullResizerTest {

	@Test
	public void sourceAndDestSameDimension() throws IOException {
		// given
		BufferedImage srcImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		BufferedImage destImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		
		// when
		new NullResizer().resize(srcImage, destImage);
		
		// then
		assertEquals(srcImage.getWidth(), destImage.getWidth());
		assertEquals(srcImage.getHeight(), destImage.getHeight());
		assertTrue(BufferedImageComparer.isRGBSimilar(srcImage, destImage));
	}
	
	@Test
	public void sourceSmallerThanDest() throws IOException {
		// given
		BufferedImage srcImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		BufferedImage destImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		
		// when
		new NullResizer().resize(srcImage, destImage);
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(srcImage, destImage.getSubimage(0, 0, 100, 100)));
	}
	
	@Test
	public void sourceLargerThanDest() throws IOException {
		// given
		BufferedImage srcImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		BufferedImage destImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		
		// when
		new NullResizer().resize(srcImage, destImage);
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(srcImage.getSubimage(0, 0, 50, 50), destImage));
	}
	
	@Test
	public void resizeNullAndNull() {
		assertThrows(Exception.class, () -> {
			// given
			BufferedImage srcImage = null;
			BufferedImage destImage = null;
			new NullResizer().resize(srcImage, destImage);
		});
	}
	
	@Test
	public void resizeSpecifiedAndNull() {
		assertThrows(Exception.class, () -> {
			// given
			BufferedImage srcImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			BufferedImage destImage = null;

			// when
			new NullResizer().resize(srcImage, destImage);
		});
	}
	
	@Test
	public void resizeNullAndSpecified() {
		assertThrows(Exception.class, () -> {
			// given
			BufferedImage srcImage = null;
			BufferedImage destImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			// when
			new NullResizer().resize(srcImage, destImage);
		});
	}
}
