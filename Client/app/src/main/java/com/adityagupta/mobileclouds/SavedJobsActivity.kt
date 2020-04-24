package com.adityagupta.mobileclouds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityagupta.mobileclouds.adapters.MySavedJobsAdapter
import com.adityagupta.mobileclouds.model.MySavedJobs
import com.adityagupta.mobileclouds.utils.RetrofitHelper
import kotlinx.android.synthetic.main.activity_saved_jobs.recyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedJobsActivity : AppCompatActivity() {

    var adapter: MySavedJobsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_jobs)

        recyclerView.layoutManager = LinearLayoutManager(this)

        getAllSavedJobs()
    }

    private fun getAllSavedJobs() {
        RetrofitHelper.getSavedJobs(this, object : Callback<MySavedJobs?> {
            override fun onFailure(call: Call<MySavedJobs?>, t: Throwable) {
            }

            override fun onResponse(call: Call<MySavedJobs?>, response: Response<MySavedJobs?>) {
                response.body()?.let { allJobs ->
                    adapter = MySavedJobsAdapter(allJobs.allSavedJobs, this@SavedJobsActivity)
                    recyclerView.adapter = adapter
                }
            }
        })
    }
}
