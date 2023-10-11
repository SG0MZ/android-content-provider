package com.example.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.contentproviderexample.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1
class MainActivity : AppCompatActivity() {

    private var readGranted = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this,READ_CONTACTS)
        Log.d(TAG,"onCreate: checkSelfPermission returned $hasReadContactPermission")

        if (hasReadContactPermission == ContextCompat.checkSelfPermission(this, READ_CONTACTS)) {
            Log.d(TAG,"onCreate: permission granted")
            readGranted = true
        } else {
            Log.d(TAG,"onCreate: requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS)
        }

        binding.fab.setOnClickListener { view ->
            Log.d(TAG,"fab onClick: starts")
            if (readGranted) {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                val contacts = ArrayList<String>()
                cursor?.use {
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }
                val adapter = ArrayAdapter<String>(this,R.layout.contact_detail,R.id.contact_names,contacts)
                contact_names.adapter = adapter
            } else {
                Snackbar.make(view,"Please grant access to your Contacts",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action",{
                        Toast.makeText(it.context,"Snackbar action clicked",Toast.LENGTH_SHORT).show()
                    }).show()
            }
            Log.d(TAG,"fab onClick: ends")
        }
        Log.d(TAG,"onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG,"onRequestPermissionsResult: starts")
        when(requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                readGranted = if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"onRequestPermissionsResult: permission granted")
                    true
                } else {
                    Log.d(TAG,"onRequestPermissionsResult: permission refused")
                    false
                }
            }
        }
        Log.d(TAG,"onRequestPermissionsResult: ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}