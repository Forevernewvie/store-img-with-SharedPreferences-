package com.jaebin.md

import android.app.Application

class SPF: Application()
{
    companion object { lateinit var prefs: SharedPreference }
    override fun onCreate()

    {   prefs = SharedPreference(applicationContext)
        super.onCreate()
    }


}
