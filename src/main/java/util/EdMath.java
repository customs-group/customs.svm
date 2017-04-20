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
package util;

/**
 * Created by edwardlol on 2017/4/20.
 */
public final class EdMath {
    //~ Constructors -----------------------------------------------------------

    private EdMath() {
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * calculate mean value of a double array
     * @param data source data
     * @return mean of data
     */
    public static double mean(double[] data) {
        double sum = 0.0;
        for (double _data : data) {
            sum += _data;
        }
        return sum / data.length;
    }

    /**
     * calculate standard deviation of a double array
     * @param data source data
     * @return standard deviation of data
     */
    public static double standardDeviation(double[] data) {
        double mean = mean(data);
        double deviation = 0.0;
        for (double _data : data) {
            deviation += (mean - _data) * (mean - _data);
        }
        return Math.sqrt(deviation / data.length);
    }
}

// End EdMath.java
