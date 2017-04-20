package data;

import libsvm.svm_node;
import util.EdMath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The dataset structure.
 * Consists of a list of {@link Sample}s.
 *
 * @author edwardlol
 *         Created by edwardlol on 17-4-18.
 */
public class Dataset extends ArrayList<Sample> {
    //~ Instance fields --------------------------------------------------------

    private boolean isScaled = false;

    private boolean isTraining;

    /**
     * Number of features of every sample.
     */
    private int featureNum = 0;

    //~ Methods ----------------------------------------------------------------

    /**
     * Record all samples to a file.
     *
     * @param filename file name to store data
     */
    public void record(String filename) {

        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (Sample sample : this) {
                bw.write(sample.toString() + '\n');
            }
            System.out.println("Dataset record done! see " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Linear scale training data so that all features fit in [-1.0d, 1.0d]
     *
     * @return scale parameter, see{@link this#linearScale(double, double)}
     */
    public LinearScaleParam linearScale() {
        return linearScale(-1.0d, 1.0d);
    }

    /**
     * Linear scale all samples so that every feature fits in [lowerBound, upperBound].
     *
     * @param lowerBound scale lower bound
     * @param upperBound scale upper bound
     * @return a {@link LinearScaleParam}
     */
    public LinearScaleParam linearScale(double lowerBound, double upperBound) {
        /* step 1: initiate */
        LinearScaleParam param = new LinearScaleParam(this.featureNum);
        param.setLowerBound(lowerBound);
        param.setUpperBound(upperBound);

		/* step 2: find out min/max value */
        for (Sample sample : this) {
            for (int i = 0; i < this.featureNum; i++) {
                param.updateMinMax(i, sample.getFeatureValue(i));
            }
        }

		/* step 3: linearScale */
        linearScaleFrom(param);
        return param;
    }

    /**
     * Linear scale the dataset with a scale parameter.
     * Usually used to scale testing data so that
     * it has the same scaling as the training data.
     *
     * @param scaleParam the result returned by {@code linearScale} on training data
     */
    public void linearScaleFrom(LinearScaleParam scaleParam) {
        for (Sample sample : this) {
            for (int i = 0; i < this.featureNum; i++) {

                double featureMin = scaleParam.getFeatureMin(i);

                double newFeature = (sample.getFeatureValue(i) - featureMin)
                        * scaleParam.getBoundarySpan()
                        / scaleParam.getFeatureSpan(i)
                        + scaleParam.getLowerBound();
                sample.modifyFeature(i, newFeature);
            }
            this.isScaled = true;
        }
    }

    public SoftScaleParam softScale() {
        SoftScaleParam param = new SoftScaleParam(this.featureNum);
        Itr it = this.columnIter();

        while (it.hasNext()) {
            int index = it.getCursor();
            double[] feature = it.next();

            param.setMean(index, EdMath.mean(feature));
            param.setSD(index, EdMath.standardDeviation(feature));
        }

        softScaleFrom(param);
        return param;
    }

    public void softScaleFrom(SoftScaleParam param) {
        for (Sample sample : this) {
            for (int i = 0; i < sample.size(); i++) {
                double newFeature = (sample.getFeatureValue(i) - param.getMean(i))
                        / param.getSD(i)
                        / 2;

                sample.modifyFeature(i, newFeature);
            }
        }
        this.isScaled = true;
    }

    /**
     * Union this dataset with another one.
     * There are 3 requirements before they can be unioned.
     * 1. The two datasets must be equal in feature number.
     * More specifically, they should have same features,
     * and each feature should be at the same position.
     * 2. The two datasets must be both unscaled, to ensure they have the same scaling parameter.
     * 3. The two datasets should both be training data or testing data.
     * If we meet all the 3 requirements, the {@code other} is unioned into {@code this}.
     *
     * @param other the dataset to be unioned
     * @return this
     */
    public Dataset union(Dataset other) {
        if (this.featureNum != other.featureNum) {
            System.out.println("cannot union, two datasets has different feature numbers");
        } else if (!this.isScaled && !other.isScaled) {
            System.out.println("cannot union, the union flag is different");
        } else if (this.isTraining != other.isTraining) {
            System.out.println("cannot union, the training flag is different");
        } else {
            this.addAll(other);
        }
        return this;
    }

    /**
     * Get the feature column in an array.
     *
     * @param index
     * @return
     */
    public double[] getFeature(int index) {
        double[] result = new double[this.getSampleNum()];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.get(i).getFeatureValue(index);
        }
        return result;
    }

    public int getSampleNum() {
        return this.size();
    }

    public int getFeatureNum() {
        return this.featureNum;
    }

    public void setFeatureNum(int featureNum) {
        this.featureNum = featureNum;
    }

    public boolean isScaled() {
        return this.isScaled;
    }

    public boolean isTraining() {
        return this.isTraining;
    }

    public void setTraining(boolean training) {
        this.isTraining = training;
    }

    private class Itr implements Iterator<double[]> {
        int cursor = 0;
        int lastRet = -1;

        @Override
        public double[] next() {
            int i = cursor;
            double[] next = getFeature(i);
            lastRet = i;
            cursor = i + 1;
            return next;
        }

        @Override
        public boolean hasNext() {
            return cursor != featureNum;
        }

        public int getCursor() {
            return cursor;
        }
    }

    public Itr columnIter() {
        return new Itr();
    }
}

// End Dataset.java
