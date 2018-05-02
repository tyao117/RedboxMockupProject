import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called CartServlet, which maps to url "/form"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class Cart extends HttpServlet {
	private static final long serialVersionUID = 2L;

//    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
    
    // Use http GET
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	response.setContentType("application/json");    // Response mime type
    	
    	String id = request.getParameter("id");
    	String value = request.getParameter("value");
    	System.out.println("value" + value);
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();
        HttpSession session = request.getSession();
        
        
        HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("previousItems");
     // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (cart == null) {
            cart = new HashMap<String, Integer>();
            session.setAttribute("previousItems", cart);
        }
        try {        	
        	 synchronized (cart) {
        		 if (cart != null) {
        			 if (value != null) {
        				 if (cart.get(id) != null) {
        					 int x = cart.get(id);
        					 cart.put(id, cart.get(id) + Integer.parseInt(value));
        				 } else {
        					 cart.put(id, 1);
        				 }
        			 }
        			 Iterator<Entry<String, Integer>> it = cart.entrySet().iterator();
                	 while (it.hasNext()) {
                		 Map.Entry pair = (Map.Entry)it.next();
                		 if ((Integer) pair.getValue() <= 0)
                			 it.remove();
                		 else {
                		 JsonObject responseJsonObject = new JsonObject();
                		 responseJsonObject.addProperty("movie_id", pair.getKey().toString());
                		 responseJsonObject.addProperty("movie_quantity", pair.getValue().toString());
                		 jsonArray.add(responseJsonObject);
                		  // avoids a ConcurrentModificationException
                		 }
                	 }
        		 }
        	 }
        	 session.setAttribute("previousItems", cart);
        	 out.write(jsonArray.toString());
        	 // Close all structures
        } catch (Exception ex) {
        	
            // Output Error Massage to html
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "exception");
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
            return;
        }
        out.close();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	doPost(request, response);
    }
}
