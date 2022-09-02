package id.melur.binar.mocktest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


}