package com.pvaindia.tiktokclone0.ui

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pvaindia.tiktokclone0.R
import com.pvaindia.tiktokclone0.databinding.ActivityImageBinding

class ImageViewerActivity : AppCompatActivity() {

    companion object {
        var INTENT_IMAGE_URI = "image_uri"
    }

    private lateinit var dataBinding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //causing crash
//        supportActionBar?.hide()

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val uriString = intent.getStringExtra(INTENT_IMAGE_URI)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_image)
        dataBinding.imageViewer.setImageURI(Uri.parse(uriString))
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}