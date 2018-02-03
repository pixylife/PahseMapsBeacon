package com.phasemaps.azio.phasemapsbeacon.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by DELL on 6/2/2017.
 */

public class Advertiser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idadvertiser;

    private String name;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation

    private String email;

    private String contactNo;

    private String address;

    private String company;

    private String password;

    private Collection<Beacon> beaconCollection;

    public Advertiser() {
    }

    public Advertiser(Integer idadvertiser) {
        this.idadvertiser = idadvertiser;
    }

    public Integer getIdadvertiser() {
        return idadvertiser;
    }

    public void setIdadvertiser(Integer idadvertiser) {
        this.idadvertiser = idadvertiser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Beacon> getBeaconCollection() {
        return beaconCollection;
    }

    public void setBeaconCollection(Collection<Beacon> beaconCollection) {
        this.beaconCollection = beaconCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idadvertiser != null ? idadvertiser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Advertiser)) {
            return false;
        }
        Advertiser other = (Advertiser) object;
        if ((this.idadvertiser == null && other.idadvertiser != null) || (this.idadvertiser != null && !this.idadvertiser.equals(other.idadvertiser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.technobratz.phasemaps.database.entity.Advertiser[ idadvertiser=" + idadvertiser + " ]";
    }
}
