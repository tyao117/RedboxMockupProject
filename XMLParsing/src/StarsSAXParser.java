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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StarsSAXParser extends DefaultHandler {
	//get xml
	private File starsXML;
	private String name;
	private String birthYear;
	private String tempVal;
	private int maxId;
	private Statement statement;
	private Writer writer;
	private HashMap<String, String> stars;
	
	public StarsSAXParser(Statement statement) {
		starsXML = new File("stanford-movies/actors63.xml");
		name = "";
		birthYear = "";
		maxId = 0;
		this.statement = statement;
		stars = new HashMap<String, String>();
	}
    
    public HashMap<String,String> getStarsMapping() {
    	return stars;
    }

	public void runStarsParser() {
		parseDocument();
	}

	private void parseDocument() {
        createHashes();
		getMaxId();

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(starsXML, this);

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
        	String query = "select * from stars";
    		ResultSet rs = statement.executeQuery(query);
    		while(rs.next()) {
    			String star = "";
    			String id = rs.getString("id");
    			star = rs.getString("name");
    			stars.put(star, id);
    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block 
    		e.printStackTrace();
    	}
    }
    
    private void getMaxId() {
    	try {
        	String query = "select max(id) as id from stars;";
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			String max = rs.getString("id");
			max = max.substring(2);
			maxId = Integer.parseInt(max);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    //Event Handler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	if (qName.equalsIgnoreCase("actors")) {
    		try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("stars.txt"), "ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else if (qName.equalsIgnoreCase("actor")) {
    		name = null;
    		birthYear = null;
    	}
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if (qName.equalsIgnoreCase("actor")) {
    		if(name == null) {
    			System.out.println("Actor's name cannot be null.");
    			return;
    		}
    		String star = name;
    		int id = maxId+1;
    		String sId = "nm" + Integer.toString(id);
    		myMap<String, String> map = new myMap<String, String>(stars);
    		if(map.add(star, sId)) {
    			++maxId;
    			star = sId + "|" + star + "|" + birthYear + "\n";
    			try {
					writer.write(star);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	} else if (qName.equalsIgnoreCase("stagename")) {
    		name = (tempVal.length() > 0) ? tempVal : null;
    	} else if (qName.equalsIgnoreCase("dob")) {
    		birthYear = (tempVal.length() > 0) ? tempVal : null;
    	} else if (qName.equalsIgnoreCase("actors")) {
    		try {
    			writer.close();

				
				String query = "load data local infile 'stars.txt' into table stars "
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
