package app.lessafakerny.android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningMaterialDao {
    @Query("SELECT * FROM learning_materials ORDER BY addedDateMillis DESC")
    fun getAllMaterials(): Flow<List<LearningMaterial>>

    @Query("SELECT * FROM learning_materials WHERE subjectName = :subjectName ORDER BY addedDateMillis DESC")
    fun getMaterialsForSubject(subjectName: String): Flow<List<LearningMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: LearningMaterial): Long

    @Update
    suspend fun updateMaterial(material: LearningMaterial)

    @Delete
    suspend fun deleteMaterial(material: LearningMaterial)

    @Query("DELETE FROM learning_materials")
    suspend fun clearAll()
}
