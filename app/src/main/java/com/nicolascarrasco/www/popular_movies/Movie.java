package com.nicolascarrasco.www.popular_movies;

/**
 * Created by Nicolás Carrasco on 23-08-2015.
 */
public class Movie {
    private String title;
    private String synopsis;
    private String posterPath;
    private String userRating;
    private String releaseDate;

    //Constructor
    public Movie() {
    }

    //getters & setters
    public String getPosterPath() {
        return this.posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return this.userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
