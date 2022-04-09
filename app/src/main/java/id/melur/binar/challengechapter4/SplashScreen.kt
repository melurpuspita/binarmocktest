package id.melur.binar.challengechapter4

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.melur.binar.challengechapter4.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay

class SplashScreen : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private var dataUsername: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPref = context.getSharedPreferences("username", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val editor = sharedPref.edit()
//        editor.clear()
//        editor.apply()

        dataUsername = sharedPref.getString("username", "")
        lifecycleScope.launchWhenCreated {
            splashToLogin()
        }
    }

    private suspend fun splashToLogin() {
        delay(2000)
        if (dataUsername == "") {
            findNavController().navigate(R.id.action_splashScreen_to_loginScreen)
        } else {
            findNavController().navigate(R.id.action_splashScreen_to_homeScreen)
        }
    }
}