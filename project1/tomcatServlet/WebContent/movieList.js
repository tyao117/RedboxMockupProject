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

document.getElementById("Main").onclick = function() {
    window.location.replace("main.html");
}

// start of the page is here
let movie_title = getParameterByName("movie_title");
let movie_year= getParameterByName("movie_year");
let director= getParameterByName("director");
let star_name= getParameterByName("star_name");
let genre = getParameterByName("genre");
let search = getParameterByName("s");
let getString = "";

console.log(movie_title);
getString = handleGStr(movie_title, "movie_title=", getString);
getString = handleGStr(movie_year, "movie_year=", getString);
getString = handleGStr(director, "director=", getString);
getString = handleGStr(star_name, "star_name=", getString);
getString = handleGStr(genre, "genre=", getString);
getString = handleGStr(search, "s=", getString);

$(document).ready(function() {
    $('#movie_table').DataTable({
        "processing": true,
        "serverSide": false,
        "draw": 1,
        "recordsTotal": 1000,
        "recordsFiltered": 1000,
        "pagingType": "simple",
        "ajax":{"url":"api/movielist" + getString,"dataSrc":""},
        "columns" : [ {
            "data" : "movie_id"
        }, {
            "data" : "movie_title",
            "render": function(data, type, row, meta){
                if(type === 'display'){
                    data = '<a href="single-movie.html?id=' + row.movie_id + '">' + data + '</a>';
                }
                return data;
            }
        }, {
            "data" : "movie_year"
        }, {
            "data" : "movie_director"
        }, {
        	"data" : "movie_rating"
        }, {
        	"data" : "movie_genre"
        }, {
        	"data" : "row.star_id_array",
            "render": function(data, type, row, meta){
            	var array_length = row.star_name_array.length;
            	data = "";
            	for (var i = 0; i < array_length; i++){
                    if(type === 'display'){
                		data += '<a href="single-star.html?id=' + row.star_id_array[i] + '">' + row.star_name_array[i] + ', ' +'</a>';
                    }
            	}
            	return data;
            }
        }, {
        	"data" : "movie_id",
        	"render": function(data, type, row, meta){
                if(type === 'display'){
                	data = '<a href="cart.html?id=' + row.movie_id + "&value=1" +'">'+ 'Add' + '</a>';
                }
                return data;
            }
        }
        ]
    }
);
} );
