/**
 * 
 */

var todos = angular.module('genres', ['ui.booststrap']);

todos.controller('genreController', function($scope, $http) {
	$scope.genreList = [];
	
	$http.get("api/browse").then(function(genreJSON){
		$scope.genreList = genreJSON.data;
	})
});