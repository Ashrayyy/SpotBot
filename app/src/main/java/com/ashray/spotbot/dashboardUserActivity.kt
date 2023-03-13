package com.ashray.spotbot

import android.media.FaceDetector
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashray.spotbot.databinding.ActivityDashboardUserBinding
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class dashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDashboardUserBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        var detector: FaceDetector? = null
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
//        detector = FaceDetection.getClient()(highAccuracyOpts)
    }
}