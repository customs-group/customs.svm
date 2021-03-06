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
package data;

import libsvm.svm_node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The data structure of of svm sample data.
 * Including a label and several features.
 *
 * @author edwardlol
 *         Created by edwardlol on 17-4-18.
 */
public class Sample extends ArrayList<svm_node> implements Serializable {
    //~ Instance fields --------------------------------------------------------

    /**
     * The label of this sample, aka class.
     */
    private double label;

    public Sample() {
    }

    public Sample(double[] features) {
        super();
        for (int i = 0; i < features.length; i++) {
            svm_node node = new svm_node();
            node.index = i + 1;
            node.value = features[i];
            this.add(node);
        }
    }

    public Sample(List<Double> features) {
        super();
        for(int i = 0; i < features.size(); i++) {
            svm_node node = new svm_node();
            node.index = i + 1;
            node.value = features.get(i);
            this.add(node);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Modify feature value of this sample.
     * not very good I think, because the original data will be lost
     *
     * @param i     the index of modification
     * @param value the value to be modified
     * @throws IndexOutOfBoundsException when {@code i} is out of the feature array's boundary
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
     * Get the features of this sample in an svm_node array.
     *
     * @return the features of this sample in an svm_node array
     */
    public svm_node[] getFeatureArray() {
        return this.toArray(new svm_node[this.size()]);
    }

    public double getFeatureValue(int i) {
        return this.get(i).value;
    }

    /**
     * Deep copy this sample into a new instance.
     *
     * @return a deep copy of this sample
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
     * Represent this sample in string.
     * form: {@code "label:{@link this#label}; index:value,...\n"}
     *
     * @return a string representing this sample
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.label).append(',');
        for (svm_node svm_node : this) {
            sb.append(svm_node.index).append(':').append(svm_node.value).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public double getLabel() {
        return this.label;
    }
}

// End Sample.java
