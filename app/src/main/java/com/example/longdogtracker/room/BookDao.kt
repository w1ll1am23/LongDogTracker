package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): List<RoomBook>

    @Query("SELECT * FROM books WHERE id IS :id")
    fun getBookById(id: Int): RoomBook

    @Insert
    fun insertAll(vararg book: RoomBook)

    @Update
    fun updateBook(book: RoomBook)

}