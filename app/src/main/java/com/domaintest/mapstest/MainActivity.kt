package com.domaintest.mapstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.domaintest.mapstest.ui.theme.MapsTestTheme

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG: String = "MAIN_ACTIVITY"
        private const val STORAGE_PERMISSION_CODE: Int = 100
    }


    private val locationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.INTERNET
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )
    }

    // Lista de permisos denegados
    private val deniedPerms = ArrayList<String>()

    // Permisos concedidos -> ALL
    private var allGranted = false



    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        permissions ->
        if (checkPermissions(permissions.keys.toList())){
            allGranted = true
        } else {
            permissions.keys.forEach {
                deniedPerms.add(it)
                Log.d(TAG, "Denied perm ${it}: ")
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsTestTheme {
                if (checkPermissions(locationPermissions)){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(modifier = Modifier.padding(20.dp),onClick = {
                            openMaps()
                            finish()
                            Log.d(TAG, "onCreate: permissions granted, initializating Activity")
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3083f0) )) {
                            Text(text = "Mostrar ubicación")
                        }
                    }
                    //openMaps()
                    //finish()
                    //
                } else {
                    toast("Location permission denied, request!")
                    try{
                        requestPerms(deniedPerms)
                    } catch (e: Exception){
                        Log.d(TAG, "onCreate: ${e.message}")
                    }
                }
            }
        }
    }

    private fun checkPermissions(permissions: List<String>): Boolean {
        return permissions.all {
            permission -> ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPerms(permissions: List<String>){
        permissionsLauncher.launch(permissions.toTypedArray())
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (write && read){
                    Log.d(TAG, "requestPermissionResult: ")
                    openMaps()
                } else {
                    Log.d(TAG, "requestPermissionResult: External storage permissions denied")
                    toast("External storage permissions denied!")
                }
            }
        }
    }

    private fun openMaps(){
        Intent(this, MapsActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
