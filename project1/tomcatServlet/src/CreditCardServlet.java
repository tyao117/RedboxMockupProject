import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "CreditCardServlet", urlPatterns = "/api/creditcard")
public class CreditCardServlet extends HttpServlet {
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
        try {
        	Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			if (envCtx == null)
				throw new Exception("envCtx is NULL");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            // Create a new connection to database
            Connection dbCon = ds.getConnection();

            // Get the current session
            HttpSession session = request.getSession();
            
            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String id = request.getParameter("id");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String expiration = request.getParameter("expiration");
            String dbId = "";
            String dbfirstName = "";
            String dblastName = "";
            String dbexpiration = "";
            String sessionId = request.getSession().getAttribute("CCID").toString();

            // Generate a SQL query
            String query = String.format("SELECT * "
            							+ "FROM moviedb.creditcards "
            							+ "WHERE id=? "
            							+ "AND firstName=? "
            							+ "AND lastName=? "
            							+ "AND expiration=? ");
            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);
            // Perform the query
            ResultSet rs = statement.executeQuery();
            //Check if you have a login
            while(rs.next()) {
            	dbId = rs.getString("id");
            	dbfirstName = rs.getString("firstName");
            	dblastName = rs.getString("lastName");
            	dbexpiration = rs.getString("expiration");
            }
            JsonObject JsonObject = new JsonObject();
            // Give a result status
            if (id.equals(dbId) && firstName.equals(dbfirstName)
            		&& lastName.equals(dblastName) && expiration.equals(dbexpiration)
            		&& sessionId.equals(id)) {
            	// Credit success:
            	String cusID = (String)request.getSession().getAttribute("customerId");
                JsonObject.addProperty("status", "success");
                JsonObject.addProperty("message", "success");
                JsonObject.addProperty("id", cusID);
            } else {
            	// Credit card fail
                JsonObject.addProperty("status", "fail");
                if (!id.equals(sessionId)) {
                	JsonObject.addProperty("message", "session is not correct");
                }
                if (!id.equals(dbId)) {
                	JsonObject.addProperty("message", "id is not correct");
                }
                if (!firstName.equals(dbfirstName)) {
                	JsonObject.addProperty("message", "firstName does not match");
                }
                if (!lastName.equals(dblastName)) {
                	JsonObject.addProperty("message", "lastName does not match");
                }
                if (!dbexpiration.equals(dbexpiration)) {
                	JsonObject.addProperty("message", "expiration does not work");
                }
            }
            response.getWriter().write(JsonObject.toString());
            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (SQLException ex) {
			ex.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        } catch (Exception ex) {
            // Output Error Massage to html
        	System.out.println(ex.getStackTrace());
            JsonObject object = new JsonObject();
            object.addProperty("status", "fail");
            object.addProperty("message", "dbConnection or something else");
            out.write(object.toString());
            response.setStatus(500);
        }
        out.close();
    }
}
