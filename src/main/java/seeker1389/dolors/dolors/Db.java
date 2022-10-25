package seeker1389.dolors.dolors;

import java.sql.*;

public class Db {

   private String userName="root",password="root",dbName="videodb", tableName="video";
    Connection con;
    int id=1;

    Db(String dbName, String tableName, String userName, String password){
        this.dbName=dbName;
        this.tableName=tableName;
        this.password=password;
        this.userName=userName;
        System.out.println("[-log-]Db connection created for db: "+dbName);
        DBdriver();//starting the database and obtaining the connection obj.
    }

    private void DBdriver(){
        String dburl= "jdbc:mysql://localhost:3306/"+this.dbName;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = (Connection) DriverManager.getConnection(dburl, this.userName, this.password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }//initiates the connections...invoked by the constructor.

    public void starter(String table,String args[]) {
        String statement = createInsertStatement(table,args);
        // executeUpdate(statement);     <-------------------temporary for testing--------[disabled database]
        System.out.println("DB initialized for "+args[1]);

    }//invoked by other classes for storage ... passes the args




   private String createInsertStatement(String table,String data[]){
        String statement=null;
        statement="insert into "+table+"values("
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
   private void executeUpdate(String tabel,String statement) {

        try {
            Statement stmt = con.createStatement();
            int res=stmt.executeUpdate(statement);
            System.out.println("ADDED to DB:");
            if(res>0){
                System.out.println("DB updated Successfully");
            }else{
                System.err.println("DB update UN-Successfully");
            }
            con.close();
        } catch (Exception e) {
            System.err.println(e);
        }

   }


    /*
    //String data[]={dbName,dbtableName, ScraperLink,site,mediaThumb,mediatype};
      for storing
     */




}
