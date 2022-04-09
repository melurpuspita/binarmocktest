package id.melur.binar.challengechapter4.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId : Int?,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String
)