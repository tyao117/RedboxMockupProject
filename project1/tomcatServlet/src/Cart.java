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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class Cart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	System.out.println("Going here");
    	response.setContentType("application/json");    // Response mime type
    	
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String name;
        
        try {        	
        	name = (String) request.getSession().getAttribute("user");
            // Give a result status
            if ( name != null) {
            	// Login success:

                // set this user into the session
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                out.write(responseJsonObject.toString());
            } else {
            	// Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                out.write(responseJsonObject.toString());
            }

            // Close all structures


        } catch (Exception ex) {
        	
            // Output Error Massage to html
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "exception");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        out.close();
    }

}
