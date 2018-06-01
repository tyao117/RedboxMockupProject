package ics.uci.edu.fabflixmobile;

public class SearchModel {
    String id;
    String title;
    String director;
    String year;
    String genres;
    String stars;

    //constructor
    public SearchModel(String id, String title, String director, String year, String genres, String stars) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDirector() {
        return this.director;
    }

    public String getYear() {
        return this.year;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getStars() {
        return this.stars;
    }
}
