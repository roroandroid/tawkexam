package com.ts.tawkexam.ui.profile

import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ts.tawkexam.R
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.databinding.FragmentProfileBinding
import com.ts.tawkexam.ui.viewmodel.UserViewModel
import com.ts.tawkexam.ui.viewmodel.UserViewModelFactory
import com.ts.tawkexam.utils.negativeColorFilter
import dagger.android.support.DaggerFragment
import org.jetbrains.anko.sdk27.coroutines.onClick
import timber.log.Timber
import javax.inject.Inject

class ProfileView : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: UserViewModelFactory

    private val args: ProfileViewArgs by navArgs()

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)
    }

    private lateinit var user: User

    private var doRefresh: Boolean = false

    private var note: String? = null

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = args.user

        Timber.e("User $user")
        binding.tvName.text = user.name ?: getString(R.string.no_name)
        binding.tvFollowerCount.text = user.followers.toString()
        binding.tvFollowingCount.text = user.following.toString()
        binding.tvCompany.text = user.company ?: getString(R.string.no_company)
        binding.tvBlog.text = user.blog ?: getString(R.string.no_blog)
        note = user.note
        binding.etNote.setText(note)
        binding.btnSave.onClick {
            note = binding.etNote.text.toString()
            viewModel.updateNote(user.id, binding.etNote.text.toString())
            binding.btnSave.isEnabled = false
            doRefresh = true
        }

        val inverted = user.roomId  % 4 == 0
        if(inverted) {
            binding.civProfileImage.colorFilter = ColorMatrixColorFilter(negativeColorFilter)
        }

        Glide.with(this)
            .load(user.avatarUrl)
            .apply(RequestOptions().error(R.drawable.default_avatar))
            .into(binding.civProfileImage)

        initEditTextListener()

        binding.ivBack.onClick { activity?.onBackPressed() }
    }

    private fun initEditTextListener() {
        binding.etNote.doAfterTextChanged {
            val text = it.toString()
            binding.btnSave.isEnabled = text != user.note
        }
    }
}