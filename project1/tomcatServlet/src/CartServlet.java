import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

//Declaring a WebServlet called CartServlet, which maps to url "/api/cart"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(); // Get instance of current session
		//ArrayList<String> cartItems = (ArrayList<String>) session.getAttribute("cartItems"); // Get everything that was in cart from previous sessions
		JsonArray cartItems = (JsonArray) session.getAttribute("cartItems");
		
		
		//If "cartItems" is not found, then it is a new user, then create a new ArrayList
		if(cartItems == null) {
			cartItems = new JsonArray();
			session.setAttribute("cartItems", cartItems);
		}
		
		String newItem = request.getParameter("newItem");
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", newItem);
		
		//This is to prevent multiple clients/requests from altering cartItems at the same time. We lock the ArrayList while updating it.
		synchronized(cartItems) {
			if(jsonObject != null) {
				cartItems.add(jsonObject);
			}
		}
		
        // write JSON string to output
        out.write(cartItems.toString());
        // set response status to 200 (OK)
        response.setStatus(200);
	}
	
}
