package com.ts.tawkexam.ui.user_list.main

import com.ts.tawkexam.data_source.model.User

interface UserListCallback {
    fun onViewProfile(id: User, inverted: Boolean)
}
