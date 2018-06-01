import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jasypt.util.password.StrongPasswordEncryptor;
/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/api/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        // Getting the reCAPTCHA response
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
        	RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	JsonObject responseJsonObject = new JsonObject();
        	responseJsonObject.addProperty("status", "fail");
        	responseJsonObject.addProperty("message", e.getMessage());
        	out.write(responseJsonObject.toString());
        	out.close();
        	return;
        }
        if (gRecaptchaResponse == null)
        {
        	JsonObject responseJsonObject = new JsonObject();
        	responseJsonObject.addProperty("status", "fail");
        	responseJsonObject.addProperty("message", "must click on Recaptcha");
        	out.write(responseJsonObject.toString());
        	out.close();
        	return;
        }
        
        try {
        	
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String name = request.getParameter("email");
            String password = request.getParameter("password");
            // Make new variables for the result
            String dbName = null;
            String dbPassword = null;
            String dbID = null;
            String dbccid = null;
            // Generate a SQL query
            
            String query = String.format("SELECT email, password, ccId, id from customers where email=? or password=? limit 20");
            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, password);
            
            
            // Perform the query
            ResultSet rs = statement.executeQuery();
            //Check if you have a login
            while(rs.next()) {
            	dbName = rs.getString("email");
            	dbPassword = rs.getString("password");
            	dbID = rs.getString("id");
            	dbccid = rs.getString("ccId");
            }
            boolean passwordSuccess = new StrongPasswordEncryptor().checkPassword(password,dbPassword);
            // Give a result status
            if (name.equals(dbName) && passwordSuccess) {
            	// Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(name));
                request.getSession().setAttribute("customerId", dbID);
                request.getSession().setAttribute("CCID", dbccid);
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

        } catch (Exception ex) {
        	
           System.out.println(ex.toString());
            return;
        }
        out.close();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	doGet(request, response);
    }
}
