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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.evonit.thumbnailator2.TestUtils;
import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.geometry.AbsoluteSize;
import net.evonit.thumbnailator2.geometry.Coordinate;
import net.evonit.thumbnailator2.geometry.Positions;
import net.evonit.thumbnailator2.geometry.Region;
import net.evonit.thumbnailator2.test.BufferedImageAssert;
import net.evonit.thumbnailator2.test.BufferedImageComparer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class FileImageSourceTest {

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class Tests {

		@TempDir
		public File temporaryFolder;

		@Test
		public void fileDoesNotExists() throws IOException {
			// given
			File nonExistentFile = new File(temporaryFolder, "nonExistentFile");
			FileImageSource source = new FileImageSource(nonExistentFile);

			FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
				// when
				source.read();
			});

			// then
			assertTrue(exception.getMessage().contains("Could not find file"));
		}

		@Test
		public void fileDoesNotExists_AsString() throws IOException {
			// given
			File nonExistentFile = new File(temporaryFolder, "nonExistentFile");
			FileImageSource source = new FileImageSource(nonExistentFile.getAbsolutePath());

			FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
				// when
				source.read();
			});

			// then
			assertTrue(exception.getMessage().contains("Could not find file"));
		}

		@Test
		public void fileExists_getInputFormatNameBeforeRead() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			FileImageSource source = new FileImageSource(sourceFile);

			IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
				// when
				source.getInputFormatName();
			});

			// then
			assertEquals("Input has not been read yet.", exception.getMessage());
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
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);
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
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);
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
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);
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
			assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
		}

		@Test
		public void appliesSourceRegionNotSpecified() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);
			source.setThumbnailParameter(
					new ThumbnailParameterBuilder()
							.size(20, 20)
							.build()
			);

			// when
			BufferedImage img = source.read();

			// then
			assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
		}

		@Test
		public void useExifOrientationIsTrue_OrientationHonored() throws Exception {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Exif/source_2.jpg", temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(20, 20)
							.useExifOrientation(true)
							.build();

			source.setThumbnailParameter(param);

			// when
			source.read();

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
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Exif/source_2.jpg", temporaryFolder
			);
			FileImageSource source = new FileImageSource(sourceFile);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(20, 20)
							.useExifOrientation(false)
							.build();

			source.setThumbnailParameter(param);

			// when
			BufferedImage result = source.read();

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

		// What we really want to check the file resource is released.
		@Test
		public void canRemoveSourceImage() throws IOException {
			// given
			File inputFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			FileImageSource source = new FileImageSource(inputFile);

			// when
			source.read();

			// then
			assertEquals(inputFile, source.getSource());
			assertTrue(inputFile.exists());
			assertTrue(inputFile.delete());
			assertFalse(inputFile.exists());
		}

		// What we really want to check the file resource is released.
		// Reproducible on Windows, not Linux. (Issue #143)
		@Test
		public void canRemoveSourceImageOnReadFailure() throws IOException {
			// given
			File inputFile = new File(temporaryFolder, "something.png");
			TestUtils.copyFile(
					TestUtils.copyResourceToTemporaryFile(
							"Thumbnailator/grid.png", temporaryFolder
					)
					, inputFile, 200
			);

			FileImageSource source = new FileImageSource(inputFile);

			// when
			try {
				source.read();
				fail();
			} catch (Exception e) {
				// expected
			}

			// then
			assertEquals(inputFile, source.getSource());
			assertTrue(inputFile.exists());
			assertTrue(inputFile.delete());
			assertFalse(inputFile.exists());
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class FileReadTests {

		@TempDir
		public File temporaryFolder;

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.tasks.io.FileImageSourceTest#formats")
		public void fileExistsUsingFile(String format, String expectedFormat) throws IOException {
			test(sourceFile -> new FileImageSource(sourceFile), format, expectedFormat);
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.tasks.io.FileImageSourceTest#formats")
		public void fileExistsUsingString(String format, String expectedFormat) throws IOException {
			test(sourceFile -> new FileImageSource(sourceFile.getAbsolutePath()), format, expectedFormat);
		}

		public void test(FileImageSourceSupplier supplier, String format, String expectedFormat) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Thumbnailator/grid.%s", format), temporaryFolder
			);
			FileImageSource source = supplier.get(sourceFile);

			// when
			BufferedImage img = source.read();

			// then
			assertEquals(100, img.getWidth());
			assertEquals(100, img.getHeight());
			assertEquals(expectedFormat, source.getInputFormatName());
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class OrientationTests {

		@TempDir
		public File temporaryFolder;

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.tasks.io.FileImageSourceTest#values")
		public void readImageUnaffectedByOrientation(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/source_%s.jpg", orientation), temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder().size(20, 20).build();
			source.setThumbnailParameter(param);

			// when
			BufferedImage img = source.read();

			// then
			assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.tasks.io.FileImageSourceTest#values")
		public void containsCorrectFilterForOrientation(int orientation) throws Exception {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/source_%s.jpg", orientation), temporaryFolder
			);
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			FileImageSource source = new FileImageSource(sourceFile);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder().size(20, 20).build();
			source.setThumbnailParameter(param);

			// when
			source.read();

			// then
			if (orientation == 1) {
				assertTrue(param.getImageFilters().isEmpty());
				return;
			}

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
	}

	public static Object[][] formats() {
		return new Object[][] {
				new Object[] { "png", "png" },
				new Object[] { "jpg", "JPEG" },
				new Object[] { "bmp", "bmp" },
		};
	}

	public static Object[] values() {
		return new Object[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	}
}

interface FileImageSourceSupplier {
	FileImageSource get(File sourceFile);
}