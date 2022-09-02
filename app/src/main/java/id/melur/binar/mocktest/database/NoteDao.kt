package id.melur.binar.mocktest.database

import androidx.room.*

@Dao
interface NoteDao {
//    @Query("SELECT * FROM Note")
//    fun getAllNotes(username: String) : List<Note>
    @Query("SELECT * FROM Note")
    fun getAllNotes() : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note) : Long

//    @Insert(onConflict = IGNORE)
//    suspend fun addUser(user: User) : Long

    @Update
    fun updateNote(note: Note) : Int

    @Delete
    fun deleteNote(note: Note) : Int
}