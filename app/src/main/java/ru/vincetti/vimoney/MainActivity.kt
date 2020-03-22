package ru.vincetti.vimoney

import androidx.appcompat.app.AppCompatActivity
import ru.vincetti.vimoney.utils.createNotificationChannel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onResume() {
        super.onResume()
        createNotificationChannel(this)
    }
}