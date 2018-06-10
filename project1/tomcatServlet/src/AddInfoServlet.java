

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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

		if (id == "")
			id = null;
		if (star == "")
			star = null;
		if (genre == "")
			genre = null;
        //rating = Objects.toString(rating, "");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			if (envCtx == null)
				throw new Exception("envCtx is NULL");
			dataSource = (DataSource) envCtx.lookup("jdbc/WriteDB");
            Connection dbCon = dataSource.getConnection();
            
            String query = "call add_info(?, ?, ?);";        
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            if (id == null)
            	preparedStatement.setString(1, null);
            else 
            	preparedStatement.setString(1, id);
            if (genre == null)
            	preparedStatement.setString(2, null);
            else 
            	preparedStatement.setString(2, genre);
            if (star == null)
            	preparedStatement.setString(3, null);
            else 
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