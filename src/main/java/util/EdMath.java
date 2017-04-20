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
