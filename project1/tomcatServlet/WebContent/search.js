// Get id from URL

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleSearchResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
    	console.log("success")
    	let string = "movieList.html";
    	string += "?movie_title=" + resultData["movie_title"];
    	string += "&movie_year=" + resultData["movie_year"];
    	string += "&director=" + resultData["director"];
    	string += "&star_name=" + resultData["star_name"];
    	window.location.replace(string);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitForm(formSubmitEvent) {
    console.log("submit search form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    
    jQuery.get(
        "api/search",
        // Serialize the login form to the data sent by POST request
        jQuery("#search_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#search_form").submit((event) => submitForm(event));
