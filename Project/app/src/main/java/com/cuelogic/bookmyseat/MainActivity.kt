package com.cuelogic.bookmyseat


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var RC_SIGN_IN: Int = 123
    private lateinit var auth:FirebaseAuth
    lateinit var signIn: Button
    private lateinit var layout: RelativeLayout
    private var userId:String?=null
    private var emailAddress:String?=null
    private var employeeName:String?=null


    override fun onStart() {
        super.onStart()
        var session = User(MainActivity@this)
        var value = session.getUId()
        if(value!=""){
            var intent: Intent = Intent(applicationContext,SeatBookActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        layout=findViewById(R.id.relativeOnes)
        auth = FirebaseAuth.getInstance()

        signIn = findViewById<Button>(R.id.button_googleSign)
        createRequest()
        signIn.setOnClickListener() {
            signIn()
        }
    }

    private fun createRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Snackbar.make(layout,"${account.email}", Snackbar.LENGTH_LONG).show()
                Log.i("Name","${account.email}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()                // ...
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {

       val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var session = User(MainActivity@this)
                    val user = auth.currentUser
                    var uid = user?.uid
                    Log.i("Name","udi is $uid")
                    //Store UID into sharedpreferences
                    session.setUId(uid.toString())

                    //Firebase storedata

                    emailAddress=user?.email
                     employeeName = user?.displayName
                    var firebaseReference = FirebaseDatabase.getInstance().getReference("Employees")
                    var  uidKey= firebaseReference.push().key
                    Log.i("Name","udi222 is $uid")
                   // val myUser=EmployeeData(UIDkey!!,employeeName!!,emailAddress!!)
                    firebaseReference!!.child(uidKey!!).setValue(EmployeeData(uid!!,employeeName!!,emailAddress!!))


                    var intent = Intent(applicationContext,SeatBookActivity::class.java)
                    startActivity(intent)
                } else {
                }
                // ...
            }
    }

}
