package com.api.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "poster")
public class Poster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "titre", nullable = false)
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
