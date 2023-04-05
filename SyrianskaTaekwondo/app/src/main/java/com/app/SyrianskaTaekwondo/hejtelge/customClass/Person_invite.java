package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple container object for contact data
 *
 * Created by mgod on 9/12/13.
 * @author mgod
 */
public class Person_invite implements Parcelable {
    private String name;
    private String email;

    public Person_invite(String n, String e) {
        name = n;
        email = e;
    }


    public String getName() { return name; }
    String getEmail() { return email; }

    @Override
    public String toString() { return name; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
    }

    public Person_invite(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
    }

    public static final Creator<Person_invite> CREATOR = new Creator<Person_invite>() {
        @Override
        public Person_invite createFromParcel(Parcel source) {
            return new Person_invite(source);
        }

        @Override
        public Person_invite[] newArray(int size) {
            return new Person_invite[size];
        }
    };
}