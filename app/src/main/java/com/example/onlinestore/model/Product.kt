package com.example.onlinestore.model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val categoryId: Int,
    val colorVariant: String,
    val content: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val rate: Double,
    val review: Int,
    val subCategoryId: Int,
    val typeVariant: String
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(categoryId)
        parcel.writeString(colorVariant)
        parcel.writeString(content)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeDouble(rate)
        parcel.writeInt(review)
        parcel.writeInt(subCategoryId)
        parcel.writeString(typeVariant)
    }

    override fun describeContents(): Int {
        return 0
    }

//    companion object CREATOR : Parcelable.Creator<ProductItem> {
//        override fun createFromParcel(parcel: Parcel): ProductItem {
//            return ProductItem(parcel)
//        }
//
//        override fun newArray(size: Int): Array<ProductItem?> {
//            return arrayOfNulls(size)
//        }
//    }
    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}