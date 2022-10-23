package seeker1389.dolors.dolors;

import java.sql.*;

public class Db {
    Connection con;
    int id=1;

    String createStatement(String data[]){
        String statement=null;
        statement="insert into video values("
                +"'".concat(data[0])
                .concat("'")
                .concat(",")+"'"
                .concat(data[1])
                .concat("'")
                .concat(",")+"'"
                .concat(data[2])
                .concat("'")
                .concat(",")+"'"
                .concat(data[3])
                .concat("'")
                .concat(",")+"'"
                .concat(data[4])
                .concat("'")+")";
        return statement;
    }
    void executeUpdate(String statement) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/videodb", "root", "seeker1389");
            Statement stmt = con.createStatement();
            int res=stmt.executeUpdate(statement);
            ResultSet rs = stmt.executeQuery("select * from video");
            if(res>0){
                System.out.println("DB updated Successfully");
            }else{
                System.err.println(" DB update UN-Successfully");
            }
            con.close();

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void starter(String args[]) {
        String statement = createStatement(args);
        executeUpdate(statement);
        System.out.println("DB initialized,,");

    }
}
