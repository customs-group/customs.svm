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

/**
 * Created by edwardlol on 17-4-19.
 */
public final class Utils {
    //~ Constructors -----------------------------------------------------------

    private Utils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Parse a {@link String} object to a {@link Double} object, in case some features are "null", "I", "E" or ""
     * where "I" refers to "Import" and "E" refers to "Export"
     * also support svm example data, which is like"index:value"
     * in this case this method will ignore the index and only read the value
     *
     * @param string feature in string
     * @return feature in double
     */
    static double stod(String string) {
        double result = 2.0d; // should handle this error: "cannnot convert string to Double"
        if (string == null || string.equals("") || string.equals("null") || string.equals("I")) {
            result = 0.0d;
        } else if (string.equals("E")) {
            result = 1.0d;
        } else {
            try {
                result = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                String[] tmp = string.split(":");
                try {
                    result = Double.parseDouble(tmp[1]);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * normalize the labels to 1 or -1, so that there will only be 2 classes in the dataset
     * and also, -1 and 1 are better labels than 0 and 1
     * the default action is to make nagative and 0 label -1, others 1
     * be careful of using this method
     *
     * @param label original label
     * @return normalize label
     */
    static double biLabel(double label) {
        if (label < 0.0d) {
            return -1.0d;
        }
        if (Math.abs(label - 0.0d) < 0.00001) {
            return -1.0d;
        }
        return 1.0d;
    }
}

// End Utils.java
