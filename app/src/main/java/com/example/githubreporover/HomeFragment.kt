package com.example.githubreporover

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubreporover.adapter.RepoAdapter
import com.example.githubreporover.adapter.RepoEntityAdapter
import com.example.githubreporover.data.Repo
import com.example.githubreporover.databinding.FragmentHomeBinding
import com.example.githubreporover.model.RepoRoverDBViewModel
import com.example.githubreporover.model.RepoRoverDBViewModelFactory
import com.example.githubreporover.model.RepoRoverViewModel


class HomeFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener {

    private lateinit var repoAdapter: RepoAdapter
    private lateinit var repoEntityAdapter: RepoEntityAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RepoRoverViewModel by activityViewModels()
    private lateinit var searchView: SearchView
    private val dbViewModel: RepoRoverDBViewModel by activityViewModels {
        RepoRoverDBViewModelFactory(
            (activity?.application as GitHubRepoRoverApplication).database.repoRoverDao()
        )
    }
    private var isNetworkConnected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //option menu implementation
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isNetworkConnected = dbViewModel.isNetworkConnected(requireContext())


        setUpAdapters()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

    }

    private fun handleBackButtonOnSearch(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                //handling goBack stack off web view
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        setUpAdapters()
                    }
                }
            })
    }

    private fun setUpAdapters(){
        try {
            if(isNetworkConnected) {
                setRepoAdapter(true)
                dbViewModel.deleteAllCacheRepo()
                populateRepoAdapter()
            }else{
                setRepoEntityAdapterOffline()
                dbViewModel.getAllCacheRepo().observe(this.viewLifecycleOwner){
                    repoEntityAdapter.submitList(it)
                }
            }
        } catch (e: Exception) {
            Log.e("crash", "crash")
        }

    }

    private fun setRepoAdapter(isCacheData: Boolean){
        repoAdapter = RepoAdapter({
            getDetailsOfRepo(it)
        }, dbViewModel,isCacheData,viewModel,this.viewLifecycleOwner)

        // To stop blinking on update of data base
        repoAdapter.setHasStableIds(true)

        binding.repoRecyclerView.adapter = repoAdapter
        binding.repoRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }


    private fun setRepoEntityAdapterOffline(){
        repoEntityAdapter = RepoEntityAdapter {
            getDetailsOfRepo(it.getRepo())
        }
        // To stop blinking on update of data base
        repoEntityAdapter.setHasStableIds(true)

        binding.repoRecyclerView.adapter = repoEntityAdapter
        binding.repoRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun populateRepoAdapter(){
        val gitHubUserName = requireActivity().getSharedPreferences(
            "com.example.githubreporover.setting",
            Context.MODE_PRIVATE
        ).getString("UserId","")
        viewModel.getReposByUserName(gitHubUserName!!)
        viewModel.repo.observe(requireActivity()) { values ->
            values.let {
                binding.homeLoading.visibility = View.VISIBLE
                repoAdapter.submitList(it)
                binding.homeLoading.visibility = View.GONE
            }
        }
    }

    private fun refreshData() {
        if(dbViewModel.isNetworkConnected(requireContext())){
            setRepoAdapter(true)
            populateRepoAdapter()
        }
        binding.swipeRefreshLayout.isRefreshing = false // hide the spinner
    }

    private fun getDetailsOfRepo(repo: Repo) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToRepoDetailsFragment(repo.getParcelizeRepo())
        this.findNavController().navigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.app_bar_search)
        searchView = menuItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.isIconifiedByDefault = false
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = "Type here to search "
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
            val actionBarDrawerToggle = ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                R.string.nav_open,
                R.string.nav_close
            )
            drawerLayout.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()
            if (actionBarDrawerToggle.onOptionsItemSelected(menuItem))
                return true
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.queryHint = "Type here to search in Github"
        setRepoAdapter(false)
        binding.homeLoading.visibility = View.VISIBLE
        viewModel.findPublicRepo(query!!)
        viewModel.publicRepo.observe(this.viewLifecycleOwner) { list ->
            list.let {
                repoAdapter.submitList(it.items)
                binding.homeLoading.visibility = View.GONE
            }
        }
        handleBackButtonOnSearch()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}