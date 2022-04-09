package id.melur.binar.challengechapter4.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.melur.binar.challengechapter4.R
import id.melur.binar.challengechapter4.database.Note


class NoteAdapter(private val onDelete : (Note) -> Unit,
                  private val onEdit : (Note) -> Unit,
                  private val listener: NoteActionListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val difCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, difCallback)

    fun updateData(note: List<Note>) = differ.submitList(note)
//    fun updateData(users: List<User>) = differ.submitList(users)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvNoteId = view.findViewById<TextView>(R.id.tvNoteId)
        private val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        private val tvNote = view.findViewById<TextView>(R.id.tvNote)
        private val btnDelete = view.findViewById<ImageView>(R.id.btnDelete)
        private val btnEdit = view.findViewById<ImageView>(R.id.btnEdit)

        fun bind(note: Note) {
            tvNoteId.text = note.noteId.toString()
            tvTitle.text = note.title
            tvNote.text = note.note

            btnDelete.setOnClickListener {
                onDelete.invoke(note)
                listener.onDelete(note)
            }

            btnEdit.setOnClickListener {
                onEdit.invoke(note)
                listener.onEdit(note)
            }
        }
    }

}

interface NoteActionListener {
    fun onDelete(note: Note)
    fun onEdit(note: Note)
}