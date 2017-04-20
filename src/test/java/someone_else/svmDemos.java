/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package someone_else;

import core.SVM;
import data.Dataset;
import data.LinearScaleParam;
import data.SVMFileReader;
import data.Sample;
import libsvm.svm_model;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All possible demos came accros to my mind.
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public final class svmDemos {
    //~ Methods ----------------------------------------------------------------

    /**
     * Simple train demo.
     */
    @Test
    public void train() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset trainData = reader.read("./datasets/train");

        trainData.linearScale();

        SVM svm = SVM.getInstance();
        svm.train(trainData);
        // 保存模型以供后续测试使用
        svm.saveModel("./results/model");
    }

    /**
     * Simple parameter selection demo.
     */
    @Test
    public void setParam() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset trainData = reader.read("./datasets/train");

        trainData.linearScale();

        SVM svm = SVM.getInstance();
        svm.setC(0.03125);
        svm.setGamma(0.03125);

        svm_model model = svm.train(trainData);
        svm.saveModel("./results/svm/model");
        svm.valid(model, trainData, true);
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
        svm.saveModel("./results/model");
        svm.valid(model, trainData, true);
        svm.valid(model, testData, true);
    }

    /**
     * Predict the label of new sample.
     */
    @Test
    public void predictDemo() {
        SVM svm = SVM.getInstance();
        // 使用上一个 demo 中保存的 model
        svm_model model = svm.loadModel("./results/model");

        try (FileReader fr = new FileReader("./datasets/test");
        BufferedReader br = new BufferedReader(fr)){
            String line = br.readLine();
            String[] contents = line.split(" ");
            // // 通过数组生成一个 feature 完全随机的 Sample
            double[] features1 = new double[contents.length];
            for (int i = 0; i < features1.length; i++) {
                features1[i] = Math.random();
            }
            Sample sample1 = new Sample(features1);
            System.out.println(svm.predict(model, sample1));

            // 通过 List 生成一个 feature 完全随机的 Sample
            List<Double> features2 = new ArrayList<>();
            for (int i = 0; i < contents.length; i++) {
                features2.add(Math.random());
            }
            Sample sample2 = new Sample(features2);
            System.out.println(svm.predict(model, sample2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

// End svmDemos.java
