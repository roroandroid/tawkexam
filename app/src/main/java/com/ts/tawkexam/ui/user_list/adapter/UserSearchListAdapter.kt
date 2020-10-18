package com.ts.tawkexam.ui.user_list.adapter

import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.ts.tawkexam.R
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.ui.user_list.main.UserListCallback
import com.ts.tawkexam.utils.inflater
import com.ts.tawkexam.utils.negativeColorFilter
import kotlinx.android.synthetic.main.user_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class UserSearchListAdapter(private val users: ArrayList<User>, private val listener: UserListCallback) :
    RecyclerView.Adapter<UserSearchListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            parent.inflater(R.layout.user_item)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = run {
        holder.setIsRecyclable(false)
        holder.bind(users[position], listener, position)
    }

    override fun getItemCount(): Int = users.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            user: User,
            listener: UserListCallback,
            position: Int
        ) = with(itemView) {
            tvUsername.text = user.name ?: context.getString(R.string.no_name)
            tvUserDetails.text = user.bio ?: context.getString(R.string.no_bio)

            //Get every fourth user in list and invert color of image
            val inverted = user.roomId  % 4 == 0
            if(inverted) {
                civUserImage.colorFilter = ColorMatrixColorFilter(negativeColorFilter)
            }

            //If user has note, display note icon
            ivNote.isVisible = user.note != null && user.note.isNotEmpty()

            Glide.with(this)
                .load(user.avatarUrl)
                .apply(RequestOptions().error(R.drawable.default_avatar))
                .into(civUserImage)


            itemView.onClick { listener.onViewProfile(user, inverted) }

        }
    }

    fun addUsers(userList: List<User>) {
        users.addAll(userList)
        notifyDataSetChanged()
    }

    fun addToList(user: User) {
        users.add(user)
        notifyItemInserted(users.size)
    }

    fun clear() {
        users.clear()
        notifyDataSetChanged()
    }
}



