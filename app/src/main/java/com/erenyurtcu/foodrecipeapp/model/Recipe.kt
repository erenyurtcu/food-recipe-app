package com.erenyurtcu.foodrecipeapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // Ensure this has a default value
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "ingredient")
    var ingredient: String,
    @ColumnInfo(name = "image")
    var image: ByteArray
)
