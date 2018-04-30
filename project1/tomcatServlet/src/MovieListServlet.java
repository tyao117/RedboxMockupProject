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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "MovieServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
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
		String year = request.getParameter("movie_year");
		String director = request.getParameter("director");
		String star_name = request.getParameter("star_name");
		String genre = request.getParameter("genre");
		String orderBy = request.getParameter("order_by");
		
		title = (title != null) ? title : "";
		year = (year != null) ? year : "";
		director = (director != null) ? director : "";
		star_name = (star_name != null) ? star_name : "";

		System.out.println("going to movieServlet");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			String query = "";
			if (genre != null) {
				System.out.println("going into Genre!!!!!");
				query = "select distinct result.* \n" + 
						"from (select ml.id, ml.title, ml.year, ml.director, ml.rating, group_concat(distinct g.name separator', ') as genre\n" + 
						"from (SELECT distinct m.id, title, year, director, rating\n" + 
						"	from movies m, ratings r\n" + 
						"	where m.id=r.movieId\n" + 
						"	order by m.id\n" + 
						") ml, genres g, genres_in_movies gm\n" + 
						"where g.id=gm.genreId and gm.movieId=ml.id and g.name = '"+ genre +"'\n" + 
						"group by ml.id, ml.title, ml.year, ml.director, ml.rating) as result, genres as g";
				if (orderBy != null)
				{
					query += "\n order by " + orderBy;
				}
			} else {
				query = "select distinct movies.*\n" + 
						"from (select ml.id, ml.title, ml.year, ml.director, ml.rating, group_concat(distinct g.name separator', ') as genre\n" + 
						"from (SELECT distinct m.id, title, year, director, rating\n" + 
						"	from movies m, ratings r\n" + 
						"	where m.id=r.movieId and m.title like '"+title+"%' and m.year like '%"+year+"%' and m.director like '%"+director+"%'\n" + 
						"	order by m.id\n" + 
						") ml, genres g, genres_in_movies gm\n" + 
						"where g.id=gm.genreId and gm.movieId=ml.id \n" + 
						"group by ml.id, ml.title, ml.year, ml.director, ml.rating) as movies, stars as s, stars_in_movies as sm\n" + 
						"where s.id=sm.starId and sm.movieId=movies.id and s.name like '%"+star_name+"%' ";
				if (orderBy != null)
				{
					query += "\n order by " + orderBy;
				}
			}
			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

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
				genre = rs.getString("genre");
				
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();	
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_rating", movieRating);
				jsonObject.addProperty("genre", genre);
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }

}
