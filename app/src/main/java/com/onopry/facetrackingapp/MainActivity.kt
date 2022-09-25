package com.onopry.facetrackingapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.onopry.facetrackingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()


    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "opening camera", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_FOR_CAMERA_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_FOR_CAMERA_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // start camera
                    Toast.makeText(this, "opening camera", Toast.LENGTH_SHORT).show()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        onPermissionNotGrantedForever()
                    } else {
                        onPermissionNotGrantedOnce()
                    }
                }
            }
        }
    }

    private fun onPermissionNotGrantedForever() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(
                appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("""You have denied permission forever.
                    | You can change your decision in app settings.
                    | Would you like to open app settings?
                """.trimMargin())
                .setPositiveButton("Open") { _, _ -> startActivity(appSettingsIntent) }
                .create()
                .show()
        }
    }

    private fun onPermissionNotGrantedOnce() {
        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_PERMISSION_FOR_CAMERA_CODE = 1
    }
}