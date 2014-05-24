package net.ussoft.archive.util;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c)2007-6-13
 * </p>
 * <p>
 * Company: fuen
 * </p>
 * 
 * @author 杨振朋
 * @version 1.0
 */
public class ResizeImage {
	private String srcFile;
	private String destFile;
	private int width;
	private int height;
	private Image img;

	/**
	 * 构造函数
	 * 
	 * @param fileName
	 *            String
	 * @throws IOException
	 */
	public ResizeImage(String fileName) throws IOException {
		File _file = new File(fileName); // 读入文件
		this.srcFile = _file.getName();
		this.destFile = "/Users/wangf/software/pic/33.jpg";// this.srcFile.substring(0,
									// this.srcFile.lastIndexOf(".")) +"_s.jpg";
		img = javax.imageio.ImageIO.read(_file); // 构造Image对象
		width = img.getWidth(null); // 得到源图宽
		height = img.getHeight(null); // 得到源图长
	}

	/**
	 * 强制压缩/放大图片到固定的大小
	 * 
	 * @param w
	 *            int 新宽度
	 * @param h
	 *            int 新高度
	 * @throws IOException
	 */
	public void resize(int w, int h) throws IOException {
		BufferedImage _image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		_image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
		FileOutputStream newimageout = new FileOutputStream(destFile); // 输出到文件流
		/*
		 * JPEGImageEncoder 将图像缓冲数据编码为 JPEG 数据流。该接口的用户应在 Raster 或 BufferedImage
		 * 中提供图像数据，在 JPEGEncodeParams 对象中设置必要的参数， 并成功地打开 OutputStream（编码 JPEG
		 * 流的目的流）。JPEGImageEncoder 接口可 将图像数据编码为互换的缩略 JPEG 数据流，该数据流将写入提供给编码器的
		 * OutputStream 中。 注意：com.sun.image.codec.jpeg 包中的类并不属于核心 Java API。它们属于
		 * Sun 发布的 JDK 和 JRE 产品的组成部分。虽然其它获得许可方可能选择发布这些类，但开发人员不能寄 希望于从非 Sun
		 * 实现的软件中得到它们。我们期望相同的功能最终可以在核心 API 或标准扩 展中得到。
		 */
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimageout);
		encoder.encode(_image); // 近JPEG编码
		newimageout.close();
	}

	/**
	 * 按照固定的比例缩放图片
	 * 
	 * @param t
	 *            double 比例
	 * @throws IOException
	 */
	public void resize(double t) throws IOException {
		int w = (int) (width * t);
		int h = (int) (height * t);
		resize(w, h);
	}

	/**
	 * 以宽度为基准，等比例放缩图片
	 * 
	 * @param w
	 *            int 新宽度
	 * @throws IOException
	 */
	public void resizeByWidth(int w) throws IOException {
		int h = (int) (height * w / width);
		resize(w, h);
	}

	/**
	 * 以高度为基准，等比例缩放图片
	 * 
	 * @param h
	 *            int 新高度
	 * @throws IOException
	 */
	public void resizeByHeight(int h) throws IOException {
		int w = (int) (width * h / height);
		resize(w, h);
	}

	/**
	 * 按照最大高度限制，生成最大的等比例缩略图
	 * 
	 * @param w
	 *            int 最大宽度
	 * @param h
	 *            int 最大高度
	 * @throws IOException
	 */
	public void resizeFix(int w, int h) throws IOException {
		if (width / height > w / h) {
			resizeByWidth(w);
		} else {
			resizeByHeight(h);
		}
	}

	/**
	 * 设置目标文件名 setDestFile
	 * 
	 * @param fileName
	 *            String 文件名字符串
	 */
	public void setDestFile(String fileName) throws Exception {
//		if (!fileName.endsWith(".jpg")) {
//			throw new Exception("Dest File Must end with \".jpg\".");
//		}
		destFile = fileName;
	}

	/**
	 * 获取目标文件名 getDestFile
	 */
	public String getDestFile() {
		return destFile;
	}

	/**
	 * 获取图片原始宽度 getSrcWidth
	 */
	public int getSrcWidth() {
		return width;
	}

	/**
	 * 获取图片原始高度 getSrcHeight
	 */
	public int getSrcHeight() {
		return height;
	}

	/*
	 * 调用测试
	 */
	public static void main(String[] args) throws Exception {
//		ccc ccc = new ccc("c:/image2.jpg");
		ResizeImage ccc = new ResizeImage("/Users/wangf/software/pic/left_photocn.jpg");
		ccc.resizeFix(500, 300);
	}
}