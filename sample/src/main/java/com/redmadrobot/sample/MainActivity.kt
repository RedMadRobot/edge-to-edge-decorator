package com.redmadrobot.sample

import android.os.Bundle
import com.redmadrobot.e2e.decorator.EdgeToEdgeDecorator
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main) {

    override val edgeToEdgeCompatibilityManager = EdgeToEdgeDecorator.updateConfig {
        // custom config
        isEdgeToEdgeEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app_bar.applySystemWindowInsetsToPadding(top = true)
    }
}
