package someone_else;

import core.SVM;
import data.Dataset;
import data.LinearScaleParam;
import data.SVMFileReader;
import libsvm.svm_model;
import org.junit.Test;

import java.io.*;

/**
 * All possible demos came accros to my mind.
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public class svmDemos {
    //~ Methods ----------------------------------------------------------------

    /**
     * Simple train demo.
     */
    @Test
    public void train() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        LinearScaleParam scaleParam = trainData.linearScale();
        testData.linearScaleFrom(scaleParam);

        SVM svm = SVM.getInstance();
        svm.train(trainData);
        // 保存模型以供后续测试使用
        svm.saveModel("./results/model");
    }

    @Test
    public void saveModel() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        SVM svm = SVM.getInstance();
        svm.train(trainData);
        // 保存模型以供后续测试使用
        svm.saveModel("./results/fuck/model");
    }

    /**
     * Simple parameter selection demo.
     */
    @Test
    public void setParam() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        LinearScaleParam scaleParam = trainData.linearScale();
        testData.linearScaleFrom(scaleParam);

        SVM svm = SVM.getInstance();
        svm.setC(0.03125);
        svm.setGamma(0.03125);

        svm_model model = svm.train(trainData);
        svm.saveModel("./results/svm/model");
        svm.valid(model, trainData, true);
        svm.valid(model, testData, true);
    }

    @Test
    public void gridSearch() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        LinearScaleParam scaleParam = trainData.linearScale();
        testData.linearScaleFrom(scaleParam);

        SVM svm = SVM.getInstance();
        svm.gridSearch(trainData);

        svm_model model = svm.train(trainData);
        svm.valid(model, trainData, true);
        svm.valid(model, testData, true);
    }

    @Test
    public void predictFileDemo() {
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        SVM svm = SVM.getInstance();
        svm_model model = svm.loadModel("./results/svm/model");
        svm.valid(model, testData, true);

    }


}

// End svmDemos.java
