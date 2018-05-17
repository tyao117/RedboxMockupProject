

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class AddMovieServlet
 */
@WebServlet(name = "AddInfoServlet", urlPatterns = "/api/add_info")
public class AddInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
response.setContentType("application/json"); // Response mime type
		
		// Retrieve parameter id from url request.
		String id = request.getParameter("movie_id");
		String star = request.getParameter("movie_star");
		String genre = request.getParameter("movie_genre");
		//String rating = request.getParameter("rating");

        id = id.isEmpty() ? "NULL" : id;
        genre = genre.isEmpty() ? "NULL" : genre;
        star = star.isEmpty() ? "NULL" : star;
        
        //rating = Objects.toString(rating, "");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
            Connection dbCon = dataSource.getConnection();
            String query = "call add_info(?, ?, ?)";        
            java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, star);
            preparedStatement.executeUpdate();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "Successful insertion. ");
		    response.getWriter().write(jsonObject.toString());
		    response.setStatus(200);
			
		} catch (SQLException ex) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", ex.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
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