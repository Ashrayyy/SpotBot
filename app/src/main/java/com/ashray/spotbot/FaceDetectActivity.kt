package com.ashray.spotbot

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.ashray.spotbot.databinding.ActivityDashboardAdminBinding
import com.ashray.spotbot.databinding.ActivityFaceDetectBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectBinding
    private lateinit var originalIv:ImageView
    private lateinit var croppedIv:ImageView
    private lateinit var detectFaceBtn: Button
    private lateinit var detector:FaceDetector

    private companion object{
        private const val SCALING_FACTOR=10
        private const val TAG="FACE_DETECT_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFaceDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options=FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .build()

        detector=FaceDetection.getClient(options)




        originalIv=binding.originalIv
        detectFaceBtn=binding.detectFaceBtn
        croppedIv=binding.croppedIv


//// Important      from drawables
        val resourceimg=R.drawable.messi
        val bitmap1=BitmapFactory.decodeResource(resources,resourceimg)
        originalIv.setImageResource(resourceimg)

//// Important       from image view
//        val bitmapDrawable=originalIv.drawable as   BitmapDrawable
//        val bitmap2=bitmapDrawable.bitmap
//
////// Important       from uri
//        val imageUri: Uri?=null
//        try {
//            val bitmap3=MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//        }catch (e:Exception) {
//            Log.e(TAG, "OnCreate: ", e)
//        }


        detectFaceBtn.setOnClickListener {
            analyzePhoto(bitmap1)
        }
    }
    private fun analyzePhoto(bitmap:Bitmap){
        Log.d(TAG, "analyze Photo: ")

        val smallerBitmap=Bitmap.createScaledBitmap(
            bitmap,bitmap.width/ SCALING_FACTOR,
            bitmap.height/ SCALING_FACTOR,
            false
        )

        val inputImage=InputImage.fromBitmap(smallerBitmap,0)
        detector.process(inputImage)
            .addOnSuccessListener {faces->
                Log.d(TAG,"analyzePhoto: Successfully detected face ")
                Toast.makeText(this,"Face Detected",Toast.LENGTH_SHORT).show()

                for(face in faces){
                    val rect=face.boundingBox
                    rect.set(rect.left* SCALING_FACTOR,
                    rect.top*(SCALING_FACTOR-1),
                    rect.right*(SCALING_FACTOR),
                        rect.bottom* SCALING_FACTOR +90)
                }

                cropDetectedFace(bitmap,faces)
            }
            .addOnFailureListener{e->
                Log.e(TAG,"analyzePhoto: ",e)
                Toast.makeText(this,"Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun cropDetectedFace(bitmap: Bitmap,faces:List<Face>){
        Log.d(TAG,"cropDetectedFaces")

        val rect=faces[0].boundingBox
        val x=Math.max(rect.left,0)
        val y=Math.max(rect.top,0)
        val width=rect.width()
        val height=rect.height()

        val croppedBitmap=Bitmap.createBitmap(
            bitmap,
            x,
            y,
            if(x+width>bitmap.width) bitmap.width - x else width,
            if(y+height>bitmap.height)bitmap.height-y else height
        )

        croppedIv.setImageBitmap(croppedBitmap)
    }


}