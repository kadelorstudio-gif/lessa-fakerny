package app.lessafakerny.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Lecture::class, Exam::class, AppSettings::class, AttendanceRecord::class, LearningMaterial::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lectureDao(): LectureDao
    abstract fun examDao(): ExamDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun learningMaterialDao(): LearningMaterialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE app_settings ADD COLUMN is24HourFormat INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE app_settings ADD COLUMN userRole TEXT NOT NULL DEFAULT 'STUDENT'")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS learning_materials (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        subjectName TEXT NOT NULL,
                        content TEXT NOT NULL,
                        resourceLink TEXT NOT NULL,
                        addedDateMillis INTEGER NOT NULL,
                        authorRole TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lessafakerny_database"
                )
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
