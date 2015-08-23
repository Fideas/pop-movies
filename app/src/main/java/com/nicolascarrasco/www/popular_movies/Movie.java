package com.nicolascarrasco.www.popular_movies;

/**
 * Created by Nicolás Carrasco on 23-08-2015.
 */
public class Movie {
    private String title;
    private String synopsis;
    private String posterPath;
    private String userRating;

    //Constructor
    public Movie() {
    }

    //setters
    public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    //getters
    public String getPosterPath() {
        return this.posterPath;
    }
    public String getTitle() {
        return this.title;
    }
    public String getSynopsis() {
        return this.synopsis;
    }
    public String getUserRating() {
        return this.userRating;
    }
}
