package com.phasemaps.azio.phasemapsbeacon.model;

import java.io.Serializable;

/**
 * Created by DELL on 6/2/2017.
 */

public class Beacon implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idBeacon;

    private String subject;

    private String description;

    private Advertiser advertiseridadvertiser;

    private long latitude;

    private long longitude;

    public Beacon() {
    }

    public Beacon(Integer idBeacon) {
        this.idBeacon = idBeacon;
    }

    public Integer getIdBeacon() {
        return idBeacon;
    }

    public void setIdBeacon(Integer idBeacon) {
        this.idBeacon = idBeacon;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Advertiser getAdvertiseridadvertiser() {
        return advertiseridadvertiser;
    }

    public void setAdvertiseridadvertiser(Advertiser advertiseridadvertiser) {
        this.advertiseridadvertiser = advertiseridadvertiser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idBeacon != null ? idBeacon.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Beacon)) {
            return false;
        }
        Beacon other = (Beacon) object;
        if ((this.idBeacon == null && other.idBeacon != null) || (this.idBeacon != null && !this.idBeacon.equals(other.idBeacon))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.technobratz.phasemaps.database.entity.Beacon[ idBeacon=" + idBeacon + " ]";
    }

    /**
     * @return the latitude
     */
    public long getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public long getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}