package com.example.audioguiasandroid.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import com.example.audioguiasandroid.R
import com.example.audioguiasandroid.model.data.AudioGuide
import com.example.audioguiasandroid.model.repository.AudioGuideRepository
import com.example.audioguiasandroid.view.adapter.AudioGuideAdapter
import com.example.audioguiasandroid.viewmodel.showAuth
import com.example.audioguiasandroid.viewmodel.showUserProfile
import com.example.audioguiasandroid.viewmodel.showVerify
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity()
    //, NavigationView.OnNavigationItemSelectedListener
{
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var audioGuideAdapter: AudioGuideAdapter
    private var storage = Firebase.storage
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val user = Firebase.auth.currentUser
        if (user == null){
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth(this,"Alerta", "Los credenciales de tu cuenta se han perdido. Por favor, vuelve a iniciar sesión.")
        }else{
            setup()
        }

        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", Firebase.auth.currentUser?.email.toString())
        prefs.apply()




    }

    private fun setup() {
        val userImageView = findViewById<ImageView>(R.id.userImageView_Home)
        val searchEditText = findViewById<EditText>(R.id.searchEditText_Home)

        storage.reference.child("images/" + Firebase.auth.currentUser?.email.toString() + "/profile").downloadUrl
            .addOnSuccessListener { uri->
                Picasso.get()
                    .load(uri)
                    .into(userImageView)
            }
            .addOnFailureListener {
                storage.reference.child("images/default/profile.png").downloadUrl
                    .addOnSuccessListener { uri->
                        Picasso.get()
                            .load(uri)
                            .into(userImageView)
                    }
            }
        //TODO: implementar fragment con el navigation drawer
        //TODO: terminar de refactorizar con maincontroller (funciones show)

        //initNavigationDrawer()

        //initRecyclerView()

        searchEditText.addTextChangedListener {filter ->
            db.collection("audioGuide")
                .get()
                .addOnSuccessListener { result ->
                    val listAudioGuide : MutableList<AudioGuide> = AudioGuideRepository().getAllAudioGuides(result).toMutableList()
                    //Al ser Firebase una base de datos no relacional no se pueden realizar consultas complejas como LIKE y contratar un servicio externo como Algolia (permite realizar consultas complejas) tiene un costo
                    val resultList = listAudioGuide.filter { audioGuide ->
                        val cityMatches = audioGuide.city?.lowercase()?.contains(filter.toString().lowercase()) ?: false
                        val countryMatches = audioGuide.country?.lowercase()?.contains(filter.toString().lowercase()) ?: false
                        val titleMatches = audioGuide.title?.lowercase()?.contains(filter.toString().lowercase()) ?: false

                        cityMatches || countryMatches || titleMatches
                    }
                    audioGuideAdapter.updateData(resultList)

                }
        }

        userImageView.setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true){
                showUserProfile(this)
            }else{
                showVerify(this)
            }
        }
    }
    /*
        private fun initNavigationDrawer() {
            val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
            setSupportActionBar(toolbar)

            drawer = findViewById(R.id.drawerLayout_Home)



            toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)

            val navigationView : NavigationView = findViewById(R.id.navigatioView_Home)
            navigationView.setNavigationItemSelectedListener(this)


        }

        private fun initRecyclerView(){
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerAudioGuide_Home)
            db.collection("audioGuide").get()
                .addOnSuccessListener { result ->
                    val listAudioGuide : List<AudioGuide> = AudioGuideRepository().getAllAudioGuides(result)
                    val manager = LinearLayoutManager(this)
                    recyclerView.layoutManager = manager
                    audioGuideAdapter = AudioGuideAdapter(listAudioGuide){ onItemSelected(this, it) }
                    recyclerView.adapter = audioGuideAdapter
                    Log.d(ContentValues.TAG, "Getting audio guides data successfully.")
                    //audioGuideAdapter.updateData(listAudioGuide.toList())
                }
                .addOnFailureListener { e ->

                    Log.w(ContentValues.TAG, "Error getting audio guides data.", e)
                }
        }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.nav_item_home -> showHome(this)
                    R.id.nav_item_downloads -> Toast.makeText(this, "Descargas", Toast.LENGTH_SHORT).show()
                    R.id.nav_item_favs -> showFavs(this)
                    R.id.nav_item_help -> Toast.makeText(this, "Ayuda y comentarios", Toast.LENGTH_SHORT).show()
                    R.id.nav_item_config -> Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                    R.id.nav_item_profile -> showUserProfile(this)
                    R.id.nav_item_logout -> {
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.clear()
                        prefs.apply()
                        showAuth(this,"Información", "Se ha cerrado sesión.")
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


     */
}
