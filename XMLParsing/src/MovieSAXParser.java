import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieSAXParser extends DefaultHandler {

	//get the xml
	private File movieXML;
	//the data that we are interested in
	private String mId;
	private String mName;
	private String mYear;
	private String mDir;
	private String tempVal;
	private Statement statement;
	private int maxMId;
	private int maxGId;
	private String xmlId;
	
	private HashMap<String, String> movies;
	private HashSet<String> movieGenres;
	private HashMap<String, Integer> genres;
	private HashMap<String, String> movieIdMapping;
	private HashSet<String> genres_in_movies;
	
	private Writer writer1;
	private Writer writer2;
	private Writer writer3;
	


    public MovieSAXParser(Statement statement) {
    	movieXML = new File("stanford-movies/mains243.xml");
        mId = "";
        mName = "";
        mYear = "";
        mDir = "";
        this.statement = statement;
        movies = new HashMap<String, String>();
        movieGenres = new HashSet<String>();
        genres = new HashMap<String, Integer>();
        movieIdMapping = new HashMap<String, String>();
        genres_in_movies = new HashSet<String>();
    }

    public void runMovieParser() {       	
            parseDocument();

    }
    
    public HashMap<String,String> getMovieMapping() {
    	return movieIdMapping;
    }

    private void parseDocument() {
//        System.out.println("set up parsing");
        createHashes();
        getMaxIds();

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(movieXML, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    public void createHashes()
    {
    	try {
    		//create movies hashMap and movieIds hashMap
        	String query = "select * from movies";
    		ResultSet rs = statement.executeQuery(query);
    		while(rs.next()) {
    			String movie = "";
    			String id = rs.getString("id");
    			movie = rs.getString("title") + "|" + rs.getString("year") + "|" + rs.getString("director");
    			movies.put(movie, id);
    			movieIdMapping.put(id, id);
    		}
    		
    		//create genres hashMap
    		query = "select * from genres";
    		rs = statement.executeQuery(query);
    		while(rs.next()) {
    			genres.put(rs.getString("name"), rs.getInt("id"));
    		}
    		
    		//create genres_in_movies hashSet
    		query = "select * from genres_in_movies";
    		rs = statement.executeQuery(query);
    		while(rs.next()) {
    			genres_in_movies.add(rs.getString("genreId") + "|" + rs.getString("movieId"));
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block 
    		e.printStackTrace();
    	}
    }
    
    public void getMaxIds() {
    	try {
        	String query = "select max(id) as id from movies;";
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			String max = rs.getString("id");
			max = max.substring(2);
			maxMId = Integer.parseInt(max);
			
			query = "select max(id) as id from genres;";
			rs = statement.executeQuery(query);
			rs.next();
			max = rs.getString("id");
			maxGId = Integer.parseInt(max);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
    	if (qName.equalsIgnoreCase("movies")) {
            try {
    			writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("movies.txt"), "ISO-8859-1"));
    			writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres.txt"), "ISO-8859-1"));
    			writer3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres_in_movies.txt"), "ISO-8859-1"));
    		} catch (UnsupportedEncodingException | FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	} else if (qName.equalsIgnoreCase("film")) {
        	//Clear movie data for next movie by same director
        	mId = null;
        	mName = null;
        	mYear = null;
        	mDir = null;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
    }

    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
        	if (xmlId == null || mName == null || mYear == null || mDir == null) {
        		System.out.println("This table does not take null values. "
        				+ "The following movie was not added id: " + xmlId + ", title: " + mName + ", year: " + mYear + ", director: " + mDir);
        		return;
        	}
        	
        	//move id increment to the above if-statement if time allows it.
        	maxMId += 1;
        	mId = Integer.toString(maxMId);
        	
        	if(mId.length() == 6)
        		mId = "tt0".concat(mId);
        	else
        		mId = "tt".concat(mId);
        	String movie = mName + "|" + mYear + "|" + mDir;
    		myMap<String, String> map = new myMap<String, String>(movies);
    		
        	if(map.add(movie, mId)) // If successfully add the new movie, write movie to csv file and add the movie_id mapping to the movieIds hashMap
        	{
        		String oMovie = mId + "|" + movie + "\n";
        		movieIdMapping.put(xmlId, mId);
        		try {
					writer1.write(oMovie);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	} else { // else change the key of the movie_id mapping to reflect the fact that the movie from the the xml already exists in database
        		movieIdMapping.put(xmlId, movieIdMapping.remove(movies.get(movie)));
        	}
        	
        	for(String temp : movieGenres) {
        		
        		if(temp.isEmpty()) {
        			System.out.println("no genre avaliable");
        			continue;
        		}
        		
        		myMap<String, Integer> map2 = new myMap<String, Integer>(genres);
        		if(map2.add(temp, maxGId+1))
        		{
        			++maxGId;
        			String newGenre = Integer.toString(genres.get(temp)) + "|" + temp + "\n";
        			try {
						writer2.write(newGenre);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		
        		String gim = genres.get(temp) + "|" + movies.get(movie);// reminder: movies.get(movie) returns the movie_id
        		if(genres_in_movies.add(gim)) {
        			gim += "\n";
        			try {
						writer3.write(gim);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        	
        	movieGenres.clear();
        } else if (qName.equalsIgnoreCase("fid")) {
        	xmlId = (tempVal.length() > 0) ? tempVal : null;
//            System.out.println(mId + " " + xmlId);
        } else if (qName.equalsIgnoreCase("t")) {
            mName = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("year")) {
            mYear = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("dirn") && mDir == null) {
        	mDir = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("cat")) {
        	if (tempVal.length() == 0) {
        		System.out.println("one of genres was empty for " + xmlId + ", " + mName + ", " + mYear + ", " + mDir);
        	}
        	movieGenres.add(tempVal);
        } else if (qName.equalsIgnoreCase("movies")) {
        	try {
				writer1.close();
				writer2.close();
				writer3.close();

				
				String query = "load data local infile 'movies.txt' into table movies "
					  + "columns terminated by '|' "
					  + "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres.txt' into table genres "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres_in_movies.txt' into table genres_in_movies "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}