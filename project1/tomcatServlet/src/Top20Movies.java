

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/top20movies")
public class Top20Movies extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Top20Movies() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        out.print("<link rel=\"stylesheet\" text=\"text/css\" href=\"" + request.getContextPath() + "/css/style.css\"/></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "select top20.title, top20.year, top20.director, top20.rating, group_concat(distinct s.name separator', ') as stars, group_concat(distinct g.name separator', ') as genre\n" + 
        				"from (SELECT distinct m.id, title, year, director, rating\n" + 
        				"from movies m, ratings r\n" + 
        				"where m.id=r.movieId\n" + 
        				"order by r.rating desc\n" + 
        				"limit 20) top20, stars s, stars_in_movies sm, genres g, genres_in_movies gm\n" + 
        				"where s.id=sm.starId and sm.movieId=top20.id and g.id=gm.genreId and gm.movieId=top20.id\n" + 
        				"group by top20.title, top20.year, top20.director, top20.rating\n" + 
        				"order by top20.rating desc";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1>MovieDB Top 20 Movies</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>title</td>");
        		out.println("<td>year</td>");
        		out.println("<td>director</td>");
        		out.println("<td>rating</td>");
        		out.println("<td>stars</td>");
        		out.println("<td>genre</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String title = resultSet.getString("title");
        			String year = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String rating = resultSet.getString("rating");
        			String stars = resultSet.getString("stars");
        			String genre = resultSet.getString("genre");
        					
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("<td>" + stars + "</td>");
        			out.println("<td>" + genre + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
