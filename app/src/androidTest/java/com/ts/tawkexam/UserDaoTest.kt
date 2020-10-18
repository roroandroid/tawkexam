package com.ts.tawkexam

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ts.tawkexam.data_source.local.UserDao
import com.ts.tawkexam.data_source.local.UserDatabase
import com.ts.tawkexam.data_source.model.User
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.contains
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type


lateinit var userList: List<User>
lateinit var db: UserDatabase
lateinit var userDao: UserDao
lateinit var context: Context

class UserDaoTest {

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        db =
            Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).allowMainThreadQueries()
                .build()
        userDao = db.userDao()

        userList = listOf(
            User(id = 1, name = "One", login = "OneLogin", note = "First Note"),
            User(id = 2, name = "Two", login = "TwoLogin"),
            User(id = 3, name = "Three", login = "ThreeLogin", note = "Third Note"),
            User(id = 4, name = "Four", login = "FourLogin"),
            User(id = 5, name = "Five", login = "FiveLogin", note = "ZeroOneTwoThreeFour"),
            User(id = 6, name = "Six", login = "SixLogin", note = "Sixth Note")
        )

        userDao.upsertAll(userList)

        userDao.updateNoteInDb(3, "Third Note")
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUsers_ifUsersInserted_returnListOfUsers() {
        //Given
        val givenUserList = listOf(
            User(id = 7, login = "SevenLogin", name = "Seven"),
            User(id = 8, login = "EightLogin", name = "Eight"),
            User(id = 9, login = "NineLogin", name = "Nine")
        )
        //When
        userDao.upsertAll(givenUserList)

        //Result
        //Used another Dao with same query since no best way to retrieve PagingSource data
        val userDataInDatabase: List<User> = userDao.testGetAllUsers()
        assertTrue(userDataInDatabase.containsAll(givenUserList))
    }


    @Test
    fun updateNoteForUserWithoutNote_ifNoteUpdated_returnUpdatedNote() {
        //Given
        val note = "User is good"
        val userId = 2

        //When
        userDao.updateNoteInDb(userId, note)

        //Result
        val userDataInDatabase: User = userDao.searchUserById(userId)
        assertEquals(userDataInDatabase.note, note)
    }

    @Test
    fun updateNoteForUserWithNonNullNote_ifNoteUpdated_returnUpdatedNote() {
        //Given
        val note = "This is my new note"
        val userId = 1

        //When
        userDao.updateNoteInDb(userId, note)

        //Result
        val userDataInDatabase: User = userDao.searchUserById(userId)
        assertEquals(userDataInDatabase.note, note)
    }

    @Test
    fun searchUserByNote_ifUsersSearchedByKeywordUsingNote_returnUsers() {
        //Given
        val note = "ote"
        val keyword = "%$note%"

        //When
        val users = userDao.searchUserByKeyword(keyword).blockingGet()

        //Result
        assertTrue("User list is empty", users.isNotEmpty())
        for(user in users){
            assertTrue("User note: ${user.note} does not contain $note", user.note!!.contains(note))
        }
    }

    @Test
    fun searchUserByName_ifUsersSearchedByKeywordUsingName_returnUsers() {
        //Given
        val name = "ix"
        val keyword = "%$name%"

        //When
        val users = userDao.searchUserByKeyword(keyword).blockingGet()

        //Result
        Timber.e("RESULT $users")
        assertTrue("User list is empty", users.isNotEmpty())
        for(user in users){
            assertTrue("User note: ${user.name} does not contain $name", user.note!!.contains(name))
        }
    }

    @Test
    fun searchUserByNameAndNote_ifUsersSearchedByKeywordUsingNameAndNote_returnUsers() {
        //Given
        val note = "One"
        val keyword = "%$note%"

        //When
        val users = userDao.searchUserByKeyword(keyword).blockingGet()

        //Result
        assertEquals(users.size, 2)
    }

}
