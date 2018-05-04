

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
 * Handles the data return by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 * @param movieId movieId
 */
function handleMovieResult(rData, movieId) {
    let movieTableBodyElement = jQuery("#" + movieId);
    let rowHTML = "";
    rowHTML += '<a href="single-movie.html?id=' + rData[0]['movie_id'] + '">' + rData[0]['movie_title'] + '</a>';
    movieTableBodyElement.append(rowHTML);
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log(resultData)
    let movieTableBodyElement = jQuery("#cart_table_body");
    for (let i = 0; i < resultData.length; i++) {
        let movieId = resultData[i]["movie_id"];
        let num = parseInt(resultData[i]["movie_quantity"]);
        console.log(num);
        let baseURL = "cart.html?" + "id=" + resultData[i]["movie_id"] + "&value=";
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th id=" + movieId + "></th>";
        rowHTML += "<th>";
        rowHTML += "<a href=" + baseURL + (-1) + ">-</a>";
        rowHTML += resultData[i]["movie_quantity"];
        rowHTML += "<a href=" + baseURL + (1) + ">+</a>";
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
    $(document).ready(function () {
        $('#cart_table').DataTable();
    });
}

// Get id from URL
let movieId = getParameterByName('id');
let title = getParameterByName('title');
let value = getParameterByName('value');
if (movieId !== null) {
    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "POST",// Setting request method
        url: "api/cart?id=" + movieId + '&value=' + value, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}
else {
    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

document.getElementById("Checkout").onclick = function() {
    window.location.replace("checkout.html");
}