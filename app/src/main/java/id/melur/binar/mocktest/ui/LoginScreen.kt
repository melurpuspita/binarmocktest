package id.melur.binar.mocktest.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.melur.binar.mocktest.R
import id.melur.binar.mocktest.database.NoteDatabase
import id.melur.binar.mocktest.viewmodel.ViewModel
import id.melur.binar.mocktest.databinding.FragmentLoginScreenBinding
import id.melur.binar.mocktest.helper.Repository
import id.melur.binar.mocktest.helper.viewModelsFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginScreen : Fragment() {

    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private var mDb: NoteDatabase? = null
    private lateinit var sharedPref: SharedPreferences



    private val repository : Repository by lazy { Repository(requireContext()) }
    private val viewModel: ViewModel by viewModelsFactory { ViewModel(repository) }

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
        mDb = NoteDatabase.getInstance(requireContext())
        viewModel.userData()
        loginButtonOnPressed()
        observeData()
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
                viewModel.checkRegisteredUser(username, password)
                Handler(Looper.getMainLooper()).postDelayed({
                    // This method will be executed once the timer is over
                    if (viewModel.isLogin.value == true) {
                        Toast.makeText(requireContext(), "Login Berhasil", Toast.LENGTH_SHORT)
                            .show()
                        val editor = sharedPref.edit()
                        editor.putString("username", username)
                        editor.apply()
                        viewModel.checkLogin(username, password)
                        findNavController().navigate(R.id.action_loginScreen_to_homeScreen)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Email atau Password tidak sesuai",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 200)
            }
        }
    }

    private fun observeData() {

        viewModel.notRegistered.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Email belum terdaftar", Toast.LENGTH_SHORT).show()
        }
    }
}
