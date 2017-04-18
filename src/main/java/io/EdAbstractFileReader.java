package io;

/**
 * Created by edwardlol on 2017/4/18.
 */
public abstract class EdAbstractFileReader<T> implements EdFileReader<T> {
    //~ Static fields/initializers ---------------------------------------------

    protected static String seperator;

    protected static int columnNumber;

    //~ Methods ----------------------------------------------------------------

    public void setSeperator(String _seperator) {
        seperator = _seperator;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setColumnNumber(int _columnNumber) {
        columnNumber = _columnNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}

// End EdAbstractFileReader.java
