package com.ts.tawkexam.ui.user_list.adapter

import android.content.Context
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ts.tawkexam.R
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.ui.user_list.main.UserListCallback
import com.ts.tawkexam.utils.negativeColorFilter
import kotlinx.android.synthetic.main.user_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class UserListPagingAdapter(val context: Context, private val listener: UserListCallback) :
    PagingDataAdapter<User, UserListPagingAdapter.ViewHolder>(
        UserComparator
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        getItem(position)?.let {
            holder.bind(it, position, context, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_item, parent, false)
        )
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            user: User,
            position: Int,
            context: Context,
            listener: UserListCallback
        ) {
            itemView.tvUsername.text = user.name ?: context.getString(R.string.no_name)
            itemView.tvUserDetails.text = user.bio ?: context.getString(R.string.no_bio)

            //Get every fourth user in list and invert color of image
            val inverted = user.roomId  % 4 == 0
            if(inverted) {
                itemView.civUserImage.colorFilter = ColorMatrixColorFilter(negativeColorFilter)
            }

            //If user has note, display note icon
            itemView.ivNote.isVisible = user.note != null && user.note.isNotEmpty()

            Glide.with(context)
                .load(user.avatarUrl)
                .apply(RequestOptions().error(R.drawable.default_avatar))
                .into(itemView.civUserImage)


            itemView.onClick { listener.onViewProfile(user, inverted) }
        }
    }

    object UserComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

}
