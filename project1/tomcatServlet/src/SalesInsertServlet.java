import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SaleServlet", urlPatterns = "/api/insertsale")
public class SalesInsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http PUT
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	HttpSession session = request.getSession();
        response.setContentType("application/json");    // Response mime type
        System.out.println("Inside the servlet");
        System.out.println(request.toString());
        String data = request.getParameter("id");
        System.out.println("id=" + data);
        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        Connection dbCon = null;
    	Statement statement = null;
        // Output stream to STDOUT
        try {
        	dbCon = dbCon = dataSource.getConnection();
        	statement = dbCon.createStatement();
        	String query = "";
        	HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("previousItems");
        	synchronized (cart) {
        		Iterator<Entry<String, Integer>> it = cart.entrySet().iterator();
        		while (it.hasNext()) {
        			Map.Entry pair = (Map.Entry)it.next();
        			int size = (Integer)pair.getValue();
        			for (int i = 0; i < size; i++) {
        				System.out.println((String)pair.getKey());
        				query = "INSERT INTO sales " + "VALUES (NULL," + data + ", '" + ((String)pair.getKey()) + "', CURDATE())";
        			}
        			it.remove();
        		}
        	}
        	statement.executeUpdate(query);
        	statement.close();
        	jsonObject.addProperty("status", "sucess");
        	// Printing out a temporary Json String
        	
        	
        } catch (SQLException se) {
        	se.printStackTrace();
        	jsonObject.addProperty("status" , "failure");
        	jsonObject.addProperty("message", "SQL Error");
    	} catch (Exception ex) {
    		jsonObject.addProperty("status" , "failure");
        	jsonObject.addProperty("message", "unknown");
        } finally {
        	try {
        		if (statement !=null)
        			statement.close();
        	} catch (SQLException se) {
        		
        	}
        	try {
        		if (dbCon != null)
        			dbCon.close();
        	} catch (SQLException se) {
        		se.printStackTrace();
        	}
        }
        out.write(jsonObject.toString());
        out.close();
    }

}
