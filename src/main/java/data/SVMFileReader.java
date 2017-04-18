package data;

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
 * Singleton class to generate a {@link Dataset} from file
 * <p>
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public class SVMFileReader extends EdAbstractFileReader<Dataset> {
    //~ Static fields/initializers ---------------------------------------------

    private static SVMFileReader reader = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Private constructor to prevent instantiating.
     * Also initiate the default seperator and column number.
     */
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

    /**
     * Read data from file and generate a {@link Dataset}.
     *
     * @param file the source file
     * @return a {@link Dataset}
     */
    @Override
    public Dataset read(String file) {
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            Dataset dataset = new Dataset();

            String line = br.readLine();
            while (line != null) {
                String[] contents = line.split(seperator);

                dataset.featureNum = contents.length - 1;

                Sample sample = new Sample();
                sample.label = biLabel(stod(contents[0]));

                for (int i = 1; i < contents.length; i++) {
                    svm_node feature = new svm_node();
                    feature.index = i;
                    feature.value = stod(contents[i]);
                    sample.add(feature);
                }
                dataset.add(sample);
                line = br.readLine();
            }
            System.out.println("Dataset preparation done! Read " + dataset.size() + " samples in total");
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
     * @return {@link Dataset}
     * @throws RuntimeException when there are non-double feature
     */
    public Dataset read(Connection connection, String query) throws RuntimeException {
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            Dataset dataset = new Dataset();

            dataset.featureNum = rs.getMetaData().getColumnCount() - 1;

            while (rs.next()) {
                Sample sample = new Sample();
                sample.label = stod(rs.getString(1));

                for (int i = 1; i <= dataset.featureNum; i++) {
                    svm_node feature = new svm_node();
                    feature.index = i;

                    String elemClassName = rs.getObject(i + 1).getClass().getName();
                    switch (elemClassName) {
                        case "java.lang.String":
                            feature.value = stod(rs.getString(i + 1));
                            break;
                        case "java.lang.Long":
                        case "java.lang.Double":
                        case "java.math.BigDecimal":
                            feature.value = rs.getDouble(i + 1);
                            break;
                        default:
                            // to be continued
                            throw new RuntimeException("feature must be double! get " + elemClassName);
                    }
                    sample.add(feature);
                }
                dataset.add(sample);
            }
            System.out.println("Dataset preparation done! Read " + dataset.getSampleNum() + " samples in total");
            return dataset;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //~ tool Methods -----------------------------------------------------------

    /**
     * Parse a {@link String} object to a {@link Double} object, in case some features are "null", "I", "E" or ""
     * where "I" refers to "Import" and "E" refers to "Export"
     * also support svm example data, which is like"index:value"
     * in this case this method will ignore the index and only read the value
     *
     * @param string feature in string
     * @return feature in double
     */
    public static double stod(String string) {
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
