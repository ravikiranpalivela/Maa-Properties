package com.tekskills.sampleapp.ui.splash

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.*
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ActivitySplashBinding
import com.tekskills.sampleapp.ui.base.BaseActivity
import com.tekskills.sampleapp.ui.main.MainActivity
import com.tekskills.sampleapp.ui.main.MainViewModel

class SplashActivity : BaseActivity<ActivitySplashBinding, MainViewModel, SplashActivity>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val prefrences =
            SharedPrefManager.getInstance(this)

        prefrences.theme.asLiveData().observe(this, Observer {
            if (it.equals("Light")) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else if (it.equals("Dark")) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        })

        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                val goToMainActivity = Intent(applicationContext, MainActivity::class.java)
                val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this@SplashActivity,
                    binding.appLogo,
                    "app_logo"
                )
                startActivity(goToMainActivity, activityOptions.toBundle())

                lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_STOP) {
                            lifecycle.removeObserver(this)
                            finish()
                        }
                    }
                })
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }.start()
    }

    override fun getViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java
    override fun getViewModelStoreOwner(): SplashActivity = this
    override fun getContext(): Context = this

}