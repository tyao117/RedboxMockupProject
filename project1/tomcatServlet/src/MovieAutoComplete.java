import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieAutoCompleteServlet", urlPatterns = "/api/MovieAutoComplete")
public class MovieAutoComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
    	String term = request.getParameter("term");
    	System.out.println(term);
    	if (term.length() < 3) {
    		out.println(new JsonArray().toString());
    		out.close();
    		return;
    	}
    	JsonArray jarray = null;
    	try {
    		// System.out.println("Going here");
    		jarray = getSuggestions(term);
    	}
    	catch (SQLException s) {
    		s.printStackTrace();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	out.println(jarray.toString());
    	out.close();
    	
    }
    private String returnQueryString (String term)
    {
    	String queryString = "";
    	String[] keywordArray = term.split(" ");
    	//if keyword has more than one word
    	if (keywordArray.length != 1)
    	{
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
    
    private JsonArray getSuggestions(String term) throws Exception
    {
    	Context initCtx = new InitialContext();
		
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		// Look up our data source
		if (envCtx == null)
			throw new Exception("envCtx is NULL");
		DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
		
		// Get a connection from dataSource
		Connection dbcon = ds.getConnection();
    	JsonArray suggestions = new JsonArray();
//    	String query = "SELECT title, id FROM movies WHERE (title LIKE ? AND title LIKE ?) OR title = ? ORDER BY title LIMIT 10";
//        //replace whitespace
//         StringTokenizer tokenizer = new StringTokenizer(term);
//         String firstToken = tokenizer.nextToken();
//         term.replaceAll("\\s","%");
//         System.out.println("term=" + term);
//
//         PreparedStatement prepStmnt = dbcon.prepareStatement(query);
//         prepStmnt.setString(1, firstToken+'%');
//         prepStmnt.setString(2, term+'%');
//         prepStmnt.setString(3, term);
    	String queryString = returnQueryString(term);
    	term = "%" + term + "%";
//    	String query = "Select * from movies where match (title) against (? in boolean mode) limit 10;";
    	String query = "select id, title from movies where match (title) against (? in boolean mode) OR (title LIKE ?) OR ed(title, ?) <= 3  order by (ed(?, title)) asc LIMIT 10 ";
    	PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, queryString);
		statement.setString(2, term);
		statement.setString(3, term);
		statement.setString(4, term);
    	ResultSet rs = statement.executeQuery();

    	while(rs.next())
    	{
    		String id = rs.getString("id");
    		String title = rs.getString("title");
    		suggestions.add(generateJsonObject(id, title, "single-movie"));
    	}
    	rs.close();
    	statement.close();
    	dbcon.close();
    	return suggestions;

    }
    /*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "movies", "movie_id": nm000011 }
	 * }
	 * 
	 */
    private static JsonObject generateJsonObject(String id, String title, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", title);
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("movie_id", id);
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
    
}
