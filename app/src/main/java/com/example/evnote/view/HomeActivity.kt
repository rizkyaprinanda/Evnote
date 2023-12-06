package com.example.evnote.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.evnote.MapsActivity
import com.example.evnote.R
import com.example.evnote.view.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    companion object {
        const val PREF_NAME = "MyPreferences"
        const val KEY_LOGIN = "isLogin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_LOGIN, true).apply()

        drawerLayout = findViewById(R.id.drawerLayout)

        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        preferences = getSharedPreferences(HomeActivity.PREF_NAME, Context.MODE_PRIVATE)

        // Inisialisasi GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ganti dengan Web Client ID Anda
            .requestEmail()
            .build()

        // Inisialisasi googleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemHome -> {
                    replaceFragment(HomeFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemPopular -> {
                    replaceFragment(PopularFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemNowPlaying -> {
                    replaceFragment(NowPlayingFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemTopRated -> {
                    replaceFragment(TopRatedFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("Navigation", "Item selected: ${item.title} (ID: ${item.itemId})")
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_maps -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapsFragment2()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment()).commit()
            R.id.nav_share -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment()).commit()
            R.id.nav_logout -> {
                Log.d("Navigation", "Logout selected")
                val user = mAuth.currentUser
                if (user != null) {
                    // Pengguna sedang login, lakukan logout
                    signOutWithProgressBar()
                } else {
                    // Pengguna tidak login, lakukan tindakan sesuai kebutuhan
                    Toast.makeText(this, "Anda tidak sedang login", Toast.LENGTH_SHORT).show()
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOutWithProgressBar() {
        Log.d("Logout", "Sign out process started")
        progressDialog.show()

        // Pastikan bahwa googleSignInClient sudah diinisialisasi dengan benar sebelum digunakan
        // googleSignInClient.signOut() akan menghapus token Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener { googleSignOutTask ->
            if (googleSignOutTask.isSuccessful) {
                Log.d("Logout", "Google sign out successful")
                // Sign out dari Google berhasil, lanjut dengan sign-out dari FirebaseAuth
                mAuth.signOut()
                // Ubah status login di SharedPreferences menjadi false
                preferences.edit().putBoolean(HomeActivity.KEY_LOGIN, false).apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Gagal melakukan sign out dari Google, tangani sesuai kebutuhan
                Log.e("Logout", "Google sign out failed")
                Toast.makeText(this, "Gagal sign out", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
