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

package net.evonit.thumbnailator2.tasks.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.geometry.AbsoluteSize;
import net.evonit.thumbnailator2.geometry.Coordinate;
import net.evonit.thumbnailator2.geometry.Positions;
import net.evonit.thumbnailator2.geometry.Region;
import net.evonit.thumbnailator2.tasks.UnsupportedFormatException;
import net.evonit.thumbnailator2.test.BufferedImageAssert;
import net.evonit.thumbnailator2.test.BufferedImageComparer;

import net.evonit.thumbnailator2.TestUtils;
import org.junit.jupiter.api.Test;


public class InputStreamImageSourceTest {
	@Test
	public void givenNullInputStream() {
		NullPointerException e = assertThrows(NullPointerException.class, () -> {
			// given
			// when
			new InputStreamImageSource(null);
		});

		assertEquals("InputStream cannot be null.", e.getMessage());
	}
	
	@Test
	public void fileExists_Png() throws IOException {
		// given
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertEquals(100, img.getWidth());
		assertEquals(100, img.getHeight());
		assertEquals("png", source.getInputFormatName());
	}
	
	@Test
	public void fileExists_Jpeg() throws IOException {
		// given
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertEquals(100, img.getWidth());
		assertEquals(100, img.getHeight());
		assertEquals("JPEG", source.getInputFormatName());
	}
	
	@Test
	public void fileExists_Bmp() throws IOException {
		// given
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.bmp");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertEquals(100, img.getWidth());
		assertEquals(100, img.getHeight());
		assertEquals("bmp", source.getInputFormatName());
	}
	
	@Test
	public void cannotDetermineImageFormat() throws IOException {
		// given
		InputStream is = mock(InputStream.class);
		when(is.read()).thenThrow(new IOException("Failed on read."));
		when(is.read(any(byte[].class))).thenThrow(new IOException("Failed on read."));
		when(is.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IOException("Failed on read."));
		
		InputStreamImageSource source = new InputStreamImageSource(is);

		UnsupportedFormatException e = assertThrows(UnsupportedFormatException.class, () -> {
			// when
			source.read();
		});
		assertEquals("No suitable ImageReader found for source data.", e.getMessage());

	}
	
	@Test
	public void badImage_Png() throws IOException {
		IOException e = assertThrows(IOException.class, () -> {
			byte[] bytes = new byte[100];
			InputStream sourceIs = TestUtils.getResourceStream("Thumbnailator/grid.png");
			sourceIs.read(bytes);
			sourceIs.close();

			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			InputStreamImageSource source = new InputStreamImageSource(is);

			// when
			source.read();
		});
		assertTrue(e.getMessage().contains("Error reading PNG"));
	}
	
	@Test
	public void fileExists_getInputFormatNameBeforeRead() throws IOException {
		// given
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);

		IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
			// when
			source.getInputFormatName();
		});
		assertEquals("Input has not been read yet.", e.getMessage());
	}
	
	/*
	 *
	 *     +------+-----------+
	 *     |XXXXXX|           |
	 *     |XXXXXX|           |
	 *     +------+           |
	 *     |      region      |
	 *     |                  |
	 *     |                  |
	 *     |                  |
	 *     |                  |
	 *     +------------------+
	 *                        source
	 */
	@Test
	public void appliesSourceRegion() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(Positions.TOP_LEFT, new AbsoluteSize(40, 40)))
					.size(20, 20)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		is.close();
			
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 40, 40);
		assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
	}
	
	/*
	 *
	 *     +------------------+ source
	 *     |  +------------------+
	 *     |  |XXXXXXXXXXXXXXX|  |
	 *     |  |XXXXXXXXXXXXXXX|  |
	 *     |  |XX  final  XXXX|  |
	 *     |  |XX  region XXXX|  |
	 *     |  |XXXXXXXXXXXXXXX|  |
	 *     |  |XXXXXXXXXXXXXXX|  |
	 *     |  |XXXXXXXXXXXXXXX|  |
	 *     +--|---------------+  |
	 *        +------------------+
	 *                             region
	 */
	@Test
	public void appliesSourceRegionTooBig() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(new Coordinate(20, 20), new AbsoluteSize(100, 100)))
					.size(80, 80)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(20, 20, 80, 80);
		assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
	}
	
	/*
	 *   +-----------------+
	 *   |                 |
	 *   | +---------------|--+
	 *   | |XXXXXXXXXXXXXXX|  |
	 *   | |XXXXXXXXXXXXXXX|  |
	 *   | |XXXX final XXXX|  |
	 *   | |XXXX regionXXXX|  |
	 *   | |XXXXXXXXXXXXXXX|  |
	 *   | |XXXXXXXXXXXXXXX|  |
	 *   +-----------------+  |
	 *     |                region
	 *     +------------------+
	 *                        source
	 */
	@Test
	public void appliesSourceRegionBeyondOrigin() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(new Coordinate(-20, -20), new AbsoluteSize(100, 100)))
					.size(80, 80)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 80, 80);
		assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
	}
	
	@Test
	public void appliesSourceRegionNotSpecified() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		
		InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
		InputStreamImageSource source = new InputStreamImageSource(is);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.size(20, 20)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation1() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_1.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_1.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}

	@Test
	public void readImageUnaffectedForOrientation2() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_2.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_2.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation3() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_3.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_3.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation4() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_4.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_4.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation5() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_5.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_5.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation6() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_6.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_6.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation7() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_7.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_7.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void readImageUnaffectedForOrientation8() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_8.jpg");

		InputStream is = TestUtils.getResourceStream("Exif/source_8.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);

		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage img = source.read();
		is.close();
		
		// then
		assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
	}
	
	@Test
	public void containsCorrectFilterForOrientation1() throws Exception {
		// given
		InputStream is = TestUtils.getResourceStream("Exif/source_1.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();
		
		// then
		assertTrue(param.getImageFilters().isEmpty());
	}	
	
	@Test
	public void containsCorrectFilterForOrientation2() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_2.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_2.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();
		
		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation3() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_3.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_3.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation4() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_4.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_4.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation5() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_5.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_5.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation6() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_6.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_6.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation7() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_7.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_7.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void containsCorrectFilterForOrientation8() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_8.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_8.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder().size(20, 20).build();
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void useExifOrientationIsTrue_OrientationHonored() throws Exception {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Exif/source_2.jpg");
		
		InputStream is = TestUtils.getResourceStream("Exif/source_2.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder()
						.size(20, 20)
						.useExifOrientation(true)
						.build();
		
		source.setThumbnailParameter(param);
		
		// when
		source.read();
		is.close();

		// then
		BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
		BufferedImageAssert.assertMatches(
				result,
				new float[] {
						1, 1, 1,
						1, 1, 1,
						1, 0, 0,
				}
		);
	}
	
	@Test
	public void useExifOrientationIsFalse_OrientationIgnored() throws Exception {
		// given
		InputStream is = TestUtils.getResourceStream("Exif/source_2.jpg");
		InputStreamImageSource source = new InputStreamImageSource(is);
		
		ThumbnailParameter param =
				new ThumbnailParameterBuilder()
						.size(20, 20)
						.useExifOrientation(false)
						.build();
		
		source.setThumbnailParameter(param);
		
		// when
		BufferedImage result = source.read();
		is.close();
		
		// then
		assertTrue(param.getImageFilters().isEmpty());
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
	public void readDoesNotCloseInputStream() throws IOException {
		// given
		InputStream is = spy(TestUtils.getResourceStream("Thumbnailator/grid.png"));

		InputStreamImageSource source = new InputStreamImageSource(is);

		// when
		BufferedImage img = source.read();

		// then
		assertEquals(100, img.getWidth());
		assertEquals(100, img.getHeight());
		verify(is, never()).close();
	}
}
