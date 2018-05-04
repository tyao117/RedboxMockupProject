import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */


// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SaleServlet", urlPatterns = "/api/insertsale")
public class SalesInsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http PUT
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	
        response.setContentType("application/json");    // Response mime type
        System.out.println("Inside the servlet");
        System.out.println(request.toString());
        String movieId = request.getParameter("movieid");
        System.out.println(movieId);
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        out.close();
    }

}
