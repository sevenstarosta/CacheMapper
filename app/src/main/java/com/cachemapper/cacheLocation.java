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

    public cacheLocation()
    {

    }
    public cacheLocation(String u,String n,String desc,double lat,double lon)
    {
        this.username = u;
        this.name = n;
        this.description = desc;
        this.latitude = lat;
        this.longitude = lon;
    }

    /*public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeString(name);
        out.writeString(description);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    public static final Parcelable.Creator<cacheLocation> CREATOR
            = new Parcelable.Creator<cacheLocation>()
    {
        public cacheLocation createFromParcel(Parcel in)
        {
            return new cacheLocation(in);
        }

        public cacheLocation[] newArray(int size)
        {
            return new cacheLocation[size];
        }
    };

    private MyParcelable(Parcel in) {
        mData = in.readInt();
    }*/

}
