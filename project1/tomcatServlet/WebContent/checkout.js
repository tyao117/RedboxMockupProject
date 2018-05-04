function handleMovieResult(rData, movieId) {
    let movieTableBodyElement = jQuery("#" + movieId);
    let rowHTML = "";
    rowHTML += '<a href="single-movie.html?id=' + rData[0]['movie_id'] + '">' + rData[0]['movie_title'] + '</a>';
    movieTableBodyElement.append(rowHTML);
}

function handleConfirmResult(resultDataString) {
	console.log(resultDataString);
}

function handleResult(resultData) {
    console.log(" ajax call working");
    console.log(resultData);
    let movieTableBodyElement = jQuery("#cart_table_body");
    for (let i = 0; i < resultData.length; i++) {
        let movieId = resultData[i]["movie_id"];
        let num = parseInt(resultData[i]["movie_quantity"]);
        console.log(num);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th id=" + movieId + "></th>";
        rowHTML += "<th>";
        rowHTML += resultData[i]["movie_quantity"];
        rowHTML += "</th>";
        rowHTML += "</tr>";
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (rData) => handleMovieResult(rData, movieId) // Setting callback function to handle data returned successfully by the StarsServlet
        });
        movieTableBodyElement.append(rowHTML);
    }
    console.log("Tabbulation is finished");
}

function submitForm(formSubmitEvent) {
    console.log("submit confirm form");
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    jQuery.post(
        "api/creditcard",
        // Serialize the confirm form to the data sent by POST request
        jQuery("#confirm_form").serialize(),
        (resultDataString) => handleConfirmResult(resultDataString));
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

// Bind the submit action of the form to a handler function
jQuery("#confirm_form").submit((event) => submitForm(event));

