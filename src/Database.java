/**
 * author:      Xiang Li
 * function:    all database related methods
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class Database {

    static String dbName;

    public Database(String dbName) {
        this.dbName = dbName;
    }


    // connection to the existed database
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:"+dbName;
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    // create database
    public void createDatabase(){

        try {
            File file = new File(dbName);
            if (file.exists()) {
                System.out.println("This database already exists");
                this.connect();
            } else {
                System.out.println("Creating new database");
                Class.forName("org.sqlite.JDBC").newInstance();
                DriverManager.getConnection("jdbc:sqlite:"+dbName);
                this.createTable();
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Opened database successfully");
    }


    // create tables
    public void createTable(){

        String sql1 = "CREATE TABLE IF NOT EXISTS gossip " +
                "(id INTEGER PRIMARY KEY, " +
                " encoded         TEXT   NOT NULL , " +
                " datetime        TEXT   NOT NULL, " +
                " message         TEXT   NOT NULL UNIQUE);";

        String sql2 = "CREATE TABLE IF NOT EXISTS peers " +
                "(id INTEGER PRIMARY KEY," +
                " peername     TEXT    NOT NULL UNIQUE, " +
                " port          TEXT    NOT NULL, " +
                " ip            TEXT    NOT NULL);";

        try (Connection c = this.connect();
             Statement stmt = c.createStatement()) {

            // create new tables
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // insert data to tables
    public void insertData(String tableName, String data1, String data2, String data3) {

        String sql = null;

        if(tableName.equalsIgnoreCase("gossip") ){

            // insert data if it is new, or discard
            sql = "INSERT OR IGNORE INTO gossip(encoded, datetime, message) VALUES(?,?,?);";
        }else if(tableName.equalsIgnoreCase( "peer")){

            // update data if it is new
            sql = "INSERT OR REPLACE INTO peers(peername, port, ip) VALUES(?,?,?);";
        }else {
            System.out.println("Invalid table name");
        }

        try (Connection c = this.connect();
             PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, data1);
            pstmt.setString(2, data2);
            pstmt.setString(3, data3);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // return existed records as a list
    public List<TableValue> returnRecords(String tableName, String data1, String data2, String data3) {

        List<TableValue> tableList = new ArrayList<>();

        // check row count
        String sql1 = "SELECT count(*) AS rowCount FROM " + tableName.toLowerCase();

        String sql2 = "SELECT id" +","+ data1.toLowerCase() +"," + data2.toLowerCase() + ","+
                data3.toLowerCase() + " FROM " + tableName.toLowerCase();

        try (Connection c = this.connect();
             Statement stmt  = c.createStatement()){


            ResultSet rs = stmt.executeQuery(sql1);

            int rowCount = rs.getInt("rowCount");

            rs = stmt.executeQuery(sql2);

            // loop through the result set
            while (rs.next()) {
                TableValue tv = new TableValue();
                tv.setTableName(tableName);
                tv.setRowCount(rowCount);
                tv.setId(rs.getInt("id"));
                tv.setData1(rs.getString(data1.toLowerCase()));
                tv.setData2(rs.getString(data2.toLowerCase()));
                tv.setData3(rs.getString(data3.toLowerCase()));
                tableList.add(tv);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tableList;
    }



//    public void selectAll(String tableName, String data1, String data2, String data3){
//
//        String sql1 = "SELECT count(*) AS rowCount FROM " + tableName.toLowerCase();
//
//        String sql2 = "SELECT id" +","+ data1.toLowerCase() +"," + data2.toLowerCase() + ","+ data3.toLowerCase() +
//                " FROM " + tableName.toLowerCase();
//
//        try (Connection c = this.connect();
//             Statement stmt  = c.createStatement()){
//
//            ResultSet rs    = stmt.executeQuery(sql1);
//            System.out.println("rowCount:" + rs.getInt("rowCount"));
//
//            rs    = stmt.executeQuery(sql2);
//            // loop through the result set
//            while (rs.next()) {
//                System.out.println( rs.getInt("id") +  "\t" +
//                        rs.getString(data1) + "\t" +
//                        rs.getString(data2) + "\t" +
//                        rs.getString(data3));
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }

}
