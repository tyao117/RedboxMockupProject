/**
 * 
 */
function handleResult(resultData) {
	let tableBodyElement = jQuery("#cart_table_body");
	tableBodyElement.append(localStorage.getItem("cart"));
}

function submitForm(formSubmitEvent) {
    console.log("submit search form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    
    jQuery.get(
        "api/checkout",
        // Serialize the confirm form to the data sent by POST request
        jQuery("#confirm_form").serialize(),
        (resultDataString) => handleConfirmResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#confirm_form").submit((event) => submitForm(event));

jQuery.ajax({
	dataType: "json",  // Setting return data type
	method: "GET",// Setting request method
	url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});