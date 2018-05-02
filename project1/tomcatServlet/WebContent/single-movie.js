/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie ID: " + resultData[0]["movie_id"] + "</p>");
    movieInfoElement.append("<p>Title: " + resultData[0]["movie_title"] + "</p>");
    movieInfoElement.append("<p>Year: " + resultData[0]["movie_year"] + "</p>");
    movieInfoElement.append("<p>Director: " + resultData[0]["movie_director"] + "</p>");
    movieInfoElement.append("<p>Rating: " + resultData[0]["movie_rating"] + "</p>");
    movieInfoElement.append("<p>Genre: ");
    let genres = resultData[0]["movie_genre"].split(', ');
    for (let i = 0; i < genres.length; i++)
    	{
    	if (i > 0)
    		movieInfoElement.append(", ");
    	movieInfoElement.append(
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="movieList.html?genre=' + genres[i] + '">'
            + genres[i] +     // display star_name for the link text
            '</a>');
    	}
   movieInfoElement.append("</p>");
   document.getElementById("Add").onclick = function () {
       window.location.replace("cart.html?id=" + resultData[0]["movie_id"] + "&value=1");
   };
//    console.log(genres);
    console.log("handleResult: populating movie table from resultData");
    

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(10, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});