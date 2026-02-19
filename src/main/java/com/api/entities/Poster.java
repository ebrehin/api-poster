package com.api.entities;

public class Poster {

    private String id;
    private String url;
    private String titre;

    public Poster() {}

    public Poster(String id, String url, String titre) {
        this.id = id;
        this.url = url;
        this.titre = titre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
}
