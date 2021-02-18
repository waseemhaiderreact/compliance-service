package com.alsharqi.compliance.issuingauthority;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="t_issuingauthority")
public class IssuingAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorityName;
    private String country;
    private String location;

    //constructors
    public IssuingAuthority() {
    }

    public IssuingAuthority(String authorityName, String country, String location) {
        this.authorityName = authorityName;
        this.country = country;
        this.location = location;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
