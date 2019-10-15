package com.example.aplikacija;


public class SeriesShort {

    private String title;
    private String year;
    private String type;
    private String poster_link;
    private int SERIES_ID;

    public SeriesShort(String title, String year, String type, String poster_link) {
        this.title = title;
        this.year = year;
        this.type = type;
        this.poster_link = poster_link;
    }

    public int getSERIES_ID() {
        return SERIES_ID;
    }

    public void setSERIES_ID(int SERIES_ID) {
        this.SERIES_ID = SERIES_ID;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoster_link() {
        return poster_link;
    }

    public void setPoster_link(String poster_link) {
        this.poster_link = poster_link;
    }

    @Override
    public String toString(){
        return title + "`" + year + "`" + type + "`" + poster_link;
    }
}
