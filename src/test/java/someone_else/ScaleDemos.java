package someone_else;

import data.Dataset;
import data.LinearScaleParam;
import data.SVMFileReader;
import data.SoftScaleParam;
import org.junit.Test;

import java.io.*;

/**
 * Created by edwardlol on 2017/4/20.
 */
public final class ScaleDemos {
    //~ Methods ----------------------------------------------------------------

    /**
     * Test the scale boundary.
     */
    @Test
    public void linearScaleBoundaryTest() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset data = reader.read("./datasets/train");
        data.linearScale();
        data.record("./results/default_linear_scale");
        data.linearScale(0, 1);
        data.record("./results/customed_linear_scale");
    }

    /**
     * Test the serializability of LinearScaleParam.
     * The two output files should have same content.
     */
    @Test
    public void linearScaleSerializableTest() {
        SVMFileReader reader = SVMFileReader.getInstance();

        Dataset data1 = reader.read("./datasets/train");
        Dataset data2 = reader.read("./datasets/train");

        LinearScaleParam param1 = data1.linearScale();
        data1.record("./results/linear_scale_out");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("results/linear_scale_param"));
            oos.writeObject(param1);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("results/linear_scale_param"));
            LinearScaleParam param2 = (LinearScaleParam) ois.readObject();
            ois.close();

            data2.linearScaleFrom(param2);
            data2.record("./results/linear_scale_in");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void softScaleTest() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset data = reader.read("./datasets/train");
        data.softScale();
        data.record("./results/soft_scale");
    }

    @Test
    public void softScaleSerializableTest() {
        SVMFileReader reader = SVMFileReader.getInstance();

        Dataset data1 = reader.read("./datasets/train");
        Dataset data2 = reader.read("./datasets/train");

        SoftScaleParam param1 = data1.softScale();
        data1.record("./results/soft_scale_out");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("results/soft_scale_param"));
            oos.writeObject(param1);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("results/soft_scale_param"));
            SoftScaleParam param2 = (SoftScaleParam) ois.readObject();
            ois.close();

            data2.softScaleFrom(param2);
            data2.record("./results/soft_scale_in");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// End ScaleDemos.java
