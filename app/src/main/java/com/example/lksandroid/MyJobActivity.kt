package com.example.lksandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lksandroid.network.ApiClient
import com.example.lksandroid.network.Job
import com.example.lksandroid.network.JobResponse
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyJobActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvSavedJobs: RecyclerView
    private lateinit var bottomNavbarLayout: TabLayout

    private var allJobsList: List<Job> = listOf()
    private var isShowingSavedTab = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_job)

        tabLayout = findViewById(R.id.tabLayout)
        rvSavedJobs = findViewById(R.id.rvSavedJobs)
        bottomNavbarLayout = findViewById(R.id.bottomNavbarLayout)

        rvSavedJobs.layoutManager = LinearLayoutManager(this)

        setupBottomNavbar()
        setupTopTabs()
    }

    override fun onResume() {
        super.onResume()
        // Pastikan tab "My Job" (posisi index 1) terpilih saat Activity ini aktif
        bottomNavbarLayout.getTabAt(1)?.select()
        fetchJobsData()
    }

    private fun setupTopTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isShowingSavedTab = (tab?.position == 0)
                filterAndDisplayData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchJobsData() {
        ApiClient.instance.getJobs().enqueue(object : Callback<JobResponse> {
            override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    allJobsList = response.body()!!.data
                    filterAndDisplayData()
                }
            }

            override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                Toast.makeText(this@MyJobActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterAndDisplayData() {
        if (isShowingSavedTab) {
            // TAB SAVED JOBS:
            // 1. Job di-save di JobStateManager atau dari API (isSaved == true)
            // 2. Job HANYA muncul jika posisinya masih tersedia/vacant (quota > 0)
            val savedList = allJobsList.filter { job ->
                val isSaved = JobStateManager.isJobSaved(this, job.id) || job.isSaved
                val isAvailable = job.quota > 0
                isSaved && isAvailable
            }

            val adapter = JobAdapter(
                jobList = savedList,
                isApplicationListMode = false,
                onItemStateChanged = { fetchJobsData() }
            )
            rvSavedJobs.adapter = adapter
        } else {
            // TAB APPLICATIONS LIST:
            // Job yang sudah di-apply (isApplied == true)
            val appliedList = allJobsList.filter { job ->
                JobStateManager.isJobApplied(this, job.id) || job.isApplied
            }

            val adapter = JobAdapter(
                jobList = appliedList,
                isApplicationListMode = true,
                onItemStateChanged = { fetchJobsData() }
            )
            rvSavedJobs.adapter = adapter
        }
    }

    private fun setupBottomNavbar() {
        bottomNavbarLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // Explore (Ke JobListActivity)
                        val intent = Intent(this@MyJobActivity, JobListActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    1 -> { /* Sudah di My Job */ }
                    2 -> {
                        val intent = Intent(this@MyJobActivity, ActivityProfile::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}