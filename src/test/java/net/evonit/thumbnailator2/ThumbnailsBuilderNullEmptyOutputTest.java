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
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import net.evonit.thumbnailator2.builders.BufferedImageBuilder;
import net.evonit.thumbnailator2.name.Rename;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

public class ThumbnailsBuilderNullEmptyOutputTest {

	@Nested
	public class Tests {

		@Test
		public void asFiles_Iterable_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.asFiles((Iterable<File>) null);
			});

			// then
			assertEquals("File name iterable is null.", exception.getMessage());
		}

		@Test
		public void asFiles_Iterable_Empty() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.asFiles(Collections.<File>emptyList());
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
		}

		@Test
		public void toFiles_Iterable_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.toFiles((Iterable<File>) null);
			});

			// then
			assertEquals("File name iterable is null.", exception.getMessage());
		}

		@Test
		public void toFiles_Iterable_Empty() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.toFiles(Collections.<File>emptyList());
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
		}

		@Test
		public void asFiles_Rename_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.asFiles((Rename) null);
			});

			// then
			assertEquals("Rename is null.", exception.getMessage());
		}

		@Test
		public void toFiles_Rename_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.toFiles((Rename) null);
			});

			// then
			assertEquals("Rename is null.", exception.getMessage());
		}

		@Test
		public void toFile_File_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.toFile((File) null);
			});

			// then
			assertEquals("File cannot be null.", exception.getMessage());
		}

		@Test
		public void toFile_String_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.toFile((String) null);
			});

			// then
			assertEquals("File cannot be null.", exception.getMessage());
		}

		@Test
		public void toOutputStream() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.outputFormat("png")
						.toOutputStream((OutputStream) null);
			});

			// then
			assertEquals("OutputStream cannot be null.", exception.getMessage());
		}

		@Test
		public void toOutputStreams_Iterable_Null() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			NullPointerException exception = assertThrows(NullPointerException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.outputFormat("png")
						.toOutputStreams((Iterable<OutputStream>) null);
			});

			// then
			assertEquals("OutputStream iterable is null.", exception.getMessage());
		}

		@Test
		public void toOutputStreams_Iterable_Empty() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img)
						.size(50, 50)
						.outputFormat("png")
						.toOutputStreams(Collections.<OutputStream>emptyList());
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
		}

		@Test
		public void toOutputStreams_Iterable_NotEnough() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			OutputStream os = mock(OutputStream.class);

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(50, 50)
						.outputFormat("png")
						.toOutputStreams(Arrays.asList(os));
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
			verify(os, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
		}
	}

	@Nested
	public class FilesTests {
		// These tests require outputting files.

		@TempDir
		File temporaryFolder;

		@Test
		public void asFiles_Iterable_NotEnough() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File outFile = new File(temporaryFolder, "ofFilesNotEnough.png");

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(50, 50)
						.asFiles(Arrays.asList(outFile));
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
		}

		@Test
		public void toFiles_Iterable_NotEnough() throws IOException {
			// given
			BufferedImage img = new BufferedImageBuilder(200, 200).build();
			File outFile = new File(temporaryFolder, "ofFilesNotEnough.png");

			IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
				// when
				Thumbnails.of(img, img)
						.size(50, 50)
						.toFiles(Arrays.asList(outFile));
			});

			// then
			assertEquals("Not enough file names provided by iterator.", exception.getMessage());
		}
	}
}