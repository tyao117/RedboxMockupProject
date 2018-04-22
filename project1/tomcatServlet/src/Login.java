import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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
@WebServlet(name = "FormServlet", urlPatterns = "/login")
public class Login extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        out.println("<html><head><title>MovieDB: Found Records</title></head>");

        // Building page body
        out.println("<body><h1>MovieDB: Found Records</h1>");


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String name = request.getParameter("email");
            String password = request.getParameter("password");
            // Make new variables for the result
            String dbName = null;
            String dbPassword = null;
            // Generate a SQL query
            String query = String.format("SELECT email, password from customers where email='%s' and password='%s limit 20", name, password);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            
            //Check if you have a login
            while(rs.next()) {
            	dbName = rs.getString("email");
            	dbName = rs.getString("password");
            }

            // Give a result status
            if (name.equals(dbName) && password.equals(dbPassword)) {
            	out.println("You have successfully logged in!");
            } else {
            	RequestDispatcher rd = request.getRequestDispatcher("index.html");
            	rd.include(request, response);
            }

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
        	
            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDB: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            out.println("name");
            out.println("password");
            return;
        }
        out.close();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	doGet(request, response);
    }
}
