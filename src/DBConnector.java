import java.sql.*;

public class DBConnector {
    static Connection conn;

    static Connection getDBConnection() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/doubancrawler?useSSL=false";
        String user = "root";
        String password = "123456";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    static void saveItem(Item item,String city) throws SQLException {
        getDBConnection();
        PreparedStatement pst = null;

        String idSql = "select max(id) from "+city+"_zufang";
        pst = conn.prepareStatement(idSql);
        ResultSet rs = pst.executeQuery();
        int maxId = 0;
        if(rs.next()) {
           maxId = rs.getInt(1);
        }
        String sql = "insert into "+city+"_zufang(id,title,username,phone,link,publishdate) values(?,?,?,?,?,?);";
        pst = conn.prepareStatement(sql);
        pst.setInt(1,maxId+1);
        pst.setString(2, item.getTitle());
        pst.setString(3, item.getUsername());
        pst.setString(4, item.getPhone());
        pst.setString(5, item.getUrl());
        pst.setInt(6, item.getDate());
        pst.executeUpdate();
        System.out.println("insert成功，" + item);


    }
}
