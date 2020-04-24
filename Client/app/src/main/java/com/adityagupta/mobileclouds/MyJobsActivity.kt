package com.adityagupta.mobileclouds

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityagupta.mobileclouds.adapters.MyJobsAdapter
import com.adityagupta.mobileclouds.model.MyJobs
import com.adityagupta.mobileclouds.utils.RetrofitHelper
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.activity_my_jobs.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyJobsActivity : AppCompatActivity() {

    private val fileRequestCode: Int = 21
    var adapter: MyJobsAdapter? = null
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_jobs)
        mStorageRef = FirebaseStorage.getInstance().reference.child("jobs")

        recyclerView.layoutManager = LinearLayoutManager(this)

        addJob.setOnClickListener {
            val intent = Intent(this, FilePickerActivity::class.java)
            intent.putExtra(FilePickerActivity.CONFIGS, Configurations.Builder()
                .setCheckPermission(true)
                .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                    "zip", "tar", "gz", "rar", "7z", "torrent",
                    "doc", "docx", "odt", "ott",
                    "ppt", "pptx", "pps",
                    "xls", "xlsx", "ods", "ots")
                .setShowFiles(true)
                .setShowAudios(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build())
            startActivityForResult(intent, fileRequestCode)
        }

        getAllJobs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            fileRequestCode -> {
                val files = data?.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)
                files?.get(0)?.let { file ->
                    mStorageRef?.child(file.name)?.putFile(file.uri)
                        ?.addOnSuccessListener {
                            RetrofitHelper.addMyJob(this, file.name, file.size, object : Callback<ResponseBody?> {
                                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                }

                                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                                    getAllJobs()
                                }
                            })
                        }
                }
            }
        }
    }

    private fun getAllJobs() {
        RetrofitHelper.getMyJobs(this, object : Callback<MyJobs?> {
            override fun onFailure(call: Call<MyJobs?>, t: Throwable) {
            }

            override fun onResponse(call: Call<MyJobs?>, response: Response<MyJobs?>) {
                response.body()?.let { allJobs ->
                    adapter = MyJobsAdapter(allJobs.allJobs, this@MyJobsActivity)
                    recyclerView.adapter = adapter
                }
            }
        })
    }
}
