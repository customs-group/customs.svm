package io;

/**
 * Created by edwardlol on 2017/4/18.
 */
public interface EdFileReader<T> {

    T read(String file);

    void setSeperator(String _seperator);

    String getSeperator();

    void setColumnNumber(int _columnNumber);

    int getColumnNumber();

}

// End EdFileReader.java
