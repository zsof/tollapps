package hu.zsof.tollapps

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import hu.zsof.tollapps.Constants.MONITOR_UPDATE_REQUEST
import hu.zsof.tollapps.Constants.REQUEST_CODE
import hu.zsof.tollapps.databinding.ActivityMainBinding
import hu.zsof.tollapps.notification.NotificationReceiver
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var appUpdateManager: AppUpdateManager
    val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate()
        }
    }
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            val requestCode = it.data?.extras?.getInt(REQUEST_CODE)

            if (requestCode == MONITOR_UPDATE_REQUEST) {
                if (it.resultCode != RESULT_OK) {
                    Log.d("InAppUpdate", "Update Error! Result code: ${it.resultCode}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setPushNotification()
        checkUpdate()
    }

    override fun onResume() {
        super.onResume()

        val starterForInAppUpdate =
            IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                val request = IntentSenderRequest.Builder(intent)
                    .setFillInIntent(fillInIntent)
                    .setFlags(flagsValues, flagsMask)
                    .build()

                registerForActivityResult.launch(request)
            }

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        starterForInAppUpdate,
                        MONITOR_UPDATE_REQUEST,
                    )
                }
            }
    }

    private fun popupSnackBarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.main_activity),
            "Egy új verzió lett letöltve.",
            Snackbar.LENGTH_INDEFINITE,
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(Color.BLACK)
            show()
        }
    }

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        Log.d("InAppUpdate", "Checking for updates")
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MONITOR_UPDATE_REQUEST,
                )
                Log.d("InAppUpdate", "Update is available")
            } else {
                Log.d("InAppUpdate", "No Update available")
            }
        }
        appUpdateManager.registerListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(listener)
    }

    private fun setupNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        navController = navHostFragment.navController
    }

    private fun setPushNotification() {
        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        val intentFlag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, intentFlag)

        val timeOfAlarmHour = 20
        val timeOfAlarmMinute = 0
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, timeOfAlarmHour)
        calendar.set(Calendar.MINUTE, timeOfAlarmMinute)
        calendar.set(Calendar.SECOND, 0)

        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> setAlarmManagerRepeat(this, calendar.timeInMillis, pendingIntent)
            Calendar.TUESDAY -> setAlarmManagerRepeat(this, calendar.timeInMillis, pendingIntent)
            Calendar.WEDNESDAY -> setAlarmManagerRepeat(this, calendar.timeInMillis, pendingIntent)
            Calendar.THURSDAY -> setAlarmManagerRepeat(this, calendar.timeInMillis, pendingIntent)
            // Calendar.FRIDAY -> setAlarmManagerRepeat(this, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun setAlarmManagerRepeat(
        context: Context,
        timeOfAlarm: Long,
        pendingIntent: PendingIntent,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (System.currentTimeMillis() < timeOfAlarm) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                timeOfAlarm,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent,
            )
        }
    }
}
