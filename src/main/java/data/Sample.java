package data;

import libsvm.svm_node;

/**
 * Created by edwardlol on 17-4-18.
 */
// TODO: 17-4-18 finish this
public class Sample {
    //~ Static fields/initializers ---------------------------------------------

    //~ Instance fields --------------------------------------------------------

    boolean isTraining;

    double label;

    svm_node[] features;

    //~ Constructors -----------------------------------------------------------

    Sample(int size) {
        this.features = new svm_node[size];
    }

    //~ Methods ----------------------------------------------------------------

    int featureNum() {
        return this.features.length;
    }
}

// End Sample.java
