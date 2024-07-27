package com.erenyurtcu.foodrecipeapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erenyurtcu.foodrecipeapp.model.Recipe

@Database(entities = [Recipe::class], version = 1)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao() : RecipeDAO
}