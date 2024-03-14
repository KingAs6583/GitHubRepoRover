package com.example.githubreporover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubreporover.adapter.RepoEntityAdapter
import com.example.githubreporover.data.Repo
import com.example.githubreporover.databinding.FragmentFavRepoBinding
import com.example.githubreporover.model.RepoRoverDBViewModel
import com.example.githubreporover.model.RepoRoverDBViewModelFactory

class FavRepoFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener  {

    private lateinit var repoEntityAdapter: RepoEntityAdapter
    private val dbViewModel: RepoRoverDBViewModel by activityViewModels {
        RepoRoverDBViewModelFactory(
            (activity?.application as GitHubRepoRoverApplication).database.repoRoverDao()
        )
    }
    private var _binding: FragmentFavRepoBinding? = null
    private lateinit var searchView: SearchView

    private val binding get() = _binding!!
    private var isSearched = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
        // Inflate the layout for this fragment
        _binding = FragmentFavRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRepoEntityAdapterOffline()
        dbViewModel.getAllFavRepo().observe(viewLifecycleOwner){
            repoEntityAdapter.submitList(it)
        }
    }

    private fun handleBackButtonOnSearch(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                //handling goBack stack off web view
                override fun handleOnBackPressed() {
                    if(isSearched){
                        setRepoEntityAdapterOffline()
                        dbViewModel.getAllFavRepo().observe(viewLifecycleOwner){
                            repoEntityAdapter.submitList(it)
                        }
                        isSearched = false
                    }
                    else if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }


    private fun setRepoEntityAdapterOffline(){
        repoEntityAdapter = RepoEntityAdapter {
            getDetailsOfRepo(it.getRepo())
        }
        // To stop blinking on update of data base
        repoEntityAdapter.setHasStableIds(true)

        binding.favRepoRecyclerView.adapter = repoEntityAdapter
        binding.favRepoRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun getDetailsOfRepo(repo: Repo) {
        val action =
            FavRepoFragmentDirections.actionFavRepoFragmentToRepoDetailsFragment(repo.getParcelizeRepo(),true)
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
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        setSearchData(newText)
        return true
    }

    private  fun setSearchData(searchQuery: String?){
        val query = "%$searchQuery%"
        setRepoEntityAdapterOffline()
        dbViewModel.searchFavRepoData(query).observe(viewLifecycleOwner){
            repoEntityAdapter.submitList(it)
        }
        isSearched = true
        handleBackButtonOnSearch()
    }
}