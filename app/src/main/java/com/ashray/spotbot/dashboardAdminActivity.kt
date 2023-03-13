package com.ashray.spotbot

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ashray.spotbot.databinding.ActivityDashboardAdminBinding
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector

import com.google.mlkit.vision.face.FaceDetectorOptions


class dashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private var faceDetector: FaceDetector? = null
    val PICK_IMAGE = 1
    lateinit var image : Bitmap
    lateinit var imagebit: InputImage

//    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        Glide.with(this).load(uri).into(inputImage)
//        // Handle the returned Uri
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardAdminBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ab)
        imagebit = InputImage.fromBitmap(bitmap , 0)
        val myOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

        /* Aim
        1. get bitmap in portrait mode from firebase mlkit
        2. get face embeddings from facenet
         */

//        faceDetector = detector
        val inputImage=binding.inputImage
        val resultTv=binding.resultTv
        val button=binding.button


        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Glide.with(this).load(uri).into(inputImage)
//            image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ab)
//            imagebit = InputImage.fromBitmap(bitmap , 0)
            // Handle the returned Uri
        }

        // handle the Choose Image button to trigger
        // the image chooser function

        // handle the Choose Image button to trigger
        // the image chooser function
//        button.setOnClickListener{
//            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
//            getIntent.type = "image/*"
//
//            val pickIntent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickIntent.type = "image/*"
//
//            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//
//            startActivityForResult(chooserIntent, PICK_IMAGE)
//            onActivityResult()
//        }

        button.setOnClickListener {
            // Pass in the mime type you'd like to allow the user to select
            // as the input
            getContent.launch("image/*")

        }


//        val image = InputImage.fromBitmap(bitmap,0)

        val detector= FaceDetection.getClient(myOpts)
//            val result = detector.process(image)
        val result = detector.process(imagebit)
            .addOnSuccessListener { faces ->

//                for (face in faces) {
//                    val bounds = face.boundingBox
//                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                    // nose available):
//                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//                    leftEar?.let {
//                        val leftEarPos = leftEar.position
//                    }
//
//                    // If contour detection was enabled:
//                    val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
//                    val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
//
//                    // If classification was enabled:
//                    if (face.smilingProbability != null) {
//                        val smileProb = face.smilingProbability
//                    }
//                    if (face.rightEyeOpenProbability != null) {
//                        val rightEyeOpenProb = face.rightEyeOpenProbability
//                    }
//
//                    // If face tracking was enabled:
//                    if (face.trackingId != null) {
//                        val id = face.trackingId
//                    }
//                }
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                // ...
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
    }


}


