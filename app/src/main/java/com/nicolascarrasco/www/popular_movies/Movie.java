package com.nicolascarrasco.www.popular_movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple class to represent a Movie object as understood by theMovieDB API
 */
public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String id;
    private String title;
    private String synopsis;
    private String posterPath;
    private String userRating;
    private String releaseDate;

    //Constructors

    public Movie() {
    }

    //From parcel
    public Movie(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.synopsis = in.readString();
        this.posterPath = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(synopsis);
        out.writeString(posterPath);
        out.writeString(userRating);
        out.writeString(releaseDate);
    }

    //getters & setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
