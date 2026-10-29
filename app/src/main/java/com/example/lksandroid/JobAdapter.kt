package com.example.lksandroid

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lksandroid.network.ApiClient
import com.example.lksandroid.network.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobAdapter(
    private var jobList: List<Job>,
    private val isApplicationListMode: Boolean = false,
    private val onItemStateChanged: () -> Unit = {}
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvCompanyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvExperience: TextView = itemView.findViewById(R.id.tvExperience)
        val btnApply: Button = itemView.findViewById(R.id.btnApply)
        val btnSave: ImageButton = itemView.findViewById(R.id.btnSave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        val context = holder.itemView.context

        // SINKRONISASI STATUS LOKAL
        job.isApplied = job.isApplied || JobStateManager.isJobApplied(context, job.id)
        job.isSaved = JobStateManager.isJobSaved(context, job.id)

        holder.tvJobTitle.text = job.name
        holder.tvCompanyName.text = job.company.name
        holder.tvLocation.text = "${job.locationType} (${job.locationRegion})"
        holder.tvExperience.text = "Min. ${job.yearOfExperience} years of experience."

        if (isApplicationListMode) {
            // MODE APPLICATIONS LIST: Teks "Waiting for Confirmation" & Tombol Save disembunyikan
            holder.btnApply.text = "Waiting for Confirmation"
            holder.btnApply.isEnabled = false
            holder.btnApply.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6B4EFF"))
            holder.btnSave.visibility = View.GONE
        } else {
            // MODE NORMAL / SAVED JOBS
            holder.btnSave.visibility = View.VISIBLE
            updateSaveIcon(holder.btnSave, job.isSaved)
            updateApplyButton(holder.btnApply, job.isApplied)
        }

        // KLIK SAVE
        holder.btnSave.setOnClickListener {
            val isNowSaved = JobStateManager.toggleJobSaved(context, job.id)
            job.isSaved = isNowSaved

            ApiClient.instance.saveJob(jobID = job.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })

            Toast.makeText(context, if (isNowSaved) "Job disimpan!" else "Job dihapus dari Saved Jobs", Toast.LENGTH_SHORT).show()
            onItemStateChanged() // Otomatis meremove item jika di-unsave dari Saved Jobs Tab
        }

        // KLIK APPLY
        holder.btnApply.setOnClickListener {
            if (isApplicationListMode || job.isApplied) return@setOnClickListener

            JobStateManager.setJobApplied(context, job.id)
            job.isApplied = true
            updateApplyButton(holder.btnApply, true)

            ApiClient.instance.applyJob(job.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })

            Toast.makeText(context, "Berhasil melamar ${job.name}!", Toast.LENGTH_SHORT).show()
            onItemStateChanged()
        }

        // KLIK ITEM MENGARAH KE JOB DETAIL
        holder.itemView.setOnClickListener {
            val intent = Intent(context, JobDetailactivity::class.java)
            intent.putExtra("JOB_ID", job.id)
            context.startActivity(intent)
        }
    }

    private fun updateSaveIcon(btnSave: ImageButton, isSaved: Boolean) {
        if (isSaved) {
            btnSave.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnSave.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun updateApplyButton(btnApply: Button, isApplied: Boolean) {
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

    override fun getItemCount(): Int = jobList.size

    fun updateData(newList: List<Job>) {
        jobList = newList
        notifyDataSetChanged()
    }
}