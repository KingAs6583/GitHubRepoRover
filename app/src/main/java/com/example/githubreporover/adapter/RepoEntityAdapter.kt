package com.example.githubreporover.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubreporover.data.RepoEntity
import com.example.githubreporover.databinding.ListItemRepoOverviewBinding

class RepoEntityAdapter(
    private val onItemClicked: (RepoEntity) -> Unit
) :
    ListAdapter<RepoEntity, RepoEntityAdapter.RepoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepoViewHolder {
        return RepoViewHolder(
            ListItemRepoOverviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    //prevent the item to repeat on scroll
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class RepoViewHolder(
        private var binding: ListItemRepoOverviewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(repo: RepoEntity) {
            binding.apply {
                repoNameTextview.text = repo.name
                if(repo.description != null){
                    repoDecriptionTextview.text = repo.description
                }else{
                    repoDecriptionTextview.text = "No Description Added"
                    repo.description = "No Description Added"
                }
            }
        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RepoEntity>() {
            override fun areItemsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
