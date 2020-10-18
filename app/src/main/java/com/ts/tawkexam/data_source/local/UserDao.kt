package com.ts.tawkexam.data_source.local

import androidx.paging.PagingSource
import androidx.room.*
import com.ts.tawkexam.data_source.model.User
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllUsers(): PagingSource<Int, User>

    //Used only for Unit Test
    @Query("SELECT * FROM user ORDER BY id ASC")
    fun testGetAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(users: List<User>)

    @Query("SELECT * FROM user WHERE name LIKE :keyword OR note LIKE :keyword")
    fun searchUserByKeyword(keyword: String): Single<List<User>>

    //Used only for Unit Test
    @Query("SELECT * FROM user WHERE id LIKE :id")
    fun searchUserById(id: Int): User

    @Query("SELECT id FROM user ORDER BY id DESC LIMIT 1")
    fun getLastId(): Int

    @Query("SELECT id FROM user ORDER BY id ASC LIMIT 1")
    fun getFirstId(): Int

    @Query("DELETE FROM user")
    fun deleteUsers()

    @Query("UPDATE user SET note = :note WHERE id = :id")
    fun updateNoteInDb(id: Int, note: String)


}