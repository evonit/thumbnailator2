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

package net.evonit.thumbnailator2;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.name.Rename;
import net.evonit.thumbnailator2.resizers.DefaultResizerFactory;
import net.evonit.thumbnailator2.resizers.ResizerFactory;
import net.evonit.thumbnailator2.tasks.SourceSinkThumbnailTask;
import net.evonit.thumbnailator2.tasks.UnsupportedFormatException;
import net.evonit.thumbnailator2.tasks.io.BufferedImageSink;
import net.evonit.thumbnailator2.tasks.io.BufferedImageSource;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link Thumbnailator} class.
 *
 */
@Nested
@TestInstance(Lifecycle.PER_CLASS)
public class ThumbnailatorTest {

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	public class Tests {

		@TempDir
		public File temporaryFolder;

		@Test
		public void testHugeSizeImage() throws IOException {
			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/green_background_5000x15000.jpg", temporaryFolder
			);

			BufferedImage thumbnail = Thumbnailator.createThumbnail(sourceFile1, 300, 300);

			assertEquals(100, thumbnail.getWidth());
			assertEquals(300, thumbnail.getHeight());
		}

		@Test
		public void testCreateThumbnailCollections_nullCollection() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnailsAsCollection(
						null,
						Rename.PREFIX_DOT_THUMBNAIL,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnailCollections_nullRename() throws IOException {
			List<File> files = Collections.singletonList(
					new File("nameDoesntMatter.jpg")
			);

			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnailsAsCollection(
						files,
						null,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnails_nullCollection() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnails(
						null,
						Rename.PREFIX_DOT_THUMBNAIL,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnails_nullRename() throws IOException {
			List<File> files = Collections.singletonList(
					new File("nameDoesntMatter.jpg")
			);

			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnails(
						files,
						null,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnail_IOII_nullIS() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				InputStream is = null;
				ByteArrayOutputStream os = new ByteArrayOutputStream();

				Thumbnailator.createThumbnail(is, os, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOII_nullOS() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				byte[] bytes = makeImageData("jpg", 200, 200);
				InputStream is = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream os = null;

				Thumbnailator.createThumbnail(is, os, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOII_nullISnullOS() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnail((InputStream) null, null, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOII_IOExceptionFromIS() throws IOException {
			Assertions.assertThrows(IOException.class, () -> {
				InputStream is = mock(InputStream.class);
				doThrow(new IOException("read error!")).when(is).read();
				doThrow(new IOException("read error!")).when(is).read((byte[]) any());
				doThrow(new IOException("read error!")).when(is).read((byte[]) any(), anyInt(), anyInt());

				ByteArrayOutputStream os = new ByteArrayOutputStream();

				Thumbnailator.createThumbnail(is, os, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOII_IOExceptionFromOS() throws IOException {
			Assertions.assertThrows(IOException.class, () -> {
				byte[] bytes = makeImageData("png", 200, 200);
				InputStream is = new ByteArrayInputStream(bytes);

				OutputStream os = mock(OutputStream.class);
				doThrow(new IOException("write error!")).when(os).write(anyInt());
				doThrow(new IOException("write error!")).when(os).write((byte[]) any());
				doThrow(new IOException("write error!")).when(os).write((byte[]) any(), anyInt(), anyInt());

				Thumbnailator.createThumbnail(is, os, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOSII_IOExceptionFromIS() throws IOException {
			Assertions.assertThrows(IOException.class, () -> {
				InputStream is = mock(InputStream.class);
				doThrow(new IOException("read error!")).when(is).read();
				doThrow(new IOException("read error!")).when(is).read((byte[]) any());
				doThrow(new IOException("read error!")).when(is).read((byte[]) any(), anyInt(), anyInt());

				ByteArrayOutputStream os = new ByteArrayOutputStream();

				Thumbnailator.createThumbnail(is, os, "png", 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_IOSII_IOExceptionFromOS() throws IOException {
			Assertions.assertThrows(IOException.class, () -> {
				byte[] bytes = makeImageData("png", 200, 200);
				InputStream is = new ByteArrayInputStream(bytes);

				OutputStream os = mock(OutputStream.class);
				doThrow(new IOException("write error!")).when(os).write(anyInt());
				doThrow(new IOException("write error!")).when(os).write((byte[]) any());
				doThrow(new IOException("write error!")).when(os).write((byte[]) any(), anyInt(), anyInt());

				Thumbnailator.createThumbnail(is, os, "png", 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_FFII_nullInputFile() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				File inputFile = null;
				File outputFile = new File("bar.jpg");

				Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_FFII_nullOutputFile() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				File inputFile = new File("foo.jpg");
				File outputFile = null;

				Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_FFII_nullInputAndOutputFiles() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnail((File) null, null, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_FFII_nonExistentInputFile() throws IOException {
			File inputFile = new File("foo.jpg");
			File outputFile = new File("bar.jpg");

			IOException exception = assertThrows(IOException.class, () -> {
				Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
			});

			assertEquals("Input file does not exist.", exception.getMessage());
		}

		@Test
		@Disabled
		public void testCreateThumbnail_FFII_IOExceptionOnWrite() throws IOException {
			fail();
		}

		@Test
		public void testCreateThumbnail_FII_nullInputFile() throws IOException {
			Assertions.assertThrows(NullPointerException.class, () -> {
				Thumbnailator.createThumbnail((File) null, 50, 50);
			});
		}

		@Test
		public void testCreateThumbnail_BII_CorrectUsage() {
			BufferedImage img =
					new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build();

			BufferedImage thumbnail = Thumbnailator.createThumbnail(img, 50, 50);

			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
			assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
		}

		@Test
		public void testCreateThumbnail_III_CorrectUsage() {
			BufferedImage img =
					new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build();

			Image thumbnail = Thumbnailator.createThumbnail((Image) img, 50, 50);

			assertEquals(50, thumbnail.getWidth(null));
			assertEquals(50, thumbnail.getHeight(null));
		}

		@Test
		public void testCreateThumbnail_ThumbnailTask_ResizerFactoryBeingUsed_UsingSize() throws IOException {
			BufferedImageSource source = new BufferedImageSource(
					new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build()
			);
			BufferedImageSink sink = new BufferedImageSink();
			ResizerFactory resizerFactory = spy(DefaultResizerFactory.getInstance());

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(100, 100)
							.resizerFactory(resizerFactory)
							.build();

			Thumbnailator.createThumbnail(
					new SourceSinkThumbnailTask<BufferedImage, BufferedImage>(
							param, source, sink
					)
			);

			verify(resizerFactory)
					.getResizer(new Dimension(200, 200), new Dimension(100, 100));
		}

		@Test
		public void testCreateThumbnail_ThumbnailTask_ResizerFactoryBeingUsed_UsingScale() throws IOException {
			BufferedImageSource source = new BufferedImageSource(
					new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build()
			);
			BufferedImageSink sink = new BufferedImageSink();
			ResizerFactory resizerFactory = spy(DefaultResizerFactory.getInstance());

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.scale(0.5)
							.resizerFactory(resizerFactory)
							.build();

			Thumbnailator.createThumbnail(
					new SourceSinkThumbnailTask<BufferedImage, BufferedImage>(
							param, source, sink
					)
			);

			verify(resizerFactory)
					.getResizer(new Dimension(200, 200), new Dimension(100, 100));
		}
	}

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	public class FileIOTests {

		@TempDir
		public File temporaryFolder;

		private boolean isTemporaryFolderEmpty() {
			String[] files = temporaryFolder.list();
			if (files == null) {
				throw new IllegalStateException("Temporary folder didn't exist. Shouldn't happen.");
			}
			return files.length == 0;
		}

		@Test
		public void testCreateThumbnailCollections_NoErrors_EmptyList() throws IOException {
			List<File> files = Collections.emptyList();

			Collection<File> resultingFiles =
					Thumbnailator.createThumbnailsAsCollection(
							files,
							Rename.PREFIX_DOT_THUMBNAIL,
							50,
							50
					);

			assertTrue(resultingFiles.isEmpty());
			assertTrue(isTemporaryFolderEmpty());
		}

		@Test
		public void testCreateThumbnailCollections_NoErrors_EmptySet() throws IOException {
			Set<File> files = Collections.emptySet();

			Collection<File> resultingFiles =
					Thumbnailator.createThumbnailsAsCollection(
							files,
							Rename.PREFIX_DOT_THUMBNAIL,
							50,
							50
					);

			assertTrue(resultingFiles.isEmpty());
			assertTrue(isTemporaryFolderEmpty());
		}

		@Test
		public void testCreateThumbnailCollections_NoErrors() throws IOException {
			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File sourceFile2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			List<File> files = Arrays.asList(sourceFile1, sourceFile2);

			Collection<File> resultingFiles =
					Thumbnailator.createThumbnailsAsCollection(
							files,
							Rename.PREFIX_DOT_THUMBNAIL,
							50,
							50
					);

			Iterator<File> iter = resultingFiles.iterator();

			BufferedImage img0 = ImageIO.read(iter.next());
			assertEquals(50, img0.getWidth());
			assertEquals(50, img0.getHeight());

			BufferedImage img1 = ImageIO.read(iter.next());
			assertEquals(50, img1.getWidth());
			assertEquals(50, img1.getHeight());

			assertTrue(!iter.hasNext());
		}

		@Test
		public void testCreateThumbnailCollections_ErrorDuringProcessing_FileNotFound() throws IOException {
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File nonExistentFile = new File(temporaryFolder, "doesntExist.jpg");

			List<File> files = Arrays.asList(sourceFile, nonExistentFile);

			Assertions.assertThrows(IOException.class, () -> {
				Thumbnailator.createThumbnailsAsCollection(
						files,
						Rename.PREFIX_DOT_THUMBNAIL,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnailCollections_ErrorDuringProcessing_CantWriteThumbnail() throws IOException {
			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File sourceFile2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.gif", temporaryFolder
			);

			List<File> files = Arrays.asList(sourceFile1, sourceFile2);

			Rename brokenRenamer = new Rename() {
				@Override
				public String apply(String name, ThumbnailParameter param) {
					if (name.endsWith(".gif")) {
						return "thumbnail." + name + ".foobar";
					}

					return "thumbnail." + name;
				}
			};

			Assertions.assertThrows(UnsupportedFormatException.class, () -> {
				Thumbnailator.createThumbnailsAsCollection(
						files,
						brokenRenamer,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnailCollections_NoErrors_CollectionExtendsFile() throws IOException {
			class File2 extends File {
				private static final long serialVersionUID = 1L;

				public File2(String pathname) {
					super(pathname);
				}
			}

			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File sourceFile2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			List<File2> files = Arrays.asList(
					new File2(sourceFile1.getAbsolutePath()),
					new File2(sourceFile2.getAbsolutePath())
			);

			Collection<File> resultingFiles =
					Thumbnailator.createThumbnailsAsCollection(
							files,
							Rename.PREFIX_DOT_THUMBNAIL,
							50,
							50
					);

			Iterator<File> iter = resultingFiles.iterator();

			BufferedImage img0 = ImageIO.read(iter.next());
			assertEquals(50, img0.getWidth());
			assertEquals(50, img0.getHeight());

			BufferedImage img1 = ImageIO.read(iter.next());
			assertEquals(50, img1.getWidth());
			assertEquals(50, img1.getHeight());

			assertTrue(!iter.hasNext());
		}

		@Test
		public void testCreateThumbnails_NoErrors_EmptyList() throws IOException {
			List<File> files = Collections.emptyList();

			Thumbnailator.createThumbnails(
					files,
					Rename.PREFIX_DOT_THUMBNAIL,
					50,
					50
			);

			assertTrue(isTemporaryFolderEmpty());
		}

		@Test
		public void testCreateThumbnails_NoErrors_EmptySet() throws IOException {
			Set<File> files = Collections.emptySet();

			Thumbnailator.createThumbnails(
					files,
					Rename.PREFIX_DOT_THUMBNAIL,
					50,
					50
			);

			assertTrue(isTemporaryFolderEmpty());
		}

		@Test
		public void testCreateThumbnails_NoErrors() throws IOException {
			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File sourceFile2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			List<File> files = Arrays.asList(sourceFile1, sourceFile2);

			Thumbnailator.createThumbnails(
					files,
					Rename.PREFIX_DOT_THUMBNAIL,
					50,
					50
			);

			BufferedImage img0 =
					ImageIO.read(new File(temporaryFolder, "thumbnail.grid.jpg"));

			assertEquals(50, img0.getWidth());
			assertEquals(50, img0.getHeight());

			BufferedImage img1 =
					ImageIO.read(new File(temporaryFolder, "thumbnail.grid.png"));

			assertEquals(50, img1.getWidth());
			assertEquals(50, img1.getHeight());
		}

		@Test
		public void testCreateThumbnails_ErrorDuringProcessing_FileNotFound() throws IOException {
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File nonExistentFile = new File(temporaryFolder, "doesntExist.jpg");

			List<File> files = Arrays.asList(sourceFile, nonExistentFile);

			Assertions.assertThrows(IOException.class, () -> {
				Thumbnailator.createThumbnails(
						files,
						Rename.PREFIX_DOT_THUMBNAIL,
						50,
						50
				);
			});
		}

		@Test
		public void testCreateThumbnails_ErrorDuringProcessing_CantWriteThumbnail() throws IOException {
			File sourceFile1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			File sourceFile2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.gif", temporaryFolder
			);

			List<File> files = Arrays.asList(sourceFile1, sourceFile2);

			// This will force an UnsupportedFormatException when trying to output
			// a thumbnail whose source was a gif file.
			Rename brokenRenamer = new Rename() {
				@Override
				public String apply(String name, ThumbnailParameter param) {
					if (name.endsWith(".gif")) {
						return "thumbnail." + name + ".foobar";
					}

					return "thumbnail." + name;
				}
			};

			UnsupportedFormatException exception = Assertions.assertThrows(
					UnsupportedFormatException.class, () -> {
						// This will force an UnsupportedFormatException when trying to output
						// a thumbnail whose source was a gif file.
						Thumbnailator.createThumbnails(
								files,
								brokenRenamer,
								50,
								50
						);
					}
			);

		}

		@Test
		public void renameGivenThumbnailParameter_createThumbnails() throws IOException {
			// given
			Rename rename = mock(Rename.class);
			when(rename.apply(anyString(), any(ThumbnailParameter.class)))
					.thenReturn("thumbnail.grid.png");

			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			Thumbnailator.createThumbnails(
					Collections.singletonList(f), rename, 50, 50
			);

			// then
			ArgumentCaptor<ThumbnailParameter> ac =
					ArgumentCaptor.forClass(ThumbnailParameter.class);

			verify(rename).apply(eq(f.getName()), ac.capture());
			assertEquals(new Dimension(50, 50), ac.getValue().getSize());
		}

		@Test
		public void renameGivenThumbnailParameter_createThumbnailsAsCollection() throws IOException {
			// given
			Rename rename = mock(Rename.class);
			when(rename.apply(anyString(), any(ThumbnailParameter.class)))
					.thenReturn("thumbnail.grid.png");

			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			Thumbnailator.createThumbnailsAsCollection(
					Collections.singletonList(f), rename, 50, 50
			);

			// then
			ArgumentCaptor<ThumbnailParameter> ac =
					ArgumentCaptor.forClass(ThumbnailParameter.class);

			verify(rename).apply(eq(f.getName()), ac.capture());
			assertEquals(new Dimension(50, 50), ac.getValue().getSize());
		}

		@Test
		public void testCreateThumbnail_FFII_invalidOutputFile() throws IOException {
			/*
			 * Actual test
			 */
			File inputFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outputFile = new File(
					temporaryFolder, "@\\#?/^%*&/|!!$:#"
			);

			IOException exception = assertThrows(IOException.class, () -> {
				Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
			});
		}
	}

	public static Object[][] values() {
		return new Object[][] {
				new Object[] {-42, 42},
				new Object[] {42, -42},
				new Object[] {-42, -42}
		};
	}

	@Nested
	public class InvalidDimensionsTests {

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnailCollections(int width, int height) throws IOException {
			List<File> files = Arrays.asList(
					new File("foo.png"),
					new File("bar.jpg")
			);

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnailsAsCollection(
						files,
						Rename.PREFIX_DOT_THUMBNAIL,
						width,
						height
				);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnails(int width, int height) throws IOException {
			List<File> files = Arrays.asList(
					new File("foo.png"),
					new File("bar.jpg")
			);

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnails(
						files,
						Rename.PREFIX_DOT_THUMBNAIL,
						width,
						height
				);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnail_IOII(int width, int height) throws IOException {
			byte[] bytes = makeImageData("jpg", 200, 200);
			InputStream is = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnail(is, os, width, height);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnail_FII(int width, int height) throws IOException {
			File inputFile = new File("foo.jpg");

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnail(inputFile, width, height);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnail_BII(int width, int height) {
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnail(img, width, height);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnail_FFII(int width, int height) throws IOException {
			File inputFile = new File("foo.jpg");
			File outputFile = new File("bar.jpg");

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnail(inputFile, outputFile, width, height);
			});
		}

		@ParameterizedTest
		@MethodSource("net.evonit.thumbnailator2.ThumbnailatorTest#values")
		public void testCreateThumbnail_III(int width, int height) {
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			Assertions.assertThrows(IllegalArgumentException.class, () -> {
				Thumbnailator.createThumbnail((Image) img, width, height);
			});
		}

	}

	/**
	 * Returns test image data as an array of {@code byte}s.
	 *
	 * @param format Image format.
	 * @param width  Image width.
	 * @param height Image height.
	 * @throws IOException When a problem occurs while making image data.
	 * @return A {@code byte[]} of image data.
	 */
	public static byte[] makeImageData(String format, int width, int height)
			throws IOException {
		BufferedImage img = new BufferedImageBuilder(width, height)
				.imageType("jpg".equals(format) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB)
				.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);

		return baos.toByteArray();
	}
}
