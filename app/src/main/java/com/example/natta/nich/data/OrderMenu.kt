package com.example.natta.nich.data

import android.os.Parcel
import android.os.Parcelable

class OrderMenu constructor(var foodSize: String? = null,
                            var foodType: String? = null,
                            var amount: Int? = null,
                            var foodID: String? = null,
                            var price: Double? = null,
                            var foodName: String? = null,
                            var foodTypeName: String? = null,
                            var foodSizeName: String? = null,
                            var finish: Boolean? = null,
                            var picture: String? = null,
                            var canDo: Boolean? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodSize)
        parcel.writeString(foodType)
        parcel.writeValue(amount)
        parcel.writeString(foodID)
        parcel.writeValue(price)
        parcel.writeString(foodName)
        parcel.writeString(foodTypeName)
        parcel.writeString(foodSizeName)
        parcel.writeValue(finish)
        parcel.writeString(picture)
        parcel.writeValue(canDo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderMenu> {
        override fun createFromParcel(parcel: Parcel): OrderMenu {
            return OrderMenu(parcel)
        }

        override fun newArray(size: Int): Array<OrderMenu?> {
            return arrayOfNulls(size)
        }
    }

}