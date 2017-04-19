package data;

import java.io.Serializable;

/**
 * Created by edwardlol on 2017/4/20.
 */
public class LinearScaleParam implements Serializable {
    //~ Static fields/initializers ---------------------------------------------

    //~ Instance fields --------------------------------------------------------

    /**
     * The linear scaling parameter.
     * The param[0][0] and param[0][1] stand for lower bound and upper bound.
     * The other param[i][0]s and param[i][1]s stand for the min value and max value of this feature.
     */
    private final double[][] param;

    //~ Constructors -----------------------------------------------------------

    LinearScaleParam(int featureNum) {
        this.param = new double[featureNum + 1][2];
        for (int i = 1; i < this.param.length; i++) {
            this.param[i][0] = Double.MAX_VALUE;
            this.param[i][1] = Double.MIN_VALUE;
        }
    }

    //~ Methods ----------------------------------------------------------------


    void setLowerBound(double lowerBound) {
        this.param[0][0] = lowerBound;
    }

    double getLowerBound() {
        return this.param[0][0];
    }

    void setUpperBound(double upperBound) {
        this.param[0][1] = upperBound;
    }

    double getUpperBound() {
        return this.param[0][1];
    }

    void setFeatureMin(int i, double featureMin) {
        this.param[i + 1][0] = featureMin;
    }

    double getFeatureMin(int i) {
        return this.param[i + 1][0];
    }

    void updateMinMax(int i, double value) {
        if (this.param[i + 1][0] > value) {
            this.param[i + 1][0] = value;
        }
        if (this.param[i + 1][1] < value) {
            this.param[i + 1][1] = value;
        }
    }

    void setFeatureMax(int i, double featureMax) {
        this.param[i + 1][1] = featureMax;
    }

    double getFeatureMax(int i) {
        return this.param[i + 1][1];
    }

    double getBoundarySpan() {
        return this.param[0][1] - this.param[0][0];
    }

    double getFeatureSpan(int i) {
        return this.param[i + 1][1] - this.param[i + 1][0];
    }
}

// End LinearScaleParam.java
