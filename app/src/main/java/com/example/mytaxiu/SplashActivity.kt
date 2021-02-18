package com.example.mytaxiu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import java.util.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    companion object {
        private val LOGIN_REQUEST_CODE = 7171
    }

    private lateinit var providers:List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener


    override fun onStart() {
        super.onStart()

        delaySplashScreen()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (firebaseAuth != null && listener != null) firebaseAuth.addAuthStateListener (listener)
        super.onStop()
    }

    private fun delaySplashScreen() {
        Completable.timer(3,TimeUnit.SECONDS,AndroidSchedulers.mainThread())
            .subscribe({
                firebaseAuth.addAuthStateListener(listener)
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        providers = Arrays.asList(
        AuthUI.IdpConfig.PhoneBuilder().build()

        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener  = FirebaseAuth.AuthStateListener { myFirebaseAuth->
            val user = myFirebaseAuth.currentUser
            if (user != null){
                Toast.makeText(this.applicationContext,"Welcome "+user.uid, Toast.LENGTH_SHORT).show()
            }
            else{
                showLoginLayout()
            }
        }
    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.activity_splash)
            .setPhoneButtonId(R.id.btn_sign_up).build()


        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            , LOGIN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            }else{
                Toast.makeText(this.applicationContext," "+response!!.error!!.message, Toast.LENGTH_SHORT).show()

            }

        }
    }
}