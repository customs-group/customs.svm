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

import io.EdAbstractFileReader;
import libsvm.svm_node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static data.Utils.stod;

/**
 * Singleton class to generate a {@link Dataset} from file
 * <p>
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public class SVMFileReader extends EdAbstractFileReader<Dataset> {
    //~ Static fields/initializers ---------------------------------------------

    private static SVMFileReader reader = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Private constructor to prevent instantiating.
     * Also initiate the default seperator and column number.
     */
    private SVMFileReader() {
        seperator = " ";
        columnNumber = Integer.MAX_VALUE;
    }

    public static SVMFileReader getInstance() {
        if (reader == null) {
            reader = new SVMFileReader();
        }
        return reader;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Read data from file and generate a {@link Dataset}.
     *
     * @param file the source file
     * @return a {@link Dataset}
     */
    @Override
    public Dataset read(String file) {
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            Dataset dataset = new Dataset();

            String line = br.readLine();
            while (line != null) {
                String[] contents = line.split(seperator);

                dataset.setFeatureNum(contents.length - 1);

                Sample sample = new Sample();
                sample.setLabel(stod(contents[0]));

                for (int i = 1; i < contents.length; i++) {
                    svm_node feature = new svm_node();
                    feature.index = i;
                    feature.value = stod(contents[i]);
                    sample.add(feature);
                }
                dataset.add(sample);
                line = br.readLine();
            }
            System.out.println("Dataset preparation done! Read " + dataset.size() + " samples in total.");
            return dataset;
        } catch (IOException e) {
            System.err.println("Dataset preparation failed!");
            e.printStackTrace();
            return null;
        }
    }
}

// End SVMFileReader.java
