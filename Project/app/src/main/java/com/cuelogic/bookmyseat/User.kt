package com.cuelogic.bookmyseat

import android.content.Context
import android.content.SharedPreferences

class User(private var context: Context) {

    private lateinit var UID: String
    private lateinit var sharedPreferences:SharedPreferences

    fun getUId():String{
        sharedPreferences=context.getSharedPreferences("userUID",Context.MODE_PRIVATE)
            UID = sharedPreferences.getString("UserUid", "").toString()
        return UID
    }
    fun setUId(UID:String){
            this.UID = UID
        sharedPreferences=context.getSharedPreferences("userUID",Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("UserUid", UID).commit()
    }
}