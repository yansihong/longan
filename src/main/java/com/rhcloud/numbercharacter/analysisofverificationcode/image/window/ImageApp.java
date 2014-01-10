package com.rhcloud.numbercharacter.analysisofverificationcode.image.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhcloud.numbercharacter.analysisofverificationcode.image.ImageUtils;

public final class ImageApp {

	private static final Logger LOG = LoggerFactory.getLogger(ImageApp.class);
	private static ImageApp imageApp = null;
	private JFrame jFrame;
	private Component component;
	private BufferedImage srcImage;

	public BufferedImage getSrcImage() {
		return srcImage;
	}

	public void setSrcImage(BufferedImage srcImage) {
		this.srcImage = srcImage;
	}

	public ImageApp(JFrame frame) {
		this.jFrame = frame;
	}

	public static void filter(int code) {
		switch (code) {
		case ImageUtils.MEAN_FILTER:
			imageApp.repaint(ImageUtils.meanFilter(imageApp.getSrcImage()));
			break;
		case ImageUtils.MEDIAN_FILTER:
			imageApp.repaint(ImageUtils.medianFilter(imageApp.getSrcImage()));
			break;
		default:
			break;
		}
	}

	public void repaint(BufferedImage image) {
		if (this.component != null)
			this.jFrame.remove(component);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		double ws = 1.0;
		double hs = 1.0;
		double s = 1.0;
		if (image.getWidth() > scrSize.getWidth()) {
			ws = scrSize.getWidth() / image.getWidth() * 0.75f;
		}
		if (image.getHeight() > scrSize.getHeight()) {
			hs = scrSize.getHeight() / image.getHeight() * 0.75f;
		}
		s = ws > hs ? hs : ws;
		BufferedImage bm = image;
		if (s < 1.0) {
			bm = ImageUtils.scaling(image, s);
		}
		LOG.info("After scale..Width:" + bm.getWidth() + "\theigth:"
				+ bm.getHeight());
		ImagePanel iPanel = new ImagePanel(bm);
		iPanel.setBounds(0, 0, bm.getWidth(), bm.getHeight());
		component = this.jFrame.add(iPanel);
		this.jFrame.getContentPane().validate();
		this.srcImage = bm;
	}

	public static void initImageApp(JFrame frame) {
		imageApp = new ImageApp(frame);
	}

	public static void openImage(File file) throws FileNotFoundException,
			IOException {
		BufferedImage image = ImageIO.read(new FileInputStream(file));
		LOG.info("Width:" + image.getWidth() + "\theight:" + image.getHeight());
		imageApp.repaint(image);
	}

	static class ImagePanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage image;

		public ImagePanel(BufferedImage image) {
			this.image = image;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(this.image, 0, 0, image.getWidth(), image.getHeight(),
					null);
		}

	}
}
