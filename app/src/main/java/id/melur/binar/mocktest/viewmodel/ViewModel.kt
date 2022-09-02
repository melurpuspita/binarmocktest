package id.melur.binar.mocktest.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.melur.binar.mocktest.database.Note
import id.melur.binar.mocktest.database.User
import id.melur.binar.mocktest.helper.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(private val repository:Repository): ViewModel() {

    val user = MutableLiveData<List<User>>()

    val isLogin = MutableLiveData<Boolean>()
    val usernamee = MutableLiveData<String>()
//    val role = MutableLiveData<String>()
    val notRegistered = MutableLiveData<Boolean>()
    val validateEmailPassword = MutableLiveData<User?>()
    val isDeleted = MutableLiveData<Boolean>()

    fun userData() {
        saveToDb(1, "admin", "admin", "admin", 1)
        saveToDb(2,"Ega", "karyawan1", "karyawan1", 2)
        saveToDb(3,"Surya", "karyawan2", "karyawan2", 2)
        saveToDb(4,"Hendra", "karyawan3", "karyawan3", 2)
        saveToDb(5,"Ijan", "karyawan4", "karyawan4", 2)
        saveToDb(6,"Surti", "karyawan5", "karyawan5", 2)
    }

    fun saveToDb(userId: Int, name: String, username: String, password: String, role: Int) {
        val user = User(userId, name, username, password, role)
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertUser(user)
        }
    }

    fun checkRegisteredUser(username: String, password: String) {
        viewModelScope.launch {
            user.value = repository.getRegisteredUser(username, password)
            if (!user.value.isNullOrEmpty()) {
                isLogin.value = true
                usernamee.value = username
            }
        }
    }

    fun checkLogin(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.getUser(username)
            if (result == null) {
                notRegistered.value = true
            } else {
                if (result.username == username && result.password == password) {
                    validateEmailPassword.value = result
                }
            }
        }
    }


//    fun deleteItemDb(note: Note) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = repository.deleteNote(note)
//            if (result != 0) {
//                getDataFromDb()
//                isDeleted.value = true
//            } else {
//                isDeleted.value = false
//            }
//        }
//    }
//
//
//    fun getDataFromDb() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = repository.getAllNotes()
//            if (result != null) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    noteAdapter.updateData(result)
//                }
//            }
//        }
//    }
//
//    private fun saveNoteToDb(name: String, quantity: String, supplier: String, date: String) {
//        getData()
////        val username = dataUsername.toString()
//        val note = Note(null, name, quantity, supplier, date)
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = mDb?.noteDao()?.insertNote(note)
//            if (result != 0L) {
//                getDataFromDb()
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(requireContext(), "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(requireContext(), "Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun updateToDb(note: Note) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = mDb?.noteDao()?.updateNote(note)
//            if (result != 0) {
//                getDataFromDb()
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(requireContext(), "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(requireContext(), "Gagal Diupdate", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

}