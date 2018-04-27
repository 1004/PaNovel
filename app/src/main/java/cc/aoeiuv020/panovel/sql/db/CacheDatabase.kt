package cc.aoeiuv020.panovel.sql.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import cc.aoeiuv020.panovel.sql.dao.NovelDetailDao
import cc.aoeiuv020.panovel.sql.entity.Chapter
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import cc.aoeiuv020.panovel.sql.entity.Volume
import java.io.File

/**
 * Created by AoEiuV020 on 2018.04.25-21:50:22.
 */
@Database(
        entities = [NovelDetail::class, Chapter::class, Volume::class],
        version = 1
)
@TypeConverters(value = [Converters::class])
abstract class CacheDatabase : RoomDatabase() {
    companion object {
        lateinit var dbFile: File
            private set

        fun build(context: Context): CacheDatabase {
            dbFile = context.cacheDir.resolve("panovel-cache.db")
            return Room.databaseBuilder(
                    context.applicationContext,
                    CacheDatabase::class.java,
                    dbFile.path
            ).build()
        }
    }

    abstract fun novelDetailDao(): NovelDetailDao
}