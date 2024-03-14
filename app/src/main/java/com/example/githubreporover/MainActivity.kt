package com.example.githubreporover

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.githubreporover.databinding.ActivityMainBinding
import com.example.githubreporover.model.LoginViewModel
import com.example.githubreporover.model.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var drawerLayout: DrawerLayout

    //For Lifecycle Awareness
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout

        // googleSignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id)) // Replace with your Web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val navView: NavigationView = binding.navView

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        requestNotificationPermission(this,this)

        //setting User Details
        setUserDetailsToDrawer(navView)

        setNavigationViewListener()

        onBackButtonPress()

    }

    /**
     * override the default back Button press
     */
    private fun onBackButtonPress(){
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                //handling goBack stack off web view
                override fun handleOnBackPressed() {
                    if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                        binding.drawerLayout.closeDrawer(binding.navView)
                    } else if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    /**
     * Use to Set the Details of Login User on DrawerHeader
     */
    private fun setUserDetailsToDrawer(navView : NavigationView){
        val userDetails = loginViewModel.currentUserDetails
        val headerView = navView.getHeaderView(0) // Get the header view
        val userName: TextView = headerView.findViewById(R.id.user_name)
        userName.text = resources.getString(R.string.user_name,userDetails?.displayName)
        val email: TextView = headerView.findViewById(R.id.user_email)
        email.text =  getString(R.string.email,userDetails?.email)
        val gitId : TextView= headerView.findViewById(R.id.git_userid)
        gitId.text = getString(R.string.github_id,loginViewModel.githubUserName)
        val profile : ImageView =headerView.findViewById(R.id.user_profile_img)
        profile.load(userDetails?.photoUrl){
            crossfade(true)
            transformations(CircleCropTransformation())
        }

    }

    /**
     * requesting notification Permission for Newer Version of Android
     */
    private fun requestNotificationPermission(context: Context, activity: Activity) {
        when { // it use to manage multiple permissions
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        123
                    )
                }
            }
        }
    }


    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Setting onClick Listener of navigation Drawer elements
     */
    private fun setNavigationViewListener() {
        val navigationView: NavigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)
    }

    /**
     * When Request Is Accepted
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification Enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.logout -> {
               logout()
            }
            R.id.clear_web_cache -> {
                this.cacheDir.deleteRecursively()
            }
            R.id.nav_fav ->{
                val action = HomeFragmentDirections.actionHomeFragmentToFavRepoFragment()
                navController.navigate(action)
            }
        }
        //closing the drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    /**
     * SignOut the google Sign And remove github id from the app
     */
    private fun logout(){
        try {
            googleSignInClient.signOut().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    googleSignInClient.revokeAccess()
                    loginViewModel.edit.putString("UserId", "")
                    loginViewModel.edit.apply()
                    val loginActivity = Intent(this, LoginActivity::class.java)
                    startActivity(loginActivity)
                    this.finish()
                } else {
                    Log.e("signOut", "Sign out was not successful")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}