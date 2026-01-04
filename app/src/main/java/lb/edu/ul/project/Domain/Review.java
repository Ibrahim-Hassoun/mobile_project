package lb.edu.ul.project.Domain;

import java.io.Serializable;

public class Review implements Serializable {
    private int movieId;
    private String movieTitle;
    private float rating;
    private String reviewText;
    private long timestamp;
    private String userName;

    public Review() {
    }

    public Review(int movieId, String movieTitle, float rating, String reviewText, String userName) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.reviewText = reviewText;
        this.timestamp = System.currentTimeMillis();
        this.userName = userName;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
