/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
//    resultDataJson = JSON.parse(resultDataString);
	resultDataJson = resultDataString;
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
    	console.log("success");
    	alert("Success");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert(resultDataJson["message"]);
        //jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit info form");
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    jQuery.post("api/add_info",
        // Serialize the login form to the data sent by POST request
        jQuery("#add_info_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#add_info_form").submit((event) => submitLoginForm(event));