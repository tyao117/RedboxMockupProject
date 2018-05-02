/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */



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

//function handleStarResult(rData,movieId) {
//	let movieTableBodyElement = jQuery("#"+movieId);
//	let rowHTML="";
//	
//	//Iterate through resultData, no more than 10 entries
//	for (let i = 0; i < rData.length; i++) {
//		if (i > 0) {
//		  rowHTML += ", ";
//		}
//	rowHTML += '<a href="single-star.html?id=' + rData[i]['star_id'] + '">'+ rData[i]["star_name"] + '</a>';
//  }
//
//  movieTableBodyElement.append(rowHTML);
//}
//
//function handleMovieResult(resultData) {
//    console.log("handleStarResult: populating star table from resultData");
//
//    // Populate the star table
//    // Find the empty table body by id "star_table_body"
//    let movieTableBodyElement = jQuery("#movie_table_body");
//    
//    // Iterate through resultData, no more than 10 entries
//    for (let i = 0; i < Math.min(10, resultData.length); i++) {
//    	let movieId = resultData[i]["movie_id"];
//        
//        // Concatenate the html tags with resultData jsonObject
//        let rowHTML = "";
//        rowHTML += "<tr>";
//        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
//        rowHTML += "<th>" +
//            		// Add a link to single-star.html with id passed with GET url parameter
//            		'<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
//            		+ resultData[i]["movie_title"] +     // display star_name for the link text
//            		'</a>' +
//            		"</th>";
//        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
//        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
//        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
//        rowHTML += "<th>" + resultData[i]["genre"] + "</th>";
//        rowHTML += "<th id=" + movieId +"></th>";
//        rowHTML += "</tr>";
//        jQuery.ajax({
//            dataType: "json", // Setting return data type
//            method: "GET", // Setting request method
//            url: "api/stars?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
//            success: (rData) => handleStarResult(rData, movieId) // Setting callback function to handle data returned successfully by the StarsServlet
//        });
//
//        // Append the row created to the table body, which will refresh the page
//        movieTableBodyElement.append(rowHTML);
//    }
//}

function handleGStr(string, type, getString) {
	if (string) {
		if (getString.length) {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	console.log("function=" + getString);
	return getString;
}



// start of the page is here
var movie_title = getParameterByName("movie_title");
var movie_year= getParameterByName("movie_year");
var director= getParameterByName("director");
var star_name= getParameterByName("star_name");
var genre = getParameterByName("genre");
var orderBy = getParameterByName("order_by");
var getString = "";
getString = handleGStr(movie_title, "movie_title=", getString);
getString = handleGStr(movie_year, "movie_year=", getString);
getString = handleGStr(director, "director=", getString);
getString = handleGStr(star_name, "star_name=", getString);
getString = handleGStr(genre, "genre=", getString);
getString = handleGStr(orderBy, "order_by=", getString);

console.log("getString=" + getString);
document.getElementById("MainPage").onclick = function () {
    window.location.replace("main.html");
};
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
//jQuery.ajax({
//    dataType: "json", // Setting return data type
//    method: "GET", // Setting request method
//    url: "api/movielist" + getString, // Setting request url, which is mapped by StarsServlet in Stars.java
//    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
//});


var todos = angular.module('movies', ['ui.bootstrap']);

todos.controller('MovieController', function($scope, $http) {
  $scope.filteredMovies = [];
  $scope.movieList = [];
  $scope.currentPage = 1;
  $scope.numPerPage = 10;
  $scope.sortType = 'movie_title';
  
  $http.get("api/movielist" + getString).then(function(movieJSON){
	 //console.log(movieJSON);
	 $scope.movieList = movieJSON.data;
	 $scope.movieListTitle = movieJSON.data.slice();
	 $scope.movieListRating = movieJSON.data.slice();
	 
	 
	 $scope.movieListTitle.sort(function(a,b){
		 //console.log (a['movie_title'] < b['movie_title']);
		 if  (a['movie_title'] < b['movie_title'])
			 return -1;
		 else if  (a['movie_title'] === b['movie_title'])
			 return 0;
		 else
			 return 1;
	 });
	 console.log($scope.movieListTitle);
	 $scope.movieListRating.sort(function(a,b){return a['movie_rating'] < b['movie_rating']});
	 $scope.$watch('currentPage + numPerPage', function() {
		    var begin = (($scope.currentPage - 1) * $scope.numPerPage)
		    , end = begin + $scope.numPerPage;
		    
		    $scope.filteredMovies = $scope.movieListTitle.slice(begin, end);
		    
		    if($scope.sortType === 'movie_title')
		    	$scope.filteredMovies = $scope.movieListTitle.slice(begin, end);
		    
		    if($scope.sortType === 'movie_rating')
		    	$scope.filteredMovies = $scope.movieListRating.slice(begin, end);
		    
		    for(let i = 0; i < $scope.filteredMovies.length; i++){
		    	$http.get("api/stars?id=" + $scope.filteredMovies[i]['movie_id']).then(function(starsJSON){
		    	console.log(starsJSON);
		    	$scope.filteredMovies[i]['stars'] = starsJSON.data; 
		    	})};
		  });
  }); 
  
  
});
