package seeker1389.dolors.dolors;

import java.sql.*;
import java.util.ArrayList;

public class Db {

   private String userName="root",password="root", dburl= "jdbc:mysql://localhost:3306/";;
    Connection con;
    ResultSet rs;
    boolean dbExitst=false;
    String dbName;
    Db( String userName, String password,String dbName){
         this.password=password;
        this.userName=userName;
        this.dbName=dbName;
        System.out.println("[-log-]Db connection created");
        DBdriver();//starting the database and obtaining the connection obj.
    }

    Db( String userName, String password,String dbName,String url){
        this.password=password;
        this.userName=userName;
        this.dbName=dbName;
        this.dburl=url;
        System.out.println("[-log-]Db connection created");
        DBdriver();//starting the database and obtaining the connection obj.
    }

   private void DBdriver(){

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = (Connection) DriverManager.getConnection(dburl, this.userName, this.password);
            rs = con.getMetaData().getCatalogs();

            while (rs.next()){
                //System.out.println(rs.getString(1));
                if(rs.getString(1).equalsIgnoreCase(dbName)){
                   dbExitst=true;
                   Statement st = con.createStatement();
                   st.executeUpdate("use "+dbName);

                }
            }
            createDB();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

   }


   private void createDB(){
       if(dbExitst==true){
           return;
       }
       try {
           System.out.println("trying to create database "+dbName);
           Statement stat = con.createStatement();
           stat.executeUpdate("create database "+dbName+";");
           stat.executeUpdate("use "+dbName);
           createTable("links");
           System.out.println("database created!");
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

   }


   public void executeUpdate(String table,String args[]) {
        String statement = createInsertStatement(table,args);
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
           System.out.println("---error in adding to db----");
       }
   }

   private String createInsertStatement(String table,String data[]){
        String statement="";
        String tabStat=null;

        switch (table){
            case "links":
                statement="insert into links (url,type,date,data,scrap,crawl) values (";
                break;
        }

        for (int i=0; i<data.length;i++){
            statement=statement.concat("'").concat(data[i]).concat("'");
            int x=i+1;
            if(!(x==data.length)){
                statement=statement.concat(",");
            }
        }
        statement=statement.concat(");");
        //System.out.println(statement);
        return statement;
   }


   private void createTable(String type){

       String table=null;

       //Database for primary links storage -- contains all links discovered from a page (crawled and uncrawled)
       if(type.equalsIgnoreCase("links")){
           System.out.println("creating table statement");
           table="CREATE TABLE links(id int NOT NULL AUTO_INCREMENT, url varchar(225), type varchar(225), date varchar(225), data varchar(225), scrap varchar(225), crawl varchar(225), PRIMARY KEY(id));";
       }
       Statement stmt = null;
       try {
           stmt = con.createStatement();
           stmt.executeUpdate(table);
       } catch (SQLException e) {
           System.err.println("Creation of table failed");
           System.exit(-1);
           throw new RuntimeException(e);
       }

   }


    public int update(String query){
        try {
            Statement st = con.createStatement();
            int res = st.executeUpdate(query);
            return  res;
        } catch (SQLException e) {
            System.err.println("update to database failed!");
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> fetchLinks(String limit,String type){
        ArrayList<String> urls = new ArrayList<String>();
        String url,id, res;
        String sql ="select * from links where " +type+ " = \"false\" limit "+limit+";";//=========================
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                 url = rs.getString("url");
                 id = (String.valueOf( rs.getInt("id")));
                 res=id.concat("#").concat(url);
                 urls.add(res);
            }
            return  urls;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean contains(String table, String query) {
        boolean result=false;
        Statement stat = null;
        String q ="select * from "+table+" where "+query+";";
        //System.out.println(q);
        try {
            stat = con.createStatement();
            ResultSet res = stat.executeQuery(q);
            if(res.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[-db-]Unable to execure query");
            throw new RuntimeException(e);
        }
    }


}
