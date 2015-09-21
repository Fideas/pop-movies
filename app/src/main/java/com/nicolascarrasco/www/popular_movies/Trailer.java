package com.nicolascarrasco.www.popular_movies;

/**
 * Created by Nicolás Carrasco on 21-09-2015.
 */
public class Trailer {
    private String trailerName;
    private String trailerKey;

    public Trailer(String trailerName, String trailerKey) {
        this.trailerName = trailerName;
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return this.trailerName;
    }

    public String getTrailerKey() {
        return this.trailerKey;
    }
}
