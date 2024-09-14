package com.cuebit.io.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cuebit.io.Fragments.BottomSheetFragment
import com.cuebit.io.Fragments.HomeFragment
import com.cuebit.io.Fragments.ProfileFragment
import com.cuebit.io.R
import com.cuebit.io.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_UPDATE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the theme based on system setting
        setAppTheme()

        createNotificationChannel()
        val homeFragment = HomeFragment()
        loadFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.Profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "TimeItReminderChannel"
            val description = "Channel for tasks reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TimeIt", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    //Function loadFragment is to set the view of the selected fragment
    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finishAffinity()
    }

    // Function checkForAppUpdate is to check for new version of application on playstore
    /*private fun checkForAppUpdate() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                showUpdateSnackbar(appUpdateInfo)
            }
        }
    }*/

    //Code for showing sanckbar with new android application version information available on playstore
    /*private fun showUpdateSnackbar(appUpdateInfo: AppUpdateInfo) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "A new version is available.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Update") {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                this,
                AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE),
                REQUEST_CODE_UPDATE
            )
        }
        snackbar.setActionTextColor(resources.getColor(R.color.black, null))
        snackbar.setAction("Cancel") {
            snackbar.dismiss()
        }
        snackbar.show()
    }*/

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Update the theme when the configuration changes
        setAppTheme()
    }

    //onActivityResult will be called after startUpdateFlowForResult() in showUpdateSnackbar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Update failed. Please try again later.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    // Function to set the theme based on the system setting
    private fun setAppTheme() {
        // Restart the activity to apply the new theme
        updateUIWithNewTheme()
        updateThemeColors()
        refreshFragment()
    }

    //Function to set fragment background color
    private fun updateThemeColors() {
        val backgroundColor = ContextCompat.getColor(applicationContext, R.color.primary)
        binding?.root?.setBackgroundColor(backgroundColor)
    }

    //Function to update app theme according to mobile theme
    private fun updateUIWithNewTheme() {
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        rootView.post {
            val theme = AppCompatDelegate.getDefaultNightMode()
            rootView.setBackgroundColor(ContextCompat.getColor(this, getThemeColorRes(theme)))
        }
    }

    //Function to relaunch fragment after changing the theme
    private fun refreshFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        fragment?.let {
            val newFragment = it::class.java.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, newFragment)
                .commitAllowingStateLoss()
        }
    }

    private fun getThemeColorRes(theme: Int): Int {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val newNightMode = when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> R.color.primary
            AppCompatDelegate.MODE_NIGHT_NO -> R.color.primary
            else -> R.color.primary
        }
        return newNightMode
    }

}
