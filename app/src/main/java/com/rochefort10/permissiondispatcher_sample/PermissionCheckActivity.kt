package com.rochefort10.permissiondispatcher_sample

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.util.Log
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class PermissionCheckActivity : AppCompatActivity() {

    var isWriteExternalStorageAllowed = false
    var isCameraAllowed = false
    val REQUEST_CODE_MAGIC = 1212

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startPermissionCheck()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // ---------- WRITE_EXTERNAL_STORAGE ----------
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showWriteExternalStorage() {
        isWriteExternalStorageAllowed = true
        showCameraWithPermissionCheck()
    }

//    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    fun showWriteExternalStoraga(request: PermissionRequest) {
//        showRationaleDialog("Reason to use external storage.",request)
//    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onWriteExternalStorageDenied() {
        isWriteExternalStorageAllowed = false
        showCameraWithPermissionCheck()
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onWriteExternalStorageNeverAskAgain() {
        isWriteExternalStorageAllowed = false
        showCameraWithPermissionCheck()
    }

    // ----------- CAMERA ----------
    @NeedsPermission(Manifest.permission.CAMERA)
    fun showCamera() {
        isCameraAllowed = true
        startNextActivity()
    }

//    @OnShowRationale(Manifest.permission.CAMERA)
//    fun showRationaleForCamera(request: PermissionRequest) {
//        showRationaleDialog("Reason to use camera access.",request)
//    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        isCameraAllowed = false
        startNextActivity()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        isCameraAllowed = false
        startNextActivity()
    }

    private fun startPermissionCheck() {
        showWriteExternalStorageWithPermissionCheck()
    }

    private fun startNextActivity() {

        if ((isCameraAllowed == true) && (isWriteExternalStorageAllowed == true)) {

            // Every permission are set, moving to the next step
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            // Not enough permissions, finish with some message
            if (!isCameraAllowed)
                Log.d("CAM", "No")
            if (!isWriteExternalStorageAllowed)
                Log.d("STO", "No")

            AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .setNegativeButton(R.string.button_name_app_info) { _, _ ->
                    val uriString = "package:$packageName"
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString))
                    startActivityForResult(intent, REQUEST_CODE_MAGIC)
                }
                .setCancelable(false)
                .setMessage(R.string.alert_diaglog_message_for_permission)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_CODE_MAGIC) {
            // Start again
            startPermissionCheck()
        }
    }

}
