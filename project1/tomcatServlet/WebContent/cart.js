function handleCartResult(resultData) {
	console.log("")
    console.log("handleCartResult: populating cart table from resultData");
	
	//Populate the cart table
	//Find the empty table body by id "cart_body"
	let cartBodyElement = jQuery("#cart_body");
	
	//Iterate through restultData
	for(let i = 0; i < resultData.length; i++) {
		let movieId = result[i][item]["movie_id"];
		
		// Concatenate the html tags with resultData jsonObject
	}
}

/**
 * Once this .js is loaded, following the scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleCartResult
jQuery.ajax({
	dataType: "json", // Setting return data type
	method: "GET", // Setting request method
	url: "api/cart", // Setting request url, which is mapped by CartServlet in CartServlet.java
	success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the CartServlet
});