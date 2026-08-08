package com.findora.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DocumentEntity::class, DocumentFts::class],
    version = 1,
    exportSchema = false,
)
abstract class FindoraDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var instance: FindoraDatabase? = null

        fun get(context: Context): FindoraDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FindoraDatabase::class.java,
                    "findora.db",
                ).build().also { instance = it }
            }
    }
}
