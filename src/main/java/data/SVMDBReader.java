package data;

import libsvm.svm_node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static data.Utils.stod;

/**
 * Created by edwardlol on 17-4-19.
 */
public class SVMDBReader {
    //~ Static fields/initializers ---------------------------------------------

    private static SVMDBReader reader = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Private constructor to prevent instantiating.
     */
    private SVMDBReader() {
    }

    public static SVMDBReader getInstance() {
        if (reader == null) {
            reader = new SVMDBReader();
        }
        return reader;
    }

    //~ Methods ----------------------------------------------------------------

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

            dataset.setFeatureNum(rs.getMetaData().getColumnCount() - 1);

            while (rs.next()) {
                Sample sample = new Sample();
                sample.setLabel(stod(rs.getString(1)));

                for (int i = 1; i <= dataset.getFeatureNum(); i++) {
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
}

// End SVMDBReader.java
