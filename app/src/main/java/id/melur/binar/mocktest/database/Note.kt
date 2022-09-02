package id.melur.binar.mocktest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId : Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: String,
    @ColumnInfo(name = "supplier") val supplier: String,
    @ColumnInfo(name = "date") val date: String

)