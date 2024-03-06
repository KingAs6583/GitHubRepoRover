package com.example.githubreporover

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.githubreporover.databinding.FragmentLoginWithGitAndGoogleBinding
import com.example.githubreporover.model.GitHubApiStatus
import com.example.githubreporover.model.LoginViewModel
import com.example.githubreporover.model.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val RC_SIGN_IN = 123 // We can choose any positive integer value

class LoginWithGitAndGoogleFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: FragmentLoginWithGitAndGoogleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels{
        LoginViewModelFactory(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWithGitAndGoogleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Skipping Login screen is signIn Before
        skipSignIn(isSignInBefore())

        // firebase googleSignIn
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id)) // Replace with your Web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.textInputLayout.error = null


        val googleSignInButton = binding.publicLogin
        googleSignInButton.setOnClickListener {
            binding.publicLoginLoading.visibility = View.VISIBLE
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    /**
     * Skip the login is user SignIn Before and not Logout
     */
    private fun skipSignIn(isSigIn: Boolean){
        if (isSigIn) {
            val mainActivity = Intent(context, MainActivity::class.java)
            startActivity(mainActivity)
            requireActivity().finish()
        }
    }

    /**
     * Check If User is SignIn Before and not Logout
     */
    private fun isSignInBefore(): Boolean{
        val userDetails = FirebaseAuth.getInstance().currentUser
        return (userDetails != null && !viewModel.sharedPreferences.getString("UserId", "").equals(""))
    }

    /**
     * get the gitHub user details from gitHub api
     */
    private fun signInWithGitUserName(userName: String){
        viewModel.getUserDetails(userName)
        viewModel.userDetails.observe(this.viewLifecycleOwner) { value ->
            value.let {
                if (it != null) {
                    viewModel.edit.apply()
                    val mainActivity = Intent(context, MainActivity::class.java)
                    startActivity(mainActivity)
                    //preventing to visit this activity on back button press
                    requireActivity().finish()
                }
            }
        }
        viewModel.status.observe(requireActivity()) { status ->
            status.let {
                if(it == GitHubApiStatus.ERROR){
                    binding.textInputLayout.error = "Invalid UserId Or Network Issue"
                }
            }
        }
        binding.publicLoginLoading.visibility = View.GONE
    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign-In failed
                Log.e("GoogleSignIn", "signInResult:failed code=${e.statusCode} ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * handling when signIn with Google is Occur
     */
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign-in success, updating UI with the signed-in user's information
                    val userName :String = binding.githubPublicUserid.text.toString()
                    viewModel.edit.putString("UserId", userName)
                    signInWithGitUserName(userName)

                    val user = auth.currentUser
                    Log.d("GoogleSignIn", "signInWithCredential:success, user=${user?.displayName}")
                } else {
                    // Sign-in failed
                    Log.e(
                        "GoogleSignIn",
                        "signInWithCredential:failure, exception=${task.exception} ${task.exception?.message}"
                    )
                }
            }
    }
}