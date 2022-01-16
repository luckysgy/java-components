package com.concise.component.javacv.opencv;

import cn.hutool.core.util.ObjectUtil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.opencv.core.CvType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.helper.opencv_core.CV_RGB;

/**
 * 需要说明的opencv中的默认读取的颜色是bgr不是rgb
 *
 * matAndBytesConvert: 推荐的mat和bytes互转的组合方式为{@link #bytesToMatByDecode(byte[])} + {@link #matToBytes(Mat)}
 * 如果 mat 转 bytes, 使用 {@link #matToBytesByEncode} 图片会变大很多
 *
 * @apiNote mat 必须需要手动释放, 否则内存会一直增长
 * opencv工具类 for javacv
 * @author shenguangyang
 * @date 2021-11-03 6:07
 */
public class OpenCvHelper implements AutoCloseable {
    private final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
    private final OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
    private final Java2DFrameConverter java2DConverter = new Java2DFrameConverter();;

    // 定义一个颜色
    public static CvScalar cvScalar = opencv_core.CV_RGB(255, 0, 0);

    public static Scalar color = new Scalar(0, 0, 255, 0);

    private final List<Mat> matList = new ArrayList<>();
    private final List<IplImage> iplImageList = new ArrayList<>();
    private final List<Frame> frameList = new ArrayList<>();

    @Override
    public void close() throws Exception {
        converterToMat.close();
        converterToIplImage.close();
        java2DConverter.close();

        for (Mat mat : matList) {
            if (ObjectUtil.isNotNull(mat)) {
                mat.release();
                mat.close();
            }
        }

        for (IplImage iplImage : iplImageList) {
            if (ObjectUtil.isNotNull(iplImage)) {
                iplImage.release();
                iplImage.close();
            }
        }

        for (Frame frame : frameList) {
            if (ObjectUtil.isNotNull(frame)) {
                frame.close();
            }
        }
    }

    /**
     * 这个方法性能比较低，推荐的mat和bytes互转的组合方式为
     * {@link #bytesToMatByDecode(byte[])} + {@link #matToBytes(Mat)}
     * 如果 mat 转 bytes, 使用 {@link #matToBytesByEncode} 图片会变大很多
     *
     * @param imgBytes
     * @return
     * @throws IOException
     */
    public Mat bytesToMat(byte[] imgBytes) throws IOException {
        BufferedImage bufferedImage = bytesToBufferedImage(imgBytes);
        return bufferedImageToMat(bufferedImage);
    }

    /**
     * byte[]转BufferImage
     * @param imgBytes
     * @return
     */
    public BufferedImage bytesToBufferedImage(byte[] imgBytes) throws IOException {
        BufferedImage tagImg = null;
        try {
            tagImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
            return tagImg;
        } catch (Exception e) {
            throw e;
        }
    }

    public Mat bufferedImageToMat(BufferedImage original) {
        Mat mat = null;
        try {
            mat = converterToMat.convert(java2DConverter.convert(original));
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
     * 将Mat 转成 bytes
     * 遇到的问题: 在win10下运行时候代码经常异常退出, 后来部署到linux上运行,
     * 一直很稳定的运行
     * @param sourceMat
     * @return
     */
    public byte[] matToBytes(Mat sourceMat) {
        BufferedImage bufferedImage = matToBufferedImage(sourceMat);
        return bufferedImageToBytes(bufferedImage);
    }

    public BufferedImage matToBufferedImage(Mat sourceMat) {
        Frame convert = null;
        try {
            convert = converterToIplImage.convert(sourceMat);
            return java2DConverter.convert(convert);
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(sourceMat)) {
                matList.add(sourceMat);
            }

            if (ObjectUtil.isNotNull(convert)) {
                frameList.add(convert);
            }
        }
    }

    public byte[] bufferedImageToBytes(BufferedImage original){
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(original, "jpg", bStream);
        } catch (IOException e) {
            throw new RuntimeException("bugImg读取失败:"+e.getMessage(),e);
        }
        return bStream.toByteArray();
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
        Rect rect = null;
        try {
            srcMat = inputStreamToMat(srcInputStream);
            // 目标Mat
            imgDesc = new Mat(width, height, CvType.CV_8UC3);
            // 设置ROI
            rect = new Rect(startX, startY, width, height);
            imgROI = new Mat(srcMat, rect);
            // 从ROI中剪切图片
            imgROI.copyTo(imgDesc);
            return imgDesc;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(rect)) {
                rect.close();
            }
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
    public Mat cutImage(Mat srcMat, int startX, int startY, int width, int height) {
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
