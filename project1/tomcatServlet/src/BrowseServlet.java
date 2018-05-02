import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Declaring a WebServlet called BrowseServlet, which maps to url "/api/browse"
@WebServlet(name = "BrowseServlet", urlPatterns = "/api/browse")
public class BrowseServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

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
		//response.setContentType("text/html");
		// Retrieve parameter id from url request.
		String genre = request.getParameter("genre");

		System.out.println("going to movieServlet");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			String query = "";
			
			query = "select name from genres";
			
			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);
			// Perform the query
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			// Iterate through each row of rs
			while (rs.next()) {
				genre = rs.getString("genre");
				
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_genre", genre);
				jsonArray.add(jsonObject);
			}
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
}
