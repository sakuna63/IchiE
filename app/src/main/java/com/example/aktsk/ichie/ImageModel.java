package com.example.aktsk.ichie;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {
    private String name;
    private String path;

    public ImageModel(String name, String url) {
        this.name = name;
        this.path = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return path;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
    }

    private ImageModel(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        public ImageModel createFromParcel(Parcel source) {
            return new ImageModel(source);
        }

        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
}
