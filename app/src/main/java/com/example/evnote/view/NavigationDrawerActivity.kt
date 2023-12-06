package com.example.evnote.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.evnote.R
import com.example.evnote.databinding.ActivityNavigationDrawerBinding
import com.example.evnote.view.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class NavigationDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityNavigationDrawerBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var progressDialog: ProgressDialog
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var preferences: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 123
        private const val PREF_NAME = "MyPreferences"
        private const val KEY_FIRST_TIME = "isFirstTime"
        private const val KEY_DARK_MODE = "isDarkMode"
        private const val KEY_JOINED = "isJoined"
        private const val KEY_LOGIN = "isLogin"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        drawerLayout = findViewById(R.id.drawerLayout1)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view1)
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

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // Inisialisasi ProgressDialog
        progressDialog = ProgressDialog(this)

        // Inisialisasi SharedPreferences
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Inisialisasi GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ganti dengan Web Client ID Anda
            .requestEmail()
            .build()

        // Inisialisasi googleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()

            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()

            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment()).commit()

            R.id.nav_share -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment()).commit()

            R.id.nav_logout -> {

                val user = mAuth.currentUser
                if (user != null) {
                    // Pengguna sedang login, lakukan logout
                    signOutWithProgressBar()
                } else {
                    // Pengguna tidak login, lakukan tindakan sesuai kebutuhan
                    Toast.makeText(this, "Anda tidak sedang login", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()


            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOutWithProgressBar() {
        progressDialog.show()

        // Pastikan bahwa googleSignInClient sudah diinisialisasi dengan benar sebelum digunakan
        // googleSignInClient.signOut() akan menghapus token Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener { googleSignOutTask ->
            if (googleSignOutTask.isSuccessful) {
                // Sign out dari Google berhasil, lanjut dengan sign-out dari FirebaseAuth
                mAuth.signOut()
                // Ubah status login di SharedPreferences menjadi false

                preferences.edit().putBoolean(KEY_LOGIN, false).apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()

            } else {
                // Gagal melakukan sign out dari Google, tangani sesuai kebutuhan
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