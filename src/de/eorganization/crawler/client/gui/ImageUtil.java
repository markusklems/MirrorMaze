/**
 * 
 */
package de.eorganization.crawler.client.gui;

import com.google.gwt.user.client.ui.Image;

/**
 * @author mugglmenzel
 * 
 */
public final class ImageUtil {

	public static int getScaledImageWidth(String url, int height) {
		if (url == null)
			return 0;
		Image.prefetch(url);
		Image img = new Image(url);
		if (img.getHeight() > 0 && img.getWidth() > 0)
			return height * img.getWidth() / img.getHeight();
		else
			return height;
	}

	public static int getScaledImageHeight(String url, int width) {
		if (url == null)
			return 0;
		Image.prefetch(url);
		Image img = new Image(url);
		if (img.getHeight() > 0 && img.getWidth() > 0)
			return width * img.getHeight() / img.getWidth();
		else
			return width;
	}

}
