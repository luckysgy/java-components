package com.concise.component.javacv.opencv;

import cn.hutool.core.util.ObjectUtil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;

/**
 * 需要说明的opencv中的默认读取的颜色是bgr不是rgb
 * @apiNote mat 必须需要手动释放, 否则内存会一直增长
 * opencv工具类 for javacv
 * @author shenguangyang
 * @date 2021-11-03 6:07
 */
public class OpenCvHelper implements AutoCloseable {
    public static OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
    public static OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

    // 定义一个颜色
    public static CvScalar cvScalar = opencv_core.CV_RGB(255, 0, 0);

    public static Scalar color = new Scalar(0, 0, 255, 0);

    private final List<Mat> matList = new ArrayList<>();
    private final List<IplImage> iplImageList = new ArrayList<>();

    @Override
    public void close() throws Exception {
        for (Mat mat : matList) {
            if (ObjectUtil.isNotNull(mat)) {
                mat.release();
                mat.clone();
            }
        }

        for (IplImage iplImage : iplImageList) {
            if (ObjectUtil.isNotNull(iplImage)) {
                iplImage.release();
                iplImage.clone();
            }
        }
    }

    /**
     * 将Mat 转成 bytes
     */
    public byte[] matToBytes(Mat mat){
        try {
            byte[] b = new byte[mat.channels() * mat.cols() * mat.rows()];
            mat.data().get(b);
            // ((DataBufferByte) Java2DFrameUtils.toBufferedImage(mat).getRaster().getDataBuffer()).getData()
            return b;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(mat)) {
                matList.add(mat);
            }
        }
    }

    /**
     * 将字节转换成Mat
     * @param bytes 字节
     * @param width 目标图片的宽
     * @param height 目标图片的高
     */
    public Mat bytesToMat(byte[] bytes, int width, int height) {
        Mat mat = null;
        try {
            mat = new Mat(height, width, opencv_core.CV_8UC3, new BytePointer(bytes));
            return mat;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(mat)) {
                matList.add(mat);
            }
        }
    }

    /**
     * 将Mat 转成 InputStream
     */
    public InputStream matToInputStream(Mat mat){
        try {
            byte[] bytes = matToBytesByEncode(mat);
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(mat)) {
                matList.add(mat);
            }
        }
    }

    /**
     * 将InputStream转换成Mat
     */
    public Mat inputStreamToMat(InputStream inputStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            os.close();
            bis.close();
            return bytesToMatByDecode(os.toByteArray());
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 将mat转成bytes [ 通过编码的方式 ]
     * note: 需要{@link this#bytesToMatByDecode(byte[])} 才能还原成 Mat
     * @param mat
     * @return
     */
    public byte[] matToBytesByEncode(Mat mat) {
        // byte[] bytes = new byte[mat.rows() * mat.rows() * mat.channels()];
        byte[] bytes = new byte[mat.rows() * mat.cols()];
        opencv_imgcodecs.imencode(".jpg", mat, bytes);
        return bytes;
    }

    /**
     * 将编码的bytes转成Mat
     * @param bytesEncode 被编码Mat的字节数组 {@link #matToBytesByEncode(Mat)}
     * @return
     */
    public Mat bytesToMatByDecode(byte[] bytesEncode) {
        Mat mat = null;
        Mat result = null;
        try {
            mat = new Mat(new BytePointer(bytesEncode));
            result = opencv_imgcodecs.imdecode(mat, opencv_imgcodecs.IMREAD_COLOR);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(mat)) {
                matList.add(mat);
            }
            if (ObjectUtil.isNotNull(result)) {
                matList.add(result);
            }
        }
    }

    /**
     * 裁剪图片
     * @param srcInputStream 原图片输入流
     * @param startX 左上角起始x
     * @param startY 左上角起始Y
     * @param width 宽
     * @param height 高
     * @return 返回Mat, 可以通过 {@link #matToInputStream(Mat)} 转成 InputStream
     */
    public Mat cutImage(InputStream srcInputStream, int startX, int startY, int width, int height) throws IOException {
        Mat srcMat = null;
        Mat imgDesc = null;
        Mat imgROI = null;
        try {
            srcMat = inputStreamToMat(srcInputStream);
            // 目标Mat
            imgDesc = new Mat(width, height, CvType.CV_8UC3);
            // 设置ROI
            imgROI = new Mat(srcMat, new Rect(startX, startY, width, height));

            // 从ROI中剪切图片
            imgROI.copyTo(imgDesc);
            return imgDesc;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(srcMat)) {
                matList.add(srcMat);
            }
            if (ObjectUtil.isNotNull(imgDesc)) {
                matList.add(imgDesc);
            }
            if (ObjectUtil.isNotNull(imgROI)) {
                matList.add(imgROI);
            }
        }
    }

    /**
     * 裁剪图片
     * @param srcMat 原图片Mat
     * @param startX 左上角起始x
     * @param startY 左上角起始Y
     * @param width 宽
     * @param height 高
     * @return 返回Mat, 可以通过 {@link #matToInputStream(Mat)} 转成 InputStream
     */
    public Mat cutImage(Mat srcMat, int startX, int startY, int width, int height) throws IOException {
        Mat imgDesc = null;
        Mat imgROI = null;
        try {
            // 目标Mat
            imgDesc = new Mat(width, height, CvType.CV_8UC3);
            // 设置ROI
            imgROI = new Mat(srcMat, new Rect(startX, startY, width, height));
            // 从ROI中剪切图片
            imgROI.copyTo(imgDesc);
            return imgDesc;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(srcMat)) {
                matList.add(srcMat);
            }
            if (ObjectUtil.isNotNull(imgDesc)) {
                matList.add(imgDesc);
            }
            if (ObjectUtil.isNotNull(imgROI)) {
                matList.add(imgROI);
            }
        }
    }

    /**
     * 画框
     * @param image
     * @param x1 矩形的左上角坐标位置 x坐标
     * @param y1 矩形的左上角坐标位置 y坐标
     * @param x2 矩形右下角坐标位置 y坐标
     * @param y2 矩形右下角坐标位置 y坐标
     */
    public void drawBox(IplImage image, int x1, int y1, int x2, int y2){
        try {
            cvRectangle (
                    // image作为画布显示矩形
                    image,
                    // 矩形的左上角坐标位置
                    cvPoint(x1, y1),
                    // 矩形右下角坐标位置
                    cvPoint(x2, y2),
                    // 边框的颜色
                    CV_RGB(255, 0, 0),
                    // 线条的宽度:正值就是线宽，负值填充矩形,例如CV_FILLED，值为-1
                    4,
                    // 线条的类型(0,8,4)
                    4,
                    // 坐标的小数点位数
                    0
            );
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(image)) {
                iplImageList.add(image);
            }
        }
    }

    /**
     * 画框
     * @param image
     * @param x1 矩形的左上角坐标位置 x坐标
     * @param y1 矩形的左上角坐标位置 y坐标
     * @param w 宽
     * @param h 高
     */
    public void drawBox(Mat image, int x1, int y1, int w, int h){
        try {
            opencv_imgproc.rectangle (
                    // image作为画布显示矩形
                    image,
                    // 矩形的左上角坐标位置
                    new Point(x1, y1),
                    // 矩形右下角坐标位置
                    new Point(x1 + w, y1 + h),
                    // 边框的颜色
                    color,
                    // 线条的宽度:正值就是线宽，负值填充矩形,例如CV_FILLED，值为-1
                    4,
                    // 线条的类型(0,8,4)
                    4,
                    // 坐标的小数点位数
                    0
            );
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(image)) {
                matList.add(image);
            }
        }
    }

}
