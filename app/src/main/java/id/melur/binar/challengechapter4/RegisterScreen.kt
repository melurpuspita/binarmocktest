package id.melur.binar.challengechapter4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import id.melur.binar.challengechapter4.database.User
import id.melur.binar.challengechapter4.database.UserDatabase
import id.melur.binar.challengechapter4.databinding.FragmentLoginScreenBinding
import id.melur.binar.challengechapter4.databinding.FragmentRegisterScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterScreen : Fragment() {

    private var _binding: FragmentRegisterScreenBinding? = null
    private val binding get() = _binding!!

//    private lateinit var userAdapter: UserAdapter
    private var mDb: UserDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDb = UserDatabase.getInstance(requireContext())
//        getDataFromDb()
        registerButtonOnPressed()
    }

    private fun registerButtonOnPressed() {
        binding.btnRegister.setOnClickListener {

            val username = binding.inputUsername.text.toString()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val password1 = binding.inputConfirmPass.text.toString()

            if (username != "" && email != "" && password != "" && password1 != "") {
                if (password == password1) {
                    saveToDb(username, email, password)
                    findNavController().navigate(R.id.action_registerScreen_to_loginScreen)
                }
                if (password != password1) {
                    createToast("Password dan Confirm Password tidak sama").show()
                }
            } else {
                createToast("Field tidak boleh kosong").show()
            }
        }
    }

    private fun createToast(message: String): Toast {
        return Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
    }

    private fun saveToDb(username: String, email: String, password: String) {
        val user = User(null, username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.userDao()?.insertUser(user)
            if (result != 0L) {
//                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Registrasi", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Registrasi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun getDataFromDb() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = mDb?.userDao()?.getAllUser()
//            if (result != null) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    userAdapter.updateData(result)
//                }
//            }
//        }
//    }
}