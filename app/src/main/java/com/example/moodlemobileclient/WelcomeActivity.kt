package com.example.moodlemobileclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import retrofit2.Call

class WelcomeActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // Token de tu servicio Moodle
    private val apiToken = "a2a6f9e1c4b7809aae2b3891c91e15fd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            signInGoogle()
        }
    }

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.result ?: run {
                Log.e("GOOGLE_AUTH", "Cuenta Google nula")
                return@registerForActivityResult
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnCompleteListener { loginTask ->
                if (loginTask.isSuccessful) {
                    Log.d("GOOGLE_AUTH", "LOGIN EXITOSO")
                    // Conectar con Moodle
                    getMoodleSiteInfo()
                } else {
                    Log.e("GOOGLE_AUTH", "Error Firebase: ${loginTask.exception}")
                }
            }

        } catch (e: Exception) {
            Log.e("GOOGLE_AUTH", "Error en Google Sign-In: $e")
        }
    }

    private fun signInGoogle() {
        googleLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun getMoodleSiteInfo() {
        val service = RetrofitClient.service
        val call = service.getSiteInfo(apiToken)

        call.enqueue(object : retrofit2.Callback<SiteInfoResponse> {
            override fun onResponse(
                call: retrofit2.Call<SiteInfoResponse>,
                response: retrofit2.Response<SiteInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val siteInfo = response.body()
                    siteInfo?.let {
                        Log.d("MOODLE", "Conexión OK → Sitio: ${it.sitename}, Usuario: ${it.username}")
                        startActivity(Intent(this@WelcomeActivity, CoursesActivity::class.java))
                        finish()
                    }
                } else {
                    Log.e("MOODLE", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<SiteInfoResponse>, t: Throwable) {
                Log.e("MOODLE", "Error al conectar con Moodle: $t")
            }
        })
    }




}
