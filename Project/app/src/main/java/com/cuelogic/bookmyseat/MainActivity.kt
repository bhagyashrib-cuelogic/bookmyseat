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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
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
        Log.d("Name","$value")
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
        //    var signIn = findViewById<Button>(R.id.button_googleSign)
        createSign()


        var signIn = findViewById<Button>(R.id.button_googleSign)
        signIn.setOnClickListener() {
            signIn()

        }
    }

    private fun createSign() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
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
                Snackbar.make(layout,"${account.email}",Snackbar.LENGTH_LONG).show()
                Log.i("Name","${account.email}")

                auth.fetchProvidersForEmail(account.email!!).addOnCompleteListener(this, OnCompleteListener() {task ->
                    if(task.isComplete)
                    {
                        var check:Boolean = !task.result!!.providers!!.isEmpty()
                        if(!check)
                        {
                            firebaseAuthWithGoogle(account.idToken!!)
                        }
                        else{
                            firebaseWithSecondTime(account.idToken!!)
                        }
                    }
                })

              //  firebaseAuthWithGoogle(account.idToken!!)
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

                    var session = User(MainActivity@ this)
                    val user = auth.currentUser
                    var uid = user?.uid
                    Log.i("Name", "udi is $uid")
                    //Store UID into sharedpreferences
                    session.setUId(uid.toString())


                    emailAddress = user?.email
                    employeeName = user?.displayName
                    var firebaseReference =
                        FirebaseDatabase.getInstance().getReference("Employees")
                    var uidKey = firebaseReference.push().key

                    firebaseReference!!.child(uidKey!!)
                        .setValue(EmployeeData(uid!!, employeeName!!, emailAddress!!)).addOnCanceledListener {
                            Toast.makeText(applicationContext,"successFull",Toast.LENGTH_LONG).show()
                        }

                    var intent:Intent = Intent(applicationContext,SeatBookActivity::class.java)
                    startActivity(intent)
                } else {

                }
                // ...
            }
    }
    private fun firebaseWithSecondTime(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var session = User(MainActivity@ this)
                    val user = auth.currentUser
                    var uid = user?.uid
                    Log.i("Name", "udi is $uid")
                    //Store UID into sharedpreferences
                    session.setUId(uid.toString())

                    var intent:Intent = Intent(applicationContext,SeatBookActivity::class.java)
                    startActivity(intent)
                } else {

                }
                // ...
            }
    }


}