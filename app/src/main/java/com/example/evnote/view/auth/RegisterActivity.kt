package com.example.evnote.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.evnote.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Pemanggilan fungsi RegisterFirebase di dalam onClickListener
        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPassword.text.toString()
            val name = binding.edtName.text.toString()
            val confirmPassword = binding.edtConfirm.text.toString()

            // Validasi First Name
            if (name.isEmpty()){
                binding.edtName.error = "Please enter your name!"
                binding.edtName.requestFocus()
                return@setOnClickListener
            }

            // Validasi email
            if (email.isEmpty()){
                binding.edtEmailRegister.error = "Email must be filled in!"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailRegister.error = "Invalid Email"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            // Validasi Password
            if (password.isEmpty()){
                binding.edtPassword.error = "Password must be filled in"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            // Validasi panjang password
            if(password.length < 6){
                binding.edtPassword.error = "Password Minimum 6 Characters"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            // Validasi Konfirmasi Password
            if (confirmPassword.isEmpty()){
                binding.edtConfirm.error = "Please confirm the password"
                binding.edtConfirm.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword != password) {
                binding.edtConfirm.error = "Passwords do not match"
                binding.edtConfirm.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password, name)

        }
    }

    private fun RegisterFirebase(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Simpan data tambahan ke database atau lakukan tindakan lain yang diperlukan
                    val user = auth.currentUser
                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(userProfileChangeRequest)

                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}