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
 * The interface for reading data from files.
 * For abstraction purpose.
 * <p>
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */

public interface EdFileReader<T> {

    /**
     * Read data from file and generate a T object.
     *
     * @param file the source file
     * @return generated T object
     */
    T read(String file);

    void setSeperator(String _seperator);

    String getSeperator();

    void setColumnNumber(int _columnNumber);

    int getColumnNumber();

}

// End EdFileReader.java
