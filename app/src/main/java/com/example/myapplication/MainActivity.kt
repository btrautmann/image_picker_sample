package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var outputPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickImageIntent.type = "image/*"

            startActivityForResult(pickImageIntent, 0)
        }

        // This largely copied from: https://developer.android.com/training/camera/photobasics
        findViewById<FloatingActionButton>(R.id.fab2).setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                val photoFile: File? = createFile()
                outputPath = photoFile!!.absolutePath
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    // We're creating this file private to our app, but could easily contribute
    // to the MediaStore.Images table if desired
    private fun createFile(): File {
        // Create an image file name
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_sample_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            val uri = data?.data
            val thumbnail = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            findViewById<ImageView>(R.id.myImageView).setImageBitmap(thumbnail)
        } else if (requestCode == 1) {
            val thumbnail = BitmapFactory.decodeFile(outputPath)
            findViewById<ImageView>(R.id.myImageView).setImageBitmap(thumbnail)
        }
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
}