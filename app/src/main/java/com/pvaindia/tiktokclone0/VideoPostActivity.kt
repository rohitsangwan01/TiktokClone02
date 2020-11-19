package com.pvaindia.tiktokclone0

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_video_post.*
import java.io.File
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VideoPostActivity : AppCompatActivity() {


    var videoCategories: ArrayList<String>? = ArrayList<String>()
    private var progressDialog: ProgressDialog? = null
    private var mStorageRef: StorageReference? = null
    private var sp: SharedPreferences? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_post)


        supportActionBar!!.hide()


        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading...")
        progressDialog!!.setCancelable(false)

        mStorageRef = FirebaseStorage.getInstance().reference;

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabase!!.reference.child("PVA").child("Videos")

        sp = getSharedPreferences("pref", 0)

        post_video.setOnClickListener(View.OnClickListener {
            uploadVideoToFirebase(Uri.parse(intent.getStringExtra("videoUri")))
        })

        back_btn.setOnClickListener(View.OnClickListener {
            finish()
        })


    }


    private fun uploadVideoToFirebase(file: Uri) {
//        Log.i("item_comment", videoPath)

//        val file = Uri.fromFile(File(videoPath))
//        val videoRef: StorageReference = mStorageRef!!.child("videos/" + System.currentTimeMillis() + ".jpg")
//
//        videoRef.putFile(file)
//            .addOnSuccessListener {
//
//                Log.i("item_comment", "video uploaded")
//            }
//            .addOnFailureListener {
//
//                Log.i("item_comment", "video uploaded FAILED")
//            }
        progressDialog!!.show()

        val  videoId = UUID.randomUUID().toString().replace("-".toRegex(), "")


//        var file: Uri
//        if (uriString == null) {
//            file = videoUri!!
//        }
//        else {
//            file = Uri.fromFile(File(videoPath))
//        }

        val ref = mStorageRef!!.child("videos/$videoId.mp4")
        val uploadTask = ref.putFile(file)



        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
//                mDatabaseRef!!.child("item_comment").setValue(downloadUri)

                Log.i("item_comment", "video uploaded")
                Log.i("item_comment", downloadUri.toString())

                var categories: String = ""



                for (category in videoCategories!!) {
                    Log.i("testritik", category)
                    categories += "$category "
                }

                Log.i("testritik", categories)




                val map = HashMap<String, String>();
                map["comments"] = "";
                map["commentsCount"] = "0";
                map["commentsImg"] = ""
                map["commentsUname"] = ""
                map["id"] = videoId.toString()
                map["liked"] = ""
                map["likes"] = "0"
                map["link"] = downloadUri.toString()
                map["music"] = "faded"
                map["reports"] = "0"
                map["shares"] = "0"
                map["tags"] = categories
                map["uid"] = sp!!.getString("id", null).toString()
                map["uname"] = sp!!.getString("uname", null).toString()
                map["views"] = "0"
                map["description"] = et_desc.text.toString()

                mDatabaseRef?.push()?.setValue(map)



                progressDialog?.cancel()

                val file: File = File(file.path)
                if (file.exists()) {
                    file.delete()
                }

                Toast.makeText(applicationContext, "Video Uploaded", Toast.LENGTH_LONG).show()
                finish()

            } else {
                // Handle failures
                // ...
                Log.i("item_comment", "video uploaded FAILED")
                Toast.makeText(applicationContext, "Video upload failed", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun selectCategory(view: View) {

        val textView: TextView = view as TextView;
        val text: String = textView.text.toString()

        if (videoCategories!!.contains(text)) {
            videoCategories!!.remove(text)
            view.background = resources.getDrawable(R.drawable.hash_tag_bg)
        }
        else {
            videoCategories!!.add(text)
            view.background = resources.getDrawable(R.drawable.hash_tag_bg_selected)
        }
    }

    override fun onStop() {
        setResult(2)
        super.onStop()
    }

    override fun onDestroy() {
        setResult(2)
        super.onDestroy()
    }

}