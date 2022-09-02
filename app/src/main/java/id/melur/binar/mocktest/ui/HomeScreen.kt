package id.melur.binar.mocktest.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.melur.binar.mocktest.R
import id.melur.binar.mocktest.adapter.NoteActionListener
import id.melur.binar.mocktest.adapter.NoteAdapter
import id.melur.binar.mocktest.database.Note
import id.melur.binar.mocktest.database.NoteDatabase
import id.melur.binar.mocktest.databinding.FragmentHomeScreenBinding
import id.melur.binar.mocktest.helper.Repository
import id.melur.binar.mocktest.helper.viewModelsFactory
import id.melur.binar.mocktest.viewmodel.ViewModel
import kotlinx.coroutines.*

class HomeScreen : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private var dataUsername: String? = ""
    private lateinit var noteAdapter: NoteAdapter
    private var mDb: NoteDatabase? = null

    private val repository : Repository by lazy { Repository(requireContext()) }
    private val viewModel: ViewModel by viewModelsFactory { ViewModel(repository) }

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
        binding.tvWelcome.text = "Selamat Datang, $username!"
//        val user = 1
        role()
        mDb = NoteDatabase.getInstance(requireContext())
        initRecyclerView()
        getDataFromDb()
        logoutButtonOnPressed()
        AddButtonOnPressed()
    }

    private fun role(){
        if(dataUsername != "admin"){
            val addButton = view?.findViewById(R.id.addButton) as FloatingActionButton
            val btnDelete = view?.findViewById<ImageView>(R.id.btnDelete)
            val btnEdit = view?.findViewById<ImageView>(R.id.btnEdit)
            addButton.isGone = true
            btnDelete?.isGone = true
            btnEdit?.isGone = true
        }
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
        val etName = customLayout.findViewById<EditText>(R.id.etName)
        val etQuantity = customLayout.findViewById<EditText>(R.id.etQuantity)
        val etSupplier = customLayout.findViewById<EditText>(R.id.etSupplier)
        val etDate = customLayout.findViewById<EditText>(R.id.etDate)
        val btnSave = customLayout.findViewById<Button>(R.id.btnSave)

        val builder = AlertDialog.Builder(requireContext())

        builder.setView(customLayout)

        val dialog = builder.create()

        if (note != null) {
            tvTitle.text = "Edit Data Barang"
            etName.setText(note.name)
            etQuantity.setText(note.quantity)
            etSupplier.setText(note.supplier)
            etDate.setText(note.date)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val quantity = etQuantity.text.toString()
            val supplier = etSupplier.text.toString()
            val date = etDate.text.toString()

            if (note != null) {
                val newNote = Note(note.noteId, name, quantity, supplier, date)
                updateToDb(newNote)
                dialog.dismiss()
            } else {
                saveToDb(name, quantity, supplier, date)
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = mDb?.noteDao()?.getAllNotes()
            if (result != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    noteAdapter.updateData(result)
                }
            }
        }
    }

    private fun saveToDb(name: String, quantity: String, supplier: String, date: String) {
        getData()
//        val username = dataUsername.toString()
        val note = Note(null, name, quantity, supplier, date)
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
}