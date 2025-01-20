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

package net.evonit.thumbnailator2;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class ThumbnailsBuilderNullEmptyInputTest {

	@Test
	public void of_BufferedImage_null() {
		BufferedImage[] img = null;

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.of(img);
		});
	}

	@Test
	public void of_File_null() {
		File[] f = null;

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.of(f);
		});
	}

	@Test
	public void of_Strings_null() {
		String[] f = null;

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.of(f);
		});
	}

	@Test
	public void of_URLs_null() {
		URL[] url = null;

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.of(url);
		});
	}

	@Test
	public void of_InputStreams_null() {
		InputStream[] is = null;

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.of(is);
		});
	}

	@Test
	public void of_BufferedImage_empty() {
		BufferedImage[] img = new BufferedImage[0];

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.of(img);
		});
	}

	@Test
	public void of_File_empty() {
		File[] f = new File[0];

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.of(f);
		});
	}

	@Test
	public void of_Strings_empty() {
		String[] f = new String[0];

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.of(f);
		});
	}

	@Test
	public void of_URLs_empty() {
		URL[] url = new URL[0];

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.of(url);
		});
	}

	@Test
	public void of_InputStreams_empty() {
		InputStream[] is = new InputStream[0];

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.of(is);
		});
	}

	@Test
	public void fromImages_Collection_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromImages((Collection<BufferedImage>) null);
		});
	}

	@Test
	public void fromFiles_Collection_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromFiles((Collection<File>) null);
		});
	}

	@Test
	public void fromFilenames_Collection_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromFilenames((Collection<String>) null);
		});
	}

	@Test
	public void fromURLs_Collection_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromURLs((Collection<URL>) null);
		});
	}

	@Test
	public void fromInputStreams_Collection_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromInputStreams((Collection<InputStream>) null);
		});
	}

	@Test
	public void fromImages_Collection_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromImages(Collections.<BufferedImage>emptyList());
		});
	}

	@Test
	public void fromFiles_Collection_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromFiles(Collections.<File>emptyList());
		});
	}

	@Test
	public void fromFilenames_Collection_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromFilenames(Collections.<String>emptyList());
		});
	}

	@Test
	public void fromURLs_Collection_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromURLs(Collections.<URL>emptyList());
		});
	}

	@Test
	public void fromInputStreams_Collection_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromInputStreams(Collections.<InputStream>emptyList());
		});
	}

	@Test
	public void fromImages_Iterable_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromImages((Iterable<BufferedImage>) null);
		});
	}

	@Test
	public void fromFiles_Iterable_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromFiles((Iterable<File>) null);
		});
	}

	@Test
	public void fromFilenames_Iterable_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromFilenames((Iterable<String>) null);
		});
	}

	@Test
	public void fromURLs_Iterable_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromURLs((Iterable<URL>) null);
		});
	}

	@Test
	public void fromInputStreams_Iterable_null() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			Thumbnails.fromInputStreams((Iterable<InputStream>) null);
		});
	}

	@Test
	public void fromImages_Iterable_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromImages((Iterable<BufferedImage>) Collections.<BufferedImage>emptyList());
		});
	}

	@Test
	public void fromFiles_Iterable_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromFiles((Iterable<File>) Collections.<File>emptyList());
		});
	}

	@Test
	public void fromFilenames_Iterable_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromFilenames((Iterable<String>) Collections.<String>emptyList());
		});
	}

	@Test
	public void fromURLs_Iterable_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromURLs((Iterable<URL>) Collections.<URL>emptyList());
		});
	}

	@Test
	public void fromInputStreams_Iterable_empty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Thumbnails.fromInputStreams((Iterable<InputStream>) Collections.<InputStream>emptyList());
		});
	}
}