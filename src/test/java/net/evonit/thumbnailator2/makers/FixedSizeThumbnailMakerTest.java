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
import static org.mockito.Mockito.*;

/**
 * A class which tests the behavior of the
 * {@link FixedSizeThumbnailMaker} class.
 */
public class FixedSizeThumbnailMakerTest {

	private static BufferedImage makeTestImage200x200() {
		return new BufferedImageBuilder(200, 200).build();
	}

	@Test
	public void uninitializedWithNoArgConstructor() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker().make(img);
		});

	}

	@Test
	public void uninitializedWithTwoArgConstructor() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(100, 100).make(img);
		});

			}

	@Test
	public void unintializedNoArgConstructorAndAspectRatioSpecified() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker()
					.keepAspectRatio(true)
					.make(img);
		});

	}

	@Test
	public void uninitializedTwoArgConstructorAndAspectRatioSpecified() {
		BufferedImage img = makeTestImage200x200();

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(100, 100)
					.keepAspectRatio(true)
					.make(img);
		});

	}

	@Test
	public void twoArgConstructorAndAspectRatioAndFitWithinDimensionsSpecified() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void threeArgumentConstructorThenFitWithinDimenions() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100, true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void fourArgumentConstructor() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100, true, true)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioFalseAndFitWithinDimensionsTrueAllowed() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(false)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioFalseAndFitWithinDimensionsFalseAllowed() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(false)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioWithOffRatioTargetSizeForVertical() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(50, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioWithOffRatioTargetSizeForHorizontal() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(50, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void noKeepAspectRatioWithOffRatioTargetSizeForVertical() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50)
				.keepAspectRatio(false)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(50, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void noKeepAspectRatioWithOffRatioTargetSizeForHorizontal() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(false)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForVertical() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50)
				.keepAspectRatio(true)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void keepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForHorizontal() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(true)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void noKeepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForVertical() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50)
				.keepAspectRatio(false)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(100, thumbnail.getWidth());
		assertEquals(50, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void noKeepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForHorizontal() {
		BufferedImage img = makeTestImage200x200();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100)
				.keepAspectRatio(false)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(50, thumbnail.getWidth());
		assertEquals(100, thumbnail.getHeight());
		assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
	}

	@Test
	public void twoArgConstructorThenSize() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100)
					.size(50, 100);
		});

			}

	@Test
	public void threeArgConstructorThenKeepAspectRatio() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100, true)
					.keepAspectRatio(true);
		});

			}

	@Test
	public void threeArgConstructorThenSize() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100, true)
					.size(100, 100);
		});

			}

	@Test
	public void fourArgConstructorThenSize() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100, true, true)
					.size(100, 100);
		});

			}

	@Test
	public void fourArgConstructorThenKeepAspectRatio() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100, true, true)
					.keepAspectRatio(true);
		});

			}

	@Test
	public void fourArgConstructorThenFitWithinDimensions() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			new FixedSizeThumbnailMaker(50, 100, true, true)
					.fitWithinDimensions(true);
		});

			}

	@Test
	public void verifyResizerFactoryBeingCalled() {
		BufferedImage img = makeTestImage200x200();
		Resizer spyResizer = spy(new ProgressiveBilinearResizer());
		ResizerFactory resizerFactory = mock(ResizerFactory.class);
		when(resizerFactory.getResizer(any(Dimension.class), any(Dimension.class)))
				.thenReturn(spyResizer);

		new FixedSizeThumbnailMaker(100, 100)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.resizerFactory(resizerFactory)
				.make(img);

		verify(resizerFactory, atLeastOnce())
				.getResizer(new Dimension(200, 200), new Dimension(100, 100));
		verify(spyResizer).resize(eq(img), any(BufferedImage.class));
	}

	@Test
	public void heightZeroIfTruncatedButOneIfRounded_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(100, 6).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(1, thumbnail.getHeight());
	}

	@Test
	public void heightZeroIfTruncated_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(100, 4).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(1, thumbnail.getHeight());
	}

	@Test
	public void widthZeroIfTruncatedButOneIfRounded_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(6, 100).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(1, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void widthZeroIfTruncated_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(4, 100).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(1, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void sizeGivenZeroForWidthViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(0, 10);
		});

			}

	@Test
	public void sizeGivenZeroForWidthViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(0, 10);
		});

			}

	@Test
	public void sizeGivenZeroForHeightViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(10, 0);
		});

			}

	@Test
	public void sizeGivenZeroForHeightViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(10, 0);
		});

			}

	@Test
	public void sizeGivenZeroForWidthAndHeightViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(0, 0);
		});

			}

	@Test
	public void sizeGivenZeroForWidthAndHeightViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(0, 0);
		});

			}

	@Test
	public void sizeGivenNegativeForWidthViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(-1, 10);
		});

			}

	@Test
	public void sizeGivenNegativeForWidthViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(-1, 10);
		});
	}

	@Test
	public void sizeGivenNegativeForHeightViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(10, -1);
		});
	}

	@Test
	public void sizeGivenNegativeForHeightViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(10, -1);
		});

	}

	@Test
	public void sizeGivenNegativeForWidthAndHeightViaConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker(-1, -1);
		});

	}

	@Test
	public void sizeGivenNegativeForWidthAndHeightViaMethod() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new FixedSizeThumbnailMaker().size(-1, -1);
		});

	}

	@Test
	public void widthBeingRounded_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(99, 100).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void widthBeingRounded_FitWithinFalse() {
		BufferedImage img = new BufferedImageBuilder(99, 100).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void heightBeingRounded_FitWithinTrue() {
		BufferedImage img = new BufferedImageBuilder(100, 99).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(true)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}

	@Test
	public void heightBeingRounded_FitWithinFalse() {
		BufferedImage img = new BufferedImageBuilder(100, 99).build();

		BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10)
				.keepAspectRatio(true)
				.fitWithinDimensions(false)
				.make(img);

		assertEquals(10, thumbnail.getWidth());
		assertEquals(10, thumbnail.getHeight());
	}
}