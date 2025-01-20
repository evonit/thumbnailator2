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

package net.evonit.thumbnailator2.resizers;

import net.evonit.thumbnailator2.builders.BufferedImageBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * A {@link Resizer} which improved progressive bilinear scaling.
 * <p>
 * This resizer reads the source file partially and merges them in case of size-downscaling.
 * so that the image is resized in multiple steps. This prevents from OutOfMemory.
 *
 * @author evonit
 *
 */
public class TileResizer extends AbstractResizer {
	/**
	 * Instantiates a {@link TileResizer} with default
	 * rendering hints.
	 */
	public TileResizer() {
		this(Collections.<RenderingHints.Key, Object>emptyMap());
	}

	/**
	 * Instantiates a {@link TileResizer} with the specified
	 * rendering hints.
	 *
	 * @param hints		Additional rendering hints to apply.
	 */
	public TileResizer(Map<RenderingHints.Key, Object> hints) {
		super(RenderingHints.VALUE_INTERPOLATION_BILINEAR, hints);
	}

	/**
	 * Resizes an image partially and merges them together using the progressive bilinear scaling technique.
	 * <p>
	 * If the source and/or destination image is {@code null}, then a
	 * {@link NullPointerException} will be thrown.
	 *
	 * @param srcImage		The source image.
	 * @param destImage		The destination image.
	 *
	 * @throws NullPointerException		When the source and/or the destination
	 * 									image is {@code null}.
	 */
	@Override
	public void resize (BufferedImage srcImage, BufferedImage destImage)
			throws NullPointerException {
		super.performChecks(srcImage, destImage);

		int currentWidth = srcImage.getWidth();
		int currentHeight = srcImage.getHeight();

		final int targetWidth = destImage.getWidth();
		final int targetHeight = destImage.getHeight();

		// If multi-step downscaling is not required, perform one-step.
		if ((targetWidth * 2 >= currentWidth) && (targetHeight * 2 >= currentHeight)) {
			Graphics2D g = createGraphics(destImage);
			g.drawImage(srcImage, 0, 0, targetWidth, targetHeight, null);
			g.dispose();
			return;
		}

		// target size is bigger than the source size, then call resize() method, or call resizeDownScale() method.
		if (targetWidth >= currentWidth && targetHeight >= currentHeight) {
			resizeUpScale(srcImage, destImage);
		} else {
			resizeDownScale(srcImage, destImage);
		}
	}

	public void resizeUpScale(BufferedImage srcImage, BufferedImage destImage)
			throws NullPointerException {

		int currentWidth = srcImage.getWidth();
		int currentHeight = srcImage.getHeight();

		final int targetWidth = destImage.getWidth();
		final int targetHeight = destImage.getHeight();

		// Temporary image used for in-place resizing of image.
		BufferedImage tempImage = new BufferedImageBuilder(
				currentWidth,
				currentHeight,
				destImage.getType()
		).build();

		Graphics2D g = createGraphics(tempImage);
		g.setComposite(AlphaComposite.Src);

		/*
		 * Determine the size of the first resize step should be.
		 * 1) Beginning from the target size
		 * 2) Increase each dimension by 2
		 * 3) Until reaching the original size
		 */
		int startWidth = targetWidth;
		int startHeight = targetHeight;

		while (startWidth < currentWidth && startHeight < currentHeight) {
			startWidth *= 2;
			startHeight *= 2;
		}

		currentWidth = startWidth / 2;
		currentHeight = startHeight / 2;

		// Perform first resize step.
		g.drawImage(srcImage, 0, 0, currentWidth, currentHeight, null);

		// Perform an in-place progressive bilinear resize.
		while (	(currentWidth >= targetWidth * 2) && (currentHeight >= targetHeight * 2) ) {
			currentWidth /= 2;
			currentHeight /= 2;

			if (currentWidth < targetWidth) {
				currentWidth = targetWidth;
			}
			if (currentHeight < targetHeight) {
				currentHeight = targetHeight;
			}

			g.drawImage(
					tempImage,
					0, 0, currentWidth, currentHeight,
					0, 0, currentWidth * 2, currentHeight * 2,
					null
			);
		}

		g.dispose();

		// Draw the resized image onto the destination image.
		Graphics2D destg = createGraphics(destImage);
		destg.drawImage(tempImage, 0, 0, targetWidth, targetHeight, 0, 0, currentWidth, currentHeight, null);
		destg.dispose();
	}

	public void resizeDownScale(BufferedImage srcImage, BufferedImage destImage)
			throws NullPointerException {

		final int targetWidth = destImage.getWidth();
		final int targetHeight = destImage.getHeight();

		Graphics2D g2d = destImage.createGraphics();

		try {
			int originalWidth = srcImage.getWidth();
			int originalHeight = srcImage.getHeight();

			int tileWidth = Math.min(originalWidth, 512);  // tile width 512 is the maximum size of the tile
			int tileHeight = Math.min(originalHeight, 512); // tile height 512 is the maximum size of the tile

			float scaleX = (float) targetWidth / originalWidth;
			float scaleY = (float) targetHeight / originalHeight;

			int scaledTileWidth = Math.round(tileWidth * scaleX);
			int scaledTileHeight = Math.round(tileHeight * scaleY);

			// process each tile
			int scaledY = 0;
			for (int y = 0; y < originalHeight; y += tileHeight) {
				int currentTileHeight = Math.min(tileHeight, originalHeight - y);
				int actualScaledTileHeight = (y + tileHeight >= originalHeight)
						? targetHeight - scaledY
						: scaledTileHeight;

				// src:x-currentTileWidth, +currentTileWidth -> dest:scaledX-actualScaledTileWidth, +actualScaledTileWidth

				int scaledX = 0;
				for (int x = 0; x < originalWidth; x += tileWidth) {
					// calculate the current tile width
					int currentTileWidth = Math.min(tileWidth, originalWidth - x);

					// calculate the actual scaled tile width
					int actualScaledTileWidth = (x + tileWidth >= originalWidth)
							? targetWidth - scaledX
							: scaledTileWidth;

					// read the tile
					BufferedImage tile = srcImage.getSubimage(x, y, currentTileWidth, currentTileHeight);

					// resize the tile and draw it to the destination image
					g2d.drawImage(tile, scaledX, scaledY, actualScaledTileWidth, actualScaledTileHeight, null);

					// calculate the next X coordinate
					scaledX += actualScaledTileWidth;
				}
				// calculate the next Y coordinate
				scaledY += scaledTileHeight;
			}
		} finally {
			g2d.dispose(); // release resources
		}

	}


}
