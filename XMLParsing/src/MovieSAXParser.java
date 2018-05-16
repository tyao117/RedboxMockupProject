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
	


    public MovieSAXParser() {
    	movieXML = new File("stanford-movies/mains243.xml");
        mId = "";
        mName = "";
        mYear = "";
        mDir = "";
        movies = new HashMap<String, String>();
        movieGenres = new HashSet<String>();
        genres = new HashMap<String, Integer>();
        movieIdMapping = new HashMap<String, String>();
        genres_in_movies = new HashSet<String>();
    }

    public void runMovieParser(Statement statement) {       	

            this.statement = statement;
            parseDocument();

    }
    
    public HashMap<String,String> getMovieMapping() {
    	return movieIdMapping;
    }

    private void parseDocument() {
//        System.out.println("set up parsing");
        createHashes();
        createTempTable();

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
    
    public void createTempTable() {
//    	System.out.println("select max(id) as id from movies");
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
    			writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("movies.txt"), "utf-8"));
    			writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres.txt"), "utf-8"));
    			writer3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres_in_movies.txt"), "utf-8"));
    		} catch (UnsupportedEncodingException | FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	} else if (qName.equalsIgnoreCase("film")) {
//            System.out.println("Found a film");
        	//Clear movie data for next movie by same director
        	mId = null;
        	mName = null;
        	mYear = null;
        	mDir = null;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
        	String movie = mName + "|" + mYear + "|" + mDir;
    		myMap<String, String> map = new myMap<String, String>(movies);
    		
        	if(map.add(movie, mId)) // If successfully add the new movie, write movie to csv file and add the movie_id mapping to the movieIds hashMap
        	{
        		String oMovie = mId + "|" + movie + "\n";
        		movieIdMapping.put(xmlId, mId);
//        		System.out.println("Write " + oMovie + " to movies.txt file");
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
        		temp = temp.trim();
        		
        		if(temp.isEmpty()) {
        			System.out.println("no genre avaliable");
        			continue;
        		}
        		
        		myMap<String, Integer> map2 = new myMap<String, Integer>(genres);
        		if(map2.add(temp, maxGId+1))
        		{
        			++maxGId;
        			String newGenre = Integer.toString(genres.get(temp)) + "|" + temp + "\n";
//        			System.out.println("Write " + newGenre + " to genres.txt file");
        			try {
						writer2.write(newGenre);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		
        		String gim = genres.get(temp) + "|" + movies.get(movie);// reminder: movies.get(movie) returns the movie_id
        		if(genres_in_movies.add(gim)) {
//        			System.out.println("Write " + gim + " to genres_in_movies.txt file");
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
        	//move id increment to the above if-statement if time allows it.
        	maxMId += 1;
        	mId = Integer.toString(maxMId);
        	
        	if(mId.length() == 6)
        		mId = "tt0".concat(mId);
        	else
        		mId = "tt".concat(mId);
        	
            xmlId = tempVal;
//            System.out.println(mId + " " + xmlId);
        } else if (qName.equalsIgnoreCase("t")) {
            mName = tempVal;
        } else if (qName.equalsIgnoreCase("year")) {
            mYear = tempVal;
        } else if (qName.equalsIgnoreCase("dirn") && mDir == null) {
        	mDir = tempVal;
        } else if (qName.equalsIgnoreCase("cat")) {
        	movieGenres.add(tempVal);
        } else if (qName.equalsIgnoreCase("movies")) {
        	try {
				writer1.close();
				writer2.close();
				writer3.close();

				/*
				String query = "load data local infile 'movies.txt' into table movie_id_maps "
					  + "columns terminated by '|' "
					  + "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres.txt' into table movie_id_maps "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres_in_movies.txt' into table movie_id_maps "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}






//This following was originally used to condense the amount of genres needed to be added. May re-implement if time allows it.

//public void insertGenre(String genre) {
//	System.out.println("insert ignore into genres (name) values (" + genre + ");");
//}
//
//public void insertMovie() {
//	System.out.println("insert ignore into movies values (" + mId + ", " + mName + ", " + mYear + ", " + mDir + ");");
//}
//
//public void insertGenreInMovie(String genre) {
//	System.out.println("insert ignore into genres_in_movies values ((select id from genres where name =" + genre + "), " + mId +");");
//}

//if(temp.equalsIgnoreCase("comd") | temp.equalsIgnoreCase("comdx")) {
//insertGenre("comedy");
//insertMovie();
//insertGenreInMovie("comedy");           		
//} else if(temp.equalsIgnoreCase("epic")) {
//insertGenre("epic");
//insertMovie();
//insertGenreInMovie("epic");           		
//} else if(temp.equalsIgnoreCase("s.f.") || temp.equalsIgnoreCase("Scfi")  || temp.equalsIgnoreCase("Sxfi")|| temp.equalsIgnoreCase("Scif")) {
//insertGenre("Sci-Fi");
//insertMovie();
//insertGenreInMovie("Sci-Fi");           		
//} else if(temp.equalsIgnoreCase("stage musical") || temp.equalsIgnoreCase("Muscl") || temp.equalsIgnoreCase("Musc")|| temp.equalsIgnoreCase("Muusc")) {
//insertGenre("Musical");
//insertMovie();
//insertGenreInMovie("Musical");           		
//} else if(temp.equalsIgnoreCase("myst")) {
//insertGenre("Mystery");
//insertMovie();
//insertGenreInMovie("Mystery");           		
//} else if(temp.equalsIgnoreCase("susp")) {
//insertGenre("Suspense");
//insertMovie();
//insertGenreInMovie("Suspense");           		
//} else if(temp.equalsIgnoreCase("avga") || temp.equalsIgnoreCase("Avant Garde")) {
//insertGenre("Avant-Garde");
//insertMovie();
//insertGenreInMovie("Avant-Garde");           		
//} else if(temp.equalsIgnoreCase("dram") || temp.equalsIgnoreCase("draam") || temp.equalsIgnoreCase("dramn") || temp.equalsIgnoreCase("drama") || temp.equalsIgnoreCase("dramd") || temp.equalsIgnoreCase("Dram>")) {
//insertGenre("Drama");
//insertMovie();
//insertGenreInMovie("Drama");           		
//} else if(temp.equalsIgnoreCase("actn") || temp.equalsIgnoreCase("act") || temp.equalsIgnoreCase("axtn")) {
//insertGenre("Action");
//insertMovie();
//insertGenreInMovie("Action");           		
//} else if(temp.equalsIgnoreCase("Hor") || temp.equalsIgnoreCase("Horr")) {
//insertGenre("Horror");
//insertMovie();
//insertGenreInMovie("Horror");           		
//} else if(temp.equalsIgnoreCase("Expm")) {
//insertGenre("Experimental");
//insertMovie();
//insertGenreInMovie("Experimental");           		
//} else if(temp.equalsIgnoreCase("verite")) {
//insertGenre("Verite");
//insertMovie();
//insertGenreInMovie("Verite");           		
//} else if(temp.equalsIgnoreCase("Advt")) {
//insertGenre("Adventure");
//insertMovie();
//insertGenreInMovie("Adventure");           		
//} else if(temp.equalsIgnoreCase("Psyc")) {
//insertGenre("Psychological");
//insertMovie();
//insertGenreInMovie("Psychological");           		
//} else if(temp.equalsIgnoreCase("porn") || temp.equalsIgnoreCase("porb")) {
//insertGenre("Adult");
//insertMovie();
//insertGenreInMovie("Adult");           		
//} else if(temp.equalsIgnoreCase("fant")) {
//insertGenre("Fantasy");
//insertMovie();
//insertGenreInMovie("Fantasy");           		
//} else if(temp.equalsIgnoreCase("Hist")) {
//insertGenre("Historical");
//insertMovie();
//insertGenreInMovie("Historical");           		
//} else if(temp.equalsIgnoreCase("romt") || temp.equalsIgnoreCase("ront")) {
//insertGenre("Romantic");
//insertMovie();
//insertGenreInMovie("Romantic");           		
//} else if(temp.equalsIgnoreCase("docu") || temp.equalsIgnoreCase("ducu") || temp.equalsIgnoreCase("dicu") || temp.equalsIgnoreCase("duco")) {
//insertGenre("Documentary");
//insertMovie();
//insertGenreInMovie("Documentary");           		
//} else if(temp.equalsIgnoreCase("fam") || temp.equalsIgnoreCase("faml")) {
//insertGenre("Family");
//insertMovie();
//insertGenreInMovie("Family");           		
//} else if(temp.equalsIgnoreCase("Cult")) {
//insertGenre("Cult");
//insertMovie();
//insertGenreInMovie("Cult");           		
//} else if(temp.equalsIgnoreCase("West1") || temp.equalsIgnoreCase("West")) {
//insertGenre("Western");
//insertMovie();
//insertGenreInMovie("Western");           		
//} else if(temp.equalsIgnoreCase("sport")) {
//insertGenre("Sport");
//insertMovie();
//insertGenreInMovie("Sport");           		
//} else if(temp.equalsIgnoreCase("cart")) {
//insertGenre("Cartoon");
//insertMovie();
//insertGenreInMovie("Cartoon");           		
//} else if(temp.equalsIgnoreCase("biop")) {
//insertGenre("Biography");
//insertMovie();
//insertGenreInMovie("Biography");           		
//} else if(temp.equalsIgnoreCase("noir")) {
//insertGenre("Noir");
//insertMovie();
//insertGenreInMovie("Noir");           		
//} else {
//System.out.println("Invalid genre: " + temp + ", movie is not added");
//}