package com.ts.tawkexam.ui.user_list.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.ts.tawkexam.base.Outcome
import com.ts.tawkexam.R
import com.ts.tawkexam.base.GenericFragment
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.databinding.FragmentUserListBinding
import com.ts.tawkexam.ui.user_list.adapter.UserListLoadStateAdapter
import com.ts.tawkexam.ui.user_list.adapter.UserListPagingAdapter
import com.ts.tawkexam.ui.user_list.adapter.UserSearchListAdapter
import com.ts.tawkexam.ui.viewmodel.UserViewModel
import com.ts.tawkexam.ui.viewmodel.UserViewModelFactory
import com.ts.tawkexam.utils.hideKeyboard
import org.jetbrains.anko.support.v4.longToast
import timber.log.Timber
import javax.inject.Inject


class UserListView : GenericFragment(),
    UserListCallback {

    var hasNetworkConnection: Boolean = true

    @Inject
    lateinit var viewModelFactory: UserViewModelFactory

    lateinit var userSearchListSearchAdapter: UserSearchListAdapter

    lateinit var userListPagingAdapter: UserListPagingAdapter

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)
    }


    private lateinit var binding: FragmentUserListBinding

    enum class PageState {
        LOADING,
        LOADED,
        ERROR
    }

    private var persistingView: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.e("onCreateView")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        persistingView = binding.root
        initComponents()
        initSearchViewListener()
        startLoadData()
        return persistingView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("onViewCreated")
        Timber.e("onState $state")
    }

    override fun onResume() {
        super.onResume()
        Timber.e("onResume")
    }

    private fun initComponents() {

        // Initialize Search RecyclerViewAdapter
        userSearchListSearchAdapter =
            UserSearchListAdapter(
                arrayListOf(),
                this
            )
        binding.rvUsersSearch.adapter = userSearchListSearchAdapter
        //Retain scroll position on configuration change
        userSearchListSearchAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.somethingWentWrongPage.btnRetry.setOnClickListener {
            onRetry()
        }


        // Initialize Paging 3.0 Adapter
        userListPagingAdapter =
            UserListPagingAdapter(
                requireContext(),
                this
            )
        //Retain scroll position on configuration change
        userListPagingAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListPagingAdapter
            //Footer loading item / error item
            adapter = userListPagingAdapter.withLoadStateFooter(
                footer = UserListLoadStateAdapter {
                    userListPagingAdapter.retry()
                }
            )
        }
        userListPagingAdapter.addLoadStateListener { loadState ->
            val errorState = loadState.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
            if (state != PageState.LOADED) {
                errorState?.let {
                    /*
                    Refresh action by RemoteMediator has returned an error, display something went wrong page
                    */
                    state =
                        PageState.ERROR
                }
            }
        }

        //Listener to check if list has been populated
        userListPagingAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                //itemCount > 1 means DB has been populated
                if (itemCount > 1) {
                    binding.svUser.isVisible = true
                    state =
                        PageState.LOADED
                }
            }
        })

    }

    /*
    Handle page state if loading/loaded/error
     */
    private var state: PageState =
        PageState.LOADING
        set(value) {
            if (state != PageState.LOADED) {
                when (value) {
                    PageState.LOADING -> {
                        binding.somethingWentWrongPage.clSomethingWentWrong.isVisible = false
                        binding.shimmerViewContainer.startShimmerAnimation()
                        binding.shimmerViewContainer.isVisible = true
                        binding.rvUsers.isVisible = false
                        binding.svUser.isVisible = false
                    }
                    PageState.LOADED -> {
                        binding.somethingWentWrongPage.clSomethingWentWrong.isVisible = false
                        binding.shimmerViewContainer.stopShimmerAnimation()
                        binding.shimmerViewContainer.isVisible = false
                        binding.rvUsers.isVisible = true
                        binding.svUser.isVisible = true
                    }
                    PageState.ERROR -> {
                        binding.somethingWentWrongPage.clSomethingWentWrong.isVisible = true
                        binding.shimmerViewContainer.stopShimmerAnimation()
                        binding.shimmerViewContainer.isVisible = false
                        binding.rvUsers.isVisible = true
                        binding.svUser.isVisible = true
                    }
                }
                field = value
            }
        }

    /*
    Hide full user list to display search list
     */
    var isSearching: Boolean = false
        set(value) {
            field = value
            binding.rvUsersSearch.isVisible = value
            binding.rvUsers.isVisible = !value
        }


    private fun startLoadData() {
        compositeDisposable.add(viewModel.getAllUsers().subscribe {
            userListPagingAdapter.submitData(lifecycle, it)
        })
        if (hasNetworkConnection) {
            state = PageState.LOADING
        } else
            if (state != PageState.LOADED)
                state = PageState.ERROR
    }

    /*
    OnClick retry for something went wrong page and paging footer item error
     */
    private fun onRetry() {
        if (hasNetworkConnection) {
            if (state != PageState.LOADED) {
                state = PageState.LOADING
                userListPagingAdapter.retry()
            }
        } else
            longToast(getString(R.string.please_check_your_network_connection))

    }


    /*
    Observer for Search List User Adapter
     */

    private fun initSearchViewListener() {
        binding.svUser.setOnClickListener {
            binding.svUser.onActionViewExpanded()
        }

        binding.svUser.setIconifiedByDefault(false)
        binding.svUser.findViewById<ImageView>(R.id.search_close_btn).setOnClickListener {
            isSearching = false
            binding.svUser.setQuery("", false);
            hideKeyboard()
        }

        //Search View listener, query local DB for users
        binding.svUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != "") {
                    Timber.e("newText $newText")
                    viewModel.searchUserWithKeyword(newText)
                    isSearching = true
                } else {
                    isSearching = false
                }
                return false
            }
        })
        viewModel.userListSearchOutcome.observe(
            viewLifecycleOwner,
            Observer<Outcome<List<User>>> { outcome ->
                when (outcome) {
                    is Outcome.Progress -> userSearchListSearchAdapter.clear()
                    is Outcome.Success -> {
                        if (binding.rvUsersSearch.isVisible) {
                            Timber.e("Users " + outcome.data)
                            userSearchListSearchAdapter.addUsers(outcome.data)
                        }
                    }
                }
            })
    }

    /*
    Navigate to Profile Page supplying user data and inverted data
     */
    override fun onViewProfile(user: User, inverted: Boolean) {
        val action =
            UserListViewDirections.actionUserListViewToProfileView(
                user
            )
        findNavController().navigate(action)
    }


    override fun onInternetConnect() {
        hasNetworkConnection = true
        onRetry()
    }

    override fun onInternetDisconnect() {
        hasNetworkConnection = false
    }

}