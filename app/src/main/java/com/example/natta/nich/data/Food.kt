package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable

class Food constructor(var foodName: String? = "",
                       var price: Double? = 0.0,
                       var picture: String? = "",
                       var restaurantID: String? = "",
                       var available: Boolean? = true,
                       var rate: Double? = 0.0,
                       var type: String? = "",
                       var orderAmount : Int? = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodName)
        parcel.writeValue(price)
        parcel.writeString(picture)
        parcel.writeString(restaurantID)
        parcel.writeValue(available)
        parcel.writeValue(rate)
        parcel.writeString(type)
        parcel.writeValue(orderAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }
}