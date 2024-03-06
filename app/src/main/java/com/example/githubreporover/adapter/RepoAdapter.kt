package com.example.githubreporover.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubreporover.data.Repo
import com.example.githubreporover.databinding.ListItemRepoOverviewBinding
import com.example.githubreporover.model.RepoRoverDBViewModel
import com.example.githubreporover.model.RepoRoverViewModel

class RepoAdapter(
    private val onItemClicked: (Repo) -> Unit,
    private val viewModel: RepoRoverDBViewModel,
    private val isNetworkConnected: Boolean,
    private val repoRoverViewModel: RepoRoverViewModel,
    private val viewLifecycle: LifecycleOwner
) :
    ListAdapter<Repo, RepoAdapter.RepoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepoViewHolder {
        return RepoViewHolder(
            ListItemRepoOverviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            ), viewModel, isNetworkConnected
        )
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current, repoRoverViewModel, viewLifecycle)
    }

    //prevent the item to repeat on scroll
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class RepoViewHolder(
        private var binding: ListItemRepoOverviewBinding,
        private val viewModel: RepoRoverDBViewModel,
        private val isNetworkConnected: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(repo: Repo, repoViewModel: RepoRoverViewModel, viewLifecycle: LifecycleOwner) {
            binding.apply {
                repoNameTextview.text = repo.name
                if (repo.description != null) {
                    repoDecriptionTextview.text = repo.description
                } else {
                    repoDecriptionTextview.text = "No Description Added"
                    repo.description = "No Description Added"
                }
                if (isNetworkConnected) {
                    viewModel.insertCacheRepo(repo.getRepoEntity())
                }
//                repoViewModel.getStarsCount(repo.owner!!.login, repo.name)
//                repoViewModel.starsCount.observe(viewLifecycle) { count ->
//                    count.let {
//                        repoStarCount.text = it.toString()
//                        repo.visibility = it.toString()
//                    }
//                }
            }
        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
