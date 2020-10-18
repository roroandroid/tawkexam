package com.ts.tawkexam

import com.ts.tawkexam.data_source.model.User
import org.junit.Before
import org.junit.Test

class UserTest {

    //All fields of User is val, as it is not necessary for direct value change

    @Test(expected = IllegalArgumentException::class)
    fun createUserWithNegativeId_ReturnException(){
        val user = User(roomId = -1, id = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createUserWithZeroId_ReturnException(){
        val user = User(roomId = 1, id = 0)

    }

    @Test(expected = IllegalArgumentException::class)
    fun createUserWithNullLogin_ReturnException(){
        val user = User(roomId = 1, id = 2, login = null)
    }
}