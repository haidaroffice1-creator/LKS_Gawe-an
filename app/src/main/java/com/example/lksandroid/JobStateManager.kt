package com.example.lksandroid

import android.content.Context
import android.content.SharedPreferences

object JobStateManager {
    private const val PREF_NAME = "gawean_job_prefs"
    private const val KEY_APPLIED_JOBS = "applied_jobs_ids"
    private const val KEY_SAVED_JOBS = "saved_jobs_ids"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setJobApplied(context: Context, jobId: Int) {
        val appliedSet = getAppliedJobIds(context).toMutableSet()
        appliedSet.add(jobId.toString())
        getPrefs(context).edit().putStringSet(KEY_APPLIED_JOBS, appliedSet).apply()
    }

    fun isJobApplied(context: Context, jobId: Int): Boolean {
        return getAppliedJobIds(context).contains(jobId.toString())
    }

    private fun getAppliedJobIds(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_APPLIED_JOBS, emptySet()) ?: emptySet()
    }

    fun toggleJobSaved(context: Context, jobId: Int): Boolean {
        val savedSet = getSavedJobIds(context).toMutableSet()
        val jobIdString = jobId.toString()
        val newState: Boolean

        if (savedSet.contains(jobIdString)) {
            savedSet.remove(jobIdString)
            newState = false
        } else {
            savedSet.add(jobIdString)
            newState = true
        }

        getPrefs(context).edit().putStringSet(KEY_SAVED_JOBS, savedSet).apply()
        return newState
    }

    fun isJobSaved(context: Context, jobId: Int): Boolean {
        return getSavedJobIds(context).contains(jobId.toString())
    }

    private fun getSavedJobIds(context: Context): Set<String> {
        return  getPrefs(context).getStringSet(KEY_SAVED_JOBS, emptySet()) ?: emptySet()
    }
}
