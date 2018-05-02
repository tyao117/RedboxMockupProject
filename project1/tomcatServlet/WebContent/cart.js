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
	console.log(resultData)
	console.log("going here");
}


// Get id from URL
let movieId = getParameterByName('id');
if (movieId !== null) {
	// Makes the HTTP GET request and registers on success callback function handleResult
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		url: "api/cart?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
		success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
	});
}