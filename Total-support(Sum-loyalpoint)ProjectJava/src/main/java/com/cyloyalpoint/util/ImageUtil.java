package com.cyloyalpoint.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageUtil {

	private ImageUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static BufferedImage cropImage(URL url, int x, int y, int radius, int margin) {
		try {
			BufferedImage bimg = ImageIO.read(url);
			BufferedImage bi = new BufferedImage((2 * radius) + (2 * margin), (2 * radius) + (2 * margin),
					BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = bi.createGraphics();
			g2.translate(bi.getWidth() / 2, bi.getHeight() / 2);

			Arc2D myarea = new Arc2D.Float(0 - radius, 0 - radius, 2 * radius, 2 * radius, 0, -360, Arc2D.OPEN);

			AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
			g2.setComposite(composite);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g2.setClip(myarea);
			g2.drawImage(bimg.getSubimage(x - radius, y - radius, x + radius, y + radius), -radius, -radius, null);

			return bi;

		} catch (Exception e) {
			return null;
		}
	}
}
