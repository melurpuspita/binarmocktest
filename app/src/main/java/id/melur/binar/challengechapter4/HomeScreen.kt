package id.melur.binar.challengechapter4

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.melur.binar.challengechapter4.adapter.NoteActionListener
import id.melur.binar.challengechapter4.adapter.NoteAdapter
import id.melur.binar.challengechapter4.database.Note
import id.melur.binar.challengechapter4.database.NoteDatabase
import id.melur.binar.challengechapter4.databinding.FragmentHomeScreenBinding
import kotlinx.coroutines.*

class HomeScreen : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private var dataUsername: String? = ""
    private lateinit var noteAdapter: NoteAdapter
    private var mDb: NoteDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
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
        sharedPref = requireContext().getSharedPreferences("username", Context.MODE_PRIVATE)
        getData()

        val username = sharedPref.getString("username", "")
        binding.tvWelcome.text = "Welcome, $username!"
//        val user = 1
        mDb = NoteDatabase.getInstance(requireContext())
        checkRegisteredNote()
        initRecyclerView()
        getDataFromDb()
        logoutButtonOnPressed()
        AddButtonOnPressed()
    }

    private fun logoutButtonOnPressed() {
        binding.tvLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            dataUsername = sharedPref.getString("username", "")
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            // ngecek user udh ilang apa blom
//            Toast.makeText(requireContext(), "username adalah $dataUsername", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_homeScreen_to_loginScreen)
        }
    }

    private fun AddButtonOnPressed() {
        binding.addButton.setOnClickListener {
            showAlertDialog(null)
            val dataUsername = sharedPref.getString("username", "ini default value")
        }
    }

    private fun getData() {
        dataUsername = sharedPref.getString("username", "")
    }

    private fun showAlertDialog(note: Note?) {
        getData()
        val customLayout =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog, null, false)

        val tvTitle = customLayout.findViewById<TextView>(R.id.textView2)
        val etTitle = customLayout.findViewById<EditText>(R.id.etTitle)
        val etNote = customLayout.findViewById<EditText>(R.id.etNote)
        val btnSave = customLayout.findViewById<Button>(R.id.btnSave)

        val builder = AlertDialog.Builder(requireContext())

        builder.setView(customLayout)

        val dialog = builder.create()

        if (note != null) {
            tvTitle.text = "Edit Data"
            etTitle.setText(note.title)
            etNote.setText(note.note)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val notes = etNote.text.toString()
            val username = dataUsername.toString()

            if (note != null) {
                val newNote = Note(note.noteId, note.username, title, notes)
                updateToDb(newNote)
                dialog.dismiss()
            } else {
                binding.ivEmpty.isVisible = false
                binding.tvEmpty.isVisible = false
                saveToDb(username, title, notes)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showDeleteDialog(note: Note) {
        getData()
        val customLayout =
            LayoutInflater.from(requireContext()).inflate(R.layout.delete_dialog, null, false)

        val btnDelete = customLayout.findViewById<Button>(R.id.btnDelete1)
        val btnCancel = customLayout.findViewById<Button>(R.id.btnCancel)

        val builder = AlertDialog.Builder(requireContext())

        builder.setView(customLayout)

        val dialog = builder.create()

        btnDelete.setOnClickListener {
            deleteItemDb(note)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initRecyclerView() {
        binding.apply {
            noteAdapter = NoteAdapter({}, {}, action)
            rvData.adapter = noteAdapter
            rvData.layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private val action = object : NoteActionListener {
        override fun onDelete(note: Note) {
            showDeleteDialog(note)
        }

        override fun onEdit(note: Note) {
            showAlertDialog(note)
        }
    }


    private fun deleteItemDb(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.noteDao()?.deleteNote(note)
            if (result != 0) {
                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Dihapus", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getDataFromDb() {
//        getData()
        CoroutineScope(Dispatchers.IO).launch {
            val username = dataUsername.toString()
            val result = mDb?.noteDao()?.getAllNotes(username = username)
//            val result = mDb?.noteDao()?.getAllNotes()
            if (result != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    noteAdapter.updateData(result)
                }
            }
        }
    }

    private fun saveToDb(username: String, title: String, note: String) {
        getData()
//        val username = dataUsername.toString()
        val note = Note(null, username, title, note)
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.noteDao()?.insertNote(note)
            if (result != 0L) {
                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateToDb(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.noteDao()?.updateNote(note)
            if (result != 0) {
                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Diupdate", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkRegisteredNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val username = dataUsername.toString()
            val result = mDb?.noteDao()?.getAllNotes(username)
            if (result.isNullOrEmpty()) {
                binding.ivEmpty.isVisible = true
                binding.tvEmpty.isVisible = true
            }
            if (!result.isNullOrEmpty()) {
                binding.ivEmpty.isVisible = false
                binding.tvEmpty.isVisible = false
            }
        }
    }
}