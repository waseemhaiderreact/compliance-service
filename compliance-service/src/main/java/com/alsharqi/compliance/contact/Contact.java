package com.alsharqi.compliance.contact;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="t_contact")
public class Contact {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String authority;

//    @JsonIgnore
//    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
//    private Set<Compliance> user = new HashSet<Compliance>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "issuingAuthority",cascade = { CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
//    private Set<Compliance> issuingAuthorities = new HashSet<Compliance>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "vendor",cascade = { CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
//    private Set<Compliance> vendors = new HashSet<Compliance>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public Set<Compliance> getUser() {
//        return user;
//    }
//
//    public void setUser(Set<Compliance> user) {
//        this.user = user;
//    }
//
//    public Set<Compliance> getIssuingAuthorities() {
//        return issuingAuthorities;
//    }
//
//    public void setIssuingAuthorities(Set<Compliance> issuingAuthorities) {
//        this.issuingAuthorities = issuingAuthorities;
//    }
//
//    public Set<Compliance> getVendors() {
//        return vendors;
//    }
//
//    public void setVendors(Set<Compliance> vendors) {
//        this.vendors = vendors;
//    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
