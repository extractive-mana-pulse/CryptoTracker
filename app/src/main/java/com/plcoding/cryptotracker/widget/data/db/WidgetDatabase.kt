package com.plcoding.cryptotracker.widget.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WidgetCoin::class], version = 2, exportSchema = false)
abstract class WidgetDatabase : RoomDatabase() {
    abstract fun widgetCoinDao(): WidgetCoinDao
}