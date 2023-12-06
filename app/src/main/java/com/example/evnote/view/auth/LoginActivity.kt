@file:Suppress("DEPRECATION")

package com.example.evnote.view.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.evnote.R
import com.example.evnote.databinding.ActivityLoginBinding
import com.example.evnote.view.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnGoogle: RelativeLayout
    private lateinit var preferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    companion object {
        private const val RC_SIGN_IN = 123
        private const val PREF_NAME = "MyPreferences"
        private const val KEY_FIRST_TIME = "isFirstTime"
        private const val KEY_JOINED = "isJoined"
        private const val KEY_LOGIN = "isLogin"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in...")


        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val isLogin = preferences.getBoolean(KEY_LOGIN, false)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.cvGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

            initializeAuthentication()
            setupGoogleSignIn()

            binding.btnlogin.setOnClickListener {
                val email = binding.edtEmailLogin.text.toString()
                val password = binding.edtPasswordLogin.text.toString()


                if (validateCredentials(email, password)) {
                    loginFirebase(email, password)
                }
            }


            binding.tvToRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

        if (isLogin) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // menangani proses login google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Jika berhasil
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
            }
        }


    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { error ->
                error.localizedMessage?.let { showToast(it) }
                progressDialog.dismiss()
            }



    }

    private fun initializeAuthentication() {
        auth = FirebaseAuth.getInstance()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmailLogin.error = "Invalid Email"
            binding.edtEmailLogin.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.edtPasswordLogin.error = "Password must be filled in"
            binding.edtPasswordLogin.requestFocus()
            return false
        }

        return true
    }

    private fun loginFirebase(email: String, password: String) {
        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showToast("Welcome $email to Cinematrix")


                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()


                } else {
                    showToast("${it.exception?.message}")
                }
                progressDialog.dismiss()
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, LENGTH_SHORT).show()
    }


}


