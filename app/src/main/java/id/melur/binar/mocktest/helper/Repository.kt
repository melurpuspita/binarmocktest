package id.melur.binar.mocktest.helper

import android.content.Context
import id.melur.binar.mocktest.database.Note
import id.melur.binar.mocktest.database.NoteDatabase
import id.melur.binar.mocktest.database.User
import id.melur.binar.mocktest.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(context: Context) {

    private val mDbUser = UserDatabase.getInstance(context)
    private val mDbNote = NoteDatabase.getInstance(context)

    suspend fun getDataUser() = withContext(Dispatchers.IO) {
        mDbUser?.userDao()?.getAllUser()
    }

//    suspend fun updateUser(username: String, name: String, dateOfBirth: String, address: String) = withContext(
//        Dispatchers.IO) {
//        mDb?.userDao()?.updateUser(username, name, dateOfBirth, address)
//    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        mDbUser?.userDao()?.insertUser(user)
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        mDbNote?.noteDao()?.deleteNote(note)
    }

    suspend fun getAllNotes() = withContext(Dispatchers.IO) {
        mDbNote?.noteDao()?.getAllNotes()
    }

    suspend fun getRegisteredUser(username: String, password: String) = withContext(Dispatchers.IO) {
        mDbUser?.userDao()?.getRegisteredUser(username, password)
    }

    suspend fun getUser(username: String) = withContext(Dispatchers.IO) {
        mDbUser?.userDao()?.getUser(username)
    }
//    suspend fun getUser(username: String) = withContext(Dispatchers.IO) {
//        mDb?.userDao()?.getUser(username)
//    }
//
//    suspend fun coba(username: String) = withContext(Dispatchers.IO) {
//        mDb?.userDao()?.getUser(username)
//    }
}