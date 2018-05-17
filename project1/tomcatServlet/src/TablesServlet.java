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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "TablesServlet", urlPatterns = "/api/tablelist")
public class TablesServlet extends HttpServlet {
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

	
		System.out.println("going to tableServlet");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			String query = "show tables";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Perform the query
			ResultSet rs = statement.executeQuery();
			ArrayList<String> lst = new ArrayList<String>();
			JsonArray jsonArray = new JsonArray();
			// Iterate through each row of rs
			while (rs.next()) {
				lst.add(rs.getString("Tables_in_moviedb"));
			}
			for (String x : lst)
			{
				query = "describe ?";
				System.out.println("x = " + x);
				statement = dbcon.prepareStatement(query);
				statement.setString(1, x);
				System.out.println(statement);
				rs = statement.executeQuery();
				JsonObject jsonObject = new JsonObject();
				while (rs.next()) {
					System.out.println(rs.getMetaData());
					String field = rs.getString("Field");
					String type = rs.getString("Type");
					jsonObject.addProperty("Field", field);
					jsonObject.addProperty("Type", type);
				}
				jsonArray.add(jsonObject);
			}
			// Create a JsonObject based on the data we retrieve from rs
			

			// write JSON string to output
			out.write(jsonArray.toString());
			// set response status to 200 (OK)
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }

}