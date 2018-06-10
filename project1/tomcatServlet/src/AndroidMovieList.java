import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
@WebServlet(name = "AndroidMovieServlet", urlPatterns = "/api/android-movielist")
public class AndroidMovieList extends HttpServlet {
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
		String title = request.getParameter("movie_title");
		String genre = request.getParameter("genre");
		String orderBy = request.getParameter("order_by");
		String frmSearch = request.getParameter("s");
		
		String terms = returnQueryString(title);
		if (title != null && frmSearch != null)
			title = "%" + title;
		title = (title != null) ? title : "";
		
		System.out.println("AndroidTitleTemp = "+ title +", terms = " + terms);

		System.out.println("going to AndroidmovieServlet");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			Context initCtx = new InitialContext();
			
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			if (envCtx == null)
				throw new Exception("envCtx is NULL");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
			
			// Get a connection from dataSource
			Connection dbcon = ds.getConnection();
			String query = "";
			query = ("SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name separator ',') AS genres, GROUP_CONCAT(DISTINCT s.name, ',', s.id separator ',') AS starNameID, r.rating \r\n" + 
					"FROM movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim, ratings r \r\n" + 
					"WHERE m.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND m.id = gim.movieId AND m.id = r.movieId \r\n" + 
					"AND ( m.title LIKE ? or match (m.title) against ( ? in boolean mode) or ed(m.title, ?) <= 3) \r\n" + 
					"GROUP BY m.id, m.title, m.year, m.director, r.rating \r\n" + 
					"order by (ed(?, m.title)) asc \r\n" +
					"LIMIT 250"); 

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

						// Set the parameter represented by "?" in the query to the id we get from url,
						// num 1 indicates the first "?" in the query
			title = title + "%";
			statement.setString(1, title);
			statement.setString(2, terms);
			statement.setString(3, title);
			statement.setString(4, title);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			//statement.setString(1, id);

			// Perform the query
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			// Iterate through each row of rs
			while (rs.next()) {
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movieRating = rs.getString("rating");
				genre = rs.getString("genres");
				String stars = rs.getString("starNameID");
				
				String[] individualStar = stars.split(",");
				List<String> starList = new ArrayList<String>(Arrays.asList(individualStar));
				JsonArray starNameArray = new JsonArray();
				for (int i = 0; i < starList.size(); i = i+2) {
					starNameArray.add(starList.get(i));
				}
				JsonArray starIDArray = new JsonArray();
				for (int i = 1; i < starList.size(); i = i+2) {
					starIDArray.add(starList.get(i));
				}
				
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();	
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_rating", movieRating);
				jsonObject.addProperty("movie_genre", genre);
				jsonObject.add("star_name_array", starNameArray);
				jsonObject.add("star_id_array", starIDArray);
				jsonArray.add(jsonObject);
			}
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
			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }
	
	private String returnQueryString (String term)
    {
    	String queryString = "";
    	String[] keywordArray = term.split(" ");
    	//if keyword has more than one word
    	if (keywordArray.length != 1) {
    		for (String s: keywordArray) {
    			String newString = "";
    			if (!s.substring(0).equals("-") && !s.substring(0).equals("+")) {
    				newString += "+" + s;
    			} else {
    				newString += s;
    			}

    			if (!s.substring(s.length()-1).equals("*")) {
    				newString += "*";
    			}
    			queryString += newString + " ";	
    		}
    	} else {
    		queryString += term.trim() + "*";
    	}
    	return queryString;
    }

}