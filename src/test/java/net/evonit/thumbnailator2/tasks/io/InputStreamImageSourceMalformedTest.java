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

package net.evonit.thumbnailator2.tasks.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InputStreamImageSourceMalformedTest {

	public static Collection<Object[]> testCases() {
		List<Object[]> cases = new ArrayList<>();
		for (String type : Arrays.asList("jpg", "png", "bmp")) {
			for (int i = 1; i <= 40; i++) {
				cases.add(new Object[]{type, i});
			}
		}
		return cases;
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void terminatesProperlyWithWorkaround(String type, Integer length) {
		runTest(type, length);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void terminatesProperlyWithoutWorkaround(String type, Integer length) {
		System.setProperty("thumbnailator.disableExifWorkaround", "true");
		runTest(type, length);
	}

	@BeforeEach
	@AfterEach
	public void cleanup() {
		System.clearProperty("thumbnailator.disableExifWorkaround");
	}

	private void runTest(String type, Integer length) {
		try {
			byte[] bytes = new byte[length];
			InputStream sourceIs = ClassLoader.getSystemResourceAsStream(String.format("Thumbnailator/grid.%s", type));
			sourceIs.read(bytes);
			sourceIs.close();

			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			InputStreamImageSource source = new InputStreamImageSource(is);

			source.read();

		} catch (Exception e) {
			// terminates properly, even if an exception is thrown.
		}
	}
}