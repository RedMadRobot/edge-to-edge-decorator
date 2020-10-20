package com.redmadrobot.sample

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.redmadrobot.e2e.decorator.EdgeToEdgeDecorator
import com.redmadrobot.sample.databinding.ActivityMainBinding
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding

class MainActivity : BaseActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding

    override val edgeToEdgeCompatibilityManager = EdgeToEdgeDecorator.updateConfig {
        // custom config
        isEdgeToEdgeEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_App)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.appBar.applySystemWindowInsetsToPadding(top = true)

        val data = (0..10).map {
            SampleData(
                image = R.drawable.ic_launcher_foreground,
                title = "Title $it",
                description = "Description $it"
            )
        }

        with(binding.recycler) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = SampleAdapter(data)
            applySystemWindowInsetsToPadding(bottom = true)
        }
    }
}
