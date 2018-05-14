// Get id from URL

document.getElementById("Main").onclick = function() {
    window.location.replace("main.html");
}

function handleGStr(string, type, getString) {
	if (string) {
		if (getString !== "movieList.html") {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	return getString;
}
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleSearchResult(resultDataString) {
	console.log("handle search response");
	console.log(resultDataString)
	resultDataJson = resultDataString;
    //resultDataJson = JSON.parse(resultDataString); // Don't need this, jQuery does this for you. 
    console.log(resultDataJson);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
    	console.log("success")
    	let getString = "movieList.html";
    	getString = handleGStr(resultDataJson["movie_title"], "movie_title=", getString);
    	getString = handleGStr(resultDataJson["movie_year"], "movie_year=", getString);
    	getString = handleGStr(resultDataJson["director"], "director=", getString);
    	getString = handleGStr(resultDataJson["star_name"], "star_name=", getString);
    	getString = handleGStr("yes", "s=", getString);
    	console.log("string=" + getString+"&s=yes");
    	window.location.replace(getString);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        alert(resultDataJson["message"]);
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
