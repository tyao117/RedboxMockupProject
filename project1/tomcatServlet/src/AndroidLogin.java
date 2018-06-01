
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AndroidLogin", urlPatterns = "/api/android-login")
public class AndroidLogin extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public AndroidLogin() {
		super();
	}
	
	// Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	System.out.println("Received android request");
    	
    	try {
    		response.setContentType("application/json"); 	
    		// Create a new connection to database
    		Connection dbCon = dataSource.getConnection();

    		// Declare a new statement

    		// Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
    		String name = request.getParameter("email");
    		String password = request.getParameter("password");
    		// Make new variables for the result
    		String dbName = null;
    		String dbPassword = null;

    		// Generate a SQL query

    		String query = String.format("SELECT email, password, ccId, id from customers where email=? or password=? limit 20");
    		PreparedStatement statement = dbCon.prepareStatement(query);
    		statement.setString(1, name);
    		statement.setString(2, password);


    		// Perform the query
    		ResultSet rs = statement.executeQuery();
    		while(rs.next()) {
            	dbName = rs.getString("email");
            	dbPassword = rs.getString("password");
            }
            boolean passwordSuccess = new StrongPasswordEncryptor().checkPassword(password,dbPassword);
            // Give a result status
            if (name.equals(dbName) && passwordSuccess) {
            	
            	JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                response.getWriter().write(responseJsonObject.toString());
            } else {
            	// Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                if (!name.equals(dbName)) {
                    responseJsonObject.addProperty("message", "user " + name + " doesn't exist");
                } else if (!passwordSuccess) {
                	
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                response.getWriter().write(responseJsonObject.toString());
            }
            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        }catch(Exception ex){
            response.getWriter().println("Error!!!!!!");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
