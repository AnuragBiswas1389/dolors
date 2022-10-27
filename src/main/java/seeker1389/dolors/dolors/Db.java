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
   public void executeUpdate(String table,String args[]) {

        /*
      String data[]={dbName,dbtableName, ScraperLink,site,mediaThumb,mediatype};
        for storing
    */

        String statement = createInsertStatement(table,args);
        updateHandler(statement);
        System.out.println("DB initialized for "+args[1]);

   }
   private String createInsertStatement(String table,String data[]){
        String statement=null;
        statement="insert into "+table+"(mediaLink,site,thumbnail,title,type) "+"values("
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
                .concat("'")+")";

        return statement;
   }
   private void updateHandler(String statement) {
        try {
            Statement stmt = con.createStatement();
            int res=stmt.executeUpdate(statement);
            System.out.println("ADDED to DB:");
            if(res>0){
                System.out.println("DB updated Successfully");
            }else {
                System.err.println("DB update UN-Successfully");
            }
        } catch (Exception e) {
            System.err.println(e);
        }

   }
   void createTable(String tableName,String type){

       if(type.equalsIgnoreCase("media")){
           String mediaTable="CREATE TABLE "+tableName+"(" +
                   "id int NOT NULL AUTO_INCREMENT," +
                   "mediaLink varchar(255)," +
                   "site varchar(255)," +
                   "thumbnail varchar(255)," +
                   "type(255)" +"PRIMARY KEY (id)"+
          ");";

           Statement stmt = null;
           try {
               stmt = con.createStatement();
               int res=stmt.executeUpdate(mediaTable);
               if(res>0){
                   System.out.println("New table created successfully ");
               }else{
                   System.err.println("Creation of table failed");
               }
           } catch (SQLException e) {
               throw new RuntimeException(e);
           }




       }

   }
    private void executeQueryHandler(String tableName, String query){

    }




}
