package com.example.lksandroid

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lksandroid.network.ApiClient
import com.example.lksandroid.network.JobDetail
import com.example.lksandroid.network.JobDetailResponse
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDetailactivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var tvJobTitle: TextView
    private lateinit var tvCompanyName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var tvContent: TextView
    private lateinit var btnApply: Button
    private lateinit var btnShare: ImageButton
    private lateinit var btnSave: ImageButton

    private var currentJobDetail: JobDetail? = null
    private var jobID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)

        jobID = intent.getIntExtra("JOB_ID", -1)

        btnBack = findViewById(R.id.btnBack)
        tvJobTitle = findViewById(R.id.tvJobTitle)
        tvCompanyName = findViewById(R.id.tvCompanyName)
        tvLocation = findViewById(R.id.tvLocation)
        tvExperience = findViewById(R.id.tvExperience)
        tabLayout = findViewById(R.id.tabLayout)
        tvContent = findViewById(R.id.tvContent)
        btnApply = findViewById(R.id.btnApply)
        btnShare = findViewById(R.id.btnShare)
        btnSave = findViewById(R.id.btnSave)

        btnBack.setOnClickListener { finish() }

        setupTabs()
        fetchJobDetail()
    }

    private fun fetchJobDetail() {
        if (jobID == -1) return

        ApiClient.instance.getJobDetail(jobID).enqueue(object : Callback<JobDetailResponse> {
            override fun onResponse(call: Call<JobDetailResponse>, response: Response<JobDetailResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val detail = response.body()!!.data

                    // SINKRONISASI STATUS DARI STATE MANAGER LOKAL
                    detail.isApplied = detail.isApplied || JobStateManager.isJobApplied(this@JobDetailactivity, detail.id)
                    detail.isSaved = JobStateManager.isJobSaved(this@JobDetailactivity, detail.id)

                    currentJobDetail = detail
                    bindDataToUI(detail)
                } else {
                    Toast.makeText(this@JobDetailactivity, "Gagal memuat detail job.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JobDetailResponse>, t: Throwable) {
                Toast.makeText(this@JobDetailactivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindDataToUI(job: JobDetail) {
        tvJobTitle.text = job.name
        tvCompanyName.text = job.company.name
        tvLocation.text = "${job.locationType} (${job.locationRegion})"
        tvExperience.text = "Min. ${job.yearOfExperience} years of experience"

        showCompanyOverview(job)
        updateSaveButtonStatus(job.isSaved)
        updateApplyButtonStatus(job.isApplied)

        btnShare.setOnClickListener {
            val shareText = "Hey! Checkout this job: ${job.jobLink ?: "https://gawean.id/job/${job.id}"}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share job via"))
        }

        // KLIK SAVE
        btnSave.setOnClickListener {
            val isNowSaved = JobStateManager.toggleJobSaved(this, job.id)
            job.isSaved = isNowSaved
            updateSaveButtonStatus(isNowSaved)

            ApiClient.instance.saveJob(jobID = job.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })

            Toast.makeText(this, if (isNowSaved) "Job disimpan!" else "Batal disimpan", Toast.LENGTH_SHORT).show()
        }

        // KLIK APPLY
        btnApply.setOnClickListener {
            if (job.isApplied) {
                Toast.makeText(this, "Kamu sudah melamar pekerjaan ini!", Toast.LENGTH_SHORT).show()
            } else {
                JobStateManager.setJobApplied(this, job.id)
                job.isApplied = true
                updateApplyButtonStatus(true)

                ApiClient.instance.applyJob(job.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })

                Toast.makeText(this, "Berhasil melamar pekerjaan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentJobDetail?.let { job ->
                    if (tab?.position == 0) {
                        showCompanyOverview(job)
                    } else {
                        showAboutJob(job)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showCompanyOverview(job: JobDetail) {
        val overview = job.description ?: "Company Overview\n\nNo description available."
        tvContent.text = overview
    }

    private fun showAboutJob(job: JobDetail) {
        val textBuilder = StringBuilder()

        textBuilder.append("Key Responsibilities:\n")
        textBuilder.append(job.responsibilities ?: "- Maintain and build software applications.").append("\n\n")

        textBuilder.append("Required Qualifications:\n")
        textBuilder.append(job.qualifications ?: "- Relevant degree or experience.").append("\n\n")

        textBuilder.append("Soft Skills:\n")
        textBuilder.append(job.softSkills ?: "- Communication, Teamwork.")

        tvContent.text = textBuilder.toString()
    }

    private fun updateSaveButtonStatus(isSaved: Boolean) {
        if (isSaved) {
            btnSave.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnSave.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun updateApplyButtonStatus(isApplied: Boolean) {
        if (isApplied) {
            btnApply.text = "Applied"
            btnApply.isEnabled = false
            btnApply.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#888888"))
        } else {
            btnApply.text = "Apply"
            btnApply.isEnabled = true
            btnApply.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6B4EFF"))
        }
    }
}