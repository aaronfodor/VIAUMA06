package com.arpadfodor.communityparking.android.app.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.arpadfodor.communityparking.android.app.ApplicationRoot
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.model.AccountService
import com.arpadfodor.communityparking.android.app.model.repository.GeneralRepository
import com.arpadfodor.communityparking.android.app.view.utils.AppSnackBarBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var settingsKeepScreenAlive = ""
    private var settingsAutoSync = ""
    private var settingsLastSyncedDbReports = ""

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {

        super.onResume()

        settingsKeepScreenAlive = getString(R.string.SETTINGS_KEEP_SCREEN_ALIVE)
        settingsAutoSync = getString(R.string.SETTINGS_AUTO_SYNC)
        settingsLastSyncedDbReports = getString(R.string.LAST_SYNCED_DB_REPORTS)

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        
        updatePreferenceUpdatedTimestamp(preferences)

        val syncButton: Preference? = findPreference(getString(R.string.SETTINGS_SYNC_NOW))

        syncButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            GeneralRepository.updateAll{ isReportsSuccess ->

                    if(isReportsSuccess){
                        val currentTime = Calendar.getInstance().time.toString()
                        preferences.edit().putString(getString(R.string.LAST_SYNCED_DB_REPORTS), currentTime)
                            .apply()
                    }

                    dbUpdateResultSnackBar(isReportsSuccess)

            }

            true

        }

    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Called when a shared preference is changed, added, or removed.
     * This may be called even if a preference is set to its existing value.
     * This callback will be run on main thread.
     * Note: This callback will not be triggered when preferences are cleared via[Editor.clear].
     *
     * @param sharedPreferences The [SharedPreferences] that received the change
     * @param key The key of the preference that was changed, added, or removed
     **/
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let {
            changeSettings(sharedPreferences){
                updatePreferenceUpdatedTimestamp(sharedPreferences)
            }
        }
    }

    private fun changeSettings(sharedPreferences: SharedPreferences?, callback: (Boolean) -> Unit) {

        sharedPreferences?: return

        with (sharedPreferences.edit()) {

            remove(settingsKeepScreenAlive)
            val keepScreenOn = sharedPreferences.getBoolean(settingsKeepScreenAlive,
                resources.getBoolean(R.bool.settings_keep_screen_alive_default)
            )
            putBoolean(settingsKeepScreenAlive, keepScreenOn)
            ApplicationRoot.keepScreenAlive = keepScreenOn

            remove(settingsAutoSync)
            putBoolean(settingsAutoSync, sharedPreferences.getBoolean(settingsAutoSync,
                resources.getBoolean(R.bool.settings_auto_sync_default)
            ))

            remove(settingsLastSyncedDbReports)
            val updatedDbReportsTimestamp = sharedPreferences.getString(settingsLastSyncedDbReports,
                resources.getString(R.string.settings_last_synced_default)).toString()
            putString(settingsLastSyncedDbReports, updatedDbReportsTimestamp)

            apply()
        }

        callback(true)

    }

    private fun updatePreferenceUpdatedTimestamp(sharedPreferences: SharedPreferences){

        val dbReportsTimestamp = sharedPreferences.getString(settingsLastSyncedDbReports,
            resources.getString(R.string.settings_last_synced_default)).toString()
        preferenceManager.findPreference<Preference>(getString(R.string.LAST_SYNCED_DB_REPORTS))?.
        summary = dbReportsTimestamp

    }

    private fun dbUpdateResultSnackBar(isSuccess: Boolean){

        if(isSuccess){
            AppSnackBarBuilder.buildSuccessSnackBar(requireContext().applicationContext, this.requireView(),
                getString(R.string.updated), Snackbar.LENGTH_SHORT).show()
        }
        else{
            AppSnackBarBuilder.buildAlertSnackBar(requireContext().applicationContext, this.requireView(),
                getString(R.string.update_failed), Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun dbDeleteResultSnackBar(isSuccess: Boolean){

        if(isSuccess){
            AppSnackBarBuilder.buildInfoSnackBar(requireContext().applicationContext, this.requireView(),
                getString(R.string.deleted), Snackbar.LENGTH_SHORT).show()
        }
        else{
            AppSnackBarBuilder.buildAlertSnackBar(requireContext().applicationContext, this.requireView(),
                getString(R.string.delete_failed), Snackbar.LENGTH_SHORT).show()
        }

    }

}