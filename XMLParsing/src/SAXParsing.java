import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SAXParsing {

    public static void main(String[] args) {

    	Connection dbCon;
    	Statement statement = null;
    	
        // Create a new connection to database
        try {
        	String mySQLUrl = "jdbc:mysql://localhost:3306/moviedb";
        	String username = "mytestuser";
        	String password = "mypassword";
        	dbCon = DriverManager.getConnection(mySQLUrl, username, password);

        // Declare a new statement
			statement = dbCon.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        MovieSAXParser spe = new MovieSAXParser();
        spe.runMovieParser(statement);
//        System.out.println(genres);
        timeCompare tc = new timeCompare(spe.getMovieMapping(), statement);
        tc.runTimeCompare();
    }

}
