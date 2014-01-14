package com.rhcloud.numbercharacter.analysisofverificationcode.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {

	public static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);
	public static final int MAX = 255;
	private static final Color WHITE_32 = Color.WHITE;
	// private static final int BLACK_32 = (255 << 24) | (0 << 16) | (0 << 8) |
	// 0;
	private static final Color BLACK_32 = Color.BLACK;
	/**
	 * 中值滤波
	 */
	public static final int MEDIAN_FILTER = 10;
	/**
	 * 均值滤波
	 */
	public static final int MEAN_FILTER = 11;

	public static final int LINE_GRAY = 12;

	public static final int GRAY_FILTER = 13;

	/**
	 * 膨胀
	 */
	public static final int ERODE_FILTER = 20;
	/**
	 * 腐蚀
	 */
	public static final int DILATE_FILTER = 21;

	/**
	 * 二值化
	 * 
	 * @param image
	 * @param grey
	 */
	public static BufferedImage binary(BufferedImage image, int gray) {
		LOG.info("binary:" + gray);
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 0; i < width * height; i++) {
			int red, green, blue;
			int alpha = cm.getAlpha(pixels[i]);
			int rgb = pixels[i];
			/*
			 * 使用getRGB(i,j)获取的该点的颜色值是ARGB， 而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB，
			 * 即bufImg.getRGB(i, j) & 0xFFFFFF。
			 */
			// int r = (rgb & 0xff0000) >> 16;
			// int g = (rgb & 0xff00) >> 8;
			// int b = (rgb & 0xff);
			int r = cm.getRed(rgb);
			int g = cm.getGreen(rgb);
			int b = cm.getBlue(rgb);
			int _gray = (int) (r * 0.3 + g * 0.59 + b * 0.11); // 计算灰度值
			// if (cm.getRed(pixels[i]) > gray) {
			// red = MAX;
			// } else {
			// red = 0;
			// }
			// if (cm.getGreen(pixels[i]) > gray) {
			// green = MAX;
			// } else {
			// green = 0;
			// }
			// if (cm.getBlue(pixels[i]) > gray) {
			// blue = MAX;
			// } else {
			// blue = 0;
			// }
			if (_gray > gray) {
				pixels[i] = WHITE_32.getRGB();
			} else {
				pixels[i] = BLACK_32.getRGB();
			}
			// pixels[i] = alpha << 24 | red << 16 | green << 8 | blue;
		}
		for (int i = 0; i < pixels.length; i++) {
			int r = cm.getRed(pixels[i]);
			int g = cm.getGreen(pixels[i]);
			int b = cm.getBlue(pixels[i]);
			if ((r != 255 && r != 0) || (g != 255 && g != 0)
					|| (b != 255 && b != 0)) {
				System.out.println("r=" + r + "\tg=" + g + "\tb=" + b);
			}
		}
		return pixelsToImage(pixels, width, height,
				BufferedImage.TYPE_BYTE_BINARY);
	}

	/**
	 * 锐化
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage sharp(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 象素的中间变量
		int tempPixels[] = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			tempPixels[i] = pixels[i];
		}

		// 对图像进行尖锐化处理，Alpha值保持不变
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				int alpha = cm.getAlpha(pixels[i * width + j]);
				// 对图像进行尖锐化
				int red6 = cm.getRed(pixels[i * width + j + 1]);
				int red5 = cm.getRed(pixels[i * width + j]);
				int red8 = cm.getRed(pixels[(i + 1) * width + j]);
				int sharpRed = Math.abs(red6 - red5) + Math.abs(red8 - red5);

				int green5 = cm.getGreen(pixels[i * width + j]);
				int green6 = cm.getGreen(pixels[i * width + j + 1]);
				int green8 = cm.getGreen(pixels[(i + 1) * width + j]);

				int sharpGreen = Math.abs(green6 - green5)
						+ Math.abs(green8 - green5);

				int blue5 = cm.getBlue(pixels[i * width + j]);
				int blue6 = cm.getBlue(pixels[i * width + j + 1]);
				int blue8 = cm.getBlue(pixels[(i + 1) * width + j]);

				int sharpBlue = Math.abs(blue6 - blue5)
						+ Math.abs(blue8 - blue5);
				if (sharpRed > MAX) {
					sharpRed = MAX;
				}
				if (sharpGreen > MAX) {
					sharpGreen = MAX;
				}
				if (sharpBlue > MAX) {
					sharpBlue = MAX;
				}
				tempPixels[i * width + j] = alpha << 24 | sharpRed << 16
						| sharpGreen << 8 | sharpBlue;
			}
		}
		return pixelsToImage(tempPixels, width, height);

	}

	/**
	 * 中值滤波
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage medianFilter(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}

		// 对图像进行中值滤波，Alpha值保持不变
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				int red, green, blue;
				int alpha = cm.getAlpha(pixels[i * width + j]);
				// int red2 = cm.getRed(pixels[(i - 1) * iw + j]);

				int red4 = cm.getRed(pixels[i * width + j - 1]);
				int red5 = cm.getRed(pixels[i * width + j]);
				int red6 = cm.getRed(pixels[i * width + j + 1]);
				// int red8 = cm.getRed(pixels[(i + 1) * iw + j]);
				// 水平方向进行中值滤波
				if (red4 >= red5) {
					if (red5 >= red6) {
						red = red5;
					} else {
						if (red4 >= red6) {
							red = red6;
						} else {
							red = red4;
						}
					}
				} else {
					if (red4 > red6) {
						red = red4;
					} else {
						if (red5 > red6) {
							red = red6;
						} else {
							red = red5;
						}
					}
				}

				// int green2 = cm.getGreen(pixels[(i - 1) * iw + j]);
				int green4 = cm.getGreen(pixels[i * width + j - 1]);
				int green5 = cm.getGreen(pixels[i * width + j]);
				int green6 = cm.getGreen(pixels[i * width + j + 1]);

				// int green8 = cm.getGreen(pixels[(i + 1) * iw + j]);
				// 水平方向进行中值滤波
				if (green4 >= green5) {
					if (green5 >= green6) {
						green = green5;
					} else {
						if (green4 >= green6) {
							green = green6;
						} else {
							green = green4;
						}
					}
				} else {
					if (green4 > green6) {
						green = green4;
					} else {
						if (green5 > green6) {
							green = green6;
						} else {
							green = green5;
						}
					}
				}

				// int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
				int blue4 = cm.getBlue(pixels[i * width + j - 1]);
				int blue5 = cm.getBlue(pixels[i * width + j]);
				int blue6 = cm.getBlue(pixels[i * width + j + 1]);

				// int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);
				// 水平方向进行中值滤波
				if (blue4 >= blue5) {
					if (blue5 >= blue6) {
						blue = blue5;
					} else {
						if (blue4 >= blue6) {
							blue = blue6;
						} else {
							blue = blue4;
						}
					}
				} else {
					if (blue4 > blue6) {
						blue = blue4;
					} else {
						if (blue5 > blue6) {
							blue = blue6;
						} else {
							blue = blue5;
						}
					}
				}
				pixels[i * width + j] = alpha << 24 | red << 16 | green << 8
						| blue;
			}
		}
		// 将数组中的象素产生一个图像
		return pixelsToImage(pixels, width, height);
	}

	/**
	 * 均值滤波
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage meanFilter(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				int red1 = cm.getRed(pixels[(i - 1) * width + j - 1]);
				int red2 = cm.getRed(pixels[(i - 1) * width + j]);
				int red3 = cm.getRed(pixels[(i - 1) * width + j + 1]);
				int red4 = cm.getRed(pixels[i * width + j - 1]);
				int red6 = cm.getRed(pixels[i * width + j + 1]);
				int red7 = cm.getRed(pixels[(i + 1) * width + j - 1]);
				int red8 = cm.getRed(pixels[(i + 1) * width + j]);
				int red9 = cm.getRed(pixels[(i + 1) * width + j + 1]);
				int meanRed = (red1 + red2 + red3 + red4 + red6 + red7 + red8 + red9) / 8;

				int green1 = cm.getGreen(pixels[(i - 1) * width + j - 1]);
				int green2 = cm.getGreen(pixels[(i - 1) * width + j]);
				int green3 = cm.getGreen(pixels[(i - 1) * width + j + 1]);
				int green4 = cm.getGreen(pixels[i * width + j - 1]);
				int green6 = cm.getGreen(pixels[i * width + j + 1]);
				int green7 = cm.getGreen(pixels[(i + 1) * width + j - 1]);
				int green8 = cm.getGreen(pixels[(i + 1) * width + j]);
				int green9 = cm.getGreen(pixels[(i + 1) * width + j + 1]);
				int meanGreen = (green1 + green2 + green3 + green4 + green6
						+ green7 + green8 + green9) / 8;

				int blue1 = cm.getBlue(pixels[(i - 1) * width + j - 1]);
				int blue2 = cm.getBlue(pixels[(i - 1) * width + j]);
				int blue3 = cm.getBlue(pixels[(i - 1) * width + j + 1]);
				int blue4 = cm.getBlue(pixels[i * width + j - 1]);
				int blue6 = cm.getBlue(pixels[i * width + j + 1]);
				int blue7 = cm.getBlue(pixels[(i + 1) * width + j - 1]);
				int blue8 = cm.getBlue(pixels[(i + 1) * width + j]);
				int blue9 = cm.getBlue(pixels[(i + 1) * width + j + 1]);
				int meanBlue = (blue1 + blue2 + blue3 + blue4 + blue6 + blue7
						+ blue8 + blue9) / 8;

				int rgb = MAX << 24 | meanRed << 16 | meanGreen << 8 | meanBlue;
				// pixels[(i - 1) * width + j - 1] = rgb;
				pixels[i * width + j] = rgb;
			}
		}
		return pixelsToImage(pixels, width, height);
	}

	/**
	 * 线性灰度转换
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage lineGray(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}

		// 对图像进行进行线性拉伸，Alpha值保持不变
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 0; i < width * height; i++) {
			int alpha = cm.getAlpha(pixels[i]);
			int red = cm.getRed(pixels[i]);
			int green = cm.getGreen(pixels[i]);
			int blue = cm.getBlue(pixels[i]);
			// 增加了图像的亮度
			red = (int) (1.1 * red + 30);
			green = (int) (1.1 * green + 30);
			blue = (int) (1.1 * blue + 30);
			if (red >= MAX) {
				red = MAX;
			}
			if (green >= MAX) {
				green = MAX;
			}
			if (blue >= MAX) {
				blue = MAX;
			}
			pixels[i] = alpha << 24 | red << 16 | green << 8 | blue;
		}
		return pixelsToImage(pixels, width, height);
	}

	/** 转换为黑白灰度图 */
	public static BufferedImage grayFilter(BufferedImage image) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(image, null);
	}

	/** 平滑缩放 */
	public static BufferedImage scaling(BufferedImage image, double s) {
		AffineTransform tx = new AffineTransform();
		tx.scale(s, s);
		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image, null);
	}

	public static BufferedImage scale(BufferedImage image, Float s) {
		int srcW = image.getWidth();
		int srcH = image.getHeight();
		int newW = Math.round(srcW * s);
		int newH = Math.round(srcH * s);
		// 先做水平方向上的伸缩变换
		BufferedImage tmp = new BufferedImage(newW, newH, image.getType());
		Graphics2D g = tmp.createGraphics();
		for (int x = 0; x < newW; x++) {
			g.setClip(x, 0, 1, srcH);
			// 按比例放缩
			g.drawImage(image, x - x * srcW / newW, 0, null);
		}
		g.dispose();

		// 再做垂直方向上的伸缩变换
		BufferedImage dst = new BufferedImage(newW, newH, image.getType());
		// Graphics2D g = dst.createGraphics();
		g = dst.createGraphics();
		for (int y = 0; y < newH; y++) {
			g.setClip(0, y, newW, 1);
			// 按比例放缩
			g.drawImage(tmp, 0, y - y * srcH / newH, null);
		}
		g.dispose();
		return dst;
	}

	/**
	 * 图片灰度直方图，横坐标为灰度，纵坐标为其在图像中出现的个数
	 * 
	 * @param image
	 * @return
	 */
	public static int[] gray(BufferedImage image) {
		int[] grays = new int[MAX + 1];
		int width = image.getWidth();
		int height = image.getHeight();
		// Raster raster = image.getData();
		int pixels[] = new int[width * height];
		// pixels = raster.getPixels(0, 0, width, height, pixels);
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		for (int i = 0; i < pixels.length; i++) {
			int rgb = pixels[i];
			/*
			 * 应为使用getRGB(i,j)获取的该点的颜色值是ARGB， 而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB，
			 * 即bufImg.getRGB(i, j) & 0xFFFFFF。
			 */
			int r = (rgb & 0xff0000) >> 16;
			int g = (rgb & 0xff00) >> 8;
			int b = (rgb & 0xff);
			int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11); // 计算灰度值
			grays[gray]++;
		}
		return grays;
	}

	/**
	 * 使用整幅图像的灰度平均值作为二值化的阈值，一般该方法可作为其他方法的初始猜想值
	 * 
	 * @param histGram
	 * @return
	 */
	public static int getMeanThreshold(int[] histGram) {
		int sum = 0, amount = 0;
		for (int i = 0; i < histGram.length; i++) {
			amount += histGram[i];
			sum += (i * histGram[i]);
		}
		return sum / amount;
	}

	/**
	 * Doyle于1962年提出的P-Tile
	 * (即P分位数法)可以说是最古老的一种阈值选取方法。该方法根据先验概率来设定阈值，使得二值化后的目标或背景像素比例等于先验概率
	 * ，该方法简单高效，但是对于先验概率难于估计的图像却无能为力
	 * 
	 * @param histGram
	 * @return
	 */
	public static int getPTileThreshold(int[] histGram) {
		int tile = 50;// 背景在图像中所占的面积百分比
		int i, amount = 0, sum = 0;
		for (i = 0; i < 256; i++)
			amount += histGram[i]; // 像素总数
		for (i = 0; i < 256; i++) {
			sum = sum + histGram[i];
			if (sum >= amount * tile / 100)
				return i;
		}
		return -1;
	}

	/**
	 * 此方法实用于具有明显双峰直方图的图像，其寻找双峰的谷底作为阈值，但是该方法不一定能获得阈值，对于那些具有平坦的直方图或单峰图像，该方法不合适
	 * 
	 * 该函数的实现是一个迭代的过程，每次处理前对直方图数据进行判断，看其是否已经是一个双峰的直方图，如果不是，则对直方图数据进行半径为1（窗口大小为3）
	 * 的平滑，如果迭代了一定的数量比如1000次后仍未获得一个双峰的直方图，则函数执行失败，如成功获得，则最终阈值取两个双峰之间的谷底值作为阈值。
	 * 
	 * 在处理过程中，平滑的处理需要当前像素之前的信息，因此需要对平滑前的数据进行一个备份。另外，首数据类型精度限制，不应用整形的直方图数据，
	 * 必须转换为浮点类型数据来进行处理，否则得不到正确的结果。
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int getMinimumThreshold(int[] HistGram) {
		int Y, Iter = 0;
		double[] HistGramC = new double[256]; // 基于精度问题，一定要用浮点数来处理，否则得不到正确的结果
		double[] HistGramCC = new double[256]; // 求均值的过程会破坏前面的数据，因此需要两份数据
		for (Y = 0; Y < 256; Y++) {
			HistGramC[Y] = HistGram[Y];
			HistGramCC[Y] = HistGram[Y];
		}

		// 通过三点求均值来平滑直方图
		while (IsDimodal(HistGramCC) == false) // 判断是否已经是双峰的图像了
		{
			HistGramCC[0] = (HistGramC[0] + HistGramC[0] + HistGramC[1]) / 3; // 第一点
			for (Y = 1; Y < 255; Y++)
				HistGramCC[Y] = (HistGramC[Y - 1] + HistGramC[Y] + HistGramC[Y + 1]) / 3; // 中间的点
			HistGramCC[255] = (HistGramC[254] + HistGramC[255] + HistGramC[255]) / 3; // 最后一点
			System.arraycopy(HistGramCC, 0, HistGramC, 0, 256);
			// System.Buffer.BlockCopy(HistGramCC, 0, HistGramC, 0, 256 *
			// sizeof(double));
			Iter++;
			if (Iter >= 1000)
				return -1; // 直方图无法平滑为双峰的，返回错误代码
		}
		// 阈值极为两峰之间的最小值
		boolean Peakfound = false;
		for (Y = 1; Y < 255; Y++) {
			if (HistGramCC[Y - 1] < HistGramCC[Y]
					&& HistGramCC[Y + 1] < HistGramCC[Y])
				Peakfound = true;
			if (Peakfound == true && HistGramCC[Y - 1] >= HistGramCC[Y]
					&& HistGramCC[Y + 1] >= HistGramCC[Y])
				return Y - 1;
		}
		return -1;
	}

	/**
	 * 判断直方图是否是双峰
	 */
	private static boolean IsDimodal(double[] HistGram) // 检测直方图是否为双峰的
	{
		// 对直方图的峰进行计数，只有峰数位2才为双峰
		int Count = 0;
		for (int Y = 1; Y < 255; Y++) {
			if (HistGram[Y - 1] < HistGram[Y] && HistGram[Y + 1] < HistGram[Y]) {
				Count++;
				if (Count > 2)
					return false;
			}
		}
		if (Count == 2)
			return true;
		else
			return false;
	}

	/**
	 * 和基于谷底最小值的阈值方法类似，只是最后一步不是取得双峰之间的谷底值，而是取双峰的平均值作为阈值
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int getIntermodesThreshold(int[] HistGram) {
		int Y, Iter = 0, Index;
		double[] HistGramC = new double[256]; // 基于精度问题，一定要用浮点数来处理，否则得不到正确的结果
		double[] HistGramCC = new double[256]; // 求均值的过程会破坏前面的数据，因此需要两份数据
		for (Y = 0; Y < 256; Y++) {
			HistGramC[Y] = HistGram[Y];
			HistGramCC[Y] = HistGram[Y];
		}
		// 通过三点求均值来平滑直方图
		while (IsDimodal(HistGramCC) == false) // 判断是否已经是双峰的图像了
		{
			HistGramCC[0] = (HistGramC[0] + HistGramC[0] + HistGramC[1]) / 3; // 第一点
			for (Y = 1; Y < 255; Y++)
				HistGramCC[Y] = (HistGramC[Y - 1] + HistGramC[Y] + HistGramC[Y + 1]) / 3; // 中间的点
			HistGramCC[255] = (HistGramC[254] + HistGramC[255] + HistGramC[255]) / 3; // 最后一点
			// System.Buffer.BlockCopy(HistGramCC, 0, HistGramC, 0, 256 *
			// sizeof(double)); // 备份数据，为下一次迭代做准备
			System.arraycopy(HistGramCC, 0, HistGramC, 0, 256);
			Iter++;
			if (Iter >= 10000)
				return -1; // 似乎直方图无法平滑为双峰的，返回错误代码
		}
		// 阈值为两峰值的平均值
		int[] Peak = new int[2];
		for (Y = 1, Index = 0; Y < 255; Y++)
			if (HistGramCC[Y - 1] < HistGramCC[Y]
					&& HistGramCC[Y + 1] < HistGramCC[Y])
				Peak[Index++] = Y - 1;
		return ((Peak[0] + Peak[1]) / 2);
	}

	/**
	 * 先假定一个阈值，然后计算在该阈值下的前景和背景的中心值，当前景和背景中心值得平均值和假定的阈值相同时，则迭代中止，并以此值为阈值进行二值化
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int getIterativeBestThreshold(int[] HistGram) {
		int X, Iter = 0;
		int MeanValueOne, MeanValueTwo, SumOne, SumTwo, SumIntegralOne, SumIntegralTwo;
		int MinValue, MaxValue;
		int Threshold, NewThreshold;

		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;

		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色

		Threshold = MinValue;
		NewThreshold = (MaxValue + MinValue) >> 1;
		while (Threshold != NewThreshold) // 当前后两次迭代的获得阈值相同时，结束迭代
		{
			SumOne = 0;
			SumIntegralOne = 0;
			SumTwo = 0;
			SumIntegralTwo = 0;
			Threshold = NewThreshold;
			for (X = MinValue; X <= Threshold; X++) // 根据阈值将图像分割成目标和背景两部分，求出两部分的平均灰度值
			{
				SumIntegralOne += HistGram[X] * X;
				SumOne += HistGram[X];
			}
			MeanValueOne = SumIntegralOne / SumOne;
			for (X = Threshold + 1; X <= MaxValue; X++) {
				SumIntegralTwo += HistGram[X] * X;
				SumTwo += HistGram[X];
			}
			MeanValueTwo = SumIntegralTwo / SumTwo;
			NewThreshold = (MeanValueOne + MeanValueTwo) >> 1; // 求出新的阈值
			Iter++;
			if (Iter >= 1000)
				return -1;
		}
		return Threshold;
	}

	/**
	 * 该算法是1979年由日本大津提出的，主要是思想是取某个阈值，使得前景和背景两类的类间方差最大，
	 * matlab中的graythresh即是以该算法为原理执行的
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int getOSTUThreshold(int[] HistGram) {
		int Y, Amount = 0;
		int PixelBack = 0, PixelFore = 0, PixelIntegralBack = 0, PixelIntegralFore = 0, PixelIntegral = 0;
		double OmegaBack, OmegaFore, MicroBack, MicroFore, SigmaB, Sigma; // 类间方差;
		int MinValue, MaxValue;
		int Threshold = 0;

		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;
		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色

		for (Y = MinValue; Y <= MaxValue; Y++)
			Amount += HistGram[Y]; // 像素总数

		PixelIntegral = 0;
		for (Y = MinValue; Y <= MaxValue; Y++)
			PixelIntegral += HistGram[Y] * Y;
		SigmaB = -1;
		for (Y = MinValue; Y < MaxValue; Y++) {
			PixelBack = PixelBack + HistGram[Y];
			PixelFore = Amount - PixelBack;
			OmegaBack = (double) PixelBack / Amount;
			OmegaFore = (double) PixelFore / Amount;
			PixelIntegralBack += HistGram[Y] * Y;
			PixelIntegralFore = PixelIntegral - PixelIntegralBack;
			MicroBack = (double) PixelIntegralBack / PixelBack;
			MicroFore = (double) PixelIntegralFore / PixelFore;
			Sigma = OmegaBack * OmegaFore * (MicroBack - MicroFore)
					* (MicroBack - MicroFore);
			if (Sigma > SigmaB) {
				SigmaB = Sigma;
				Threshold = Y;
			}
		}
		return Threshold;
	}

	/**
	 * 一维最大熵
	 * 
	 * 该算法把信息论中熵的概念引入到图像中，通过计算阈值分割后两部分熵的和来判断阈值是否为最佳阈值
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int get1DMaxEntropyThreshold(int[] HistGram) {
		int X, Y, Amount = 0;
		double[] HistGramD = new double[256];
		double SumIntegral, EntropyBack, EntropyFore, MaxEntropy;
		int MinValue = 255, MaxValue = 0;
		int Threshold = 0;

		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;
		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色

		for (Y = MinValue; Y <= MaxValue; Y++)
			Amount += HistGram[Y]; // 像素总数

		for (Y = MinValue; Y <= MaxValue; Y++)
			HistGramD[Y] = (double) HistGram[Y] / Amount + 1e-17;

		MaxEntropy = Double.MIN_VALUE;
		for (Y = MinValue + 1; Y < MaxValue; Y++) {
			SumIntegral = 0;
			for (X = MinValue; X <= Y; X++)
				SumIntegral += HistGramD[X];
			EntropyBack = 0;
			for (X = MinValue; X <= Y; X++)
				EntropyBack += (-HistGramD[X] / SumIntegral * Math
						.log(HistGramD[X] / SumIntegral));
			EntropyFore = 0;
			for (X = Y + 1; X <= MaxValue; X++)
				EntropyFore += (-HistGramD[X] / (1 - SumIntegral) * Math
						.log(HistGramD[X] / (1 - SumIntegral)));
			if (MaxEntropy < EntropyBack + EntropyFore) {
				Threshold = Y;
				MaxEntropy = EntropyBack + EntropyFore;
			}
		}
		return Threshold;
	}

	/**
	 * 力矩保持法
	 * 
	 * 该算法通过选择恰当的阈值从而使得二值后的图像和原始的灰度图像具有三个相同的初始力矩值
	 * 
	 * @param HistGram
	 * @return
	 */
	public static byte getMomentPreservingThreshold(int[] HistGram) {
		int Y = 0, Index = 0, Amount = 0;
		double[] Avec = new double[256];
		double X2, X1, X0, Min;

		for (Y = 0; Y <= 255; Y++)
			Amount += HistGram[Y]; // 像素总数
		for (Y = 0; Y < 256; Y++)
			Avec[Y] = (double) A(HistGram, Y) / Amount; // The threshold is
														// chosen such that
														// A(y,t)/A(y,n) is
														// closest to x0.

		// The following finds x0.

		X2 = (double) (B(HistGram, 255) * C(HistGram, 255) - A(HistGram, 255)
				* D(HistGram, 255))
				/ (double) (A(HistGram, 255) * C(HistGram, 255) - B(HistGram,
						255) * B(HistGram, 255));
		X1 = (double) (B(HistGram, 255) * D(HistGram, 255) - C(HistGram, 255)
				* C(HistGram, 255))
				/ (double) (A(HistGram, 255) * C(HistGram, 255) - B(HistGram,
						255) * B(HistGram, 255));
		X0 = 0.5 - (B(HistGram, 255) / A(HistGram, 255) + X2 / 2)
				/ Math.sqrt(X2 * X2 - 4 * X1);

		for (Y = 0, Min = Double.MAX_VALUE; Y < 256; Y++) {
			if (Math.abs(Avec[Y] - X0) < Min) {
				Min = Math.abs(Avec[Y] - X0);
				Index = Y;
			}
		}
		return (byte) Index;
	}

	private static double A(int[] HistGram, int Index) {
		double Sum = 0;
		for (int Y = 0; Y <= Index; Y++)
			Sum += HistGram[Y];
		return Sum;
	}

	private static double B(int[] HistGram, int Index) {
		double Sum = 0;
		for (int Y = 0; Y <= Index; Y++)
			Sum += (double) Y * HistGram[Y];
		return Sum;
	}

	private static double C(int[] HistGram, int Index) {
		double Sum = 0;
		for (int Y = 0; Y <= Index; Y++)
			Sum += (double) Y * Y * HistGram[Y];
		return Sum;
	}

	private static double D(int[] HistGram, int Index) {
		double Sum = 0;
		for (int Y = 0; Y <= Index; Y++)
			Sum += (double) Y * Y * Y * HistGram[Y];
		return Sum;
	}

	/**
	 * Kittler最小错误分类法
	 * 
	 * @param HistGram
	 * @return
	 */
	public static int getKittlerMinError(int[] HistGram) {
		int X, Y;
		int MinValue, MaxValue;
		int Threshold;
		int PixelBack, PixelFore;
		double OmegaBack, OmegaFore, MinSigma, Sigma, SigmaBack, SigmaFore;
		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;
		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色
		Threshold = -1;
		MinSigma = 1E+20;
		for (Y = MinValue; Y < MaxValue; Y++) {
			PixelBack = 0;
			PixelFore = 0;
			OmegaBack = 0;
			OmegaFore = 0;
			for (X = MinValue; X <= Y; X++) {
				PixelBack += HistGram[X];
				OmegaBack = OmegaBack + X * HistGram[X];
			}
			for (X = Y + 1; X <= MaxValue; X++) {
				PixelFore += HistGram[X];
				OmegaFore = OmegaFore + X * HistGram[X];
			}
			OmegaBack = OmegaBack / PixelBack;
			OmegaFore = OmegaFore / PixelFore;
			SigmaBack = 0;
			SigmaFore = 0;
			for (X = MinValue; X <= Y; X++)
				SigmaBack = SigmaBack + (X - OmegaBack) * (X - OmegaBack)
						* HistGram[X];
			for (X = Y + 1; X <= MaxValue; X++)
				SigmaFore = SigmaFore + (X - OmegaFore) * (X - OmegaFore)
						* HistGram[X];
			if (SigmaBack == 0 || SigmaFore == 0) {
				if (Threshold == -1)
					Threshold = Y;
			} else {
				SigmaBack = Math.sqrt(SigmaBack / PixelBack);
				SigmaFore = Math.sqrt(SigmaFore / PixelFore);
				Sigma = 1 + 2 * (PixelBack * Math.log(SigmaBack / PixelBack) + PixelFore
						* Math.log(SigmaFore / PixelFore));
				if (Sigma < MinSigma) {
					MinSigma = Sigma;
					Threshold = Y;
				}
			}
		}
		return Threshold;
	}

	/**
	 * 腐蚀
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage dilate(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		int[] erodePix = new int[height * width];
		for (int u = 0; u < height - 1; u++)
			for (int v = 0; v < width - 1; v++)
				erodePix[width * u + v] = WHITE_32.getRGB();

		for (int u = 1; u < height - 2; u++)
			for (int v = 1; v < width - 2; v++)
				if (erode_area8(pixels, u, v, WHITE_32.getRGB(), width))
					erodePix[u * width + v] = WHITE_32.getRGB();
				else
					erodePix[u * width + v] = BLACK_32.getRGB();
		return pixelsToImage(erodePix, width, height,
				BufferedImage.TYPE_3BYTE_BGR);
	}

	private static boolean erode_area8(int[] pix, int u, int v, int foreground,
			int w) {
		if (pix[(u - 1) * w + v - 1] == foreground
				&& pix[(u - 1) * w + v] == foreground
				&& pix[(u - 1) * w + v + 1] == foreground
				&& pix[u * w + v + 1] == foreground
				&& pix[u * w + v] == foreground
				&& pix[u * w + v - 1] == foreground
				&& pix[(u + 1) * w + v - 1] == foreground
				&& pix[(u + 1) * w + v] == foreground
				&& pix[(u + 1) * w + v + 1] == foreground)
			return true;
		else
			return false;
	}

	/**
	 * 膨胀
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage erode(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		int[] dilatePix = new int[width * height];
		// 全部设置为黑色
		for (int u = 0; u < height - 1; u++)
			for (int v = 0; v < width - 1; v++)
				dilatePix[u * width + v] = BLACK_32.getRGB();// Ϳ��

		// 如果某个像素为前景色？则周围9个像素全部置为该颜色
		int foreground = WHITE_32.getRGB();
		for (int u = 1; u < height - 2; u++)
			for (int v = 1; v < width - 2; v++) {
				if (pixels[u * width + v] == foreground) {
					dilatePix[(u - 1) * width + v - 1] = foreground;
					dilatePix[(u - 1) * width + v] = foreground;
					dilatePix[(u - 1) * width + v + 1] = foreground;
					dilatePix[u * width + v + 1] = foreground;
					dilatePix[u * width + v] = foreground;
					dilatePix[u * width + v - 1] = foreground;
					dilatePix[(u + 1) * width + v - 1] = foreground;
					dilatePix[(u + 1) * width + v] = foreground;
					dilatePix[(u + 1) * width + v + 1] = foreground;
				}
			}
		return pixelsToImage(dilatePix, width, height,
				BufferedImage.TYPE_3BYTE_BGR);
	}

	public static BufferedImage thinHilditch(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		ColorModel cm = ColorModel.getRGBdefault();
		int[][] thin = new int[height][width];
		// 初始化二维数组，假定白色为背景色，黑色为前景色
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				thin[i][j] = cm.getRGB(pixels[i * width + j]) == WHITE_32
						.getRGB() ? 0 : 1;
			}
		}
		Vector<Point> mark = new Vector<Point>();// 用于标记要删除的点的x和y坐标；
		boolean IsModified = true;
		int[] nnb = new int[8];
		// 去掉边框像素
		for (int i = 0; i < width; i++) {
			thin[0][i] = 0;
			thin[height - 1][i] = 0;
		}
		for (int i = 0; i < height; i++) {
			thin[i][0] = 0;
			thin[i][width - 1] = 0;
		}

		do {
			IsModified = false;

			// 每次周期循环判断前，先将数组中被标记的点变成0；
			for (int i = 0; i < mark.size(); i++) {
				Point p = mark.get(i);
				thin[p.x][p.y] = 0;
			}
			mark.clear();// 将向量清空
			int[][] nb = new int[3][3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					// 条件1必须为黑点
					if (thin[i][j] != 1)
						continue;
					// 赋值3*3领域
					for (int m = 0; m < 3; m++) {
						for (int n = 0; n < 3; n++) {
							nb[m][n] = thin[i - 1 + m][j - 1 + n];
						}
					}
					// 复制
					nnb[0] = nb[1][2];
					nnb[1] = nb[0][2];
					nnb[2] = nb[0][1];
					nnb[3] = nb[0][0];
					nnb[4] = nb[1][0];
					nnb[5] = nb[2][0];
					nnb[6] = nb[2][1];
					nnb[7] = nb[2][2];
					// 条件2：p0,p2,p4,p6 不皆为前景点 ，4邻域点不能全为1；
					if (nnb[0] == 1 && nnb[2] == 1 && nnb[4] == 1
							&& nnb[6] == 1) {
						continue;
					}
					// 条件3: p0~p7至少两个是前景点 ，8邻域至少有2个点为1；
					int icount = 0;
					for (int ii = 0; ii < 8; ii++) {
						icount += nnb[ii];
					}
					if (icount < 2) {
						continue;
					}
					// 条件4：联结数等于1
					if (1 != detectConnectivity(nnb)) {
						continue;
					}
					// 条件5: 假设p2已标记删除，则令p2为背景，不改变p的联结数
					Point p2 = new Point(i - 1, j);
					if (mark.indexOf(p2) != -1) // 如果在向量mark中找到点p2
					{
						nnb[2] = 0;
						if (1 != detectConnectivity(nnb)) {
							nnb[2] = 1;
							continue;
						}
						nnb[2] = 1;
					}

					// 条件6: 假设p4已标记删除，则令p4为背景，不改变p的联结数
					Point p4 = new Point(i, j - 1);
					if (mark.indexOf(p4) == -1) // 如果p4没有标记将p点标记
					{
						Point p = new Point(i, j);
						mark.add(p);
						IsModified = true;
						continue;
					}

					// 如果p4 标记了，先把点p4变成0
					nnb[4] = 0;
					if (1 == detectConnectivity(nnb)) {// 如果p的连接数没有改变；将p标记
						Point p = new Point(i, j);
						mark.add(p);
						IsModified = true;
					}

				}
			}
		} while (IsModified);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// thin[i][j] =
				// cm.getRGB(pixels[i*width+j])==WHITE_32.getRGB()?0:1;
				pixels[i * width + j] = thin[i][j] == 0 ? WHITE_32.getRGB()
						: BLACK_32.getRGB();
			}
		}
		return pixelsToImage(pixels, width, height);
	}

	public static BufferedImage thinnerRosenfeld(BufferedImage image) {
		int ly = image.getHeight();
		int lx = image.getWidth();
		int[] f = new int[lx * ly];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, lx, ly, f,
				0, lx);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		int[] g = new int[lx * ly];
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 0; i < f.length; i++) {
			f[i] = cm.getRGB(f[i]) == BLACK_32.getRGB() ? 1 : 0;
			g[i] = f[i];
		}
		//

		int shori, ii, kk, jj, kk1, kk2, kk3;
		int nrnd, cond, n48, n26, n24, n46, n68, n82, n123, n345, n567, n781;
		int[] a = { 0, -1, 1, 0, 0 };
		int[] b = { 0, 0, 0, 1, -1 };
		int[] n = new int[10];
		do {
			shori = 0;
			for (int k = 1; k <= 4; k++) {
				for (int i = 1; i < lx - 1; i++) {
					ii = i + a[k];

					for (int j = 1; j < ly - 1; j++) {
						kk = i * ly + j;
						if (f[kk] == 0)
							continue;

						jj = j + b[k];
						kk1 = ii * ly + jj;

						if (f[kk1] != 0)
							continue;

						kk1 = kk - ly - 1;
						kk2 = kk1 + 1;
						kk3 = kk2 + 1;
						n[3] = f[kk1];
						n[2] = f[kk2];
						n[1] = f[kk3];
						kk1 = kk - 1;
						kk3 = kk + 1;
						n[4] = f[kk1];
						n[8] = f[kk3];
						kk1 = kk + ly - 1;
						kk2 = kk1 + 1;
						kk3 = kk2 + 1;
						n[5] = f[kk1];
						n[6] = f[kk2];
						n[7] = f[kk3];

						nrnd = n[1] + n[2] + n[3] + n[4] + n[5] + n[6] + n[7]
								+ n[8];
						if (nrnd <= 1)
							continue;

						cond = 0;
						n48 = n[4] + n[8];
						n26 = n[2] + n[6];
						n24 = n[2] + n[4];
						n46 = n[4] + n[6];
						n68 = n[6] + n[8];
						n82 = n[8] + n[2];
						n123 = n[1] + n[2] + n[3];
						n345 = n[3] + n[4] + n[5];
						n567 = n[5] + n[6] + n[7];
						n781 = n[7] + n[8] + n[1];

						if (n[2] == 1 && n48 == 0 && n567 > 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[6] == 1 && n48 == 0 && n123 > 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[8] == 1 && n26 == 0 && n345 > 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[4] == 1 && n26 == 0 && n781 > 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[5] == 1 && n46 == 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[7] == 1 && n68 == 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[1] == 1 && n82 == 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						if (n[3] == 1 && n24 == 0) {
							if (cond != 0)
								continue;
							g[kk] = 0;
							shori = 1;
							continue;
						}

						cond = 1;
						if (cond != 0)
							continue;
						g[kk] = 0;
						shori = 1;
					}
				}

				for (int i = 0; i < lx; i++) {
					for (int j = 0; j < ly; j++) {
						kk = i * ly + j;
						f[kk] = g[kk];
					}
				}
			}
		} while (shori > 0);

		for (int i = 0; i < g.length; i++) {
			g[i] = g[i] == 0 ? WHITE_32.getRGB() : BLACK_32.getRGB();
		}

		return pixelsToImage(g, lx, ly);
	}

	private static int detectConnectivity(int[] a) {
		int size = 0;
		int a0, a1, a2, a3, a4, a5, a6, a7;
		a0 = 1 - a[0];
		a1 = 1 - a[1];
		a2 = 1 - a[2];
		a3 = 1 - a[3];
		a4 = 1 - a[4];
		a5 = 1 - a[5];
		a6 = 1 - a[6];
		a7 = 1 - a[7];
		size = a0 - a0 * a1 * a2 + a2 - a2 * a3 * a4 + a4 - a4 * a5 * a6 + a6
				- a6 * a7 * a0;
		return size;
	}

	private static BufferedImage pixelsToImage(int[] pixels, int width,
			int height, int type) {
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, pixels, 0, width));
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);
		// Graphics2D g = bufferedImage.createGraphics();
		// g.drawImage(image, 0, 0, null);
		ColorModel cm = ColorModel.getRGBdefault();
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				// int rgb = cm.getRGB(pixels[h*width+w]);
				// if (pixels[h * width + w] != WHITE_32.getRGB()
				// && pixels[h * width + w] != BLACK_32.getRGB()) {
				// System.out.println(pixels[h * width + w]);
				// }
				// if (pixels[h * width + w] == WHITE_32.getRGB())
				// bufferedImage.setRGB(w, h, 0xFFFFFF);
				// else
				// bufferedImage.setRGB(w, h, 0x000000);
				bufferedImage.setRGB(w, h, pixels[h * width + w]);
			}

		// for (int i = 0; i < pixels.length; i++) {
		// int r = cm.getRed(pixels[i]);
		// int g = cm.getGreen(pixels[i]);
		// int b = cm.getBlue(pixels[i]);
		// if ((r!=255&&r!=0)||(g!=255&&g!=0)||(b!=255&&b!=0)){
		// System.out.println("r="+r+"\tg="+g+"\tb="+b);
		// }
		// }
		// try {
		// testimage(bufferedImage);
		// ImageIO.write(bufferedImage, "png", new
		// File("C:\\images\\0001.png"));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return bufferedImage;
	}

	private static void testimage(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] pixels = new int[height * width];
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, width,
				height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 0; i < pixels.length; i++) {
			int r = cm.getRed(pixels[i]);
			int g = cm.getGreen(pixels[i]);
			int b = cm.getBlue(pixels[i]);
			if ((r != 255 && r != 0) || (g != 255 && g != 0)
					|| (b != 255 && b != 0)) {
				System.out.println("r=" + r + "\tg=" + g + "\tb=" + b);
			}
		}
	}

	private static BufferedImage pixelsToImage(int[] pixels, int width,
			int height) {
		return pixelsToImage(pixels, width, height, BufferedImage.TYPE_INT_RGB);
	}

}
