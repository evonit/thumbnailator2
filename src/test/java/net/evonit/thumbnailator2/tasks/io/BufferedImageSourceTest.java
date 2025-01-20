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

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.geometry.AbsoluteSize;
import net.evonit.thumbnailator2.geometry.Coordinate;
import net.evonit.thumbnailator2.geometry.Positions;
import net.evonit.thumbnailator2.geometry.Region;
import net.evonit.thumbnailator2.test.BufferedImageComparer;

import net.evonit.thumbnailator2.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BufferedImageSourceTest {
	@Test
	public void givenNullImage() throws IOException {
		NullPointerException e = assertThrows(NullPointerException.class, () -> {
			// given
			// when
			new BufferedImageSource(null);
		});
		assertEquals("Image cannot be null.", e.getMessage());
	}
	
	@Test
	public void givenValidImage() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		BufferedImageSource source = new BufferedImageSource(sourceImage);
		
		// when
		BufferedImage img = source.read();
		
		// then
		assertSame(sourceImage, img);
		assertEquals(100, img.getWidth());
		assertEquals(100, img.getHeight());
		assertEquals(null, source.getInputFormatName());
	}
	
	@Test
	public void givenValidImage_getInputFormatNameBeforeRead() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		BufferedImageSource source = new BufferedImageSource(sourceImage);

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
		
		BufferedImageSource source = new BufferedImageSource(sourceImage);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(Positions.TOP_LEFT, new AbsoluteSize(40, 40)))
					.size(20, 20)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
			
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 40, 40);
		assertTrue(BufferedImageComparer.isSame(expectedImg, img));
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
		
		BufferedImageSource source = new BufferedImageSource(sourceImage);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(new Coordinate(20, 20), new AbsoluteSize(100, 100)))
					.size(80, 80)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(20, 20, 80, 80);
		assertTrue(BufferedImageComparer.isSame(expectedImg, img));
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
		
		BufferedImageSource source = new BufferedImageSource(sourceImage);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.region(new Region(new Coordinate(-20, -20), new AbsoluteSize(100, 100)))
					.size(80, 80)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		
		// then
		BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 80, 80);
		assertTrue(BufferedImageComparer.isSame(expectedImg, img));
	}
	
	@Test
	public void appliesSourceRegionNotSpecified() throws IOException {
		// given
		BufferedImage sourceImage = TestUtils.getImageFromResource("Thumbnailator/grid.png");
		
		BufferedImageSource source = new BufferedImageSource(sourceImage);
		source.setThumbnailParameter(
				new ThumbnailParameterBuilder()
					.size(20, 20)
					.build()
		);
		
		// when
		BufferedImage img = source.read();
		
		// then
		assertEquals(sourceImage, img);
	}
}
