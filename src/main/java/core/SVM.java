package core;

import data.Dataset;
import data.Sample;
import libsvm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main working class.
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public class SVM {
    //~ Static fields/initializers ---------------------------------------------

    private static SVM instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(SVM.class);

    /**
     * Suppress training log outputs.
     */
    private static svm_print_interface svm_print_null = s -> {
    };

    /**
     * Date format for generating output file.
     * May be removed in the future because the only usage {@link this#test} is deprecated.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");

    //~ Instance fields --------------------------------------------------------

    /**
     * A reference of {@link svm_model}.
     */
    private svm_model model;

    /**
     * An instance of {@link svm_parameter}.
     * Only some important params are exposed.
     */
    private final svm_parameter param = new svm_parameter();

    /**
     * The base of param C.
     */
    public int C_Base = 2;

    /**
     * The searching step of param C.
     */
    public int C_Step = 1;

    /**
     * The start power of param C.
     */
    public int C_StartPower = -8;

    /**
     * The stop power of param C.
     */
    public int C_StopPower = 8;

    /**
     * The base of param gamma.
     */
    public int G_Base = 2;

    /**
     * The searching step of param gamma.
     */
    public int G_Step = 1;

    /**
     * The start power of param gamma.
     */
    public int G_StartPower = -8;

    /**
     * The stop power of param gamma.
     */
    public int G_StopPower = 8;

    //~ Constructors -----------------------------------------------------------

    /**
     * Initializing default params.
     */
    private SVM() {
        this.param.svm_type = svm_parameter.C_SVC;
        this.param.kernel_type = svm_parameter.RBF;
        this.param.C = 0.015625; // for C_SVC, EPSILON_SVR and NU_SVR, default 1.0 / data.getFeatureNum()
        this.param.gamma = 0.0625;
        this.param.eps = 0.01;
        this.param.cache_size = 100.0d;
    }

    public static SVM getInstance() {
        if (instance == null) {
            instance = new SVM();
        }
        return instance;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Train svm model.
     *
     * @param dataset the training set
     * @return an {@link svm_model}
     */
    public svm_model train(Dataset dataset) {
        /* set svm problem */
        svm_problem problem = new svm_problem();
        problem.l = dataset.size();
        problem.x = new svm_node[problem.l][];
        problem.y = new double[problem.l];
        for (int i = 0; i < problem.l; i++) {
            problem.x[i] = dataset.get(i).getFeatureArray();
            problem.y[i] = dataset.get(i).getLabel();
        }

        /* train svm model */
        String error_msg = svm.svm_check_parameter(problem, this.param);
        if (error_msg == null) {
            this.model = svm.svm_train(problem, this.param);
            return this.model;
        } else {
            LOGGER.error("svm parameter error: {}", error_msg);
            return null;
        }
    }

    /**
     * Save the {@link this#model} to destination file.
     *
     * @param modelFile destination file name.
     */
    public void saveModel(String modelFile) {
        try {
            svm.svm_save_model(modelFile, this.model);
        } catch (IOException e) {
            LOGGER.error("save svm model failed!");
            e.printStackTrace();
        }
    }

    /**
     * Load {@link this#model} from the source file.
     *
     * @param modelFile the source file
     * @return {@link this#model}
     */
    public svm_model loadModel(String modelFile) {
        try {
            this.model = svm.svm_load_model(modelFile);
            return this.model;
        } catch (IOException e) {
            LOGGER.error("load svm model failed!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Predict the label of the sample with the given model.
     *
     * @param model  trained model
     * @param sample sample to be predicted
     * @return the predict label of the sample
     */
    public double predict(svm_model model, Sample sample) {
        return svm.svm_predict(model, sample.getFeatureArray());
    }

    /**
     * valid model accuracy
     *
     * @param model model to valid
     * @param data  valid set
     * @return total hit num in valid set
     */
    public int valid(svm_model model, Dataset data, boolean debug) {
        int hit = 0;

        for (Sample sample : data) {
            double predictLabel = predict(model, sample);

            if (debug) {
                LOGGER.info("predict label: {}; real label: {}", predictLabel, sample.toString());
            }

            if (Math.abs(predictLabel - sample.getLabel()) < 0.00001) {
                hit++;
            }
        }
        if (debug) {
            double hitRate = 100.0 * hit / data.getSampleNum();
            System.out.println("SVM accuracy: " + String.format("%.2f", hitRate) + "%");
        }
        return hit;
    }

    /**
     * search the best svm parameter
     *
     * @param data training data
     * @return svm_parameter
     */
    public svm_parameter gridSearch(Dataset data) {
        // suppress training outputs
        svm.svm_set_print_string_function(svm_print_null);

        int bestPowerOfC = this.C_StartPower;
        int bestPowerOfG = this.G_StartPower;

        double bestAccuracy = 0.0;

        for (int powerOfC = C_StartPower; powerOfC < C_StopPower; powerOfC += C_Step) {
            for (int powerOfG = G_StartPower; powerOfG < G_StopPower; powerOfG += G_Step) {
                // TODO: 17-4-17 hard code 10 fold, but it seems to be reasonable
                double accuracy = crossValidation(data, powerOfC, powerOfG, 10);

                System.out.printf("C: " + Math.pow(this.C_Base, powerOfC)
                        + "gamma: " + Math.pow(this.G_Base, powerOfG)
                        + ", accuracy: %.2f%%\n", accuracy);
                if (accuracy > 0.6) {
                    if ((accuracy > bestAccuracy)
                            // when get same accuracy, prefer the smaller C
                            || (Math.abs(accuracy - bestAccuracy) < 0.00001 && powerOfC < bestPowerOfC)) {
                        bestAccuracy = accuracy;
                        bestPowerOfC = powerOfC;
                        bestPowerOfG = powerOfG;
                    }
                }
                System.out.printf("best C: " + Math.pow(this.C_Base, bestPowerOfC)
                        + ", best gamma: " + Math.pow(this.G_Base, bestPowerOfG)
                        + ", best accuracy: %.2f%%\n", bestAccuracy);
            }
        }
        this.param.C = Math.pow(2, bestPowerOfC);
        this.param.gamma = Math.pow(2, bestPowerOfG);
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("best C: " + this.param.C + "; best gamma: " + this.param.gamma + "; accuracy: " + bestAccuracy);
        return this.param;
    }

    /**
     * do cross validation
     *
     * @param dataset    training data
     * @param power_of_c power of c, see{@link svm_parameter#C}
     * @param power_of_g power of g, see{@link svm_parameter#gamma}
     * @param numFolds   the number n of n fold validation
     * @return best accuracy under this set of c and g
     */
    private double crossValidation(Dataset dataset, int power_of_c, int power_of_g, int numFolds) {
        if (!dataset.isScaled()) {
            dataset.linearScale();
        }

        this.param.C = Math.pow(C_Base, power_of_c);
        this.param.gamma = Math.pow(G_Base, power_of_g);

        int totalHit = 0;
        // valid set length
        int vsLen = dataset.size() / numFolds;

        for (int i = 0; i <= numFolds; i++) {
            Dataset trainData = new Dataset();
            Dataset validData = new Dataset();

            // valid set start index
            int vsStart = i * vsLen;
            // valid set end index
            int vsEnd = i == numFolds ? dataset.size() : (i + 1) * vsLen;

            for (int j = 0; j < vsStart; j++) {
                trainData.add(dataset.get(j).clone());
            }
            for (int j = vsStart; j < vsEnd; j++) {
                validData.add(dataset.get(j).clone());
            }
            for (int j = vsEnd; j < dataset.size(); j++) {
                trainData.add(dataset.get(j).clone());
            }
            svm_model model = train(trainData);
            totalHit += valid(model, validData, false);
        }
        // n is in set.size()
        return 100.0 * totalHit / dataset.size();
    }

    //~ Getter/Setter methods --------------------------------------------------

    /**
     * set the svm type
     *
     * @param svmType see{@link svm_parameter#svm_type}
     */
    public void setSVMType(int svmType) {
        this.param.svm_type = svmType;
    }

    /**
     * set the svm kernal type
     *
     * @param kernelType see{@link svm_parameter#kernel_type}
     */
    public void setKernelType(int kernelType) {
        this.param.kernel_type = kernelType;
    }

    /**
     * set the svm C
     *
     * @param C see{@link svm_parameter#C}
     */
    public void setC(double C) {
        this.param.C = C;
    }

    /**
     * set the svm gamma
     *
     * @param gamma see{@link svm_parameter#gamma}
     */
    public void setGamma(double gamma) {
        this.param.gamma = gamma;
    }

    /**
     * set the svm eps
     *
     * @param eps see{@link svm_parameter#eps}
     */
    public void setEps(double eps) {
        this.param.eps = eps;
    }

    //~ Deprecated methods -----------------------------------------------------

    /**
     * predicte the labels of valid data
     *
     * @param model      model trained by train
     * @param data       data used to valid the model
     * @param resultFile result file
     */
    @Deprecated
    public void test(svm_model model, Dataset data, String resultFile) {
        final Logger logger = LoggerFactory.getLogger(SVM.class);

        logger.debug("Hello world, I'm a DEBUG level message");
        /* preparation for the log file */
        Date now = new Date();
        String suffix = dateFormat.format(now);

        try (FileWriter fw = new FileWriter(resultFile + suffix + ".log");
             BufferedWriter bw = new BufferedWriter(fw)) {

            int hit = 0;

            for (int i = 0; i < data.getSampleNum(); i++) {
                Sample sample = data.get(i);

                double realLabel = data.get(i).getLabel();
                double predictLabel = predict(model, sample);

                bw.append("predict label: ")
                        .append(Double.toString(predictLabel))
                        .append("; real label: ");

                bw.append(sample.toString()).flush();

                if (Math.abs(predictLabel - realLabel) < 0.001) {
                    hit++;
                }
            }
            double hitRate = 100.0 * hit / data.getSampleNum();
            System.out.println("SVM accuracy: " + String.format("%.2f", hitRate) + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// End SVM.java
