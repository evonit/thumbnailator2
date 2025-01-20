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

package net.evonit.thumbnailator2.makers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.resizers.ProgressiveBilinearResizer;
import net.evonit.thumbnailator2.resizers.Resizer;
import net.evonit.thumbnailator2.resizers.ResizerFactory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ScaledThumbnailMakerTest {

	private static BufferedImage makeTestImage200x200() {
		return new BufferedImageBuilder(200, 200).build();
	}

	@Test
	public void uninitializedWithNoArgConstructor() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new ScaledThumbnailMaker().make(img);
		});

			}

	@Test
	public void makeWithOneArgConstructor() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.5).make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void makeWithOneArgConstructorWithScaleOneArg() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new ScaledThumbnailMaker(0.5).scale(0.5).make(img);
		});

			}

	@Test
	public void makeWithOneArgConstructorWithScaleTwoArg() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new ScaledThumbnailMaker(0.5).scale(0.5, 0.5).make(img);
		});

			}

	@Test
	public void makeWithTwoArgConstructor() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.6, 0.4).make(img);

		assertEquals(120, thumbnail.getWidth());
		assertEquals(80, thumbnail.getHeight());
	}

	@Test
	public void makeWithTwoArgConstructorWithScaleOneArg() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new ScaledThumbnailMaker(0.6, 0.4).scale(0.5).make(img);
		});

			}

	@Test
	public void makeWithTwoArgConstructorWithScaleTwoArg() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new ScaledThumbnailMaker(0.6, 0.4).scale(0.5, 0.5).make(img);
		});

			}

	@Test
	public void makeWithNoArgConstructorAndScaleOneArg() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new ScaledThumbnailMaker().scale(0.5).make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void makeWithNoArgConstructorAndScaleTwoArg() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new ScaledThumbnailMaker().scale(0.6, 0.4).make(img);

		assertEquals(120, thumbnail.getWidth());
		assertEquals(80, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void verifyResizerFactoryBeingCalled() {
		BufferedImage img = makeTestImage200x200();
		Resizer spyResizer = spy(new ProgressiveBilinearResizer());
		ResizerFactory resizerFactory = mock(ResizerFactory.class);
		when(resizerFactory.getResizer(any(Dimension.class), any(Dimension.class)))
				.thenReturn(spyResizer);

		new ScaledThumbnailMaker(0.5).resizerFactory(resizerFactory).make(img);

		verify(resizerFactory, atLeastOnce()).getResizer(new Dimension(200, 200), new Dimension(100, 100));
		verify(spyResizer).resize(eq(img), any(BufferedImage.class));
	}

	@Test
	public void scaleIsZeroThroughOneArgConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker(0);
		});

			}

	@Test
	public void scaleIsZeroThroughOneArgScaleMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker().scale(0);
		});

			}

	@Test
	public void scaleIsZeroThroughTwoArgConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker(0, 0);
		});

			}

	@Test
	public void scaleIsZeroThroughTwoArgScaleMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker().scale(0, 0);
		});

			}

	@Test
	public void scaleIsNegativeThroughOneArgConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker(-1);
		});

			}

	@Test
	public void scaleIsNegativeThroughOneArgScaleMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker().scale(-1);
		});

			}

	@Test
	public void scaleIsNegativeThroughTwoArgConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker(1, -1);
		});

			}

	@Test
	public void scaleIsNegativeThroughTwoArgScaleMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new ScaledThumbnailMaker().scale(1, -1);
		});

			}

	@Test
	public void isRoundingWidthRatherThanTruncate_scaleOneArg() {
		BufferedImage img = new BufferedImageBuilder(99, 100).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void isRoundingHeightRatherThanTruncate_scaleOneArg() {
		BufferedImage img = new BufferedImageBuilder(100, 99).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void isRoundingWidthRatherThanTruncate_scaleTwoArg() {
		BufferedImage img = new BufferedImageBuilder(99, 100).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1, 0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void isRoundingHeightRatherThanTruncate_scaleTwoArg() {
		BufferedImage img = new BufferedImageBuilder(100, 99).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1, 0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void widthBecomesZeroIfTruncated() {
		BufferedImage img = new BufferedImageBuilder(9, 100).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(1, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void widthBecomesZeroIfTruncatedButIsOneIfRounded() {
		BufferedImage img = new BufferedImageBuilder(10, 100).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(1, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void widthBecomesZeroIfTruncatedAndIsZeroIfRounded() {
		BufferedImage img = new BufferedImageBuilder(1, 100).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(1, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void heightBecomesZeroIfTruncated() {
		BufferedImage img = new BufferedImageBuilder(100, 9).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(1, thumbnail.getHeight());
	}

	@Test
	public void heightBecomesZeroIfTruncatedButIsOneIfRounded() {
		BufferedImage img = new BufferedImageBuilder(100, 10).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(1, thumbnail.getHeight());
	}

	@Test
	public void heightBecomesZeroIfTruncatedAndIsZeroIfRounded() {
		BufferedImage img = new BufferedImageBuilder(100, 1).build();

		BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(1, thumbnail.getHeight());
	}
}