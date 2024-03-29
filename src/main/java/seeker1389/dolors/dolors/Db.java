package seeker1389.dolors.dolors;

import java.sql.*;
import java.util.ArrayList;

public class Db {

    private String userName="root",password="root", dburl= "jdbc:mysql://localhost:3306/";;
    private Connection con;
    private Connection scraperConn;
    private ResultSet rs;
    boolean dbExitst=false;
    boolean scraperDb=false;
    private String dbName;
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
            scraperConn = (Connection) DriverManager.getConnection(dburl, this.userName, this.password);
            rs = con.getMetaData().getCatalogs();

            while (rs.next()){
                //System.out.println(rs.getString(1));
                if(rs.getString(1).equalsIgnoreCase(dbName)){
                   dbExitst=true;
                   Statement st = con.createStatement();
                   st.executeUpdate("use "+dbName);
                }
                //check the scraper db...
                if(rs.getString(1).equalsIgnoreCase("scraper")){
                   scraperDb=true;
                   Statement scraperStat= scraperConn.createStatement();
                   scraperStat.executeUpdate("use scraper");
                }

            }
            createDB();

        } catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

   }


   private void createDB(){
       try {
      if (!dbExitst) {
            System.out.println("trying to create crawler database " + dbName);
              Statement stat = con.createStatement();
              stat.executeUpdate("create database " + dbName + ";");
              stat.executeUpdate("use " + dbName);
              System.err.println("creating initial tables for the database");
              //creating tables for crawling---
              createTable("links", con);
              //creating tables for scraping---
              createTable("scrap",con);
              createTable("videoScraped",con);
              System.out.println("Crawler database created!");
           }
           if(!scraperDb){
               System.out.println("trying to create database " + dbName);
               Statement scraperStat = scraperConn.createStatement();
               scraperStat.executeUpdate("create database scraper ;");
               scraperStat.executeUpdate("use scraper");
               //---create tables for scraper----
               createTable("players",scraperConn);
               createTable("spam",scraperConn);

               System.out.println("scraper database created!");
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

   }

  // returns a connection with any db
    private Connection createDB(String name) {

      Connection newConnection=null;

    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      newConnection =(Connection) DriverManager.getConnection(dburl, this.userName, this.password);
      ResultSet r = newConnection.getMetaData().getCatalogs();

      while (r.next()) {
        // System.out.println(rs.getString(1));
        if (r.getString(1).equalsIgnoreCase(dbName)) {
          Statement st = newConnection.createStatement();
          st.executeUpdate("use " + dbName);

          return newConnection;
        }
      }
      try {
        System.out.println("trying to create database " + dbName);
        Statement stat = newConnection.createStatement();

        stat.executeUpdate("create database " + dbName + ";");
        stat.executeUpdate("use " + dbName);

        System.out.println("database created!");
        return  newConnection;

      } catch (SQLException e) { System.err.println("error in CreateDB"); }

    } catch (Exception e) { System.err.println("[error at createDB "+e); }

    return  newConnection;
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
                statement="insert into links (url,type,thumbnail,date,data,scrap,crawl) values (";
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


   private void createTable(String type, Connection con){

       String table=null;

       //Database for primary links storage -- contains all links discovered from a page (crawled and uncrawled)
       if(type.equalsIgnoreCase("links")){
           System.out.println("creating table links----");
           table="CREATE TABLE links(id int NOT NULL AUTO_INCREMENT, url varchar(225), type varchar(225), thumbnail varchar(225), date varchar(225), data varchar(225), scrap varchar(225), crawl varchar(225), PRIMARY KEY(id));";
       }
       //Database for player links storage
       if(type.equalsIgnoreCase("players")){
           System.out.println("creating table players----");
           table="CREATE TABLE players(id int NOT NULL AUTO_INCREMENT, player varchar(225), type varchar(225), date varchar(225), data varchar(225), data2 varchar(225), data3 varchar(225), PRIMARY KEY(id));";
       }

       //Database for spam links storage
       if(type.equalsIgnoreCase("spam")){
           System.out.println("creating table spam----");
           table="CREATE TABLE spam(id int NOT NULL AUTO_INCREMENT, url varchar(225), type varchar(225), date varchar(225), data varchar(225), data2 varchar(225), data3 varchar(225), PRIMARY KEY(id));";
       }

       //Database for scrap data links storage
       if(type.equalsIgnoreCase("scrap")){
           System.out.println("creating table scrap----");
           table="CREATE TABLE scrap(id int NOT NULL AUTO_INCREMENT, url varchar(225), type varchar(225), date varchar(225), data varchar(225), tags varchar(225), title varchar(225),thumbnail varchar(225), category varchar(225), hostUrl varchar(225), data2 varchar(225),PRIMARY KEY(id));";
       }

       //Database for storing scraped video
       if(type.equalsIgnoreCase("videoScraped")){
           System.out.println("creating table videoScraped-----");
           //id  title(src title) src(videoLink), thumbnail, category,type, tags, sourceURl, date, data, data2
           table="CREATE TABLE videoScraped(id int NOT NULL AUTO_INCREMENT, title varchar(225), src varchar(225),thumbnail varchar(225),category varchar(225), type varchar(225), tags varchar(225), sourceUrl varchar(225), date varchar(225), data varchar(225), data2 varchar(225),PRIMARY KEY(id));";
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
        String url,id,typ="",res,data, thumbUrl="null";
        String sql ="select * from links where " +type+ " = \"false\" limit "+limit+";";//=========================
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                 url = rs.getString("url");
                 id = (String.valueOf( rs.getInt("id")));
                 thumbUrl=rs.getString("thumbnail");
                 typ=rs.getString("type");
                 data=rs.getString("data");
                 res=id.concat("#").concat(url).concat("#").concat(thumbUrl).concat("#").concat(typ).concat("#").concat(data);
                 urls.add(res);
            }
            return  urls;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScraped(String id){
        String query="update links set scrap =\"true\" where id= "+id+";";
        Statement st = null;
        try {
            st = con.createStatement();
            int res = st.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<String> getScraperTables(String query,String column){
        ArrayList<String> res = new ArrayList<String>();
        String player,id;
        try {
            Statement st = scraperConn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                player = rs.getString(column);
               // id = (String.valueOf( rs.getInt("id")));[did not use the id ]
                res.add(player);
            }
            return  res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public boolean contains(String table, String query,String db) {
        boolean result=false;
        Statement stat = null;
        String q ="select * from "+table+" where "+query+";";
        //System.out.println(q);
        try {
        if (db.equalsIgnoreCase("crawler")) {
            stat = con.createStatement();
        }
        if(db.equalsIgnoreCase("scraper")){
            stat=scraperConn.createStatement();
        }
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
