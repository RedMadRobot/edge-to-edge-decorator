package com.redmadrobot.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.redmadrobot.e2e.decorator.EdgeToEdgeDecorator

abstract class BaseActivity(activityMain: Int) : AppCompatActivity(activityMain) {

    protected open val edgeToEdgeCompatibilityManager = EdgeToEdgeDecorator.updateConfig {
        // default config
        isEdgeToEdgeEnabled = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        edgeToEdgeCompatibilityManager.apply(this, window)
    }
}
