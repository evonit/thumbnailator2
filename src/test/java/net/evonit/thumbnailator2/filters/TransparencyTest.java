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

package net.evonit.thumbnailator2.filters;

import static net.evonit.thumbnailator2.filters.ImageFilterTestUtils.assertImageTypeRetained;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import net.evonit.thumbnailator2.test.BufferedImageComparer;
import net.evonit.thumbnailator2.util.BufferedImages;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Transparency} filter.
 * 
 * @author coobird
 *
 */
public class TransparencyTest {

	/**
	 * Checks that the input image contents are not altered, when using the
	 * {@link Transparency#Transparency(double)} constructor.
	 */
	@Test
	public void inputContentsAreNotAltered_DoubleConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Transparency(0.5);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}
	
	/**
	 * Checks that the input image contents are not altered, when using the
	 * {@link Transparency#Transparency(float)} constructor.
	 */
	@Test
	public void inputContentsAreNotAltered_FloatConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Transparency(0.5f);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_DoubleConstructor() {
		assertImageTypeRetained(new Transparency(0.5));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_FloatConstructor() {
		assertImageTypeRetained(new Transparency(0.5f));
	}
}
