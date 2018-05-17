import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastSAXParser extends DefaultHandler {
	//get xml
		private File castsXML;
		private String sId;
		private String actor;
		private String mId;
		private String movieId;
		private String tempVal;
		private Statement statement;
		private Writer writer;
		
		private HashSet<String> casts;
		private HashMap<String, String> stars;
		private HashMap<String, String> movie;
		
		public CastSAXParser(Statement statement, HashMap<String, String> stars, HashMap<String, String> movie) {
			castsXML = new File("stanford-movies/casts124.xml");
			sId = null;
			mId = null;
			this.statement = statement;
			casts = new HashSet<String>();
			this.stars = stars;
			this.movie = movie;
		}
		
		public void runCastsParser() {
			parseDocument();
		}

		private void parseDocument() {
	        createHashes();
	        
	        //get a factory
	        SAXParserFactory spf = SAXParserFactory.newInstance();
	        try {

	            //get a new instance of parser
	            SAXParser sp = spf.newSAXParser();

	            //parse the file and also register this class for call backs
	            sp.parse(castsXML, this);

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
	    		//create stars hashMap
	        	String query = "select * from stars_in_movies";
	    		ResultSet rs = statement.executeQuery(query);
	    		while(rs.next()) {
	    			String sId = rs.getString("starId");
	    			String mId = rs.getString("movieId");
	    			String str = sId + "|" + mId;
	    			casts.add(str);
	    		}
	    		
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block 
	    		e.printStackTrace();
	    	}
	    }
	    
	    //Event Handler
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	    	if (qName.equalsIgnoreCase("casts")) {
	    		try {
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("casts.txt"), "ISO-8859-1"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	} else if (qName.equalsIgnoreCase("m")) {
	    		sId = null;
	    		mId = null;
	    	}
	    }

	    public void characters(char[] ch, int start, int length) throws SAXException {
	        tempVal = new String(ch, start, length).trim();
	    }
		
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	    	if (qName.equalsIgnoreCase("m")) {
	    		if(sId == null || mId == null) {
//	    			System.out.println("Actor or movie does not exist in database. Actor: " + actor + ", movie id: " + movieId);
	    			return;
	    		}
	    		String cast = sId + "|" + mId;
	    		if(casts.add(cast)) {
	    			try {
						writer.write(cast);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	} else if (qName.equalsIgnoreCase("f")) {
	    		movieId = tempVal;
	    		mId = movie.get(movieId);
	    	} else if (qName.equalsIgnoreCase("a")) {
	    		actor = tempVal;
	    		sId = stars.get(actor);
	    	} else if (qName.equalsIgnoreCase("casts")) {
	    		try {
					writer.close();

					
					String query = "load data local infile 'casts.txt' into table stars_in_movies "
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
