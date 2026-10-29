package com.example.lksandroid

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lksandroid.network.ApiClient
import com.example.lksandroid.network.Job
import com.example.lksandroid.network.JobResponse
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobListActivity : AppCompatActivity() {

    private lateinit var rvJobs: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var chipGroupLocation: ChipGroup
    private lateinit var jobAdapter: JobAdapter

    private lateinit var btnApply: Button

    private lateinit var btnSave: ImageButton

    private var originalJobList: List<Job> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_list)

        rvJobs = findViewById(R.id.rvJobs)
        etSearch = findViewById(R.id.etSearch)
        chipGroupLocation = findViewById(R.id.chipGroupLocation)

        setupRecyclerView()
        fetchJobsFromApi()
        setupSearchAndFilter()

        val bottomNavbarLayout = findViewById<TabLayout>(R.id.bottomNavbarLayout)
        bottomNavbarLayout.getTabAt(0)?.select()

        bottomNavbarLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {}
                    1 -> {startActivity(Intent(this@JobListActivity, MyJobActivity::class.java))}
                    2 -> {startActivity(Intent(this@JobListActivity, ActivityProfile::class.java))}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter(
            jobList = listOf(),
            isApplicationListMode = false,
            onItemStateChanged = {
                // Dipanggil saat item di-apply atau di-save untuk menyegarkan tampilan
                fetchJobsFromApi()
            }
        )
        rvJobs.layoutManager = LinearLayoutManager(this)
        rvJobs.adapter = jobAdapter
    }

    private fun fetchJobsFromApi() {
        ApiClient.instance.getJobs().enqueue(object : Callback<JobResponse> {
            override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                if (response.isSuccessful) {
                    val jobs = response.body()?.data ?: listOf()
                    originalJobList = jobs
                    jobAdapter.updateData(jobs)
                } else {
                    Toast.makeText(this@JobListActivity, "Gagal mengambil data job", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                Toast.makeText(this@JobListActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchAndFilter() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterJobs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroupLocation.setOnCheckedChangeListener { _, _ ->
            filterJobs()
        }
    }

    private fun filterJobs() {
        val query = etSearch.text.toString().lowercase().trim()
        val selectedFilter = when (chipGroupLocation.checkedChipId) {
            R.id.chipOnsite -> "onsite"
            R.id.chipRemote -> "remote"
            else -> "all"
        }

        val filteredList = originalJobList.filter { job ->
            val matchesSearch = job.name.lowercase().contains(query) || job.company.name.lowercase().contains(query)
            val matchesLocation = if (selectedFilter == "all") {
                true
            } else {
                job.locationType.lowercase() == selectedFilter
            }

            matchesSearch && matchesLocation
        }
        jobAdapter.updateData(filteredList)
    }

    private fun updateSaveButtonStatus(isSaved: Boolean) {
        if (isSaved) {
            btnSave.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnSave.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    override fun onResume() {
        super.onResume()
        jobAdapter.notifyDataSetChanged()
    }
}