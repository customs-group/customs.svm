package data;

import deprecated.DataSet;
import io.EdAbstractFileReader;
import libsvm.svm_node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * singleton class to read file into {@link DataSet}
 *
 * Created by edwardlol on 2017/4/18.
 */
public class SVMFileReader extends EdAbstractFileReader<Dataset> {
    //~ Static fields/initializers ---------------------------------------------

    private static SVMFileReader reader = null;

    //~ Instance fields --------------------------------------------------------

    //~ Constructors -----------------------------------------------------------

    private SVMFileReader() {
        seperator = " ";
        columnNumber = Integer.MAX_VALUE;
    }

    public static SVMFileReader getInstance() {
        if (reader == null) {
            reader = new SVMFileReader();
        }
        return reader;
    }

    //~ Methods ----------------------------------------------------------------
//
//    @Override
//    public DataSet read(String file) {
//        try (FileReader fr = new FileReader(file);
//             BufferedReader br = new BufferedReader(fr)) {
//
//            DataSet dataSet = new DataSet();
//
//            String line = br.readLine();
//            while (line != null) {
//                String[] contents = line.split(seperator);
//                // set feature num
//                dataSet.featureNum = contents.length - 1;
//                svm_node[] sample = new svm_node[dataSet.featureNum];
//                dataSet.labels.add(biLabel(stod(contents[0])));
//
//                for (int i = 1; i < contents.length; i++) {
//                    sample[i - 1] = new svm_node();
//                    sample[i - 1].index = i;
//                    sample[i - 1].value = stod(contents[i]);
//                }
//
//                dataSet.originalSet.add(sample);
//                line = br.readLine();
//            }
//            dataSet.sampleNum = dataSet.originalSet.size();
//            System.out.println("DataSet preparation done! Read " + dataSet.getSampleNum() + " samples in total");
//            return dataSet;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public Dataset read(String file) {
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            Dataset dataset = new Dataset();

            String line = br.readLine();
            while (line != null) {
                String[] contents = line.split(seperator);
                // set feature num
                dataset.featureNum = contents.length - 1;
                Sample sample = new Sample();
                sample.label = biLabel(stod(contents[0]));

                for (int i = 1; i < contents.length; i++) {
                    svm_node node = new svm_node();
                    node.index = i;
                    node.value = stod(contents[i]);
                    sample.add(node);
                }

                dataset.add(sample);
                line = br.readLine();
            }
            System.out.println("DataSet preparation done! Read " + dataset.size() + " samples in total");
            return dataset;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read data from a db
     * may be removed in the future
     *
     * @param connection db connection
     * @param query      query to select data from db
     * @return
     */
    public DataSet read(Connection connection, String query) {
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            DataSet dataSet = new DataSet();

            dataSet.featureNum = rs.getMetaData().getColumnCount() - 1;

            while (rs.next()) {
                svm_node[] sample = new svm_node[dataSet.featureNum];
                for (int i = 0; i < dataSet.featureNum; i++) {
                    sample[i] = new svm_node();

                    sample[i].index = i + 1;

                    if (rs.getObject(i + 2).getClass().getName().equals("java.lang.String")) {
                        sample[i].value = stod(rs.getString(i + 2));
                    } else if ((rs.getObject(i + 2).getClass().getName().equals("java.lang.Long"))
                            || (rs.getObject(i + 2).getClass().getName().equals("java.math.BigDecimal"))) {
                        sample[i].value = rs.getDouble(i + 2);
                    } else {
                        // to be continued
                    }
                }
                dataSet.originalSet.add(sample);
                dataSet.labels.add(biLabel(stod(rs.getString(1))));
            }
            dataSet.sampleNum = dataSet.originalSet.size();

            System.out.println("DataSet preparation done! " + dataSet.getSampleNum() + " samples in total");
            return dataSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //~ tool Methods -----------------------------------------------------------

    /**
     * transfer String to Double, in case some features are "null", "I", "E" or ""
     * where "I" refers to "Import" and "E" refers to "Export"
     * also support svm example data, which is like"index:value"
     * in this case this method will ignore the index and only read the value
     *
     * @param string feature in string
     * @return feature in double
     */
    private static double stod(String string) {
        double result = 2.0d; // should handle this error: "cannnot convert string to Double"
        if (string == null || string.equals("") || string.equals("null") || string.equals("I")) {
            result = 0.0d;
        } else if (string.equals("E")) {
            result = 1.0d;
        } else {
            try {
                result = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                String[] tmp = string.split(":");
                try {
                    result = Double.parseDouble(tmp[1]);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * normalize the labels to 1 or -1, so that there will only be 2 classes in the dataset
     * and also, -1 and 1 are better labels than 0 and 1
     * the default action is to make nagative and 0 label -1, others 1
     * be careful of using this method
     *
     * @param label original label
     * @return normalize label
     */
    private static double biLabel(double label) {
        if (label < 0.0d) {
            return -1.0d;
        }
        if (Math.abs(label - 0.0d) < 0.00001) {
            return -1.0d;
        }
        return 1.0d;
    }
}

// End SVMFileReader.java
