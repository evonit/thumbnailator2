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

import net.evonit.thumbnailator2.ThumbnailParameter;
import net.evonit.thumbnailator2.builders.ThumbnailParameterBuilder;
import net.evonit.thumbnailator2.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static net.evonit.thumbnailator2.TestUtils.copyResourceToTemporaryFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Issue156OutputStreamImageSinkTest {

	@TempDir
	public File temporaryFolder;

	@Test
	public void compressedPngDoesntGetLarger() throws IOException {
		File originalFile = TestUtils.copyResourceToTemporaryFile("Exif/original.png", temporaryFolder);
		BufferedImage originalImage = ImageIO.read(originalFile);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamImageSink imageSink = new OutputStreamImageSink(baos);

		ThumbnailParameter param = new ThumbnailParameterBuilder()
				.scale(1.0)
				.build();
		imageSink.setThumbnailParameter(param);

		imageSink.setOutputFormatName("png");
		imageSink.write(originalImage);

		assertTrue(baos.size() < originalFile.length());
	}
}