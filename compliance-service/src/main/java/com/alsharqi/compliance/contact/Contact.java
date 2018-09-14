package com.alsharqi.compliance.contact;

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

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch=FetchType.EAGER)
    private Set<ComplianceRequest> user = new HashSet<ComplianceRequest>();

    @JsonIgnore
    @OneToMany(mappedBy = "issuingAuthority", fetch=FetchType.EAGER)
    private Set<ComplianceRequest> issuingAuthorities = new HashSet<ComplianceRequest>();

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

    public Set<ComplianceRequest> getUser() {
        return user;
    }

    public void setUser(Set<ComplianceRequest> user) {
        this.user = user;
    }

    public Set<ComplianceRequest> getIssuingAuthorities() {
        return issuingAuthorities;
    }

    public void setIssuingAuthorities(Set<ComplianceRequest> issuingAuthorities) {
        this.issuingAuthorities = issuingAuthorities;
    }
}
