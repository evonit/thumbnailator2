/*
 * Thumbnailator - a thumbnail generation library
 *
 * Copyright (c) 2008-2021 Chris Kroells
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

package net.evonit.thumbnailator2.util.exif;

import net.evonit.thumbnailator2.TestUtils;
import net.evonit.thumbnailator2.Thumbnails;
import net.evonit.thumbnailator2.test.BufferedImageAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExifWorkaroundTest {

    @ParameterizedTest
    @MethodSource("tagOrder")
    public void withWorkaround(List<String> tags) throws IOException {
        BufferedImage result = Thumbnails.of(buildJpeg(tags))
                .scale(1.0f)
                .asBufferedImage();

        assertPasses(result);
    }

    @ParameterizedTest
    @MethodSource("tagOrder")
    public void withoutWorkaround(List<String> tags) throws IOException {
        System.setProperty("thumbnailator.disableExifWorkaround", "true");

        BufferedImage result = Thumbnails.of(buildJpeg(tags))
                .scale(1.0f)
                .asBufferedImage();

        if (tags.get(0).equals("app0.segment")) {
            assertPasses(result);
        } else {
            assertFails(result);
        }
    }

    @BeforeEach @AfterEach
    public void cleanup() {
        System.clearProperty("thumbnailator.disableExifWorkaround");
    }

    private InputStream getFromResource(String name) throws IOException {
        return TestUtils.getResourceStream("Exif/fragments/" + name);
    }

    private InputStream buildJpeg(List<String> tags) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        List<String> resources = new ArrayList<>();
        resources.add("soi.segment");
        resources.addAll(tags);
        resources.add("rest");

        for (String resource : resources) {
            InputStream is = getFromResource(resource);
            while (is.available() > 0) {
                baos.write(is.read());
            }
            is.close();
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void assertPasses(BufferedImage result) {
        BufferedImageAssert.assertMatches(
                result,
                new float[] {
                        1, 1, 1,
                        1, 1, 1,
                        1, 0, 0,
                }
        );
    }

    private void assertFails(BufferedImage result) {
        BufferedImageAssert.assertMatches(
                result,
                new float[] {
                        1, 1, 1,
                        1, 1, 1,
                        0, 0, 1,
                }
        );
    }

    public static Collection<List<String>> tagOrder() {
        return Arrays.asList(
                Arrays.asList("app0.segment", "exif.segment"),
                Arrays.asList("exif.segment", "app0.segment"),
                Arrays.asList("app0.segment", "exif.segment", "xmp.segment"),
                Arrays.asList("app0.segment", "xmp.segment", "exif.segment"),
                Arrays.asList("exif.segment", "app0.segment", "xmp.segment"),
                Arrays.asList("xmp.segment", "app0.segment", "exif.segment"),
                Arrays.asList("exif.segment", "xmp.segment", "app0.segment"),
                Arrays.asList("xmp.segment", "exif.segment", "app0.segment")
        );
    }
}