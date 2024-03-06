package com.example.githubreporover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.githubreporover.data.ParcelizeRepo
import com.example.githubreporover.databinding.FragmentRepoDetailsBinding

class RepoDetailsFragment : Fragment() {

    private var _binding: FragmentRepoDetailsBinding? = null
    private val binding get() = _binding!!
    private val navigationArgs: RepoDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValues()
    }

    private fun setValues(){
        val repo : ParcelizeRepo = navigationArgs.Repo
        binding.repoTitle.text = repo.name
        binding.repoOwner.text = getString(R.string.repo_owner,repo.owner?.login)
        binding.repoDescription.text = getString(R.string.repo_desc,setDefaultValue(repo.description,"No Description is added"))
        binding.repoPgmLang.text =  getString(R.string.repo_lang,setDefaultValue(repo.language,"Text"))
        binding.repoLicense.text =  getString(R.string.repo_license,setDefaultValue(repo.license?.name,"No License is specified"))
        binding.repoCloneUrl.text = getString(R.string.repo_clone_url,repo.cloneUrl)
        binding.repoVisibility.text = getString(R.string.repo_visibility,repo.visibility)
    }

    private fun setDefaultValue(nullableValue : String?, defaultValue : String) : String{
        return nullableValue ?: defaultValue
    }

}