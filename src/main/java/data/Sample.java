package data;

import libsvm.svm_node;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * sample of svm data
 *
 * Created by edwardlol on 17-4-18.
 */
public class Sample extends ArrayList<svm_node> implements Serializable {
    //~ Instance fields --------------------------------------------------------

    public double label;

    //~ Constructors -----------------------------------------------------------

    public Sample() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * modify the features, used for scaling
     * not very good I think, because the original data will be lost
     *
     * @param i     the index of modification
     * @param value the value to be modified
     *
     * @throws IndexOutOfBoundsException when i is out of this sample's feature array
     */
    void modifyFeature(int i, double value) throws IndexOutOfBoundsException {
        if (i < 0 || i > this.size()) {
            throw new IndexOutOfBoundsException("index " + i + " out of feature boundary! feature number: " + featureNum());
        }
        this.get(i).value = value;
    }

    public int featureNum() {
        return this.size();
    }

    /**
     * get the features of this sample in an svm_node array
     *
     * @return  the features of this sample in an svm_node array
     */
    public svm_node[] getFeatureArray() {
        return this.toArray(new svm_node[this.size()]);
    }

    /**
     * deep copy this sample into a new one
     *
     * @return  a deep copy of this sample
     */
    @Override
    public Sample clone() {
        super.clone();

        Sample clone = new Sample();
        clone.label = this.label;
        for (svm_node node : this) {
            svm_node newNode = new svm_node();
            newNode.index = node.index;
            newNode.value = node.value;
            clone.add(newNode);
        }
        return clone;
    }

    /**
     * represent this sample in string
     * "label:{@link this#label}; index:value,...\n"
     *
     * @return  a string representing this sample
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("label:").append(this.label).append("; ");
        for (svm_node svm_node : this) {
            sb.append(svm_node.index).append(':').append(svm_node.value).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('\n');
        return sb.toString();
    }
}

// End Sample.java
