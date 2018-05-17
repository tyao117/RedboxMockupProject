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
    	
        MovieSAXParser msp = new MovieSAXParser(statement);
        msp.runMovieParser();
        StarsSAXParser ssp = new StarsSAXParser(statement);
        ssp.runStarsParser();
        CastSAXParser csp = new CastSAXParser(statement, ssp.getStarsMapping(), msp.getMovieMapping());
        csp.runCastsParser();
        timeCompare tc = new timeCompare(msp.getMovieMapping(), statement);
        tc.runTimeCompare();
    }

}
