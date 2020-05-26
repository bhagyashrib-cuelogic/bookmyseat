package com.cuelogic.bookmyseat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {

    private val splashTime :Long= 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        Handler().postDelayed({
            var session = User(MainActivity@this)
            var value = session.getUId()
            if (value!="") {
                startActivity(
                    Intent(this, SeatBookActivity::class.java)
                )
                finish()
            }
            else
            {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                finish()
            }
        },splashTime)

    }
}
