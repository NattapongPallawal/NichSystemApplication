package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable

data class Address constructor(
    var address: String? = null,
    var subDistrict: String? = null,
    var district: String? = null,
    var province: String? = null,
    var postalCode: Int? = null,
    var longitude: Double? = null,
    var latitude: Double? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(subDistrict)
        parcel.writeString(district)
        parcel.writeString(province)
        parcel.writeValue(postalCode)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}