package com.domaintest.mapstest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.domaintest.mapstest.ui.theme.MapsTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsTestTheme {
                openMaps()
            }
        }
    }

    private fun openMaps(){
        Intent(this, MapsActivity::class.java).also {
            startActivity(it)
        }
    }
}
