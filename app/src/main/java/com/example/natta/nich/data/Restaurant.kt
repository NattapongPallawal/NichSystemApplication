package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable


@Suppress("UNREACHABLE_CODE")
class Restaurant constructor(
    var restaurantName: String? = null,
    var picture: String? = null,
    var rating: Double? = null,
    var owner: String? = null,
    var promtPayID: String? = null,
//    var timeOpen: String? = null,
//    var timeClose: String? = null,
    var time: RestaurantTime? = RestaurantTime(),
    var address: Address? = Address(),
    var about: String? = null,
    var times: String? = "00:00 - 00:00",
    var phoneNumber: String? = null
) : Parcelable {
    private var key: String = ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(RestaurantTime::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        key = parcel.readString()
    }

    fun setKey(key: String) {
        this.key = key
    }

    fun getKey(): String? {
        return this.key
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(restaurantName)
        parcel.writeString(picture)
        parcel.writeValue(rating)
        parcel.writeString(owner)
        parcel.writeString(promtPayID)
        parcel.writeParcelable(time, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeString(about)
        parcel.writeString(times)
        parcel.writeString(phoneNumber)
        parcel.writeString(key)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }


}