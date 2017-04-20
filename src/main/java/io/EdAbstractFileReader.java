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
package io;

/**
 * An abstract base of file reader.
 * This class provide some basic and general methods.
 * <p>
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public abstract class EdAbstractFileReader<T> implements EdFileReader<T> {
    //~ Static fields/initializers ---------------------------------------------

    /**
     * The seperator between fields.
     * For example ',' in csv file and '\t' in tsv file.
     */
    protected static String seperator;

    /**
     * Number of elements each line has.
     */
    protected static int columnNumber;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSeperator(String _seperator) {
        seperator = _seperator;
    }

    @Override
    public String getSeperator() {
        return seperator;
    }

    @Override
    public void setColumnNumber(int _columnNumber) {
        columnNumber = _columnNumber;
    }

    @Override
    public int getColumnNumber() {
        return columnNumber;
    }
}

// End EdAbstractFileReader.java
