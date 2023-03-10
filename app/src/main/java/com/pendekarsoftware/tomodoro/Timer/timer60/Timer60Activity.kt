package com.pendekarsoftware.tomodoro.Timer.timer60

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pendekarsoftware.tomodoro.R
import com.pendekarsoftware.tomodoro.databinding.ActivityTimer60Binding

class Timer60Activity : AppCompatActivity() {

    private var startMilliSeconds = 3600000L

    private lateinit var countdowntimer: CountDownTimer
    private lateinit var binding: ActivityTimer60Binding
    private var isRunning: Boolean = false
    private var timeInMilliSeconds = 3600000L

    val channelID = "channelID"
    private val channelName = "channelName"
    val notifID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer25)
        binding = ActivityTimer60Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.main_color)
        }

        createNotifChannel()

        binding.ivStart.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer(timeInMilliSeconds)
            }
        }

        binding.ivReset.setOnClickListener {
            resetTimer()
        }


    }

    private fun createNotifChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID,channelName,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor= Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun pauseTimer() {

        binding.ivStart.setImageResource(R.drawable.iv_start)
        countdowntimer.cancel()
        isRunning = false
        binding.ivReset.visibility = View.VISIBLE
    }

    private fun startTimer(time_in_seconds: Long) {
        countdowntimer = object : CountDownTimer(time_in_seconds, 1000) {

            override fun onTick(p0: Long) {
                timeInMilliSeconds = p0
                updateTextUI()
            }

            override fun onFinish() {
                Toast.makeText(
                    this@Timer60Activity,
                    "Congrats! you have Finished the Timer.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@Timer60Activity, Timer60Activity::class.java)
                val pendingIntent = TaskStackBuilder.create(this@Timer60Activity).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }


                val notifManager = NotificationManagerCompat.from(this@Timer60Activity)

                val notif = NotificationCompat.Builder(this@Timer60Activity, channelID)
                    .setContentTitle("Timer Finished")
                    .setContentText("Good Job! you have finished your Timer.")
                    .setSmallIcon(R.drawable.ic_timer_notification)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .build()

                notifManager.notify(notifID, notif)
            }
        }
        countdowntimer.start()

        isRunning = true
        binding.ivStart.setImageResource(R.drawable.iv_pause)
        binding.ivReset.visibility = View.INVISIBLE

    }

    private fun resetTimer() {
        timeInMilliSeconds = startMilliSeconds
        updateTextUI()
    }

    private fun updateTextUI() {
        val minute = (timeInMilliSeconds / 1000) / 60
        val seconds = (timeInMilliSeconds / 1000) % 60

        binding.textViewCountdown.text = "$minute:$seconds"
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}