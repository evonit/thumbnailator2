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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static net.evonit.thumbnailator2.TestUtils.copyResourceToTemporaryFile;
import static org.junit.jupiter.api.Assertions.*;

public class ThumbnailatorTranscodeTest {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "png", "bmp", "gif");

    public static class SupportedFormatsBase {
        public static Collection<Object> testCases() {
            List<Object[]> cases = new ArrayList<Object[]>();
            for (String input : SUPPORTED_FORMATS) {
                cases.add(new Object[] { input });
            }
            return Arrays.asList(cases.toArray());
        }
    }

    public static class InputOutputExpectationBase {

        public static Stream<Object[]> testCases() {
            Map<String, String> expectedFormatNames = new HashMap<String, String>() {{
                put("jpg", "JPEG");
                put("png", "png");
                put("bmp", "bmp");
                put("gif", "gif");
            }};

            List<Object[]> cases = new ArrayList<>();
            for (String input : SUPPORTED_FORMATS) {
                for (String output : SUPPORTED_FORMATS) {
                    if (!input.equals(output)) {
                        cases.add(new Object[]{input, output, expectedFormatNames.get(output)});
                    }
                }
            }
            return cases.stream();
        }

        protected boolean isTestForGifOutputInJava5(String format) {
            return "gif".equals(format) && System.getProperty("java.version").startsWith("1.5");
        }
    }

    @Nested
    public class InputStreamToOutputStreamTest extends InputOutputExpectationBase {

        @ParameterizedTest
        @MethodSource("net.evonit.thumbnailator2.ThumbnailatorTranscodeTest$InputOutputExpectationBase#testCases")
        public void createThumbnailForInputStreamToOutputStream(String inputFormat, String outputFormat, String expectedFormatName) throws IOException {
            // Skip in Java 5, as GIF writer was first included in Java 6.
            if (isTestForGifOutputInJava5(outputFormat)) {
                return;
            }

            InputStream is = TestUtils.getResourceStream(String.format("Thumbnailator/grid.%s", inputFormat));
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Thumbnailator.createThumbnail(is, os, outputFormat, 50, 50);

            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage img = ImageIO.read(thumbIs);

            assertEquals(
                    expectedFormatName,
                    ImageIO.getImageReaders(
                            ImageIO.createImageInputStream(
                                    new ByteArrayInputStream(os.toByteArray()))
                    ).next().getFormatName()
            );
            assertEquals(50, img.getWidth());
            assertEquals(50, img.getHeight());
        }
    }

    @Nested
    public class FileToFileTest extends InputOutputExpectationBase {

        @TempDir
        File temporaryFolder;

        @ParameterizedTest
        @MethodSource("net.evonit.thumbnailator2.ThumbnailatorTranscodeTest$InputOutputExpectationBase#testCases")
        public void createThumbnailForFileToFile(String inputFormat, String outputFormat, String expectedFormatName) throws IOException {
            // Skip in Java 5, as GIF writer was first included in Java 6.
            if (isTestForGifOutputInJava5(outputFormat)) {
                return;
            }

            File inputFile = copyResourceToTemporaryFile(String.format("Thumbnailator/grid.%s", inputFormat), temporaryFolder);
            File outputFile = new File(temporaryFolder, String.format("test.%s", outputFormat));

            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);

            assertTrue(outputFile.exists());
            BufferedImage img = ImageIO.read(outputFile);
            assertEquals(
                    expectedFormatName,
                    ImageIO.getImageReaders(
                            ImageIO.createImageInputStream(outputFile)
                    ).next().getFormatName()
            );
            assertEquals(50, img.getWidth());
            assertEquals(50, img.getHeight());
        }
    }

    @Nested
    public class SupportedInputFormatsForFiles extends SupportedFormatsBase {

        @TempDir
        File temporaryFolder;

        @ParameterizedTest
        @MethodSource("net.evonit.thumbnailator2.ThumbnailatorTranscodeTest$SupportedFormatsBase#testCases")
        public void testCreateThumbnailForFileToFile(String supportedFormat) throws IOException {

            File inputFile = copyResourceToTemporaryFile(String.format("Thumbnailator/grid.%s", supportedFormat), temporaryFolder);
            File outputFile = new File(temporaryFolder, String.format("tmp.%s", supportedFormat));

            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);

            assertTrue(outputFile.exists());
            BufferedImage img = ImageIO.read(outputFile);
            assertEquals(50, img.getWidth());
            assertEquals(50, img.getHeight());
        }

        @ParameterizedTest
        @MethodSource("net.evonit.thumbnailator2.ThumbnailatorTranscodeTest$SupportedFormatsBase#testCases")
        public void testCreateThumbnailForFileToBufferedImage(String supportedFormat) throws IOException {

            File inputFile = copyResourceToTemporaryFile(String.format("Thumbnailator/grid.%s", supportedFormat), temporaryFolder);

            BufferedImage img = Thumbnailator.createThumbnail(inputFile, 50, 50);
            assertEquals(50, img.getWidth());
            assertEquals(50, img.getHeight());
        }
    }

    @Nested
    public class SupportedInputFormatsForStreams extends SupportedFormatsBase {

        @ParameterizedTest
        @MethodSource("net.evonit.thumbnailator2.ThumbnailatorTranscodeTest$SupportedFormatsBase#testCases")
        public void testCreateThumbnailForInputStreamToOutputStream(String supportedFormat) throws IOException {
            InputStream is = TestUtils.getResourceStream(String.format("Thumbnailator/grid.%s", supportedFormat));
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Thumbnailator.createThumbnail(is, os, 50, 50);

            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage thumb = ImageIO.read(thumbIs);
            assertEquals(50, thumb.getWidth());
            assertEquals(50, thumb.getHeight());
        }
    }
}