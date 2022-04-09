package id.melur.binar.challengechapter4

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.melur.binar.challengechapter4.database.UserDatabase
import id.melur.binar.challengechapter4.databinding.FragmentLoginScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginScreen : Fragment() {

    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private var mDb: UserDatabase? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences("username", Context.MODE_PRIVATE)
        mDb = UserDatabase.getInstance(requireContext())
//        getDataFromDb()
        regisButtonOnPressed()
        loginButtonOnPressed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPref = context.getSharedPreferences("username", Context.MODE_PRIVATE)
    }

    private fun loginButtonOnPressed() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (username != "" && password != "") {
                checkRegisteredUser(username, password)
            } else {
                Toast.makeText(requireContext(), "Field tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun regisButtonOnPressed() {
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreen_to_registerScreen)
        }
    }

    private fun checkRegisteredUser(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.userDao()?.getRegisteredUser(username, password)
            if (!result.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Login berhasil", Toast.LENGTH_SHORT).show()
                    val editor = sharedPref.edit()
                    editor.putString("username", username)
                    editor.apply()
//                    Toast.makeText(requireContext(), username, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginScreen_to_homeScreen)
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Username atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getDataFromDb() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.userDao()?.getAllUser()
            if (result != null) {
                CoroutineScope(Dispatchers.Main).launch {
//                    userAdapter.updateData(result)
                }
            }
        }
    }

}
