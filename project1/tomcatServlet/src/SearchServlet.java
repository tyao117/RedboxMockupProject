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
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
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
		
		// Retrieve parameter id from url request.
		String title = request.getParameter("movie");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star_name = request.getParameter("star");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
//				if (year.isEmpty() || !NumberUtils.isNumberic(year))
//					throw new IOException();
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "success");
				//if (!title.isEmpty())
					jsonObject.addProperty("movie_title", title);
				//if (!year.isEmpty())
					jsonObject.addProperty("movie_year", year);
				//if (!director.isEmpty())
					jsonObject.addProperty("director", director);
				//if (!star_name.isEmpty())
					jsonObject.addProperty("star_name", star_name);
			
            // write JSON string to output
            out.write(jsonObject.toString());
            System.out.println("sending it out");
            // set response status to 200 (OK)
            response.setStatus(200);

		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}
