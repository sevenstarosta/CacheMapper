package com.cachemapper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Seven on 11/13/2017.
 */
@IgnoreExtraProperties
public class cacheLocation /*implements Parcelable*/
{
    public String username;
    public String name;
    public String description;
    public Double latitude;
    public Double longitude;
    public Long count;

    public cacheLocation()
    {

    }
    public cacheLocation(String u,String n,String desc,double lat,double lon, long c)
    {
        this.username = u;
        this.name = n;
        this.description = desc;
        this.latitude = lat;
        this.longitude = lon;
        this.count = c;
    }

}
