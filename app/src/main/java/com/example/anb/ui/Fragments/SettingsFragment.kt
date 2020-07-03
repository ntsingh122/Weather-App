package com.example.anb.ui.Fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.anb.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    companion object {
        val instance: SettingsFragment
            get() {
                val args = Bundle()
                val fragment = SettingsFragment()
                fragment.arguments = args
                return fragment
            }
    }
}