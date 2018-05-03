/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");
    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleStarResult(rData,movieId) {
	let movieTableBodyElement = jQuery("#"+movieId);
	let rowHTML="";
	
	//Iterate through resultData, no more than 10 entries
	for (let i = 0; i < rData.length; i++) {
		if (i > 0) {
		  rowHTML += ", ";
		}
	rowHTML += '<a href="single-star.html?id=' + rData[i]['star_id'] + '">'+ rData[i]["star_name"] + '</a>';
  }

  movieTableBodyElement.html(rowHTML);
}

function handleMovieResult(resultData) {

    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
    	let movieId = resultData[i]["movie_id"];
        
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th>" +
            		// Add a link to single-star.html with id passed with GET url parameter
            		'<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            		+ resultData[i]["movie_title"] +     // display star_name for the link text
            		'</a>' +
            		"</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
        rowHTML += "<th id=" + movieId +"></th>";

        rowHTML += "<th>" +
					
					'<a href="cart.html?id=' + resultData[i]['movie_id'] + "&value=1" +'">'
					+ 'Add' +     // display star_name for the link text
					'</a>' +
					"</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    $(document).ready( function () {
        $('#movie_table').DataTable();
    } );
    for (let i = 0; i < resultData.length; i++) {
    	let movieId = resultData[i]["movie_id"];
    	jQuery.ajax({
    		dataType: "json", // Setting return data type
    		method: "GET", // Setting request method
    		url: "api/stars?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    		success: (rData) => handleStarResult(rData, movieId) // Setting callback function to handle data returned successfully by the StarsServlet
    	});
    }
}

function handleGStr(string, type, getString) {
	if (string) {
		if (getString.length) {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	console.log("function=" + getString);
	return getString;
}

// start of the page is here
let movie_title = getParameterByName("movie_title");
let movie_year= getParameterByName("movie_year");
let director= getParameterByName("director");
let star_name= getParameterByName("star_name");
let genre = getParameterByName("genre");
let getString = "";
getString = handleGStr(movie_title, "movie_title=", getString);
getString = handleGStr(movie_year, "movie_year=", getString);
getString = handleGStr(director, "director=", getString);
getString = handleGStr(star_name, "star_name=", getString);
getString = handleGStr(genre, "genre=", getString);

console.log("getString=" + getString);
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist" + getString, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
