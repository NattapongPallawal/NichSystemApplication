package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable

class Select constructor(
        var amount: Int? = 1,
        var foodID: String? = null,
        var foodSizeID: String? = null,
        var foodTypeID: String? = null,
        var resID: String? = null,
        var foodName: String? = null,
        var foodTypeName: String? = null,
        var foodSizeName: String? = null,
        var price: Double? = null,
        var picture: String? = null,
        var finish: Boolean? = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(amount)
        parcel.writeString(foodID)
        parcel.writeString(foodSizeID)
        parcel.writeString(foodTypeID)
        parcel.writeString(resID)
        parcel.writeString(foodName)
        parcel.writeString(foodTypeName)
        parcel.writeString(foodSizeName)
        parcel.writeValue(price)
        parcel.writeString(picture)
        parcel.writeValue(finish)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Select> {
        override fun createFromParcel(parcel: Parcel): Select {
            return Select(parcel)
        }

        override fun newArray(size: Int): Array<Select?> {
            return arrayOfNulls(size)
        }
    }
}