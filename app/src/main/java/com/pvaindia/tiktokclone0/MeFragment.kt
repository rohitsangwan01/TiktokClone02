package com.pvaindia.tiktokclone0

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pvaindia.tiktokclone0.model.HomeModel
import com.pvaindia.tiktokclone0.ui.PlayerActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MeFragment : Fragment() {
    var followers: TextView? = null
    var likes: TextView? = null
    var followings: TextView? = null
    var uname: TextView? = null
    var recyclerView: RecyclerView? = null
    var sp: SharedPreferences? = null
    var spinKitView: SpinKitView? = null
    var homeModels: MutableList<HomeModel> = ArrayList()
    var img: ImageView? = null
    var optionsBtn: ImageView? = null
    var load: LinearLayout? = null
    var uploadBtn: CardView? = null
    private var mStorageRef: StorageReference? = null

    private lateinit var path: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_me, container, false)

//        setReadStoragePermission()

        mStorageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://tiktokclone-0.appspot.com/ProfilePicture/")
        recyclerView = v.findViewById(R.id.recyclerview)
        followers = v.findViewById(R.id.txt_followers)
        likes = v.findViewById(R.id.txt_likes)
        followings = v.findViewById(R.id.txt_following)
        spinKitView = v.findViewById(R.id.spinkit)
        uname = v.findViewById(R.id.uname)
        img = v.findViewById(R.id.img)
        load = v.findViewById(R.id.load)
        optionsBtn = v.findViewById(R.id.options_btn)
        uploadBtn = v.findViewById(R.id.upload_btn)
        optionsBtn?.setOnClickListener(View.OnClickListener { v1: View? ->
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            val activity = context as Activity?
            activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        uploadBtn?.setOnClickListener(View.OnClickListener { v12: View? ->
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "video/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Video"), 13)

            val intent = Intent()
            intent.apply {
                type = "video/*"
                action = Intent.ACTION_PICK
            }
            startActivityForResult(Intent.createChooser(intent, "Select video"), 13)

        })
        img?.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 111)
        })
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(GridLayoutManager(context, 3))
        if (context != null) {
            sp = context!!.getSharedPreferences("pref", 0)
            LoadProfile()
        }
        return v
    }

    fun LoadProfile() {
        FirebaseDatabase.getInstance()
            .reference
            .child("PVA")
            .child("Users")
            .orderByChild("id")
            .equalTo(sp!!.getString("id", null))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            followings!!.text = data.child("followingsCount").getValue(
                                String::class.java
                            )
                            followers!!.text = data.child("followersCount").getValue(
                                String::class.java
                            )
                            likes!!.text = data.child("likes").getValue(String::class.java)
                            uname!!.text = data.child("uname").getValue(String::class.java)
                            if (context != null) {
                                Glide.with(context!!)
                                    .load(data.child("image").getValue(String::class.java))
                                    .placeholder(R.drawable.icon_user_profile)
                                    .error(R.drawable.icon_user_profile)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(img!!)
                            }
                            load!!.visibility = View.GONE
                            LoadMyVid()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun LoadMyVid() {
        FirebaseDatabase.getInstance()
            .reference
            .child("PVA")
            .child("Videos")
            .orderByChild("uid")
            .equalTo(sp!!.getString("id", null))
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as Map<String, String>?
                    homeModels.add(
                        HomeModel(
                            "" + map!!["uname"],
                            "" + map["tags"],
                            "" + map["views"],
                            "" + map["likes"],
                            "" + map["liked"],
                            "" + map["shares"],
                            "" + map["link"],
                            "" + map["uid"],
                            "" + map["id"],
                            "" + map["commentsCount"],
                            ""+map["description"]
                        )
                    )
                    recyclerView!!.adapter = ProfileAdapter(context, homeModels)
                    spinKitView!!.visibility = View.GONE
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.i("testritik", "1 video removed")
//                    homeModels.clear()
//                    LoadMyVid()
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        FirebaseDatabase.getInstance()
            .reference
            .child("PVA")
            .child("Videos")
            .orderByChild("uid")
            .equalTo(sp!!.getString("id", null))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(
                            context,
                            "You don't have any videos yet!",
                            Toast.LENGTH_SHORT
                        ).show()
                        spinKitView!!.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

//            val progressDialog: ProgressDialog = ProgressDialog(context)
//            progressDialog.setMessage("Compressing video...")
//            progressDialog.setCancelable(false)
//
//
//            val uri = data.data
//
//            uri?.let {
//
//                GlobalScope.launch {
//                    // run in background as it can take a long time if the video is big,
//                    // this implementation is not the best way to do it,
//                    // todo(abed): improve threading
//                    val job = async { context?.let { it1 -> getMediaPath(it1, uri) } }
//                    path = job.await().toString()
//
//                    val desFile = saveVideoFile(path)
//
//                    desFile?.let {
//                        var time = 0L
//                        VideoCompressor.start(
//                            path,
//                            desFile.path,
//                            object : CompressionListener {
//                                override fun onProgress(percent: Float) {
//                                    //Update UI
//
//                                }
//
//                                override fun onStart() {
//
//                                    progressDialog.show()
//
//                                }
//
//                                override fun onSuccess() {
//
//                                    progressDialog.cancel()
//
//                                    val uri: Uri = Uri.fromFile(desFile)
//
//                                    val intent = Intent(context, PlayerActivity::class.java)
//                                    val b = Bundle()
//                                    //            Log.i("testritik:videoPath", videoFilePath);
//                                    b.putString("player_uri", "null")
//                                    b.putString("videoUri", uri.toString())
//                                    intent.putExtras(b)
//                                    startActivity(intent)
//
//                                    Log.i("testritik", "comp success")
//
//                                }
//
//                                override fun onFailure(failureMessage: String) {
//                                    Log.wtf("failureMessage", failureMessage)
//                                }
//
//                                override fun onCancelled() {
//                                    Log.wtf("TAG", "compression has been cancelled")
//                                    // make UI changes, cleanup, etc
//                                }
//                            },
//                            VideoQuality.LOW,
//                            isMinBitRateEnabled = false,
//                            keepOriginalResolution = false,
//                        )
//                    }
//                }
//            }

            val intent = Intent(context, PlayerActivity::class.java)
            val b = Bundle()
            //            Log.i("testritik:videoPath", videoFilePath);
            b.putString("player_uri", "null")
            b.putString("videoUri", data.data.toString())
            intent.putExtras(b)
            startActivity(intent)


        }
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            Glide.with(context!!)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(img!!)
            val id = sp!!.getString("id", null)
            val temp = id + "&" + System.currentTimeMillis()
            val image =
                "https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F$temp?alt=media"
            val storage = mStorageRef!!.child(temp)
            FirebaseDatabase.getInstance()
                .reference
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val map: MutableMap<String, String> = HashMap<String, String>()
                            map["image"] = image
                            FirebaseDatabase.getInstance()
                                .reference
                                .child("PVA")
                                .child("Users")
                                .child(data.key!!)
                                .updateChildren(map as Map<String, Any>)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            storage.putFile(uri!!)
        }
    }
}