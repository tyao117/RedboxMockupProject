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
    	Connection dbCon;
    	ResultSet rs;
    	try {
    		response.setContentType("application/json"); 	
    		// Create a new connection to database
    		dbCon = dataSource.getConnection();

    		// Declare a new statement

    		// Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
    		String name = request.getParameter("email");
    		String password = request.getParameter("password");
    		// Make new variables for the result
    		String dbName = null;
    		String dbPassword = null;
    		System.out.println("name" + name);
    		System.out.println("password" + password);
    		// Generate a SQL query

    		String query = String.format("SELECT email, password, ccId, id from customers where email=? or password=? limit 20");
    		PreparedStatement statement = dbCon.prepareStatement(query);
    		statement.setString(1, name);
    		statement.setString(2, password);


    		// Perform the query
    		rs = statement.executeQuery();
    		while(rs.next()) {
            	dbName = rs.getString("email");
            	dbPassword = rs.getString("password");
            }
            boolean passwordSuccess = new StrongPasswordEncryptor().checkPassword(password,dbPassword);
            // Give a result status
            if (name.equals(dbName) && passwordSuccess) {
            	request.getSession().setAttribute("user", new User(name));
            	System.out.println("user is here" + request.getSession().getAttribute("user"));
            	JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                System.out.println("it works!!!");
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

            
        } catch (SQLException e) {
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.toString());
            response.getWriter().write(responseJsonObject.toString());
        	
        } catch(Exception ex){
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", ex.toString());
            response.getWriter().write(responseJsonObject.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}