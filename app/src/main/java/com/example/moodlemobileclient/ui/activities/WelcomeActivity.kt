package com.example.moodlemobileclient.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.moodlemobileclient.data.repository.AuthRepository
import com.example.moodlemobileclient.utils.Constants
import com.example.moodlemobileclient.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class WelcomeActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()
        setupGoogle()
        setupUI()
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            signInGoogle()
        }
    }

    private fun setupGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result ?: return@registerForActivityResult
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnSuccessListener {
                validateMoodle()
            }

        } catch (e: Exception) {
            Log.e("GOOGLE_AUTH", "Error Google", e)
        }
    }

    private fun validateMoodle() {
        authRepository.validateMoodleToken(Constants.MOODLE_API_TOKEN) { success ->
            if (success) {
                startActivity(Intent(this, CoursesActivity::class.java))
                finish()
            } else {
                Log.e("MOODLE", "Token inválido")
            }
        }
    }


    private fun signInGoogle() {
        googleLauncher.launch(googleSignInClient.signInIntent)
    }
}
