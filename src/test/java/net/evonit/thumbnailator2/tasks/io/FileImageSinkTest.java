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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;

import net.evonit.thumbnailator2.TestUtils;
import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.tasks.UnsupportedFormatException;
import net.evonit.thumbnailator2.test.BufferedImageComparer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class FileImageSinkTest {

	@Nested
	public class Tests {

		@TempDir
		public File temporaryFolder;

		@Test
		public void validFilename_File() {
			// given
			File f = new File(temporaryFolder, "test.png");

			// when
			FileImageSink sink = new FileImageSink(f);

			// then
			assertEquals(f, sink.getSink());
		}

		@Test
		public void validFilename_String() {
			// given
			String f = temporaryFolder.getAbsolutePath() + "/test.png";

			// when
			FileImageSink sink = new FileImageSink(f);

			// then
			assertEquals(new File(f), sink.getSink());
		}

		@Test
		public void nullFilename_File() {
			// given
			File f = null;

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				new FileImageSink(f);
			});

			// then
			assertEquals("File cannot be null.", exception.getMessage());
		}

		@Test
		public void nullFilename_String() {
			// given
			String f = null;

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				new FileImageSink(f);
			});

			// then
			assertEquals("File cannot be null.", exception.getMessage());
		}

		@Test
		public void write_NullImage() throws IOException {
			// given
			File f = new File(temporaryFolder, "test.png");

			BufferedImage img = null;

			FileImageSink sink = new FileImageSink(f);
			sink.setOutputFormatName("png");

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				sink.write(img);
			});

			// then
			assertEquals("Cannot write a null image.", exception.getMessage());
		}

		@Test
		public void write_ValidImage() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.png");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("png", formatName);
		}

		@Test
		public void write_ValidImage_SetOutputFormatWithSameAsExtension() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.png");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.setOutputFormatName("png");
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("png", formatName);
		}

		@Test
		public void write_ValidImage_SetOutputFormatWithDifferentExtension() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.png");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.setOutputFormatName("JPEG");
			sink.write(imgToWrite);

			// then
			outputFile = new File(temporaryFolder, "test.png.JPEG");

			assertEquals(outputFile.getAbsoluteFile(), sink.getSink().getAbsoluteFile());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("JPEG", formatName);
		}

		@Test
		public void write_ValidImage_SetOutputFormat_OutputFileHasNoExtension() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.setOutputFormatName("JPEG");
			sink.write(imgToWrite);

			// then
			outputFile = new File(temporaryFolder, "test.JPEG");

			assertEquals(outputFile.getAbsoluteFile(), sink.getSink().getAbsoluteFile());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("JPEG", formatName);
		}

		@Test
		public void write_ValidImage_InvalidFileExtension() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.foo");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			assertThrows(UnsupportedFormatException.class, () -> {
				sink.write(imgToWrite);
			});
		}

		@Test
		public void write_ValidImage_InvalidFileExtension_OutputFormatSetToValidFormat() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.foo");
			File actualOutputFile = new File(temporaryFolder, "test.foo.png");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setOutputFormatName("png");

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(actualOutputFile.getCanonicalFile(), sink.getSink().getCanonicalFile());

			BufferedImage writtenImg = ImageIO.read(actualOutputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(actualOutputFile));
			assertEquals("png", formatName);
		}

		@Test
		public void write_ValidImage_WriterCantCompress() throws IOException {
			// given
			ImageWriteParam iwParam = mock(ImageWriteParam.class);
			ImageWriter writer = mock(ImageWriter.class);
			ImageWriterSpi spi = mock(ImageWriterSpi.class);

			when(iwParam.canWriteCompressed()).thenReturn(false);

			when(writer.getDefaultWriteParam()).thenReturn(iwParam);
			when(writer.getOriginatingProvider()).thenReturn(spi);

			when(spi.getFormatNames()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.getFileSuffixes()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.createWriterInstance()).thenReturn(writer);
			when(spi.createWriterInstance(any())).thenReturn(writer);
			IIORegistry.getDefaultInstance().registerServiceProvider(spi);

			File outputFile = new File(temporaryFolder, "test.foo");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(0.8f);
			when(param.getOutputFormatType()).thenReturn(ThumbnailParameter.DEFAULT_FORMAT_TYPE);

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			try {
				// then
				assertEquals(outputFile, sink.getSink());

				verify(iwParam, never()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				verify(iwParam, never()).setCompressionType(anyString());
				verify(iwParam, never()).setCompressionQuality(anyFloat());

				// - check to see that parameters were not read, as this format doesn't
				// support compression.
				verify(param, never()).getOutputQuality();
				verify(param, never()).getOutputFormatType();

			} finally {
				// clean up
				IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
			}
		}

		@Test
		public void write_ValidImage_WriterCanCompress_NoCompressionTypeFromWriter() throws IOException {
			// given
			ImageWriteParam iwParam = mock(ImageWriteParam.class);
			ImageWriter writer = mock(ImageWriter.class);
			ImageWriterSpi spi = mock(ImageWriterSpi.class);

			when(iwParam.canWriteCompressed()).thenReturn(true);
			when(iwParam.getCompressionTypes()).thenReturn(null);

			when(writer.getDefaultWriteParam()).thenReturn(iwParam);
			when(writer.getOriginatingProvider()).thenReturn(spi);

			when(spi.getFormatNames()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.getFileSuffixes()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.createWriterInstance()).thenReturn(writer);
			when(spi.createWriterInstance(any())).thenReturn(writer);
			IIORegistry.getDefaultInstance().registerServiceProvider(spi);

			File outputFile = new File(temporaryFolder, "test.foo");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(0.8f);
			when(param.getOutputFormatType()).thenReturn(ThumbnailParameter.DEFAULT_FORMAT_TYPE);

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			// then
			try {
				assertEquals(outputFile, sink.getSink());

				verify(iwParam, atLeastOnce()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				verify(iwParam, never()).setCompressionType(anyString());
				verify(iwParam, atLeastOnce()).setCompressionQuality(0.8f);

				// - check to see that parameters was read
				verify(param, atLeastOnce()).getOutputQuality();
				verify(param, atLeastOnce()).getOutputFormatType();
			} finally {
				// clean up
				IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
			}
		}

		@Test
		public void write_ValidImage_WriterCanCompress_HasCompressionTypeFromWriter() throws IOException {
			// given
			ImageWriteParam iwParam = mock(ImageWriteParam.class);
			ImageWriter writer = mock(ImageWriter.class);
			ImageWriterSpi spi = mock(ImageWriterSpi.class);

			when(iwParam.canWriteCompressed()).thenReturn(true);
			when(iwParam.getCompressionTypes()).thenReturn(new String[]{"FOOBAR"});

			when(writer.getDefaultWriteParam()).thenReturn(iwParam);
			when(writer.getOriginatingProvider()).thenReturn(spi);

			when(spi.getFormatNames()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.getFileSuffixes()).thenReturn(new String[]{"foo", "FOO"});
			when(spi.createWriterInstance()).thenReturn(writer);
			when(spi.createWriterInstance(any())).thenReturn(writer);
			IIORegistry.getDefaultInstance().registerServiceProvider(spi);

			File outputFile = new File(temporaryFolder, "test.foo");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(0.8f);
			when(param.getOutputFormatType()).thenReturn(ThumbnailParameter.DEFAULT_FORMAT_TYPE);

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			try {
				// then
				assertEquals(outputFile, sink.getSink());

				verify(iwParam, atLeastOnce()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				verify(iwParam, atLeastOnce()).setCompressionType("FOOBAR");
				verify(iwParam, atLeastOnce()).setCompressionQuality(0.8f);

				// - check to see that parameters was read
				verify(param, atLeastOnce()).getOutputQuality();
				verify(param, atLeastOnce()).getOutputFormatType();
			} finally {
				// clean up
				IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
			}
		}

		@Test
		public void write_ValidImage_SetThumbnailParameter_BMP_QualityAndOutputFormatType_BothDefault() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.bmp");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(ThumbnailParameter.DEFAULT_QUALITY);
			when(param.getOutputFormatType()).thenReturn(ThumbnailParameter.DEFAULT_FORMAT_TYPE);

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("bmp", formatName);

			verify(param, atLeastOnce()).getOutputQuality();
			verify(param, atLeastOnce()).getOutputFormatType();
		}

		@Test
		public void write_ValidImage_SetThumbnailParameter_BMP_QualityAndOutputFormatType_BothNonDefault() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.bmp");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(0.5f);
			when(param.getOutputFormatType()).thenReturn("BI_BITFIELDS");

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("bmp", formatName);

			verify(param, atLeastOnce()).getOutputQuality();
			verify(param, atLeastOnce()).getOutputFormatType();
		}

		@Test
		public void write_ValidImage_SetThumbnailParameter_BMP_OutputFormatType() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.bmp");

			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			ThumbnailParameter param = mock(ThumbnailParameter.class);
			when(param.getOutputQuality()).thenReturn(ThumbnailParameter.DEFAULT_QUALITY);
			when(param.getOutputFormatType()).thenReturn("BI_BITFIELDS");

			FileImageSink sink = new FileImageSink(outputFile);
			sink.setThumbnailParameter(param);

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());

			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));

			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals("bmp", formatName);

			verify(param, atLeastOnce()).getOutputFormatType();
		}

		@Test
		public void write_NoExtentionSpecified() throws IOException {
			// set up
			File f = new File(temporaryFolder, "tmp-" + Math.abs(new Random().nextLong()));

			// given
			FileImageSink sink = new FileImageSink(f);

			// when
			assertThrows(UnsupportedFormatException.class, () -> {
				sink.write(new BufferedImageBuilder(100, 100).build());
			});
		}

		@Test
		public void constructorFile_write_allowOverwriteTrue() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile("Thumbnailator/grid.png", temporaryFolder);

			// given
			FileImageSink sink = new FileImageSink(f, true);

			// when
			sink.write(ImageIO.read(f));

			// then
			assertTrue(f.exists());
		}

		@Test
		public void constructorFile_write_allowOverwriteFalse() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile("Thumbnailator/grid.png", temporaryFolder);

			// given
			FileImageSink sink = new FileImageSink(f, false);

			// when
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
				sink.write(ImageIO.read(f));
			});

			// then
			assertEquals("The destination file exists.", exception.getMessage());
		}

		@Test
		public void constructorString_write_allowOverwriteTrue() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			FileImageSink sink = new FileImageSink(f.getAbsolutePath(), true);

			// when
			sink.write(ImageIO.read(f));

			// then
			assertTrue(f.exists());
		}

		@Test
		public void constructorString_write_allowOverwriteFalse() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			FileImageSink sink = new FileImageSink(f.getAbsolutePath(), false);

			// when
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
				sink.write(ImageIO.read(f));
			});

			// then
			assertEquals("The destination file exists.", exception.getMessage());

		}

		// What we really want to check the file resource is released.
		@Test
		public void write_FileDeletableAfterWrite_Issue148() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.png");

			BufferedImage imgToWrite =
					new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());
			assertTrue(outputFile.delete());
		}

		@Test
		public void write_ErrorOnWriteClosesOutputStream() throws IOException {
			// given
			File outputFile = new File(temporaryFolder, "test.png");

			BufferedImage imgToWrite =
					new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

			OutputStream mockOs = mock(OutputStream.class);
			doThrow(new IOException("Write error")).when(mockOs).write(anyInt());
			doThrow(new IOException("Write error")).when(mockOs).write(any(byte[].class));
			doThrow(new IOException("Write error")).when(mockOs).write(any(byte[].class), anyInt(), anyInt());

			FileImageSink sink = spy(new FileImageSink(outputFile));
			doReturn(mockOs).when(sink).createOutputStream(any(File.class));

			// when
			try {
				sink.write(imgToWrite);
				fail();
			} catch (IOException e) {
				assertEquals("Write error", e.getCause().getMessage());
			}

			// then
			verify(mockOs).close();
		}
	}

	private static final List<String> FORMAT_NAMES = Arrays.asList(
			"jpg",
			"jpeg",
			"Jpg",
			"Jpeg",
			"JPG",
			"JPEG",
			"png",
			"Png",
			"PNG",
			null // used for `outputFormat` not being called.
	);

	private static final Map<String, String> EXPECTED_FORMAT_NAMES =
			new HashMap<String, String>() {{
				put("jpg", "JPEG");
				put("jpeg", "JPEG");
				put("png", "png");
			}};

	private static String getExpectedFormat(String s) {
		return EXPECTED_FORMAT_NAMES.get(s.toLowerCase());
	}

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
	public class FormatDependentTests {

		@TempDir
		public File temporaryFolder;

		public Collection<Object[]> testCases() {
			List<Object[]> cases = new ArrayList<>();
			for (String extension : FORMAT_NAMES) {
				if (extension == null) {
					continue;
				}
				String specifiedName = String.format("output.%s", extension);
				String expectedFormat = getExpectedFormat(extension.toLowerCase());
				cases.add(new Object[]{specifiedName, expectedFormat});
			}
			return cases;
		}

		@ParameterizedTest
		@MethodSource("testCases")
		public void writeWithOriginalOutputFormat(String specifiedName, String expectedFormat) throws IOException {
			// given
			File outputFile = new File(temporaryFolder, specifiedName);
			BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			FileImageSink sink = new FileImageSink(outputFile);

			// when
			sink.setOutputFormatName(ThumbnailParameter.ORIGINAL_FORMAT);
			sink.write(imgToWrite);

			// then
			assertEquals(outputFile, sink.getSink());
			BufferedImage writtenImg = ImageIO.read(outputFile);
			assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));
			String formatName = TestUtils.getFormatName(new FileInputStream(outputFile));
			assertEquals(expectedFormat, formatName);
		}

		@ParameterizedTest
		@MethodSource("testCases")
		public void preferredOutputFormatName(String specifiedName, String expectedFormat) {
			// given
			File outputFile = new File(temporaryFolder, specifiedName);

			// when
			FileImageSink sink = new FileImageSink(outputFile);

			// then
			assertEquals(expectedFormat, sink.preferredOutputFormatName());
		}
	}

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
	public class FormatTests {

		@TempDir
		public File temporaryFolder;

		public Collection<Object[]> testCases() {
			List<Object[]> cases = new ArrayList<>();
			for (String extension : FORMAT_NAMES) {
				if (extension == null) {
					continue;
				}
				for (String outputFormat : FORMAT_NAMES) {
					String specifiedName = String.format("output.%s", extension);
					String expectedFormat = getExpectedFormat(outputFormat != null ?
							outputFormat.toLowerCase() :
							extension.toLowerCase()
					);
					String expectedName = specifiedName;
					if (!expectedFormat.equals(getExpectedFormat(extension))) {
						expectedName = String.format("%s.%s", specifiedName, outputFormat);
					}

					cases.add(
							new Object[]{
									specifiedName,
									outputFormat,
									expectedName,
									expectedFormat
							}
					);
				}
			}

			return cases;
		}

		@ParameterizedTest
		@MethodSource("testCases")
		public void test(String specifiedName, String outputFormat, String expectedName, String expectedFormat) throws IOException {
			// set up
			File destinationFile = new File(temporaryFolder, specifiedName);

			// given
			FileImageSink sink = new FileImageSink(destinationFile);
			if (outputFormat != null) {
				sink.setOutputFormatName(outputFormat);
			}

			// when
			sink.write(new BufferedImageBuilder(100, 100).build());

			// then
			File actualDestinationFile = new File(temporaryFolder, expectedName);
			assertEquals(actualDestinationFile, sink.getSink());
			assertTrue(actualDestinationFile.exists());
			assertEquals(
					expectedFormat,
					TestUtils.getFormatName(new FileInputStream(
							new File(temporaryFolder, expectedName))
					)
			);
		}
	}
}
