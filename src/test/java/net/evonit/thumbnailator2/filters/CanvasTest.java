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


import java.awt.Color;
import java.awt.image.BufferedImage;

import net.evonit.thumbnailator2.geometry.Positions;
import net.evonit.thumbnailator2.test.BufferedImageComparer;
import net.evonit.thumbnailator2.util.BufferedImages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link Canvas} filter.
 * 
 * @author coobird
 *
 */
public class CanvasTest {

	/**
	 * Checks that the input image contents are not altered.
	 */
	@Test
	public void inputContentsAreNotAltered_WidthHeightPositionConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}
	
	/**
	 * Checks that the input image contents are not altered.
	 */
	@Test
	public void inputContentsAreNotAltered_WidthHeightPositionCropConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, true);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}
	
	/**
	 * Checks that the input image contents are not altered.
	 */
	@Test
	public void inputContentsAreNotAltered_WidthHeightPositionColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, Color.black);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}
	
	/**
	 * Checks that the input image contents are not altered.
	 */
	@Test
	public void inputContentsAreNotAltered_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		BufferedImage copyImage = BufferedImages.copy(originalImage);
		
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, true, Color.black);
		
		// when
		filter.apply(originalImage);
		
		// then
		assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_WidthHeightPositionConstructor() {
		ImageFilterTestUtils.assertImageTypeRetained(new Canvas(100, 100, Positions.CENTER));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_WidthHeightPositionCropConstructor() {
		ImageFilterTestUtils.assertImageTypeRetained(new Canvas(100, 100, Positions.CENTER, true));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_WidthHeightPositionColorConstructor() {
		ImageFilterTestUtils.assertImageTypeRetained(new Canvas(100, 100, Positions.CENTER, Color.black));
	}

	@Test
	public void imageTypeForInputAndOutputIsTheSame_WidthHeightPositionCropColorConstructor() {
		ImageFilterTestUtils.assertImageTypeRetained(new Canvas(100, 100, Positions.CENTER, true, Color.black));
	}

	/**
	 * Checks that the image is cropped
	 */
	@Test
	public void croppingEnabled_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, true, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
	}
	
	/**
	 * Checks that the image is not cropped
	 */
	@Test
	public void croppingDisabled_WidthHeightExceeds_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, false, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(200, resultImage.getWidth());
		assertEquals(200, resultImage.getHeight());
	}
	
	/**
	 * Checks that the image is not cropped
	 * - the original width exceeds the specified width
	 * - the original height is within the specified height
	 */
	@Test
	public void croppingDisabled_WidthExceeds_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(200, 90, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, false, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(200, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
	}
	
	/**
	 * Checks that the image is not cropped
	 * - the original width is within the specified height
	 * - the original height exceeds the specified width
	 */
	@Test
	public void croppingDisabled_HeightExceeds_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 200, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, false, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(200, resultImage.getHeight());
	}
	
	/**
	 * Checks that the image is enclosed
	 */
	@Test
	public void croppingEnabled_WidthHeightSmaller_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 90, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, true, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
	}
	
	/**
	 * Checks that the image is enclosed
	 */
	@Test
	public void croppingDisabled_WidthHeightSmaller_WidthHeightPositionCropColorConstructor() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 90, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, false, Color.black);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
	}
	
	@Test
	public void subsequentImagesCroppedCorrectly() {
		/*
		 * Original code was changing the width/height settings when cropping
		 * was disabled, and if the image was larger than the width/height
		 * specified for the Canvas object.
		 */
		
		// given
		BufferedImage img1 = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
		BufferedImage img2 = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, false);
		
		// when
		BufferedImage result1 = filter.apply(img1);
		BufferedImage result2 = filter.apply(img2);
		
		// then
		assertEquals(120, result1.getWidth());
		assertEquals(120, result1.getHeight());
		assertEquals(100, result2.getWidth());
		assertEquals(100, result2.getHeight());
	}
	
	@Test
	public void usesBlackFillcolorForNonAlphaImages() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 100, BufferedImage.TYPE_INT_RGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER);

		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
		assertEquals(Color.black.getRGB(), resultImage.getRGB(1, 50));
		assertEquals(Color.black.getRGB(), resultImage.getRGB(99, 50));
	}
	
	@Test
	public void usesSpecifiedFillcolorForNonAlphaImages() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 100, BufferedImage.TYPE_INT_RGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, Color.blue);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
		assertEquals(Color.blue.getRGB(), resultImage.getRGB(1, 50));
		assertEquals(Color.blue.getRGB(), resultImage.getRGB(99, 50));
	}
	
	@Test
	public void noFillColorForAlphaImages() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 100, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
		assertEquals(0, resultImage.getRGB(1, 50));
		assertEquals(0, resultImage.getRGB(99, 50));
	}
	
	@Test
	public void usesSpecifiedFillColorForAlphaImages() {
		// given
		BufferedImage originalImage = new BufferedImage(90, 100, BufferedImage.TYPE_INT_ARGB);
		ImageFilter filter = new Canvas(100, 100, Positions.CENTER, Color.blue);
		
		// when
		BufferedImage resultImage = filter.apply(originalImage);
		
		// then
		assertEquals(100, resultImage.getWidth());
		assertEquals(100, resultImage.getHeight());
		assertEquals(Color.blue.getRGB(), resultImage.getRGB(1, 50));
		assertEquals(Color.blue.getRGB(), resultImage.getRGB(99, 50));
	}
}
