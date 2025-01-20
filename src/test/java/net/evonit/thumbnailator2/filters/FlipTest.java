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

package net.evonit.thumbnailator2.filters;

import java.awt.image.BufferedImage;

import net.evonit.thumbnailator2.test.BufferedImageAssert;
import net.evonit.thumbnailator2.test.BufferedImageComparer;
import net.evonit.thumbnailator2.util.BufferedImages;

import net.evonit.thumbnailator2.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlipTest {
	
	@Test
	public void flipHorizontal() throws Exception {
		// given
		BufferedImage img = TestUtils.getImageFromResource("Exif/original.png");
		
		// when
		BufferedImage result = Flip.HORIZONTAL.apply(img);
		
		// then
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						0, 0, 1,
				}
		);
	}
	
	@Test
	public void flipVertical() throws Exception {
		// given
		BufferedImage img = TestUtils.getImageFromResource("Exif/original.png");
		
		// when
		BufferedImage result = Flip.VERTICAL.apply(img);
		
		// then
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 0, 0,
						1, 1, 1,
						1, 1, 1,
				}
		);
	}
	
	/**
	 * Checks that the input image contents are not altered, when using the
	 * {@link Flip#HORIZONTAL}.
	 */
	@Test
	public void inputContentsAreNotAltered_UsingFlipHorizontal() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = Flip.HORIZONTAL;
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}
	
	/**
	 * Checks that the input image contents are not altered, when using the
	 * {@link Flip#VERTICAL}.
	 */
	@Test
	public void inputContentsAreNotAltered_UsingFlipVertical() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = Flip.VERTICAL;
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_UsingFlipHorizontal() {
		ImageFilterTestUtils.assertImageTypeRetained(Flip.HORIZONTAL);
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_UsingFlipVertical() {
		ImageFilterTestUtils.assertImageTypeRetained(Flip.VERTICAL);
	}
}
