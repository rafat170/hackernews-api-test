package com.hackernews.api.model;

import java.util.Arrays;

/**
 * Represents a user from the Hacker News API.
 */
public class User {
    private String id;
    private Long created;
    private Integer karma;
    private String about;
    private Long[] submitted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long[] getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Long[] submitted) {
        this.submitted = submitted;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", karma=" + karma +
                ", about='" + about + '\'' +
                ", submitted=" + (submitted != null ? submitted.length + " items" : "null") +
                '}';
    }
}
