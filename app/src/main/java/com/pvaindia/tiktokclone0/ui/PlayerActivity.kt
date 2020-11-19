package com.pvaindia.tiktokclone0.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pvaindia.tiktokclone0.R
import com.pvaindia.tiktokclone0.VideoPostActivity
import com.pvaindia.tiktokclone0.databinding.ActivityPlayerBinding
import com.pvaindia.tiktokclone0.getMediaPath
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class PlayerActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityPlayerBinding

    private var progressDialog: ProgressDialog? = null

    private var uriString: String? = null

    private var mStorageRef: StorageReference? = null
    private var videoId: String? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private var sp: SharedPreferences? = null

    private lateinit var path: String


    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        setReadStoragePermission()

        sp = getSharedPreferences("pref", 0)


//        dataBinding.etTags
//        tagsEditText?.setText("ritiktag")


        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("processing video...")
        progressDialog?.setCancelable(false)

        supportActionBar?.hide()


        if (intent.getStringExtra(INTENT_URI) != "null") {
            uriString = intent.getStringExtra(INTENT_URI)
        } else {
            videoUri = Uri.parse(intent.getStringExtra("videoUri"))
        }

        mStorageRef = FirebaseStorage.getInstance().reference;

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabase!!.reference.child("PVA").child("Videos")

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_player)

        //Creating MediaController
        val mediaController = MediaController(this)
//        mediaController.setAnchorView(dataBinding.videoView)

        //specify the location of media file
        var uri: Uri? = null

        if (uriString == null) {
            uri = videoUri!!
        }
        else {
            uri = Uri.parse(uriString)
        }

//        Log.i("testritik-tags", dataBinding.etTags.text.toString())

        dataBinding.videoView.setOnPreparedListener { mp -> mp.isLooping = true }
        //Setting MediaController and URI, then starting the videoView
//        dataBinding.videoView.setMediaController(mediaController)
        dataBinding.videoView.setVideoURI(uri)
        dataBinding.videoView.requestFocus()
        dataBinding.videoView.start()
    }

    override fun onPause() {
        super.onPause()
        if (dataBinding.videoView.isPlaying) {
            dataBinding.videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!dataBinding.videoView.isPlaying) {
            dataBinding.videoView.resume()
            dataBinding.videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        var INTENT_URI = "player_uri"
    }

    fun onClickNext(view: View) {



        var file: Uri
        if (uriString == null) {
            file = videoUri!!
        }
        else {
            file = Uri.fromFile(File(uriString.toString()))
        }

        if (intent.getStringExtra(INTENT_URI) == "null") {

            progressDialog?.show()

            Log.i("testritik", "160")

            file?.let {

                GlobalScope.launch {
                    // run in background as it can take a long time if the video is big,
                    // this implementation is not the best way to do it,
                    // todo(abed): improve threading
                    Log.i("testritik", "168")
                    val job = async { getMediaPath(this@PlayerActivity, file) }
                    Log.i("testritik", "170")
                    path = job.await().toString()


                    Log.i("testritik", "174")
                    val desFile = saveVideoFile(path)

                    Log.i("testritik", "177")

                    desFile?.let {
                        Log.i("testritik", "180")
                        VideoCompressor.start(
                            path,
                            desFile.path,
                            object : CompressionListener {
                                override fun onProgress(percent: Float) {
                                    //Update UI

                                }

                                override fun onStart() {

                                    if (dataBinding.videoView.isPlaying) {
                                        dataBinding.videoView.pause()
                                    }

                                    progressDialog?.show()

                                }

                                override fun onSuccess() {

                                    progressDialog?.dismiss()

                                    val intent: Intent = Intent(this@PlayerActivity, VideoPostActivity::class.java)
                                    intent.putExtra("videoUri", Uri.fromFile(desFile).toString())
                                    startActivityForResult(intent, 2)

//                                    uploadVideoToFirebase(Uri.fromFile(desFile))



                                    Log.i("testritik", "comp success")

                                }

                                override fun onFailure(failureMessage: String) {
                                    Log.i("testritik", failureMessage)
                                }

                                override fun onCancelled() {
                                    Log.wtf("TAG", "compression has been cancelled")
                                    // make UI changes, cleanup, etc
                                }
                            },
                            VideoQuality.LOW,
                            isMinBitRateEnabled = false,
                            keepOriginalResolution = false,
                        )
                    }
                }
            }
        }
        else {
//            uploadVideoToFirebase(file)

            val intent: Intent = Intent(this@PlayerActivity, VideoPostActivity::class.java)
            intent.putExtra("videoUri", file.toString())
            startActivity(intent)
        }


//        uploadVideoToFirebase(uriString.toString())
    }




    @Suppress("DEPRECATION")
    private fun saveVideoFile(filePath: String?): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 29) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        videoFileName
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = applicationContext.contentResolver.insert(collection, values)

                fileUri?.let {
                    application.contentResolver.openFileDescriptor(fileUri, "w").use { descriptor ->
                        descriptor?.let {
                            FileOutputStream(descriptor.fileDescriptor).use { out ->
                                FileInputStream(videoFile).use { inputStream ->
                                    val buf = ByteArray(4096)
                                    while (true) {
                                        val sz = inputStream.read(buf)
                                        if (sz <= 0) break
                                        out.write(buf, 0, sz)
                                    }
                                }
                            }
                        }
                    }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    applicationContext.contentResolver.update(fileUri, values, null, null)

                    return File(getMediaPath(applicationContext, fileUri))
                }
            } else {
                val downloadsPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()

                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                Log.i("testritik", desFile.path)

                return desFile
            }
        }
        return null
    }


    private fun setReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.i("testritik", resultCode.toString())

        if (resultCode == 0) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}