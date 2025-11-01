package org.jahangostar.busincreasement

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.jahangostar.busincreasement.ui.components.Rtl
import org.jahangostar.busincreasement.ui.navigation.SetupNavGraph
import org.jahangostar.busincreasement.ui.theme.BusIncreasementTheme
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var newIntent = mutableStateOf<Intent?>(null)
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val readStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (readStoragePermission == PackageManager.PERMISSION_GRANTED &&
            writeStoragePermission == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions already granted
        } else {
            requestPermissions()
        }

        enableEdgeToEdge()
        setContent {
            BusIncreasementTheme {
                val navController = rememberNavController()
                Rtl {
                    SetupNavGraph(
                       navController =  navController,
                        newIntent = newIntent.value,
                        onNewIntentHandled = { newIntent.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i("ON_NEW_INTENT", "onNewIntent Received: $intent")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        newIntent.value = intent
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            100
        )
    }

    private fun sendReceiptToTechPay() {
        val receiptBitmap = BitmapFactory.decodeFile("RECEIPT_FILE_ADDRESS") ?: return
        val byteArrayOutputStream = ByteArrayOutputStream()
        receiptBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val launchIntent = packageManager.getLaunchIntentForPackage("com.tech.pay")
        if (launchIntent != null) {
            launchIntent.putExtra("packageName", applicationContext.packageName)
            launchIntent.putExtra("receipt", encoded)
            startActivity(launchIntent)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("Permissions", "All permissions granted")
            } else {
                Log.d("Permissions", "One or more permissions denied")
            }
        }
    }
}