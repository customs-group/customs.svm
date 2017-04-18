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
