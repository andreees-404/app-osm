package com.domaintest.mapstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.domaintest.mapstest.ui.theme.MapsTestTheme

class MainActivity : ComponentActivity() {

    private val TAG: String = "MAIN_ACTIVITY"
    private val STORAGE_PERMISSION_CODE: Int = 1001

    private val storagePermission = false
    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.INTERNET)

    private val deniedPerms = ArrayList<String>()
    private val grantedPerms = ArrayList<String>()



    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (Environment.isExternalStorageManager()){
                Log.d(TAG, "permissionsLauncher: Permissions granted")
                openMaps()
            }
        } else {
            Log.d(TAG, "permissionsLauncher: Permissions denied ")
            toast("External storage permissions denied!")
        }
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsTestTheme {
                    openMaps()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // This line return true or false
            Environment.isExternalStorageManager()
        } else {
            // Sobre Android 11(R)
            for (i in permissions) {
                if (ContextCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED
                ) {
                    grantedPerms.add(i)
                } else {
                    // Existe al menos un permiso denegado
                    deniedPerms.add(i)
                }
                if (deniedPerms.isEmpty()) return true
            }

            return false
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPerms(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Log.d(TAG, "request permissions: try")
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                permissionsLauncher.launch(intent)
            } catch (e: Exception){
                Log.d(TAG, "request permissions: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                permissionsLauncher.launch(intent)

            }
        } else {
            ActivityCompat.requestPermissions(this,
                permissions.toTypedArray(), STORAGE_PERMISSION_CODE)
        }
    }


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
