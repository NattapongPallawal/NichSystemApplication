package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable

class RestaurantTime constructor(
        var mon : String? = null,
        var tue : String? = null,
        var wed : String? = null,
        var thu : String? = null,
        var fri : String? = null,
        var sat : String? = null,
        var sun : String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mon)
        parcel.writeString(tue)
        parcel.writeString(wed)
        parcel.writeString(thu)
        parcel.writeString(fri)
        parcel.writeString(sat)
        parcel.writeString(sun)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RestaurantTime> {
        override fun createFromParcel(parcel: Parcel): RestaurantTime {
            return RestaurantTime(parcel)
        }

        override fun newArray(size: Int): Array<RestaurantTime?> {
            return arrayOfNulls(size)
        }
    }
}