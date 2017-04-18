package svm;

import data.Dataset;
import data.SVMFileReader;
import libsvm.svm_model;
import org.junit.Test;
import util.DBManager;

import java.sql.Connection;
import java.util.Vector;

/**
 * Created by edwardlol on 2017/4/18.
 */
public class svmDemos {
    //~ Methods ----------------------------------------------------------------

    @Test
    public void readDemo() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        double[][] scaleParam = trainData.scale();
        testData.scaleFrom(scaleParam);

        SVM svm = SVM.getInstance();
        svm.gridSearch(trainData);

        svm_model model = svm.train(trainData);
        svm.test(model, trainData, "./results/");
        svm.test(model, testData, "./results/");
    }

    @Test
    public void trainFileDemo() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        double[][] scaleParam = trainData.scale();
        testData.scaleFrom(scaleParam);

        trainData.record("./results/svm/train.scaled");
        testData.record("./results/svm/test.scaled");

        SVM svm = SVM.getInstance();
        svm.setC(0.03125);
        svm.setGamma(0.03125);
        svm_model model = svm.train(trainData);
        svm.saveModel("./results/svm/model");
        svm.test(model, trainData, "./results/svm/train");
        svm.test(model, testData, "./results/svm/test");
    }

    @Test
    public void predictFileDemo() {
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        testData.record("./results/svm/test.scaled");

        SVM svm = SVM.getInstance();
        svm_model model = svm.loadModel("./results/svm/model");
        svm.test(model, testData, "./results/svm/test");

    }

    @Test
    public void gridSearchFileDemo() {
        Dataset trainData = SVMFileReader.getInstance().read("./datasets/train");
        Dataset testData = SVMFileReader.getInstance().read("./datasets/test");

        double[][] scaleParam = trainData.scale();
        testData.scaleFrom(scaleParam);

        SVM svm = SVM.getInstance();
        svm.gridSearch(trainData);

        svm_model model = svm.train(trainData);
        svm.test(model, trainData, "./results/svm/");
        svm.test(model, testData, "./results/svm/");
    }

    @Test
    public void predictDBDemo() {
        /* query: get sets from database
         * colom 1: labels
		 * colom 2 - N: features
		 */
        StringBuilder trainQuery = new StringBuilder();
        StringBuilder testQuery = new StringBuilder();
        String trainLimit = "limit 2500";
        String testLimit = "limit 2500";

        Vector<String> features = new Vector<>();
        features.add("entry_head.special_flag");
        features.add("entry_head.i_e_flag");
        features.add("entry_head.decl_port");
        features.add("entry_head.trade_country");
        features.add("entry_head.destination_port");
        features.add("UNIX_TIMESTAMP(entry_head.d_date)");
        features.add("entry_head.trade_mode");
        features.add("entry_list.code_ts");
        features.add("entry_list.qty_1");
        features.add("entry_list.usd_price");

        trainQuery.append("select");
        for (String feature : features) {
            trainQuery.append(' ').append(feature).append(',');
        }
        trainQuery.deleteCharAt(trainQuery.length());

        trainQuery.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where entry_head.d_date < '2010-03-05' and entry_head.special_flag = 1 ")
                .append(trainLimit)
                .append(" union (select");
        for (String feature : features) {
            trainQuery.append(' ').append(feature).append(',');
        }
        trainQuery.deleteCharAt(trainQuery.length());

        trainQuery.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where entry_head.d_date < '2010-03-05' and entry_head.special_flag = 0 ").append(trainLimit).append(");");

        testQuery.append("select");
        for (String feature : features) {
            testQuery.append(' ').append(feature).append(',');
        }
        testQuery.deleteCharAt(testQuery.length());

        testQuery.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where '2010-03-05' <= entry_head.d_date and entry_head.special_flag = 1 ")
                .append(testLimit)
                .append(" union (select");

        for (String feature : features) {
            testQuery.append(' ').append(feature).append(',');
        }
        testQuery.deleteCharAt(testQuery.length());
        testQuery.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where '2010-03-05' <= entry_head.d_date and entry_head.special_flag = 0 ")
                .append(testLimit)
                .append(");");

        Connection connection = DBManager.get_DB_connection();
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset trainData = reader.read(connection, trainQuery.toString());
        Dataset testData = reader.read(connection, testQuery.toString());


        trainData.record("./results/data.train");
        testData.record("./results/data.test");

        testData.scaleFrom(trainData.scale());

        trainData.record("./results/data.train");
        testData.record("./results/data.test");

        DBManager.return_DB_connection(connection);

        SVM svm = SVM.getInstance();
        svm_model model = svm.train(trainData);
        svm.test(model, trainData, "./results/svm/");
        svm.test(model, testData, "./results/svm/");

    }

    @Test
    public void gridSearchDBDemo() {
        /* query: get sets from database
         * colom 1: labels
		 * colom 2 - N: features
		 */
        StringBuilder query = new StringBuilder();
        String limit = "limit 2500";

        Vector<String> features = new Vector<>();
        features.add("entry_head.special_flag");
        features.add("entry_head.i_e_flag");
        features.add("UNIX_TIMESTAMP(entry_head.d_date)");
        features.add("entry_head.trade_mode");
        features.add("entry_list.code_ts");
        features.add("entry_list.qty_1");
        features.add("entry_list.usd_price");

		/* train query */
        query.append("select");
        for (String feature : features) {
            query.append(' ').append(feature).append(',');
        }
        query.deleteCharAt(query.length());

        query.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where entry_head.d_date < '2010-03-05' and entry_head.special_flag = 1 ")
                .append(limit)
                .append(" union (select");

        for (String feature : features) {
            query.append(' ').append(feature).append(',');
        }
        query.deleteCharAt(query.length());
        query.append(" from entry_head inner join entry_list on entry_head.entry_id = entry_list.entry_id")
                .append(" where entry_head.d_date < '2010-03-05' and entry_head.special_flag = 0 ")
                .append(limit).append(");");

        Connection connection = DBManager.get_DB_connection();

        SVMFileReader reader = SVMFileReader.getInstance();

        Dataset data = reader.read(connection, query.toString());
        DBManager.return_DB_connection(connection);
        data.scale();

        SVM svm = SVM.getInstance();
        svm.gridSearch(data);

    }
}

// End svmDemos.java
