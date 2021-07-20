package com.jimmy.cleanarchitecture.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jimmy.cleanarchitecture.databinding.ActivitySplashBinding

/**
 * Splash Screen with the app icon and name at the center, this is also the launch screen and
 * opens up in fullscreen mode. Once launched it waits for 2 seconds after which it opens the
 * MainActivity
 */
class SplashActivity : AppCompatActivity() {

  private lateinit var activitySplashBinding: ActivitySplashBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    makeFullScreen()

    activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
    setContentView(activitySplashBinding.root)

    // Using a handler to delay loading the MainActivity
    Handler(Looper.getMainLooper()).postDelayed({

      // Start activity
      startActivity(Intent(this, MainActivity::class.java))

      // Animate the loading of new activity
      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

      // Close this activity
      finish()

    }, 2000)
  }

  private fun makeFullScreen() {
    // Remove Title
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    @Suppress("DEPRECATION")
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
      window.insetsController?.hide(WindowInsets.Type.statusBars())
    }else {
      // Make Fullscreen
      window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
      )
    }

    // Hide the toolbar
    supportActionBar?.hide()
  }
}
