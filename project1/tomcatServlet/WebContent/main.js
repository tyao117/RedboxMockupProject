var movieCache = {};

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated");
	console.log("sending AJAX request to backend Java Servlet");
	
	// TODO: if you want to check past query results first, you can do it here
	console.log(movieCache);
	if (query in movieCache) {
		console.log("From the cache!!!")
		doneCallback({ suggestions: movieCache[query]});
		return;
	}
	// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
	// with the query data
	jQuery.ajax({
		"method": "GET",
		// generate the request url from the query.
		// escape the query string to avoid errors caused by special characters 
		"url": "api/MovieAutoComplete?term=" + escape(query),
		"success": function(data) {
			// pass the data, query, and doneCallback function into the success handler
			handleLookupAjaxSuccess(data, query, doneCallback) 
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful, sending back results")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	// console.log(jsonData);
	// console.log(data);
	
	// TODO: if you want to cache the result into a global variable you can do it here
	movieCache[query] = jsonData;
	// console.log(movieCache);
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"]);
	var url = suggestion["data"]["category"] + ".html" +"?id=" + suggestion["data"]["movie_id"]
	console.log(url);
	window.location.replace(url);
}


/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // highlight is true on first
    autoFocus: "true",
    // set the minimum character is 3
    minChars: 3,
    // set the groupby name in the response json data field
    groupBy: "single-movie",
    // set delay time
    deferRequestBy: 300,
    // prevents redirect on search
    triggerSelectOnValidInput: "false",
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
	url = "movieList.html?movie_title=" + query +"&s=yes";
	window.location.replace(url);
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button