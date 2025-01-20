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

package net.evonit.thumbnailator2.name;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class ConsecutiveNumbersTest {

	@TempDir
	File temporaryFolder;

	@Test
	public void noArgConstructor() {
		// given
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames();

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File("0"), iter.next());
		assertEquals(new File("1"), iter.next());
		assertEquals(new File("2"), iter.next());
		assertEquals(new File("3"), iter.next());
	}

	@Test
	public void startNumberSpecified() {
		// given
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File("5"), iter.next());
		assertEquals(new File("6"), iter.next());
		assertEquals(new File("7"), iter.next());
		assertEquals(new File("8"), iter.next());
	}

	@Test
	public void givenParentDir() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "0"), iter.next());
		assertEquals(new File(dir, "1"), iter.next());
		assertEquals(new File(dir, "2"), iter.next());
		assertEquals(new File(dir, "3"), iter.next());
	}

	@Test
	public void givenParentDir_WithTrailingSlash() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "0"), iter.next());
		assertEquals(new File(dir, "1"), iter.next());
		assertEquals(new File(dir, "2"), iter.next());
		assertEquals(new File(dir, "3"), iter.next());
	}

	@Test
	public void givenParentDir_WithFile() {
		// given
		File dir = new File(temporaryFolder, "file");

		IOException exception = assertThrows(IOException.class, () -> {
			// when
			new ConsecutivelyNumberedFilenames(dir);
		});

		// then
		assertEquals("Specified path is not a directory or does not exist.", exception.getMessage());
	}

	@Test
	public void givenParentDir_WithNonExistentDir() {
		// given
		File dir = new File(temporaryFolder, "foobar");

		IOException exception = assertThrows(IOException.class, () -> {
			// when
			new ConsecutivelyNumberedFilenames(dir);
		});

		// then
		assertEquals("Specified path is not a directory or does not exist.", exception.getMessage());
	}

	@Test
	public void formatOnly_WithZeroPadding() {
		// given
		ConsecutivelyNumberedFilenames sAndC = new ConsecutivelyNumberedFilenames("hello-%04d.jpg");

		// when+then
		Iterator<File> iter = sAndC.iterator();

		assertEquals(new File("hello-0000.jpg"), iter.next());
		assertEquals(new File("hello-0001.jpg"), iter.next());
		assertEquals(new File("hello-0002.jpg"), iter.next());
		assertEquals(new File("hello-0003.jpg"), iter.next());
	}

	@Test
	public void formatOnly_WithText() {
		// given
		ConsecutivelyNumberedFilenames sAndC = new ConsecutivelyNumberedFilenames("hello-%d.jpg");

		// when+then
		Iterator<File> iter = sAndC.iterator();

		assertEquals(new File("hello-0.jpg"), iter.next());
		assertEquals(new File("hello-1.jpg"), iter.next());
		assertEquals(new File("hello-2.jpg"), iter.next());
		assertEquals(new File("hello-3.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_StartNumberSpecified() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir, 5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "5"), iter.next());
		assertEquals(new File(dir, "6"), iter.next());
		assertEquals(new File(dir, "7"), iter.next());
		assertEquals(new File(dir, "8"), iter.next());
	}

	@Test
	public void givenParentDir_StartNumberSpecified_WhereDirIsInvalid() {
		// given
		File dir = new File(temporaryFolder, "foobar");

		IOException exception = assertThrows(IOException.class, () -> {
			// when
			new ConsecutivelyNumberedFilenames(dir, 5);
		});

		// then
		assertEquals("Specified path is not a directory or does not exist.", exception.getMessage());
	}

	@Test
	public void givenParentDir_formatWithZeroPadding() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir, "hello-%04d.jpg");

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "hello-0000.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0001.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0002.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0003.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_formatWithText() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir, "hello-%d.jpg");

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "hello-0.jpg"), iter.next());
		assertEquals(new File(dir, "hello-1.jpg"), iter.next());
		assertEquals(new File(dir, "hello-2.jpg"), iter.next());
		assertEquals(new File(dir, "hello-3.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_formatWithText_WhereDirIsInvalid() {
		// given
		File dir = new File(temporaryFolder, "foobar");

		IOException exception = assertThrows(IOException.class, () -> {
			// when
			new ConsecutivelyNumberedFilenames(dir, "hello-%d.jpg");
		});

		// then
		assertEquals("Specified path is not a directory or does not exist.", exception.getMessage());
	}

	@Test
	public void formatWithZeroPadding_StartNumberSpecified() {
		// given
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames("hello-%04d.jpg", 5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File("hello-0005.jpg"), iter.next());
		assertEquals(new File("hello-0006.jpg"), iter.next());
		assertEquals(new File("hello-0007.jpg"), iter.next());
		assertEquals(new File("hello-0008.jpg"), iter.next());
	}

	@Test
	public void formatWithText_StartNumberSpecified() {
		// given
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames("hello-%d.jpg", 5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File("hello-5.jpg"), iter.next());
		assertEquals(new File("hello-6.jpg"), iter.next());
		assertEquals(new File("hello-7.jpg"), iter.next());
		assertEquals(new File("hello-8.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_formatWithZeroPadding_StartNumberSpecified() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir, "hello-%04d.jpg", 5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "hello-0005.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0006.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0007.jpg"), iter.next());
		assertEquals(new File(dir, "hello-0008.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_formatWithText_StartNumberSpecified() throws IOException {
		// given
		File dir = temporaryFolder;
		ConsecutivelyNumberedFilenames consecutiveNumbers = new ConsecutivelyNumberedFilenames(dir, "hello-%d.jpg", 5);

		// when+then
		Iterator<File> iter = consecutiveNumbers.iterator();

		assertEquals(new File(dir, "hello-5.jpg"), iter.next());
		assertEquals(new File(dir, "hello-6.jpg"), iter.next());
		assertEquals(new File(dir, "hello-7.jpg"), iter.next());
		assertEquals(new File(dir, "hello-8.jpg"), iter.next());
	}

	@Test
	public void givenParentDir_formatWithText_StartNumberSpecified_WhereDirIsInvalid() {
		// given
		File dir = new File(temporaryFolder, "foobar");

		IOException exception = assertThrows(IOException.class, () -> {
			// when
			new ConsecutivelyNumberedFilenames(dir, "hello-%d.jpg", 5);
		});

		// then
		assertEquals("Specified path is not a directory or does not exist.", exception.getMessage());
	}
}