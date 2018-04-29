package com.omarica.bucketlist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by omarica on 3/21/18.
 */

public class BucketItem implements Parcelable {


    // A data model for a single item
    // Parcelable to be sent using an intent
    private String key;
    private String name;
    private String description;
    private String imgUrl;
    private String location;
    private boolean status;
    private long dueDate;



    public BucketItem() {

    }


    public BucketItem(String name, String description, String imgUrl, String location, boolean status, String key, long dueDate) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.location = location;
        this.status = status;
        this.key = key;
        this.dueDate = dueDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.imgUrl);
        dest.writeString(this.location);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeLong(this.dueDate);
    }

    protected BucketItem(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.imgUrl = in.readString();
        this.location = in.readString();
        this.status = in.readByte() != 0;
        this.dueDate = in.readLong();
    }

    public static final Parcelable.Creator<BucketItem> CREATOR = new Parcelable.Creator<BucketItem>() {
        @Override
        public BucketItem createFromParcel(Parcel source) {
            return new BucketItem(source);
        }

        @Override
        public BucketItem[] newArray(int size) {
            return new BucketItem[size];
        }
    };
}
