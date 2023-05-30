package com.ashray.spotbot

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ashray.spotbot.databinding.ActivityFaceDetectBinding
import com.ashray.spotbot.model.FaceNetModel
import com.ashray.spotbot.model.Models
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class FaceDetectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectBinding
    private lateinit var originalIv:ImageView
    private lateinit var croppedIv:ImageView
    private lateinit var detectFaceBtn: Button
    private lateinit var rslt:TextView
    private lateinit var g:FloatArray
    private lateinit var detector:FaceDetector
    private lateinit var recogniseBtn: Button
    private lateinit var croppedBitmap:Bitmap
    private lateinit var faceNetModel:FaceNetModel
    private lateinit var bitmap3: Bitmap
    private lateinit var grpImage:Bitmap
    private lateinit var fileReader : FileReader

    //    private lateinit var faceNet: FaceNet
//    private lateinit var faceMat: Mat
    private val useGpu = true

    // Use XNNPack to accelerate inference.
    private val useXNNPack = true

    // Use the model configs in Models.kt
    // Default is Models.FACENET ; Quantized models are faster
    private val modelInfo = Models.FACENET
    private companion object{
        private const val SCALING_FACTOR=10
        private const val TAG="FACE_DETECT_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFaceDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        System.loadLibrary("opencv_java4")


        faceNetModel = FaceNetModel( this , modelInfo , useGpu , useXNNPack )
        val options=FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()

//        val boundingBoxOverlay = activityMainBinding.bboxOverlay
//        boundingBoxOverlay.setWillNotDraw( false )
//        boundingBoxOverlay.setZOrderOnTop( true )

        detector=FaceDetection.getClient(options)
//        frameAnalyser = FrameAnalyser( this , BoundingBoxOverlay , faceNetModel )
        fileReader = FileReader(faceNetModel)


        originalIv=binding.originalIv
        detectFaceBtn=binding.detectFaceBtn
        rslt=binding.rsltTv
        croppedIv=binding.croppedIv
        recogniseBtn=binding.recogniseBtn

//        val resourceimg=R.drawable.messi

        var name=intent.getStringExtra("name")
        var phoneNum=intent.getStringExtra("phone_number")
        var desc=intent.getStringExtra("description")

        binding.name.setText(name.toString().trim())
        binding.descrip.setText(desc.toString().trim())
        binding.dialBtn.setOnClickListener {
            openDialer(phoneNum.toString().trim())
        }

        val storagename = FirebaseStorage.getInstance().reference.child("$name.jpg")
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading Image....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storagename.downloadUrl.addOnSuccessListener {uri->
            Glide.with(this).load(uri).into(originalIv)
            val imageUri = Uri.parse(uri.toString())
            loadUriToBitmap(this, imageUri) { bitmap ->
                // Use the bitmap here
                bitmap3 = bitmap
                grpImage=BitmapFactory.decodeResource(resources,R.drawable.ic)
            }
        //    Toast.makeText(this,"${uri.toString()}")
            try {
              //  bitmap3 = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                grpImage=bitmap3
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            progressDialog.dismiss()
        }

        val getCon = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val imageUri: Uri?=null
            try {
                grpImage=MediaStore.Images.Media.getBitmap(contentResolver,uri)
//                originalIv.setImageResource(bitmap3)
                Glide.with(this).load(uri).into(originalIv)
//                Toast.makeText(this,"this works",Toast.LENGTH_SHORT).show()
            }catch (e:Exception) {
                Log.e(TAG, "OnCreate: ", e)
                Toast.makeText(this,"this DOESN'T work",Toast.LENGTH_SHORT).show()
            }
        }


        detectFaceBtn.setOnClickListener {
            analyzePhoto(bitmap3)
        }
        binding.selectBtn.setOnClickListener {
            getCon.launch("image/*")
        }
        recogniseBtn.setOnClickListener {
            isPersonPresent(grpImage)
        }
    }


    fun loadUriToBitmap(context: Context, uri: Uri, onBitmapLoaded: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onBitmapLoaded(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle the case when the load is cancelled or fails
                }
            })
    }

    private fun isPersonPresent(grpBitmap: Bitmap) {

        val inputImage=InputImage.fromBitmap(grpBitmap,0)
        val arrayString = g.joinToString(", ")
        rslt.text=arrayString
        var closestMatch=0
        var distance= 100.0F

        detector.process(inputImage)
            .addOnSuccessListener {faces->
                if(faces.size>0){
                    Log.d(TAG, "grpPhoto: Successfully detected faces ")
                    Toast.makeText(this, "Group Faces Detected, No. of faces : ${faces.size}", Toast.LENGTH_SHORT).show()

                    for (num in 0..faces.size-1) {


                        var bro=BitmapUtils.cropRectFromBitmap(grpBitmap,faces[num].boundingBox)
                        var testFace=faceNetModel.getFaceEmbedding(bro)
//                        if(num==2){
//                            originalIv.setImageBitmap(bro)
//                        }
                        var sum:Float = l2NormDistance(g,testFace)

//                        for (i in 0..127){
//                            var k:Float=testFace[i]-g[i]
//                            sum+= k*k
////                            Log.i("distance","${testFace[i]} - ${g[i]} = ${k} , sum = ${sum}")
//
//                        }
//                        sum= sqrt(sum)
                        if(sum<distance){
                            closestMatch=num
                            distance=sum
                        }
                        Log.i("distance","${sum}, Closest Match : $closestMatch")
                    }
//                    Log.i("distance","${distance}")
                    var face=faces[closestMatch]
                    val rect = face.boundingBox
                    cropDetectedFace(grpBitmap, faces, closestMatch)
                }
                else{
                    Toast.makeText(this,"No face Detected, Sorry",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{e->
                Log.e(TAG,"analyzePhoto: ",e)
                Toast.makeText(this,"Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun analyzePhoto(bitmap:Bitmap){
        Log.d(TAG, "analyze Photo: ")


        val inputImage=InputImage.fromBitmap(bitmap,0)
        detector.process(inputImage)
            .addOnSuccessListener {faces->
                if(faces.size>0){
                    Log.d(TAG, "analyzePhoto: Successfully detected face ")
                    Toast.makeText(this, "Face Detected, No. of faces : ${faces.size}", Toast.LENGTH_SHORT).show()

                    cropDetectedFace(bitmap, faces)
                    binding.selectBtn.visibility = View.VISIBLE
                    binding.recogniseBtn.visibility=View.VISIBLE
                }
                else{
                    Toast.makeText(this,"No face Detected, Sorry",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{e->
                Log.e(TAG,"analyzePhoto: ",e)
                Toast.makeText(this,"Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun cropDetectedFace(bitmap: Bitmap,faces:List<Face>,closestMatch: Int=0){
        Log.i("close","$closestMatch")
        val rect=faces[closestMatch].boundingBox
        croppedBitmap=BitmapUtils.cropRectFromBitmap(bitmap,rect)
        croppedIv.setImageBitmap(croppedBitmap)
        g=faceNetModel.getFaceEmbedding(croppedBitmap)
    }

    private fun returnBitmap(bitmap: Bitmap,face:Face): Bitmap {
        val rect=face.boundingBox
        val x=Math.max(rect.left,0)
        val y=Math.max(rect.top,0)
        val width=rect.width()
        val height=rect.height()

        var newBit=Bitmap.createBitmap(
            bitmap,
            x,
            y,
            if(x+width>bitmap.width) bitmap.width - x else width,
            if(y+height>bitmap.height)bitmap.height-y else height
        )
        return newBit
    }

    private fun getResizedBitmap(bitmap: Bitmap): Bitmap {
        val size = 160 // desired size in pixels
        return Bitmap.createScaledBitmap(bitmap, size, size, true)
    }
    private fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
    private fun l2NormDistance(embedding1: FloatArray, embedding2: FloatArray): Float {
        var sum = 0.0
        for (i in 0 until embedding1.size) {
            sum += (embedding1[i] - embedding2[i]).pow(2)
        }
        return sqrt(sum).toFloat()
    }

}