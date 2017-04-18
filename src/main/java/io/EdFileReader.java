package io;

/**
 * the interface for reading files
 * for abstraction purpose
 * <p>
 * Created by edwardlol on 2017/4/18.
 */
public interface EdFileReader<T> {

    /**
     * read data from file
     * and generate a T object
     *
     * @param file the source file
     * @return generated T object
     */
    T read(String file);

    /**
     * set the seperator for this reader
     *
     * @param _seperator seperator
     */
    void setSeperator(String _seperator);

    /**
     * get current seperator
     *
     * @return current seperator
     */
    String getSeperator();

    /**
     * set the number of elements which source file should have
     *
     * @param _columnNumber the number of elements
     */
    void setColumnNumber(int _columnNumber);

    /**
     * get the number of elements of this reader's source file
     *
     * @return the number of elements
     */
    int getColumnNumber();

}

// End EdFileReader.java
