import data.Sample;
import libsvm.svm_node;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by edwardlol on 17-4-18.
 */
public class UtilTests {
    //~ Methods ----------------------------------------------------------------

    @Test
    public void sampleTest() {
        Sample sample = new Sample();

        svm_node node1 = new svm_node();
        node1.index = 1;
        node1.value = 1.0d;
        sample.add(node1);

        svm_node node2 = new svm_node();
        node2.index = 2;
        node2.value = 2.0d;
        sample.add(node2);

        sample.label = 1.0d;

        System.out.println(sample.featureNum());
    }

    @Test
    public void cloneTest() {
        Sample sample = new Sample();

        svm_node node1 = new svm_node();
        node1.index = 1;
        node1.value = 1.0d;
        sample.add(node1);

        svm_node node2 = new svm_node();
        node2.index = 2;
        node2.value = 2.0d;
        sample.add(node2);

        sample.label = 1.0d;

        Sample sample2 = sample.clone();
        System.out.println(sample);
        System.out.println(sample2);
    }

    @Test
    public void logTest() {
        final Logger logger = LoggerFactory.getLogger(UtilTests.class);

        logger.debug("Hello world, I'm a DEBUG level message");
        logger.info("Hello world, I'm an INFO level message");
        logger.warn("Hello world, I'm a WARNING level message");
        logger.error("Hello world, I'm an ERROR level message");
    }
}

// End UtilTests.java
