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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.imageio.ImageIO;

import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.name.ConsecutivelyNumberedFilenames;
import net.evonit.thumbnailator2.name.Rename;
import net.evonit.thumbnailator2.test.BufferedImageAssert;
import net.evonit.thumbnailator2.test.BufferedImageComparer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ThumbnailsBuilderInputOutputTest {

	@Nested
    class Tests {

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_asBufferedImage_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			BufferedImage thumbnail = Thumbnails.of(img)
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_asBufferedImages_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(img)
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_toOutputStream_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(img)
					.size(50, 50)
					.toOutputStream(os);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>toOutputStreams(Iterable<OutputStream>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_toOutputStreams_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(img)
					.size(50, 50)
					.toOutputStreams(Arrays.asList(os));
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_iterableBufferedImages_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(img)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}



		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_asBufferedImage_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			BufferedImage thumbnail = Thumbnails.of(img)
				.size(100, 100)
				.outputFormat("png")
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_asBufferedImages_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(img)
				.size(100, 100)
				.outputFormat("png")
				.asBufferedImages();

			// then
			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(1, thumbnails.size());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_toOutputStream_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(img)
				.size(50, 50)
				.outputFormat("png")
				.toOutputStream(os);

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toOutputStreams(Iterable<OutputStream>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_toOutputStreams_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(img)
				.size(50, 50)
				.outputFormat("png")
				.toOutputStreams(Arrays.asList(os));

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImage_iterableBufferedImages_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(img)
				.size(50, 50)
				.outputFormat("png")
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_asBufferedImage_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_asBufferedImages_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(img, img)
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_toOutputStream_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
					.size(50, 50)
					.toOutputStream(os);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>toOutputStreams(Iterable<OutputStream>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_toOutputStreams_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(img, img)
					.size(50, 50)
					.toOutputStreams(Arrays.asList(os));
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_iterableBufferedImages_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(img, img)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail1 = iter.next();
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = iter.next();
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_asBufferedImage_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
					.size(100, 100)
					.outputFormat("png")
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_asBufferedImages_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(img, img)
				.size(100, 100)
				.outputFormat("png")
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());

			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_toOutputStream_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
					.size(50, 50)
					.outputFormat("png")
					.toOutputStream(os);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toOutputStreams(Iterable<OutputStream>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_toOutputStreams_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();

			// when
			Thumbnails.of(img, img)
				.size(50, 50)
				.outputFormat("png")
				.toOutputStreams(Arrays.asList(os1, os2));

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os1.toByteArray()));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			thumbnail = ImageIO.read(new ByteArrayInputStream(os2.toByteArray()));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_BufferedImages_iterableBufferedImages_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(img, img)
				.size(50, 50)
				.outputFormat("png")
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromImages_Single_asBufferedImage() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			BufferedImage thumbnail = Thumbnails.fromImages(Arrays.asList(img))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([BufferedImage, BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImages_Multiple_asBufferedImage() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromImages(Arrays.asList(img, img))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([BufferedImage])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImages_Single_asBufferedImages() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromImages(Arrays.asList(img))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([BufferedImage, BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImages_Multiple_asBufferedImages() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromImages(Arrays.asList(img, img))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromImagesIterable_Single_asBufferedImage() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			BufferedImage thumbnail = Thumbnails.fromImages((Iterable<BufferedImage>)Arrays.asList(img))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[BufferedImage, BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImagesIterable_Multiple_asBufferedImage() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromImages((Iterable<BufferedImage>)Arrays.asList(img, img))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[BufferedImage])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImagesIterable_Single_asBufferedImages() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromImages((Iterable<BufferedImage>)Arrays.asList(img))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[BufferedImage, BufferedImage])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromImagesIterable_Multiple_asBufferedImages() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromImages((Iterable<BufferedImage>)Arrays.asList(img, img))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_asBufferedImage() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.of(f1)
				.size(50, 50)
				.asBufferedImage();

			// then
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_asBufferedImages() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(f1)
				.size(50, 50)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage thumbnail = thumbnails.get(0);
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_toOutputStream() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f1)
				.size(50, 50)
				.toOutputStream(os);

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_toOutputStreams() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f1)
				.size(50, 50)
				.toOutputStreams(Arrays.asList(os));

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_iterableBufferedImages() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(f1)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_asBufferedImage() throws IOException {
			// given
			URL f = TestUtils.getResource("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
					.size(50, 50)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and returned as BufferedImages in a List</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_asBufferedImages() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");
			URL f2 = TestUtils.getResource("Thumbnailator/grid.jpg");

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(f1, f2)
				.size(50, 50)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage thumbnail1 = thumbnails.get(0);
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = thumbnails.get(1);
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_toOutputStream() throws IOException {
			// given
			URL f = TestUtils.getResource("Thumbnailator/grid.png");
			OutputStream os = mock(OutputStream.class);

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
					.size(50, 50)
					.toOutputStream(os);
			});
			verifyNoInteractions(os);
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing will be successful.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_toOutputStreams() throws IOException {
			// given
			URL f = TestUtils.getResource("Thumbnailator/grid.png");
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f, f)
				.size(50, 50)
				.toOutputStreams(Arrays.asList(os1, os2));

			//then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os1.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os1.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			thumbnail = ImageIO.read(new ByteArrayInputStream(os2.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os2.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and an Iterable which can iterate over the
		 * two BufferedImages is returned.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_iterableBufferedImages() throws IOException {
			// given
			URL f1 = TestUtils.getResource("Thumbnailator/grid.png");
			URL f2 = TestUtils.getResource("Thumbnailator/grid.jpg");

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(f1, f2)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail1 = iter.next();
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = iter.next();
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromURLs_Single_asBufferedImage() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.fromURLs(Arrays.asList(url))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([URL, URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLs_Multiple_asBufferedImage() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromURLs(Arrays.asList(url, url))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([URL])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLs_Single_asBufferedImages() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromURLs(Arrays.asList(url))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([URL, URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLs_Multiple_asBufferedImages() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromURLs(Arrays.asList(url, url))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromURLsIterable_Single_asBufferedImage() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.fromURLs((Iterable<URL>)Arrays.asList(url))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[URL, URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLsIterable_Multiple_asBufferedImage() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromURLs((Iterable<URL>)Arrays.asList(url, url))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[URL])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLsIterable_Single_asBufferedImages() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromURLs((Iterable<URL>)Arrays.asList(url))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[URL, URL])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromURLsIterable_Multiple_asBufferedImages() throws IOException {
			// given
			URL url = TestUtils.getResource("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromURLs((Iterable<URL>)Arrays.asList(url, url))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.of(is)
				.size(50, 50)
				.asBufferedImage();

			// then
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_asBufferedImages() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(is)
				.size(50, 50)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage thumbnail = thumbnails.get(0);
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_toOutputStream() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(is)
				.size(50, 50)
				.toOutputStream(os);

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_toOutputStreams() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(is)
				.size(50, 50)
				.toOutputStreams(Arrays.asList(os));

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_iterableBufferedImages() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(is)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(is, is)
					.size(50, 50)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and returned as BufferedImages in a List</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_asBufferedImages() throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(is1, is2)
				.size(50, 50)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage thumbnail1 = thumbnails.get(0);
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = thumbnails.get(1);
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_toOutputStream() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			OutputStream os = mock(OutputStream.class);

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(is, is)
					.size(50, 50)
					.toOutputStream(os);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing will be successful.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_toOutputStreams() throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();

			// when
			Thumbnails.of(is1, is2)
				.size(50, 50)
				.toOutputStreams(Arrays.asList(os1, os2));

			//then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os1.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os1.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			thumbnail = ImageIO.read(new ByteArrayInputStream(os2.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os2.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and an Iterable which can iterate over the
		 * two BufferedImages is returned.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_iterableBufferedImages() throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(is1, is2)
				.size(50, 50)
				.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail1 = iter.next();
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = iter.next();
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreams_Single_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.fromInputStreams(Arrays.asList(is))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([InputStream, InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreams_Multiple_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromInputStreams(Arrays.asList(is, is))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([InputStream])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreams_Single_asBufferedImages() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromInputStreams(Arrays.asList(is))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages([InputStream, InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreams_Multiple_asBufferedImages() throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromInputStreams(Arrays.asList(is1, is2))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>A BufferedImage is returned</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreamsIterable_Single_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			BufferedImage thumbnail = Thumbnails.fromInputStreams((Iterable<InputStream>)Arrays.asList(is))
				.size(100, 100)
				.asBufferedImage();

			// then
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[InputStream, InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreamsIterable_Multiple_asBufferedImage() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromInputStreams((Iterable<InputStream>)Arrays.asList(is, is))
					.size(100, 100)
					.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[InputStream])</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreamsIterable_Single_asBufferedImages() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromInputStreams((Iterable<InputStream>)Arrays.asList(is))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromImages(Iterable[InputStream, InputStream])</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalStateException is thrown.</li>
		 * </ol>
		 */
		@Test
		public void fromInputStreamsIterable_Multiple_asBufferedImages() throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.png");

			// when
			List<BufferedImage> thumbnails = Thumbnails.fromInputStreams((Iterable<InputStream>)Arrays.asList(is1, is2))
				.size(100, 100)
				.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			assertEquals(100, thumbnails.get(0).getWidth());
			assertEquals(100, thumbnails.get(0).getHeight());
			assertEquals(100, thumbnails.get(1).getWidth());
			assertEquals(100, thumbnails.get(1).getHeight());
		}

		@Test
		public void useExifOrientationIsTrue_OrientationHonored() throws IOException {
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

			InputStream source1 = TestUtils.getResourceStream("Exif/source_2.jpg");
			InputStream source2 = TestUtils.getResourceStream("Exif/source_1.jpg");

			Thumbnails.of(source1, source2)
				.size(100, 100)
				.useExifOrientation(true)
				.toOutputStreams(Arrays.asList(baos1, baos2));

			BufferedImageAssert.assertMatches(
					ImageIO.read(new ByteArrayInputStream(baos1.toByteArray())),
					new float[] {
							1, 1, 1,
							1, 1, 1,
							1, 0, 0,
					}
			);
			BufferedImageAssert.assertMatches(
					ImageIO.read(new ByteArrayInputStream(baos2.toByteArray())),
					new float[] {
							1, 1, 1,
							1, 1, 1,
							1, 0, 0,
					}
			);
		}

		@Test
		public void useExifOrientationIsFalse_OrientationIgnored() throws IOException {
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

			InputStream source1 = TestUtils.getResourceStream("Exif/source_2.jpg");
			InputStream source2 = TestUtils.getResourceStream("Exif/source_1.jpg");

			Thumbnails.of(source1, source2)				.size(100, 100)
				.useExifOrientation(false)
				.toOutputStreams(Arrays.asList(baos1, baos2));

			BufferedImageAssert.assertMatches(
					ImageIO.read(new ByteArrayInputStream(baos1.toByteArray())),
					new float[] {
							1, 1, 1,
							1, 1, 1,
							0, 0, 1,
					}
			);
			BufferedImageAssert.assertMatches(
					ImageIO.read(new ByteArrayInputStream(baos2.toByteArray())),
					new float[] {
							1, 1, 1,
							1, 1, 1,
							1, 0, 0,
					}
			);
		}

		@Test
		public void toOutputStreamFailsWithoutOutputFormatSpecifiedForBufferedImage() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				// when
				Thumbnails.of(img)
					.size(100, 100)
					.toOutputStream(baos);

				fail();
			} catch (Exception e) {
				// then
			}
		}

		@Test
		public void toOutputStreamImageFormatMatchesInputForPngStream() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(is)
				.size(100, 100)
				.toOutputStream(baos);

			// then
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesInputForJpegStream() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.jpg");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(is)
				.size(100, 100)
				.toOutputStream(baos);

			// then
			assertEquals("JPEG", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesOutputFormatForPngWithBufferedImageInput() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(img)
				.size(100, 100)
				.outputFormat("png")
				.toOutputStream(baos);

			// then
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesOutputFormatForJpegWithBufferedImageInput() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(img)
				.size(100, 100)
				.outputFormat("JPEG")
				.toOutputStream(baos);

			// then
			assertEquals("JPEG", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}
	}

	/**
	 * These tests check the extension matching functionality:
	 *
	 * <ul>
	 * <li>
	 * If the destination file's extension doesn't match the format of
	 * the source image, an appropriate extension is appended.
	 * </li>
	 * <li>
	 * If the outputFormat is explicitly set and the destination file
	 * extension does not match, an appropriate extension is appended.
	 * </li>
	 * </ul>
	 */
	@Nested
    class FileToFileFormatAndNameTests {

		private final static List<String> FORMAT_NAMES = Arrays.asList(
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

		public static Collection<Object> testCases() {
			List<Object[]> cases = new ArrayList<Object[]>();

			Map<String, String> expectedFormatNames = new HashMap<String, String>() {{
				put("jpg", "JPEG");
				put("jpeg", "JPEG");
				put("png", "png");
			}};

			for (String sourceExtension : FORMAT_NAMES) {
				// Extension of null is not valid.
				if (sourceExtension == null) {
					continue;
				}
				for (String destinationExtension : FORMAT_NAMES) {
					// Extension of null is not valid.
					if (destinationExtension == null) {
						continue;
					}
					for (String outputFormat : FORMAT_NAMES) {
						cases.add(
								new Object[] {
										sourceExtension,
										destinationExtension,
										outputFormat,
										expectedFormatNames.get(
												outputFormat != null ?
														outputFormat.toLowerCase() :
														destinationExtension.toLowerCase()
										)
								}
						);
					}
				}
			}

			return Arrays.asList(cases.toArray());
		}

		@TempDir
		public File temporaryFolder;

		@ParameterizedTest
		@MethodSource("testCases")
		public void checkDestinationNameAndFormat(String sourceExtension, String destinationExtension, String outputFormat, String expectedFormat) throws IOException {
			// Choose the proper source image based on sourceExtension for this test case.
			String extension = getCanonicalFormat(sourceExtension);
			File sourceFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid." + extension,
					new File(temporaryFolder, "grid." + sourceExtension)
			);
			File destFile = new File(temporaryFolder, "output." + destinationExtension);

			// Add call to `.outputFormat` if test case has it.
			if (outputFormat != null) {
				Thumbnails.of(sourceFile)
						.size(10, 10)
						.outputFormat(outputFormat)
						.toFile(destFile);
			} else {
				Thumbnails.of(sourceFile)
						.size(10, 10)
						.toFile(destFile);
			}

			// then
			if (outputFormat != null &&
					!getCanonicalFormat(destinationExtension).equals(getCanonicalFormat(outputFormat))) {
				destFile = new File(destFile.getAbsolutePath() + "." + outputFormat);
			}
			assertTrue(destFile.exists());
			assertEquals(expectedFormat, TestUtils.getFormatName(new FileInputStream(destFile)));
		}

		private static String getCanonicalFormat(String s) {
			if (s == null) {
				return null;
			}
			return s.equalsIgnoreCase("png") ? "png" : "jpg";
		}
	}

	@Nested
    class OrientationTests {

		public static Object[] values() {
			return new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		}

		@TempDir
		public File temporaryFolder;

		private void assertOrientation(BufferedImage result) {
			BufferedImageAssert.assertMatches(
					result,
					new float[] {
							1, 1, 1,
							1, 1, 1,
							1, 0, 0,
					}
			);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationFromFile(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/source_%s.jpg", orientation),
					temporaryFolder
			);

			// when
			BufferedImage result =
					Thumbnails.of(sourceFile)
							.size(100, 100)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(100, result.getWidth());
			assertEquals(100, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationWideFromFile(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/sourceWide_%s.jpg", orientation),
					temporaryFolder
			);

			// when
			BufferedImage result =
					Thumbnails.of(sourceFile)
							.size(80, 40)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(80, result.getWidth());
			assertEquals(40, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationTallFromFile(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/sourceTall_%s.jpg", orientation),
					temporaryFolder
			);

			// when
			BufferedImage result =
					Thumbnails.of(sourceFile)
							.size(40, 80)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(40, result.getWidth());
			assertEquals(80, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationWideScaleFromFile(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/sourceWide_%s.jpg", orientation),
					temporaryFolder
			);

			// when
			BufferedImage result =
					Thumbnails.of(sourceFile)
							.scale(0.5)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(80, result.getWidth());
			assertEquals(40, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationTallScaleFromFile(int orientation) throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					String.format("Exif/sourceTall_%s.jpg", orientation),
					temporaryFolder
			);

			// when
			BufferedImage result =
					Thumbnails.of(sourceFile)
							.scale(0.5)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(40, result.getWidth());
			assertEquals(80, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationFromInputStream(int orientation) throws Exception {
			// given
			InputStream is = TestUtils.getResourceStream(
					String.format("Exif/source_%s.jpg", orientation)
			);

			// when
			BufferedImage result =
					Thumbnails.of(is)
							.size(100, 100)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(100, result.getWidth());
			assertEquals(100, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationWideFromInputStream(int orientation) throws Exception {
			// given
			InputStream is = TestUtils.getResourceStream(
					String.format("Exif/sourceWide_%s.jpg", orientation)
			);

			// when
			BufferedImage result =
					Thumbnails.of(is)
							.size(80, 40)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(80, result.getWidth());
			assertEquals(40, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationTallFromInputStream(int orientation) throws Exception {
			// given
			InputStream is = TestUtils.getResourceStream(
					String.format("Exif/sourceTall_%s.jpg", orientation)
			);

			// when
			BufferedImage result =
					Thumbnails.of(is)
							.size(40, 80)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(40, result.getWidth());
			assertEquals(80, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationWideScaleFromInputStream(int orientation) throws Exception {
			// given
			InputStream is = TestUtils.getResourceStream(
					String.format("Exif/sourceWide_%s.jpg", orientation)
			);

			// when
			BufferedImage result =
					Thumbnails.of(is)
							.scale(0.5)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(80, result.getWidth());
			assertEquals(40, result.getHeight());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void correctOrientationTallScaleFromInputStream(int orientation) throws Exception {
			// given
			InputStream is = TestUtils.getResourceStream(
					String.format("Exif/sourceTall_%s.jpg", orientation)
			);

			// when
			BufferedImage result =
					Thumbnails.of(is)
							.scale(0.5)
							.asBufferedImage();

			// then
			assertOrientation(result);
			assertEquals(40, result.getWidth());
			assertEquals(80, result.getHeight());
		}
	}

	@Nested
    class MultipleCallsCorrectOrientationTests {

		public static Object[] values() {
			return new Object[] { true, false };
		}

		@TempDir
		public File temporaryFolder;

		private List<InputStream> getSources(boolean isSame) throws IOException {
			return Arrays.asList(
					isSame ?
							TestUtils.getResourceStream("Exif/source_2.jpg") :
							TestUtils.getResourceStream("Exif/source_1.jpg"),
					TestUtils.getResourceStream("Exif/source_1.jpg")
			);
		}

		public List<File> getFileSources(boolean isSame) throws IOException {
			String source = isSame ? "Exif/source_1.jpg" : "Exif/source_2.jpg";
			return Arrays.asList(
					TestUtils.copyResourceToTemporaryFile(
							source, "first.jpg", temporaryFolder
					),
					TestUtils.copyResourceToTemporaryFile(
							"Exif/source_1.jpg", "second.jpg", temporaryFolder
					)
			);
		}

		private void assertOrientation(BufferedImage img) {
			BufferedImageAssert.assertMatches(
					img,
					new float[] {
							1, 1, 1,
							1, 1, 1,
							1, 0, 0,
					}
			);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_asBufferedImages(boolean isSame) throws IOException {
			// given
			// when
			List<BufferedImage> results =
					Thumbnails.fromInputStreams(getSources(isSame))
							.size(100, 100)
							.asBufferedImages();

			// then
			assertEquals(results.size(), 2);
			for (BufferedImage result : results) {
				assertOrientation(result);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_iterableBufferedImages(boolean isSame) throws IOException {
			// given
			// when
			Iterable<BufferedImage> results =
					Thumbnails.fromInputStreams(getSources(isSame))
							.size(100, 100)
							.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = results.iterator();
			assertOrientation(iter.next());
			assertOrientation(iter.next());
			assertFalse(iter.hasNext());
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_asFiles(boolean isSame) throws IOException {
			// given
			File outFile1 = new File(temporaryFolder, "out1.jpg");
			File outFile2 = new File(temporaryFolder, "out2.jpg");

			// when
			List<File> results =
					Thumbnails.fromInputStreams(getSources(isSame))
							.size(100, 100)
							.asFiles(Arrays.asList(outFile1, outFile2));

			// then
			assertEquals(results.size(), 2);
			for (File result : results) {
				assertOrientation(ImageIO.read(result));
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_asFilesRename(boolean isSame) throws IOException {
			// given
			// when
			List<File> results =
					Thumbnails.fromFiles(getFileSources(isSame))
							.size(100, 100)
							.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(results.size(), 2);
			for (File result : results) {
				assertOrientation(ImageIO.read(result));
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_toFiles(boolean isSame) throws IOException {
			// given
			File outFile1 = new File(temporaryFolder, "out1.jpg");
			File outFile2 = new File(temporaryFolder, "out2.jpg");

			// when
			Thumbnails.fromInputStreams(getSources(isSame))
					.size(100, 100)
					.toFiles(Arrays.asList(outFile1, outFile2));

			// then
			assertOrientation(ImageIO.read(outFile1));
			assertOrientation(ImageIO.read(outFile2));
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_toFilesRename(boolean isSame) throws IOException {
			// given
			List<File> sources = getFileSources(isSame);

			// when
			Thumbnails.fromFiles(sources)
					.size(100, 100)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			for (File source : sources) {
				assertOrientation(
						ImageIO.read(
								new File(
										temporaryFolder, "thumbnail." + source.getName()
								)
						)
				);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void multipleCallsCorrectOrientation_toOutputStreams(boolean isSame) throws IOException {
			// given
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();

			// when
			Thumbnails.fromInputStreams(getSources(isSame))
					.size(100, 100)
					.toOutputStreams(Arrays.asList(os1, os2));

			// then
			assertOrientation(ImageIO.read(new ByteArrayInputStream(os1.toByteArray())));
			assertOrientation(ImageIO.read(new ByteArrayInputStream(os2.toByteArray())));
		}
	}

	@Nested
    class FilesOutputSingleFileTests {

		public static Object[][] values() {
			return new Object[][] {
					new Object[] { true, true, true },
					new Object[] { false, true, true },
					new Object[] { true, false, false },
					new Object[] { false, false, true }
			};
		}

		@TempDir
		public File temporaryFolder;

		// States for tests.
		File outputFile;

		private void verify(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// then
			if (expectOutputChanged) {
				assertImageExists(outputFile, 50, 50);
			} else {
				assertImageExists(outputFile, 100, 100);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFileToFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			outputFile = new File(temporaryFolder, "first.png");

			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (outputFileExists) {
				TestUtils.copyFile(originalFile, outputFile);
			}

			Thumbnails.of(originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Collections.singletonList(outputFile));

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFileAsFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			outputFile = new File(temporaryFolder, "first.png");

			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (outputFileExists) {
				TestUtils.copyFile(originalFile, outputFile);
			}

			List<File> outputFiles = Thumbnails.of(originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Collections.singletonList(outputFile));

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				assertEquals(outputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFileToFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;

			File originalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "first.png")
			);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(50, 50)
							.build();
			outputFile = new File(
					temporaryFolder, rename.apply(originalFile.getName(), param)
			);

			if (outputFileExists) {
				TestUtils.copyFile(originalFile, outputFile);
			}

			Thumbnails.of(originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(rename);

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFileAsFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;

			File originalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "first.png")
			);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(50, 50)
							.build();
			outputFile = new File(
					temporaryFolder, rename.apply(originalFile.getName(), param)
			);

			if (outputFileExists) {
				TestUtils.copyFile(originalFile, outputFile);
			}

			List<File> outputFiles = Thumbnails.of(originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(rename);

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				assertEquals(outputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImageToFilesIterableNoOutputFormatSpecified(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			outputFile = new File(temporaryFolder, "first.png");
			if (outputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", outputFile);
			}

			// when
			Thumbnails.of(img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Collections.singletonList(outputFile));

			// then
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(outputFile)));
			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImageToFilesIterableOutputFormatSpecified(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			outputFile = new File(temporaryFolder, "first.png");
			if (outputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", outputFile);
			}

			// when
			Thumbnails.of(img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.outputFormat("png")
					.toFiles(Collections.singletonList(outputFile));

			// then
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(outputFile)));
			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImageAsFilesIterableNoOutputFormatSpecified(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			outputFile = new File(temporaryFolder, "first.png");
			if (outputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", outputFile);
			}

			// when
			List<File> outputFiles = Thumbnails.of(img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Collections.singletonList(outputFile));

			// then
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				File f = verificationQueue.remove();
				assertEquals(outputFile, f);
				assertEquals("png", TestUtils.getFormatName(new FileInputStream(f)));
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImageAsFilesIterableOutputFormatSpecified(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			outputFile = new File(temporaryFolder, "first.png");
			if (outputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", outputFile);
			}

			// when
			List<File> outputFiles = Thumbnails.of(img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.outputFormat("png")
					.asFiles(Collections.singletonList(outputFile));

			// then
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				File f = verificationQueue.remove();
				assertEquals(outputFile, f);
				assertEquals("png", TestUtils.getFormatName(new FileInputStream(f)));
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlToFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			outputFile = new File(temporaryFolder, "first.png");
			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");

			if (outputFileExists) {
				TestUtils.copyFile(
						TestUtils.copyResourceToTemporaryFile(
								"Thumbnailator/grid.png",
								temporaryFolder
						), outputFile
				);
			}

			Thumbnails.of(originalUrl)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Collections.singletonList(outputFile));

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlAsFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			outputFile = new File(temporaryFolder, "first.png");
			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");

			if (outputFileExists) {
				TestUtils.copyFile(
						TestUtils.copyResourceToTemporaryFile(
								"Thumbnailator/grid.png",
								temporaryFolder
						), outputFile
				);
			}

			List<File> outputFiles = Thumbnails.of(originalUrl)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Collections.singletonList(outputFile));

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				assertEquals(outputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlToFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;
			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");

			try {
				Thumbnails.of(originalUrl)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.toFiles(rename);
				fail();

			} catch (IllegalStateException e) {
				assertEquals(
						"Cannot create thumbnails to files if original images are not from files.",
						e.getMessage()
				);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlAsFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;
			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");

			try {
				Thumbnails.of(originalUrl)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.asFiles(rename);
				fail();

			} catch (IllegalStateException e) {
				assertEquals(
						"Cannot create thumbnails to files if original images are not from files.",
						e.getMessage()
				);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamToFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			outputFile = new File(temporaryFolder, "first.png");
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			if (outputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png",
						outputFile
				);
			}

			// when
			Thumbnails.of(is)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Collections.singletonList(outputFile));

			// then
			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamAsFilesIterable(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			outputFile = new File(temporaryFolder, "first.png");
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			if (outputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png",
						outputFile
				);
			}

			// when
			List<File> outputFiles = Thumbnails.of(is)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Collections.singletonList(outputFile));

			// then
			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectOutputChanged) {
				expectedListSize++;
				assertEquals(outputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(outputFileExists, allowOverwrite, expectOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamToFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(is)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			});
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamAsFilesRename(boolean outputFileExists, boolean allowOverwrite, boolean expectOutputChanged) throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(is)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.asFiles(Rename.PREFIX_DOT_THUMBNAIL);
			});
		}

		private void assertImageExists(File f, int width, int height) throws IOException {
			assertTrue(f.exists());

			BufferedImage img = ImageIO.read(f);
			assertNotNull(img);
			assertEquals(width, img.getWidth());
			assertEquals(height, img.getHeight());
		}
	}

	@Nested
    class FilesOutputMultipleFilesTests {

		public static Object[][] values() {
			return new Object[][] {
					new Object[] { true, true, true, true, true },
					new Object[] { true, false, true, true, true },
					new Object[] { false, true, true, true, true },
					new Object[] { false, false, true, true, true },
					new Object[] { true, true, false, false, false },
					new Object[] { true, false, false, false, true },
					new Object[] { false, true, false, true, false },
					new Object[] { false, false, false, true, true }
			};
		}

		@TempDir
		public File temporaryFolder;

		// States for tests.
		File firstOutputFile;
		File secondOutputFile;

		private void verify(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			// then
			if (expectFirstOutputChanged) {
				assertImageExists(firstOutputFile, 50, 50);
			} else {
				assertImageExists(firstOutputFile, 100, 100);
			}

			if (expectSecondOutputChanged) {
				assertImageExists(secondOutputFile, 50, 50);
			} else {
				assertImageExists(secondOutputFile, 100, 100);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFilesToFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(originalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(originalFile, secondOutputFile);
			}

			Thumbnails.of(originalFile, originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFilesAsFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(originalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(originalFile, secondOutputFile);
			}

			List<File> outputFiles = Thumbnails.of(originalFile, originalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFilesToFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;

			File firstOriginalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "first.png")
			);
			File secondOriginalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "second.png")
			);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(50, 50)
							.build();
			firstOutputFile = new File(
					temporaryFolder, rename.apply(firstOriginalFile.getName(), param)
			);
			secondOutputFile = new File(
					temporaryFolder, rename.apply(secondOriginalFile.getName(), param)
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(firstOriginalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(secondOriginalFile, secondOutputFile);
			}

			Thumbnails.of(firstOriginalFile, secondOriginalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(rename);

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofFilesAsFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;

			File firstOriginalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "first.png")
			);
			File secondOriginalFile = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(temporaryFolder, "second.png")
			);

			ThumbnailParameter param =
					new ThumbnailParameterBuilder()
							.size(50, 50)
							.build();
			firstOutputFile = new File(
					temporaryFolder, rename.apply(firstOriginalFile.getName(), param)
			);
			secondOutputFile = new File(
					temporaryFolder, rename.apply(secondOriginalFile.getName(), param)
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(firstOriginalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(secondOriginalFile, secondOutputFile);
			}

			List<File> outputFiles = Thumbnails.of(firstOriginalFile, secondOriginalFile)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(rename);

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}


		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImagesToFilesIterableNoOutputFormatSpecified(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", firstOutputFile
				);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", secondOutputFile
				);
			}

			Thumbnails.of(img, img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImagesToFilesIterableOutputFormatSpecified(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", firstOutputFile
				);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", secondOutputFile
				);
			}

			Thumbnails.of(img, img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.outputFormat("png")
					.toFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			assertEquals("png", TestUtils.getFormatName(new FileInputStream(firstOutputFile)));
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(secondOutputFile)));
			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImagesAsFilesIterableNoOutputFormatSpecified(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", firstOutputFile
				);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", secondOutputFile
				);
			}

			List<File> outputFiles = Thumbnails.of(img, img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofBufferedImagesAsFilesIterableOutputFormatSpecified(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", firstOutputFile
				);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile(
						"Thumbnailator/grid.png", secondOutputFile
				);
			}

			List<File> outputFiles = Thumbnails.of(img, img)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.outputFormat("png")
					.asFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals("png", TestUtils.getFormatName(new FileInputStream(firstOutputFile)));
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals("png", TestUtils.getFormatName(new FileInputStream(firstOutputFile)));
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlsToFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");
			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(originalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(originalFile, secondOutputFile);
			}

			Thumbnails.of(originalUrl, originalUrl)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlsAsFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			URL originalUrl = TestUtils.getResource("Thumbnailator/grid.png");
			File originalFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png",
					temporaryFolder
			);

			if (firstOutputFileExists) {
				TestUtils.copyFile(originalFile, firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyFile(originalFile, secondOutputFile);
			}

			List<File> outputFiles = Thumbnails.of(originalUrl, originalUrl)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlsToFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;
			URL firstOriginalUrl = TestUtils.getResource("Thumbnailator/grid.png");
			URL secondOriginalUrl = TestUtils.getResource("Thumbnailator/grid.jpg");

			try {
				Thumbnails.of(firstOriginalUrl, secondOriginalUrl)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.toFiles(rename);
				fail();

			} catch (IllegalStateException e) {
				assertEquals(
						"Cannot create thumbnails to files if original images are not from files.",
						e.getMessage()
				);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofUrlsAsFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			Rename rename = Rename.PREFIX_DOT_THUMBNAIL;
			URL firstOriginalUrl = TestUtils.getResource("Thumbnailator/grid.png");
			URL secondOriginalUrl = TestUtils.getResource("Thumbnailator/grid.jpg");

			try {
				Thumbnails.of(firstOriginalUrl, secondOriginalUrl)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.asFiles(rename);
				fail();

			} catch (IllegalStateException e) {
				assertEquals(
						"Cannot create thumbnails to files if original images are not from files.",
						e.getMessage()
				);
			}
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamsToFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.jpg", secondOutputFile);
			}

			Thumbnails.of(is1, is2)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.toFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamsAsFilesIterable(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			firstOutputFile = new File(temporaryFolder, "first.png");
			secondOutputFile = new File(temporaryFolder, "second.png");

			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			if (firstOutputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.png", firstOutputFile);
			}
			if (secondOutputFileExists) {
				TestUtils.copyResourceToFile("Thumbnailator/grid.jpg", secondOutputFile);
			}

			List<File> outputFiles = Thumbnails.of(is1, is2)
					.size(50, 50)
					.allowOverwrite(allowOverwrite)
					.asFiles(Arrays.asList(firstOutputFile, secondOutputFile));

			// Check contents of returned list.
			Queue<File> verificationQueue = new LinkedList<File>(outputFiles);
			int expectedListSize = 0;
			if (expectFirstOutputChanged) {
				expectedListSize++;
				assertEquals(firstOutputFile, verificationQueue.remove());
			}
			if (expectSecondOutputChanged) {
				expectedListSize++;
				assertEquals(secondOutputFile, verificationQueue.remove());
			}
			assertEquals(0, verificationQueue.size());
			assertEquals(expectedListSize, outputFiles.size());

			verify(firstOutputFileExists, secondOutputFileExists, allowOverwrite, expectFirstOutputChanged, expectSecondOutputChanged);
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamsToFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(is1, is2)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			});
		}

		@ParameterizedTest
		@MethodSource("values")
		public void ofInputStreamsAsFilesRename(boolean firstOutputFileExists, Boolean secondOutputFileExists, boolean allowOverwrite, boolean expectFirstOutputChanged, Boolean expectSecondOutputChanged) throws IOException {
			// given
			InputStream is1 = TestUtils.getResourceStream("Thumbnailator/grid.png");
			InputStream is2 = TestUtils.getResourceStream("Thumbnailator/grid.jpg");

			assertThrows(IllegalStateException.class, () -> {
				// when
				Thumbnails.of(is1, is2)
						.size(50, 50)
						.allowOverwrite(allowOverwrite)
						.asFiles(Rename.PREFIX_DOT_THUMBNAIL);
			});
		}

		private void assertImageExists(File f, int width, int height) throws IOException {
			assertTrue(f.exists());

			BufferedImage img = ImageIO.read(f);
			assertNotNull(img);
			assertEquals(width, img.getWidth());
			assertEquals(height, img.getHeight());
		}
	}

	@Nested
    class VariousToFileTests {

		@TempDir
		public File temporaryFolder;

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully. Image format is determined
		 * by the extension of the file.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_toFile_File_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File destFile = new File(temporaryFolder, "tmp.png");

			// when
			Thumbnails.of(img)
					.size(100, 100)
					.toFile(destFile);

			// then
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(destFile)));

			BufferedImage thumbnail = ImageIO.read(destFile);
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully. Image format is determined
		 * by the extension of the file.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_toFile_String_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			String destFilePath = new File(temporaryFolder, "tmp.png").getAbsolutePath();

			// when
			Thumbnails.of(img)
					.size(100, 100)
					.toFile(destFilePath);

			// then
			File destFile = new File(destFilePath);
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(destFile)));

			BufferedImage thumbnail = ImageIO.read(destFile);
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The thumbnail is written to the specified file</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_toFile_File_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File destFile = new File(temporaryFolder, "tmp.png");

			// when
			Thumbnails.of(img)
					.size(100, 100)
					.outputFormat("png")
					.toFile(destFile);

			// then
			BufferedImage thumbnail = ImageIO.read(destFile);
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The thumbnail is written to the specified file</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImage_toFile_String_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			String destFilePath = new File(temporaryFolder, "tmp.png").getAbsolutePath();

			// when
			Thumbnails.of(img)
					.size(100, 100)
					.outputFormat("png")
					.toFile(destFilePath);

			// then
			File destFile = new File(destFilePath);
			BufferedImage thumbnail = ImageIO.read(destFile);
			assertEquals(100, thumbnail.getWidth());
			assertEquals(100, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException will be thrown</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_toFile_File_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File destFile = new File(temporaryFolder, "tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(100, 100)
						.toFile(destFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully. Image format is determined
		 * by the extension of the file.</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_toFile_String_NoOutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			String destFilePath = new File(temporaryFolder, "tmp.png").getAbsolutePath();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(100, 100)
						.toFile(destFilePath);
			});

		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The thumbnail is written to the specified file</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_toFile_File_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File destFile = new File(temporaryFolder, "tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(100, 100)
						.outputFormat("png")
						.toFile(destFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(BufferedImage, BufferedImage)</li>
		 * <li>outputFormat("png")</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The thumbnail is written to the specified file</li>
		 * </ol>
		 */
		@Test
		public void of_BufferedImages_toFile_String_OutputFormatSpecified() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			String destFilePath = new File(temporaryFolder, "tmp.png").getAbsolutePath();

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(100, 100)
						.outputFormat("png")
						.toFile(destFilePath);
			});
			new File(destFilePath).deleteOnExit();

		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URL_toFile() throws IOException {
			// given
			URL f = TestUtils.getResource("Thumbnailator/grid.png");
			File destFile = new File(temporaryFolder, "tmp.png");

			// when
			Thumbnails.of(f)
					.size(50, 50)
					.toFile(destFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(destFile);
			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(URL, URL)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_URLs_toFile() throws IOException {
			// given
			URL f = TestUtils.getResource("Thumbnailator/grid.png");
			File destFile = new File(temporaryFolder, "tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
						.size(50, 50)
						.toFile(destFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStream_toFile() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			File destFile = new File(temporaryFolder, "tmp.png");

			// when
			Thumbnails.of(is)
					.size(50, 50)
					.toFile(destFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(destFile);
			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(InputStream, InputStream)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_InputStreams_toFile() throws IOException {
			// given
			InputStream is = TestUtils.getResourceStream("Thumbnailator/grid.png");
			File destFile = new File(temporaryFolder, "tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(is, is)
						.size(50, 50)
						.toFile(destFile);
			});
		}
	}

	@Nested
    class RenameDirectoryBehaviorTests {

		@TempDir
		public File temporaryFolder;

		@Test
		public void toFiles_Rename_WritesToSameDir_AllInputFromSameDir() throws IOException {
			File tmpDir = new File(temporaryFolder, "rename");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid2.png")
			);

			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			assertTrue(new File(tmpDir, "thumbnail.grid1.png").exists());
			assertTrue(new File(tmpDir, "thumbnail.grid2.png").exists());
		}

		@Test
		public void toFiles_Rename_WritesToSameDir_InputsFromDifferentDir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid2.png")
			);

			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			assertTrue(new File(tmpDir1, "thumbnail.grid1.png").exists());
			assertTrue(new File(tmpDir2, "thumbnail.grid2.png").exists());
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_AllInputFromSameDir() throws IOException {
			File tmpDir = new File(temporaryFolder, "rename");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid2.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			assertFalse(new File(tmpDir, "thumbnail.grid1.png").exists());
			assertFalse(new File(tmpDir, "thumbnail.grid2.png").exists());
			assertTrue(new File(targetDir, "thumbnail.grid1.png").exists());
			assertTrue(new File(targetDir, "thumbnail.grid2.png").exists());
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid2.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			assertFalse(new File(tmpDir1, "thumbnail.grid1.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid2.png").exists());
			assertTrue(new File(targetDir, "thumbnail.grid1.png").exists());
			assertTrue(new File(targetDir, "thumbnail.grid2.png").exists());
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/igrid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			Thumbnails.of(f1, f2)
					.size(100, 100)
					.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");
			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());

			// by default, overwrite is allowed.
			BufferedImageComparer.isSame(ImageIO.read(f2), ImageIO.read(out));
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName_OverwriteFalse() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/igrid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			Thumbnails.of(f1, f2)
					.size(100, 100)
					.allowOverwrite(false)
					.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");
			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());

			BufferedImageComparer.isSame(ImageIO.read(f1), ImageIO.read(out));
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName_OverwriteTrue() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			Thumbnails.of(f1, f2)
					.size(100, 100)
					.allowOverwrite(true)
					.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");
			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());

			BufferedImageComparer.isSame(ImageIO.read(f2), ImageIO.read(out));
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_OutputDirDoesntExist() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);

			try {
				File targetDir = new File(temporaryFolder, "target");
				Thumbnails.of(f1)
						.size(100, 100)
						.toFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

				fail();
			} catch (IllegalArgumentException e) {
				// then
			}
		}

		@Test
		public void toFiles_Rename_WritesToSpecifiedDir_OutputDirIsntADir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);

			// Note! This is a file, not a dir!
			File targetFile = new File(temporaryFolder, "target");

			try {
				Thumbnails.of(f1)
						.size(100, 100)
						.toFiles(targetFile, Rename.PREFIX_DOT_THUMBNAIL);

				fail();
			} catch (IllegalArgumentException e) {
				// then
			}
		}

		@Test
		public void asFiles_Rename_WritesToSameDir_AllInputFromSameDir() throws IOException {
			File tmpDir = new File(temporaryFolder, "rename");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid2.png")
			);

			List<File> result = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			File out1 = new File(tmpDir, "thumbnail.grid1.png");
			File out2 = new File(tmpDir, "thumbnail.grid2.png");

			assertTrue(out1.exists());
			assertTrue(out2.exists());
			assertTrue(result.get(0).equals(out1));
			assertTrue(result.get(1).equals(out2));
		}

		@Test
		public void asFiles_Rename_WritesToSameDir_InputsFromDifferentDir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid2.png")
			);

			List<File> result = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			File out1 = new File(tmpDir1, "thumbnail.grid1.png");
			File out2 = new File(tmpDir2, "thumbnail.grid2.png");

			assertTrue(out1.exists());
			assertTrue(out2.exists());
			assertTrue(result.get(0).equals(out1));
			assertTrue(result.get(1).equals(out2));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_AllInputFromSameDir() throws IOException {
			File tmpDir = new File(temporaryFolder, "rename");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir, "grid2.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			List<File> result = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			File out1 = new File(targetDir, "thumbnail.grid1.png");
			File out2 = new File(targetDir, "thumbnail.grid2.png");

			assertFalse(new File(tmpDir, "thumbnail.grid1.png").exists());
			assertFalse(new File(tmpDir, "thumbnail.grid1.png").exists());
			assertTrue(out1.exists());
			assertTrue(out2.exists());
			assertTrue(result.get(0).equals(out1));
			assertTrue(result.get(1).equals(out2));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid2.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			List<File> result = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out1 = new File(targetDir, "thumbnail.grid1.png");
			File out2 = new File(targetDir, "thumbnail.grid2.png");

			assertFalse(new File(tmpDir1, "thumbnail.grid1.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid2.png").exists());
			assertTrue(out1.exists());
			assertTrue(out2.exists());
			assertTrue(result.get(0).equals(out1));
			assertTrue(result.get(1).equals(out2));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/igrid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			List<File> result = Thumbnails.of(f1, f2)
					.size(100, 100)
					.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");

			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());
			assertTrue(result.get(0).equals(out));

			// by default, overwrite is allowed.
			BufferedImageComparer.isSame(ImageIO.read(f2), ImageIO.read(out));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName_OverwriteFalse() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			tmpDir1.mkdirs();
			tmpDir2.mkdirs();
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/igrid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			List<File> result = Thumbnails.of(f1, f2)
					.size(100, 100)
					.allowOverwrite(false)
					.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");

			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());
			assertTrue(result.get(0).equals(out));

			BufferedImageComparer.isSame(ImageIO.read(f1), ImageIO.read(out));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_InputsFromDifferentDir_InputSameName_OverwriteTrue() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File tmpDir2 = new File(temporaryFolder, "rename/2");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid.png")
			);
			File f2 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir2, "grid.png")
			);

			File targetDir = new File(temporaryFolder, "target");
			targetDir.mkdirs();
			List<File> result = Thumbnails.of(f1, f2)
					.size(100, 100)
					.allowOverwrite(true)
					.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File out = new File(targetDir, "thumbnail.grid.png");

			assertFalse(new File(tmpDir1, "thumbnail.grid.png").exists());
			assertFalse(new File(tmpDir2, "thumbnail.grid.png").exists());
			assertTrue(out.exists());
			assertTrue(result.get(1).equals(out));

			BufferedImageComparer.isSame(ImageIO.read(f2), ImageIO.read(out));
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_OutputDirDoesntExist() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);

			try {
				File targetDir = new File(temporaryFolder, "target");
				Thumbnails.of(f1)
						.size(100, 100)
						.asFiles(targetDir, Rename.PREFIX_DOT_THUMBNAIL);
				fail();
			} catch (IllegalArgumentException e) {
				// then
			}
		}

		@Test
		public void asFiles_Rename_WritesToSpecifiedDir_OutputDirIsntADir() throws IOException {
			File tmpDir1 = new File(temporaryFolder, "rename/1");
			File f1 = TestUtils.copyResourceToFile(
					"Thumbnailator/grid.png", new File(tmpDir1, "grid1.png")
			);

			// Note! This is a file, not a dir!
			File targetFile = new File(temporaryFolder, "target");

			try {
				Thumbnails.of(f1)
						.size(100, 100)
						.asFiles(targetFile, Rename.PREFIX_DOT_THUMBNAIL);

				fail();
			} catch (IllegalArgumentException e) {
				// then
			}
		}
	}

	@Nested
    class FileInputToOutputTests {

		@TempDir
		public File temporaryFolder;

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			// when
			Thumbnails.of(f)
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);
			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_toFiles_Rename() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.of(f1)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_asFiles_Rename() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.of(f1)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>toFiles(Iterable<File>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Iterable<File> object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_toFiles_Iterable() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			Thumbnails.of(f1)
					.size(50, 50)
					.toFiles(new ConsecutivelyNumberedFilenames(temporaryFolder, "temp-%d.png"));

			// then
			File expectedFile = new File(temporaryFolder, "temp-0.png");

			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>asFiles(Iterable<File>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Iterable<File> object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_asFiles_Iterable() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.of(f1)
					.size(50, 50)
					.asFiles(new ConsecutivelyNumberedFilenames(temporaryFolder, "temp-%d.png"));

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_asBufferedImage() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			BufferedImage thumbnail = Thumbnails.of(f1)
					.size(50, 50)
					.asBufferedImage();

			// then
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_asBufferedImages() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(f1)
					.size(50, 50)
					.asBufferedImages();

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage thumbnail = thumbnails.get(0);
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_toOutputStream() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f1)
					.size(50, 50)
					.toOutputStream(os);

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_toOutputStreams() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f1)
					.size(50, 50)
					.toOutputStreams(Arrays.asList(os));

			// then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing completes successfully.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_File_iterableBufferedImages() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(f1)
					.size(50, 50)
					.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail = iter.next();
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_toFiles_Rename() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());

			BufferedImage fromFileImage2 = ImageIO.read(outFile2);
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_asFiles_Rename() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());

			BufferedImage fromFileImage2 = ImageIO.read(thumbnails.get(1));
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>toFiles(Iterable<File>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Iterable<File> object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_toFiles_Iterable() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(new ConsecutivelyNumberedFilenames(temporaryFolder, "temp-%d.png"));

			// then
			File outFile1 = new File(temporaryFolder, "temp-0.png");
			File outFile2 = new File(temporaryFolder, "temp-1.png");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());

			BufferedImage fromFileImage2 = ImageIO.read(outFile2);
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>asFiles(Iterable<File>)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Iterable<File> object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_asFiles_Iterable() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(new ConsecutivelyNumberedFilenames(temporaryFolder, "temp-%d.png"));

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());

			BufferedImage fromFileImage2 = ImageIO.read(thumbnails.get(1));
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>asBufferedImage()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_asBufferedImage() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
						.size(50, 50)
						.asBufferedImage();
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>asBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and returned as BufferedImages in a List</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_asBufferedImages() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			List<BufferedImage> thumbnails = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asBufferedImages();

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage thumbnail1 = thumbnails.get(0);
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = thumbnails.get(1);
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>toOutputStream()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_toOutputStream() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			OutputStream os = mock(OutputStream.class);

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
						.size(50, 50)
						.toOutputStream(os);
			});
			verifyNoInteractions(os);

		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>toOutputStreams()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Processing will be successful.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_toOutputStreams() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();

			// when
			Thumbnails.of(f, f)
					.size(50, 50)
					.toOutputStreams(Arrays.asList(os1, os2));

			//then
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(os1.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os1.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());

			thumbnail = ImageIO.read(new ByteArrayInputStream(os2.toByteArray()));
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(os2.toByteArray())));
			assertEquals(50, thumbnail.getWidth());
			assertEquals(50, thumbnail.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(File, File)</li>
		 * <li>iterableBufferedImages()</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and an Iterable which can iterate over the
		 * two BufferedImages is returned.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Files_iterableBufferedImages() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			Iterable<BufferedImage> thumbnails = Thumbnails.of(f1, f2)
					.size(50, 50)
					.iterableBufferedImages();

			// then
			Iterator<BufferedImage> iter = thumbnails.iterator();

			BufferedImage thumbnail1 = iter.next();
			assertEquals(50, thumbnail1.getWidth());
			assertEquals(50, thumbnail1.getHeight());

			BufferedImage thumbnail2 = iter.next();
			assertEquals(50, thumbnail2.getWidth());
			assertEquals(50, thumbnail2.getHeight());

			assertFalse(iter.hasNext());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Single_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "/grid.tmp.png");

			// when
			Thumbnails.fromFiles(Arrays.asList(f))
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);

			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File, File])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Multiple_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromFiles(Arrays.asList(f, f))
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Single_toFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.fromFiles(Arrays.asList(f1))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File, File])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Multiple_toFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			Thumbnails.fromFiles(Arrays.asList(f1, f2))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File])</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Single_asFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.fromFiles(Arrays.asList(f1))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles([File, File])</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFiles_Multiple_asFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.fromFiles(Arrays.asList(f1, f2))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			BufferedImage fromFileImage2 = ImageIO.read(thumbnails.get(1));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Single_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			// when
			Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f))
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);

			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File, File])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Multiple_toFile() throws IOException {
			// given
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f, f))
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Single_toFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f1))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File, File])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Multiple_toFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f1, f2))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File])</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Single_asFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f1))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFiles(Iterable[File, File])</li>
		 * <li>asFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilesIterable_Multiple_asFiles() throws IOException {
			// given
			File f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);

			// when
			List<File> thumbnails = Thumbnails.fromFiles((Iterable<File>)Arrays.asList(f1, f2))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			BufferedImage fromFileImage2 = ImageIO.read(thumbnails.get(1));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_String_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			// when
			Thumbnails.of(f)
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);

			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String, String)</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Strings_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.of(f, f)
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_String_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.of(f1)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String, String)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Strings_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			Thumbnails.of(f1, f2)
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_String_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.of(f1)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.of(String, String)</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void of_Strings_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.of(f1, f2)
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));
			BufferedImage fromFileImage2 = ImageIO.read(thumbnails.get(1));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Single_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			// when
			Thumbnails.fromFilenames(Arrays.asList(f))
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);

			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String, String])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Multiple_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromFilenames(Arrays.asList(f, f))
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Single_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();

			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.fromFilenames(Arrays.asList(f1))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String, String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Multiple_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			Thumbnails.fromFilenames(Arrays.asList(f1, f2))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Single_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.fromFilenames(Arrays.asList(f1))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames([String, String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenames_Multiple_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.fromFilenames(Arrays.asList(f1, f2))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is written to the specified file.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Single_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			// when
			Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f))
					.size(50, 50)
					.toFile(outFile);

			// then
			BufferedImage fromFileImage = ImageIO.read(outFile);

			assertEquals(50, fromFileImage.getWidth());
			assertEquals(50, fromFileImage.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String, String])</li>
		 * <li>toFile(File)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An IllegalArgumentException is thrown.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Multiple_toFile() throws IOException {
			// given
			String f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			File outFile = new File(temporaryFolder, "grid.tmp.png");

			assertThrows(IllegalArgumentException.class, () -> {
				// when
				Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f, f))
						.size(50, 50)
						.toFile(outFile);
			});
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Single_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();

			File expectedFile = new File(temporaryFolder, "thumbnail.grid.png");

			// when
			Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f1))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			BufferedImage fromFileImage1 = ImageIO.read(expectedFile);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String, String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Multiple_toFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f1, f2))
					.size(50, 50)
					.toFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>An image is generated and written to a file whose name is generated
		 * from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Single_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f1))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			assertEquals(1, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(thumbnails.get(0));

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>Thumbnails.fromFilenames(Iterable[String, String])</li>
		 * <li>toFiles(Rename)</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>Two images are generated and written to a file whose name is
		 * generated from the Rename object.</li>
		 * </ol>
		 * @throws IOException
		 */
		@Test
		public void fromFilenamesIterable_Multiple_asFiles() throws IOException {
			// given
			String f1 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			).getAbsolutePath();
			String f2 = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			).getAbsolutePath();

			// when
			List<File> thumbnails = Thumbnails.fromFilenames((Iterable<String>)Arrays.asList(f1, f2))
					.size(50, 50)
					.asFiles(Rename.PREFIX_DOT_THUMBNAIL);

			// then
			File outFile1 = new File(temporaryFolder, "thumbnail.grid.png");
			File outFile2 = new File(temporaryFolder, "thumbnail.grid.jpg");

			assertEquals(2, thumbnails.size());

			BufferedImage fromFileImage1 = ImageIO.read(outFile1);
			BufferedImage fromFileImage2 = ImageIO.read(outFile2);

			assertEquals(50, fromFileImage1.getWidth());
			assertEquals(50, fromFileImage1.getHeight());
			assertEquals(50, fromFileImage2.getWidth());
			assertEquals(50, fromFileImage2.getHeight());
		}
	}

	@Nested
    class OtherTests {

		@TempDir
		public File temporaryFolder;

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>input is a file</li>
		 * <li>output is via toFile</li>
		 * <li>where the input and output file is the same</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The destination file is overwritten</li>
		 * </ol>
		 */
		@Test
		public void toFile_File_DefaultIsOverwrite() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			// when
			Thumbnails.of(f)
					.size(50, 50)
					.toFile(f);

			// then
			assertImageExists(f, 50, 50);
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>the two argument toFile(File) is called</li>
		 * <li>allowOverwrite is true</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The destination file is overwritten</li>
		 * </ol>
		 */
		@Test
		public void toFile_File_AllowOverwrite() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			// when
			Thumbnails.of(f)
					.size(50, 50)
					.allowOverwrite(true)
					.toFile(f);

			// then
			assertImageExists(f, 50, 50);
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>the two argument toFile(File) is called</li>
		 * <li>allowOverwrite is false</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The destination file is overwritten</li>
		 * </ol>
		 */
		@Test
		public void toFile_File_DisallowOverwrite() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			// when
			try {
				Thumbnails.of(f)
						.size(50, 50)
						.allowOverwrite(false)
						.toFile(f);

				fail();
			} catch (IllegalArgumentException e) {
				// then
				assertEquals("The destination file exists.", e.getMessage());
				assertImageExists(f, 100, 100);
			}
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>the two argument toFile(String) is called</li>
		 * <li>allowOverwrite is true</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The destination file is overwritten</li>
		 * </ol>
		 */
		@Test
		public void toFile_String_AllowOverwrite() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			// when
			Thumbnails.of(f)
					.size(50, 50)
					.allowOverwrite(true)
					.toFile(f.getAbsolutePath());

			// then
			assertImageExists(f, 50, 50);
		}

		/**
		 * Test for the {@link Thumbnails.Builder} class where,
		 * <ol>
		 * <li>the two argument toFile(String) is called</li>
		 * <li>allowOverwrite is false</li>
		 * </ol>
		 * and the expected outcome is,
		 * <ol>
		 * <li>The destination file is overwritten</li>
		 * </ol>
		 */
		@Test
		public void toFile_String_DisallowOverwrite() throws IOException {
			// set up
			File f = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);

			// given
			// when
			try {
				Thumbnails.of(f)
						.size(50, 50)
						.allowOverwrite(false)
						.toFile(f.getAbsolutePath());

				fail();
			} catch (IllegalArgumentException e) {
				// then
				assertEquals("The destination file exists.", e.getMessage());
				assertImageExists(f, 100, 100);
			}
		}

		@Test
		public void useOriginalFormat() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File destFile = new File(temporaryFolder, "dest.jpg");

			// when
			Thumbnails.of(sourceFile)
					.size(10, 10)
					.useOriginalFormat()
					.toFile(destFile);

			// then
			File actualDestFile = new File(destFile.getParent(), destFile.getName() + ".png");
			assertTrue(actualDestFile.exists());
			assertEquals("png", TestUtils.getFormatName(new FileInputStream(actualDestFile)));
		}

		@Test
		public void determineOutputFormat() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			File destFile = new File(temporaryFolder, "dest.jpg");

			// when
			Thumbnails.of(sourceFile)
					.size(10, 10)
					.determineOutputFormat()
					.toFile(destFile);

			// then
			assertTrue(destFile.exists());
			assertEquals("JPEG", TestUtils.getFormatName(new FileInputStream(destFile)));
		}

		@Test
		public void toOutputStreamImageFormatMatchesInputForPngFile() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.png", temporaryFolder
			);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(sourceFile)
					.size(100, 100)
					.toOutputStream(baos);

			// then
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesInputForJpegFile() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(sourceFile)
					.size(100, 100)
					.toOutputStream(baos);

			// then
			assertEquals("JPEG", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesOutputFormatForPng() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(sourceFile)
					.size(100, 100)
					.outputFormat("png")
					.toOutputStream(baos);

			// then
			assertEquals("png", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		@Test
		public void toOutputStreamImageFormatMatchesOutputFormatForJpeg() throws IOException {
			// given
			File sourceFile = TestUtils.copyResourceToTemporaryFile(
					"Thumbnailator/grid.jpg", temporaryFolder
			);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// when
			Thumbnails.of(sourceFile)
					.size(100, 100)
					.outputFormat("JPEG")
					.toOutputStream(baos);

			// then
			assertEquals("JPEG", TestUtils.getFormatName(new ByteArrayInputStream(baos.toByteArray())));
		}

		private void assertImageExists(File f, int width, int height) throws IOException {
			assertTrue(f.exists());

			BufferedImage img = ImageIO.read(f);
			assertNotNull(img);
			assertEquals(width, img.getWidth());
			assertEquals(height, img.getHeight());
		}
	}
}
