package com.example.audioguiasandroid.view

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.databinding.ActivityMainBinding
import com.example.audioguiasandroid.view.fragment.FavsFragment
import com.example.audioguiasandroid.view.fragment.HomeFragment
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showConfiguration
import com.example.audioguiasandroid.viewmodel.showHelp
import com.example.audioguiasandroid.viewmodel.showMain
import com.example.audioguiasandroid.viewmodel.showUserProfile
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private var storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,getString(R.string.information), getString(R.string.lost_credentials))
        }else{
            setup()

            val bundle = intent.extras
            val fragment = bundle?.getString("fragment")
            if (fragment != null && savedInstanceState == null){
                when(fragment){
                    "home" -> {
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<HomeFragment>(R.id.fragmentContainerView_Main)
                        }
                    }
                    "favs" -> {
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            add<FavsFragment>(R.id.fragmentContainerView_Main)
                        }
                    }
                }

            }
            //Guardar datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putString("email", Firebase.auth.currentUser?.email.toString())
            prefs.apply()
        }
    }

    private fun setup() {
        initNavigationDrawer()
        storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(binding.userImageViewMain)
            }
            .addOnFailureListener {
                storage.reference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(binding.userImageViewMain)
                    }
            }

        //FIXME:
        /*
        binding.userImageViewMain.setOnClickListener {
            showUserProfile(this)
        }
         */

    }

    private fun initNavigationDrawer() {
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        drawer = findViewById(R.id.drawerLayout_Main)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView : NavigationView = findViewById(R.id.navigatioView_Main)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_home -> showMain(this, "home")
            R.id.nav_item_favs -> showMain(this, "favs")
            R.id.nav_item_help -> showHelp(this)
            R.id.nav_item_config -> showConfiguration(this)
            R.id.nav_item_profile -> showUserProfile(this)
            R.id.nav_item_logout -> {
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()
                showAuth(this)
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}