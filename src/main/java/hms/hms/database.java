package hms.hms;
import java.sql.Connection;
import java.sql.DriverManager;

public class database {
    public static Connection connectDb (){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/hms_data", "root","");
            return connect;

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
