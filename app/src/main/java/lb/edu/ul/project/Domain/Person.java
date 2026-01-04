package lb.edu.ul.project.Domain;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {
    private String name;
    private String role; // "Actor" or "Director"
    private String biography;
    private String birthDate;
    private String imageUrl;
    private List<String> knownFor;
    private List<FilmCredit> filmography;

    public Person() {
    }

    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getKnownFor() {
        return knownFor;
    }

    public void setKnownFor(List<String> knownFor) {
        this.knownFor = knownFor;
    }

    public List<FilmCredit> getFilmography() {
        return filmography;
    }

    public void setFilmography(List<FilmCredit> filmography) {
        this.filmography = filmography;
    }

    public static class FilmCredit implements Serializable {
        private int movieId;
        private String title;
        private String year;
        private String poster;
        private String characterRole; // For actors

        public FilmCredit() {
        }

        public FilmCredit(int movieId, String title, String year, String poster) {
            this.movieId = movieId;
            this.title = title;
            this.year = year;
            this.poster = poster;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getCharacterRole() {
            return characterRole;
        }

        public void setCharacterRole(String characterRole) {
            this.characterRole = characterRole;
        }
    }
}
