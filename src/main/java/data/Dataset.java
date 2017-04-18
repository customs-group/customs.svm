package data;

import libsvm.svm_node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * dataset of svm data
 *
 * Created by edwardlol on 17-4-18.
 */
public class Dataset extends ArrayList<Sample> {
    //~ Static fields/initializers ---------------------------------------------

    //~ Instance fields --------------------------------------------------------

    private boolean isScaled = false;

    boolean isTraining;

    /**
     * number of features of every sample
     */
    int featureNum = 0;

    //~ Constructors -----------------------------------------------------------

    //~ Methods ----------------------------------------------------------------

    /**
     * record data to file
     *
     * @param filename file name to store data
     */
    public void record(String filename) {

        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (int i = 0; i < super.size(); i++) {
                Sample sample = super.get(i);

                bw.append(Double.toString(sample.label)).append(' ');

                svm_node[] features = sample.getFeatureArray();

                for (int j = 0; j < sample.featureNum(); j++) {
                    bw.append(Integer.toString(features[j].index))
                            .append(':')
                            .append(Double.toString(features[j].value))
                            .append(' ');
                }
                bw.append('\n');
                bw.flush();
            }
            System.out.println("DataSet record done! see " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * scale training data so that all features fit in [-1.0d, 1.0d]
     *
     * @return  scale parameter, see{@link this#scale(double, double)}
     */
    public double[][] scale() {
        return scale(-1.0d, 1.0d);
    }

    /**
     * scale training data so that all features fit in [lowerBound, upperBound]
     *
     * @param lowerBound scale lower bound
     * @param upperBound scale upper bound
     *
     * @return  scale parameter in double[featureNum + 1][2]
     *          the parameter[0][0] and parameter[0][1] stands for lower bound and upper bound
     *          parameter[i][0] and parameter[i][1] stands for the min value and max value of this feature
     */
    public double[][] scale(double lowerBound, double upperBound) {
		/* step 1: initiate */
        double[][] scaleParam = new double[this.featureNum + 1][2];
        scaleParam[0][0] = lowerBound;
        scaleParam[0][1] = upperBound;

        for (int i = 0; i < this.featureNum; i++) {
            scaleParam[i + 1][0] = Double.MAX_VALUE;
            scaleParam[i + 1][1] = Double.MIN_VALUE;
        }

		/* step 2: find out min/max value */
        for (Sample sample : this) {
            svm_node[] features = sample.getFeatureArray();
            for (int i = 0; i < this.featureNum; i++) {
                double currentMin = scaleParam[i + 1][0];
                double currentMax = scaleParam[i + 1][1];
                scaleParam[i + 1][0] = Math.min(currentMin, features[i].value);
                scaleParam[i + 1][1] = Math.max(currentMax, features[i].value);
            }
        }

		/* pass 3: scale */
        for (Sample sample : this) {
            svm_node[] originalFeatures = sample.getFeatureArray();

            for (int i = 0; i < this.featureNum; i++) {

                double featureMin = scaleParam[i + 1][0];
                double featureMax = scaleParam[i + 1][1];

                double newFeature = (originalFeatures[i].value - featureMin)
                        * (upperBound - lowerBound)
                        / (featureMax - featureMin)
                        + lowerBound;
                sample.modifyFeature(i, newFeature);
            }
        }
        this.isScaled = true;
        return scaleParam;
    }

    /**
     * scale testing data so that it has the same scale as the training data
     *
     * @param scaleParam the result returned by scale on training data
     */
    public void scaleFrom(double[][] scaleParam) {
		/* step 1: initiate feature bound */
        double lowerBound = scaleParam[0][0];
        double upperBound = scaleParam[0][1];


		/* pass 2: scale */
        for (Sample sample : this) {
            svm_node[] features = sample.getFeatureArray();
            for (int i = 0; i < this.featureNum; i++) {

                double featureMin = scaleParam[i + 1][0];
                double featureMax = scaleParam[i + 1][1];

                double newFeature = (features[i].value - featureMin)
                        * (upperBound - lowerBound)
                        / (featureMax - featureMin)
                        + lowerBound;
                sample.modifyFeature(i, newFeature);
            }
            this.isScaled = true;
        }
    }

    /**
     * union this dataset with another
     *
     * @param other the dataset to be unioned
     *
     * @return  this
     */
    public Dataset union(Dataset other) {
        if (this.featureNum != other.featureNum) {
            System.out.println("cannot union, two datasets has different feature numbers");
        } else if (this.isScaled != other.isScaled) {
            System.out.println("cannot union, the union flag is different");
        } else if (this.isTraining != other.isTraining) {
            System.out.println("cannot union, the training flag is different");
        } else {
            this.addAll(other);
        }
        return this;
    }

    public int getSampleNum() {
        return this.size();
    }

    public int getFeatureNum() {
        return this.featureNum;
    }

    public boolean isScaled() {
        return this.isScaled;
    }
}

// End Dataset.java
