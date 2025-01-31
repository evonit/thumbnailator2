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

package net.evonit.thumbnailator2.geometry;

import java.awt.Dimension;

/**
 * This interface is implemented by classes which calculate the size of an
 * object inside of an enclosing object.
 * 
 * @author coobird
 * @since	0.3.4
 *
 */
public interface Size {
	/**
	 * Calculates the size of the object.
	 * 
	 * @param width			Width of the object which encloses the object
	 * 						for which the size should be determined.
	 * @param height		Height of the object which encloses the object
	 * 						for which the size should be determined.
	 * @return				Calculated size of the object.
	 * @throws IllegalArgumentException	If the width and/or height is less than
	 * 									or equal to {@code 0}.	
	 */
	public Dimension calculate(int width, int height);
}
