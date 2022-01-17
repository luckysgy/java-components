package com.concise.component.javacv.opencv;

import cn.hutool.core.util.ObjectUtil;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2022-01-15 9:38
 */
public class OpencvImgcodecsHelper implements AutoCloseable {

    private final List<Mat> matList = new ArrayList<>();

    @Override
    public void close() throws Exception {
        for (Mat mat : matList) {
            if (ObjectUtil.isNotNull(mat)) {
                mat.release();
                mat.close();
            }
        }
    }

    public Mat imread(String filename) {
        Mat imread = null;
        try {
            imread = opencv_imgcodecs.imread(filename);
            return imread;
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(imread)) {
                matList.add(imread);
            }
        }
    }

    public void imwrite(String filename, Mat mat) {
        try {
            opencv_imgcodecs.imwrite(filename, mat);
        } catch (Exception e) {
            throw e;
        } finally {
            if (ObjectUtil.isNotNull(mat)) {
                matList.add(mat);
            }
        }
    }
}
