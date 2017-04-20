package someone_else;

import data.Dataset;
import data.SVMDBReader;
import data.SVMFileReader;
import org.junit.Test;
import util.DBManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwardlol on 2017/4/20.
 */
public final class IODemos {
    //~ Methods ----------------------------------------------------------------

    /**
     * Read data into {@link Dataset} from a file.
     */
    @Test
    public void readFromFile() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset data = reader.read("./datasets/train");
    }

    /**
     * Read data from a not exist file.
     */
    @Test
    public void readFromFileNotExists() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset data = reader.read("./datasets/not_exist_file");
    }

    /**
     * Set different seperator of data reader.
     */
    @Test
    public void setSeperater() {
        SVMFileReader reader = SVMFileReader.getInstance();
        Dataset data1 = reader.read("./datasets/train");
        reader.setSeperator(",");
        Dataset data2 = reader.read("./datasets/train.csv");

        data1.record("results/spaceSep");
        data2.record("results/commaSep");
    }

    /**
     * Read data into {@link Dataset} from a database.
     */
    @Test
    public void readDataFromDB() {
        /* query: get sets from database
         * colom 1: labels
		 * colom 2 - N: features
		 */
        StringBuilder trainQuery = new StringBuilder();
        String trainLimit = "limit 2500";

        List<String> features = new ArrayList<>();
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
                .append(" where entry_head.d_date < '2010-03-05' and entry_head.special_flag = 0 ")
                .append(trainLimit).append(");");

        Connection connection = DBManager.get_DB_connection();

        SVMDBReader reader = SVMDBReader.getInstance();
        Dataset trainData = reader.read(connection, trainQuery.toString());

        DBManager.return_DB_connection(connection);
    }

}

// End IODemos.java
