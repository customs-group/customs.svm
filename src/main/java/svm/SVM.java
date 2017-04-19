package svm;

import data.Dataset;
import data.Sample;
import libsvm.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by edwardlol on 2017/4/18.
 */
public class SVM {
    //~ Static fields/initializers ---------------------------------------------

    private static SVM instance;

    /**
     * suppress training log outputs
     */
    private static svm_print_interface svm_print_null = s -> {};

    /**
     * date format for generating output file
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");

    //~ Instance fields --------------------------------------------------------

    private svm_model model;

    private final svm_parameter param = new svm_parameter();

    public int C_BASE = 2;
    public int G_BASE = 2;
    public int C_STEP = 1;
    public int G_STEP = 1;
    public int C_START_VALUE = -8;
    public int C_STOP_VALUE = 8;
    public int G_START_VALUE = -8;
    public int G_STOP_VALUE = 8;

    //~ Constructors -----------------------------------------------------------

    private SVM() {
        // default params
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.C = 0.015625; // for C_SVC, EPSILON_SVR and NU_SVR, default 1.0 / data.getFeatureNum()
        param.gamma = 0.0625;
        param.eps = 0.01;
        param.cache_size = 100.0d;
    }

    public static SVM getInstance() {
        if (instance == null) {
            instance = new SVM();
        }
        return instance;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * train svm model
     * @param dataset
     *
     * @return
     */
    public svm_model train(Dataset dataset) {
		/* set svm problem */
        svm_problem problem = new svm_problem();
        problem.l = dataset.size();
        problem.x = new svm_node[problem.l][];
        problem.y = new double[problem.l];
        for (int i = 0; i < problem.l; i++) {
            problem.x[i] = dataset.get(i).getFeatureArray();
            problem.y[i] = dataset.get(i).label;
        }

        /* train svm model */
        String error_msg = svm.svm_check_parameter(problem, this.param);
        if (error_msg == null) {
            this.model = svm.svm_train(problem, this.param);
            return this.model;
        } else {
            System.err.println("svm parameter error:");
            System.err.println(error_msg);
            return null;
        }
    }

    public void saveModel(String modelFile) {
        try {
            svm.svm_save_model(modelFile, this.model);
        } catch (IOException e) {
            System.out.println("save svm model failed!");
            e.printStackTrace();
        }
    }

    public svm_model loadModel(String modelFile) {
        try {
            this.model = svm.svm_load_model(modelFile);
            return this.model;
        } catch (IOException e) {
            System.err.println("load svm model failed!");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * predicte the labels of test data
     *
     * @param model model trained by train
     * @param data  data used to test the model
     */
    public void test(svm_model model, Dataset data, String resultFile) {

        /* preparation for the log file */
        Date now = new Date();
        String suffix = dateFormat.format(now);

        try (FileWriter fw = new FileWriter(resultFile + suffix + ".log");
             BufferedWriter bw = new BufferedWriter(fw)) {

            int hit = 0;

            for (int i = 0; i < data.getSampleNum(); i++) {
                svm_node[] features = data.get(i).getFeatureArray();
                double realLabel = data.get(i).label;
                double predictLabel = svm.svm_predict(model, features);

                bw.append("predict label: ")
                        .append(Double.toString(predictLabel))
                        .append("; real label: ")
                        .append(Double.toString(realLabel))
                        .append(' ');

                for (int j = 0; j < data.getFeatureNum(); j++) {
                    bw.append(Integer.toString(features[j].index))
                            .append(':')
                            .append(Double.toString(features[j].value))
                            .append(' ');
                }
                bw.append('\n');
                bw.flush();
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

    /**
     * valid model accuracy
     *
     * @param model  model to valid
     * @param dataset    test set
     *
     * @return total hit num in test set
     */
    private int valid(svm_model model, Dataset dataset) {
        int hit = 0;

        for (Sample aDataset : dataset) {
            svm_node[] features = aDataset.getFeatureArray();
            double realLabel = aDataset.label;
            double predictLabel = svm.svm_predict(model, features);
            if (Math.abs(predictLabel - realLabel) < 0.001) {
                hit++;
            }
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
        // no training outputs
        svm.svm_set_print_string_function(svm_print_null);

        int best_power_of_c = C_START_VALUE;
        int best_power_of_g = G_START_VALUE;

        double bestAccuracy = 0.0;

        for (int power_of_c = C_START_VALUE; power_of_c < C_STOP_VALUE; power_of_c += C_STEP) {
            for (int power_of_g = G_START_VALUE; power_of_g < G_STOP_VALUE; power_of_g += G_STEP) {
                // TODO: 17-4-17 hard code 10 fold, but it seems to be reasonable
                double accuracy = crossValidation(data, power_of_c, power_of_g, 10);
                System.out.printf("power of c: " + power_of_c + "; power of g: " + power_of_g + "; accuracy: %.2f%%", accuracy);
                if (accuracy > 0.6
                        && (
                        (accuracy > bestAccuracy)
                                || (
                                Math.abs(accuracy - bestAccuracy) < 0.00001
                                        && power_of_c < best_power_of_c
                        ))) {
                    bestAccuracy = accuracy;
                    best_power_of_c = power_of_c;
                    best_power_of_g = power_of_g;
                }
                System.out.printf("; best poc: " + best_power_of_c + "; best pog: " + best_power_of_g + "; best accuracy: %.2f%%\n", bestAccuracy);
//                if (hitRate < 0.6) { // pruning hehe
//                    System.out.printf("; best poc: " + best_power_of_c + "; best pog: " + best_power_of_g + "; best hit rate: %.2f%%\n", best_hit_rate);
//                } else if ((hitRate > best_hit_rate)
//                        || (Math.abs(hitRate - best_hit_rate) < 0.00001
//                        && power_of_c < best_power_of_c)) {
//                    best_hit_rate = hitRate;
//                    best_power_of_c = power_of_c;
//                    best_power_of_g = power_of_g;
//                    System.out.printf("; best poc: " + best_power_of_c + "; best pog: " + best_power_of_g + "; best hit rate: %.2f%%\n", best_hit_rate);
//                } else {
//                    System.out.printf("; best poc: " + best_power_of_c + "; best pog: " + best_power_of_g + "; best hit rate: %.2f%%\n", best_hit_rate);
//                }
            }
        }
        param.C = Math.pow(2, best_power_of_c);
        param.gamma = Math.pow(2, best_power_of_g);
        System.out.println("best C: " + param.C + "; best gamma: " + param.gamma + "; accuracy: " + bestAccuracy);
        return param;
    }

    /**
     * do cross validation
     *
     * @param dataset       training data
     * @param power_of_c    power of c, see{@link svm_parameter#C}
     * @param power_of_g    power of g, see{@link svm_parameter#gamma}
     * @param numFolds      the number n of n fold validation
     *
     * @return best accuracy under this set of c and g
     */
    private double crossValidation(Dataset dataset, int power_of_c, int power_of_g, int numFolds) {
        if (!dataset.isScaled()) {
            dataset.linearScale();
        }

        this.param.C = Math.pow(C_BASE, power_of_c);
        this.param.gamma = Math.pow(G_BASE, power_of_g);

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
            totalHit += valid(model, validData);
        }
        // n is in set.size()
        return 100.0 * totalHit / dataset.size();
    }

    //~ Getter/Setter Methods --------------------------------------------------

    /**
     * set the svm type
     *
     * @param svmType see{@link svm_parameter#svm_type}
     */
    public void setSVMType(int svmType) {
        param.svm_type = svmType;
    }

    /**
     * set the svm kernal type
     *
     * @param kernelType see{@link svm_parameter#kernel_type}
     */
    public void setKernelType(int kernelType) {
        param.kernel_type = kernelType;
    }

    /**
     * set the svm C
     *
     * @param C see{@link svm_parameter#C}
     */
    public void setC(double C) {
        param.C = C;
    }

    /**
     * set the svm gamma
     *
     * @param gamma see{@link svm_parameter#gamma}
     */
    public void setGamma(double gamma) {
        param.gamma = gamma;
    }

    /**
     * set the svm eps
     *
     * @param eps see{@link svm_parameter#eps}
     */
    public void setEps(double eps) {
        param.eps = eps;
    }
}

// End SVM.java
