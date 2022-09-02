package id.melur.binar.mocktest.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAllUser() : List<User>

    @Query("SELECT * FROM User WHERE username = :query")
    fun getUser(query: String): User

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    fun getRegisteredUser(username: String, password: String) : List<User>

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User) : Long

//    @Insert(onConflict = IGNORE)
//    suspend fun addUser(user: User) : Long

    @Update
    fun updateUser(user: User) : Int

    @Delete
    fun deleteUser(user: User) : Int
}