import javax.annotation.Resource;
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
import java.util.Objects;


// Declaring a WebServlet called AddStarServlet, which maps to url "/api/add_star"
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add_star")
public class AddStarServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type
		
		// Retrieve parameter id from url request.
		String star = request.getParameter("star_name");
		String birthYear = request.getParameter("star_birthyear");

        star = Objects.toString(star, "");
        birthYear = Objects.toString(birthYear, "");
        //rating = Objects.toString(rating, "");
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
            Connection dbCon = dataSource.getConnection();
	 		String query = "INSERT INTO stars (id , name, birthYear) "
	 				+ "VALUES (?, ?, ?)";

            java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            
            String maxIdQuery =  "SELECT max(id) from stars;";
            java.sql.PreparedStatement maxIdStatement = dbCon.prepareStatement(maxIdQuery);
			ResultSet resultSet = maxIdStatement.executeQuery();
			while (resultSet.next()) {
				String maxID = resultSet.getString("max(id)");
				String s_maxID = maxID.substring(2);
				Integer i_maxID = Integer.parseInt(s_maxID);
				i_maxID ++;
				s_maxID = Integer.toString(i_maxID);
				s_maxID = "nm" + s_maxID;
			
            preparedStatement.setString(1, s_maxID);
            preparedStatement.setString(2, star);
            preparedStatement.setString(3, birthYear);
            }

            preparedStatement.executeUpdate();
            
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "Successful insertion. ");
		    response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}
	
	public boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  

}
