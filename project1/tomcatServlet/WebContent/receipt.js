document.getElementById("Main").onclick = function() {
    window.location.replace("main.html");
}

var items = JSON.parse(sessionStorage.getItem("checkout"));
console.log(items);

function handleMovieResult(rData, movieId) {
    let movieTableBodyElement = jQuery("#" + movieId);
    let rowHTML = "";
    rowHTML += '<a href="single-movie.html?id=' + rData[0]['movie_id'] + '">' + rData[0]['movie_title'] + '</a>';
    movieTableBodyElement.append(rowHTML);
}

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
        rowHTML += resultData[i]["movie_quantity"];
        rowHTML += "</th>";
        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (rData) => handleMovieResult(rData, movieId) // Setting callback function to handle data returned successfully by the StarsServlet
        });
        movieTableBodyElement.append(rowHTML);
    }
    sessionStorage.removeItem("checkout");
    sessionStorage.clear();
    $(document).ready(function () {
        $('#cart_table').DataTable();
    });
}
handleResult(items);