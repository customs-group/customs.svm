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

import java.io.Serializable;

/**
 * Created by edwardlol on 2017/4/20.
 */
public class SoftScaleParam implements Serializable {
    //~ Static fields/initializers ---------------------------------------------

    //~ Instance fields --------------------------------------------------------

    /**
     * The soft scaling parameter.
     * The param[i][0]s stand for the mean of feature i
     * and param[i][1]s stand for the standard deviation of feature i.
     */
    private final double[][] param;

    //~ Constructors -----------------------------------------------------------

    SoftScaleParam(int featureNum) {
        this.param = new double[featureNum][2];
    }

    //~ Methods ----------------------------------------------------------------

    void setMean(int i, double value) {
        this.param[i][0] = value;
    }

    double getMean(int i) {
        return this.param[i][0];
    }

    void setSD(int i, double value) {
        this.param[i][1] = value;
    }

    double getSD(int i) {
        return this.param[i][1];
    }
}

// End LinearScaleParam.java
