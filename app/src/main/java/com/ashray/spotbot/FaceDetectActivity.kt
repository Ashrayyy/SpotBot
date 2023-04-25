package com.ashray.spotbot

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ashray.spotbot.databinding.ActivityFaceDetectBinding
import com.bumptech.glide.Glide

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
//import org.opencv.android.Utils
//import org.opencv.core.Core
//import org.opencv.core.CvType
//import org.opencv.core.Mat
//import org.opencv.core.Scalar
//import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

//from here

import android.content.DialogInterface
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.ashray.spotbot.model.FaceNetModel
import com.ashray.spotbot.model.Models
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.common.FileUtil
import java.io.FileReader
import java.io.IOException
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class FaceDetectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectBinding
    private lateinit var originalIv:ImageView
    private lateinit var croppedIv:ImageView
    private lateinit var detectFaceBtn: Button
    private lateinit var rslt:TextView
    private lateinit var detector:FaceDetector
    private lateinit var recogniseBtn: Button
    private lateinit var croppedBitmap:Bitmap
    private lateinit var faceNetModel:FaceNetModel
    private lateinit var fileReader : com.ashray.spotbot.FileReader

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

//// Important      from drawables
        val resourceimg=R.drawable.messi
        val bitmap1=BitmapFactory.decodeResource(resources,resourceimg)
//        originalIv.setImageResource(resourceimg)

//// Important       from image view
//        val bitmapDrawable=originalIv.drawable as   BitmapDrawable
//        val bitmap2=bitmapDrawable.bitmap
//
//// Important       from uri

        var bitmap3=BitmapFactory.decodeResource(resources,resourceimg)
        var grpImage=bitmap3
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            val imageUri: Uri?=null
            try {
                bitmap3=MediaStore.Images.Media.getBitmap(contentResolver,uri)
//                originalIv.setImageResource(bitmap3)
                Glide.with(this).load(uri).into(originalIv)
//                Toast.makeText(this,"this works",Toast.LENGTH_SHORT).show()
            }catch (e:Exception) {
                Log.e(TAG, "OnCreate: ", e)
                Toast.makeText(this,"this DOESN'T work",Toast.LENGTH_SHORT).show()
            }

//            image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ab)
//            imagebit = InputImage.fromBitmap(bitmap , 0)
            // Handle the returned Uri
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

//            image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ab)
//            imagebit = InputImage.fromBitmap(bitmap , 0)
            // Handle the returned Uri
        }

        originalIv.setOnClickListener {
            getContent.launch("image/*")
        }

//        val imageUri: Uri?=null
//        try {
//            val bitmap3=MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//        }catch (e:Exception) {
//            Log.e(TAG, "OnCreate: ", e)
//        }


        detectFaceBtn.setOnClickListener {
            analyzePhoto(bitmap3)
        }
        binding.selectBtn.setOnClickListener {
            getCon.launch("image/*")
        }
        recogniseBtn.setOnClickListener {
            isPersonPresent(croppedBitmap,grpImage)
        }
    }


    private fun isPersonPresent(personBitmap: Bitmap,grpBitmap: Bitmap) {

        Toast.makeText(this,"Working",Toast.LENGTH_SHORT).show()
//        val inputImage=InputImage.fromBitmap(grpBitmap,0)

        val smallerBitmap=Bitmap.createScaledBitmap(
            grpBitmap,grpBitmap.width/ SCALING_FACTOR,
            grpBitmap.height/ SCALING_FACTOR,
            false
        )

        val inputImage=InputImage.fromBitmap(smallerBitmap,0)


        var g=faceNetModel.getFaceEmbedding(personBitmap)
        val arrayString = g.joinToString(", ")
//        rslt.text=g.toString().trim()
        rslt.text=arrayString

        Toast.makeText(this,"${g} + size: ${g.size}",Toast.LENGTH_SHORT).show()
        var closestMatch=0
        var distance=128.0
        detector.process(inputImage)
            .addOnSuccessListener {faces->
                if(faces.size>0){
                    Log.d(TAG, "grpPhoto: Successfully detected faces ")
                    Toast.makeText(this, "Group Faces Detected, No. of faces : ${faces.size}", Toast.LENGTH_SHORT).show()

                    for (num in 0..faces.size) {
                        var testFace=faceNetModel.getFaceEmbedding(personBitmap)
                        var sum=0.0
                        for (i in 0..127){
                            sum+= abs(testFace[i]-g[i])
                        }
                        if(sum<distance){
                            closestMatch=num
                            distance=sum
                        }
                    }
                    var face=faces[closestMatch]
                    val rect = face.boundingBox
                        rect.set(
                            rect.left * SCALING_FACTOR,
                            rect.top * (SCALING_FACTOR - 1),
                            rect.right * (SCALING_FACTOR),
                            rect.bottom * SCALING_FACTOR + 1
                        )


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

        val smallerBitmap=Bitmap.createScaledBitmap(
            bitmap,bitmap.width/ SCALING_FACTOR,
            bitmap.height/ SCALING_FACTOR,
            false
        )

        val inputImage=InputImage.fromBitmap(smallerBitmap,0)
        detector.process(inputImage)
            .addOnSuccessListener {faces->
                if(faces.size>0){


                    Log.d(TAG, "analyzePhoto: Successfully detected face ")
                    Toast.makeText(this, "Face Detected, No. of faces : ${faces.size}", Toast.LENGTH_SHORT).show()

                    for (face in faces) {
                        val rect = face.boundingBox
                        rect.set(
                            rect.left * SCALING_FACTOR,
                            rect.top * (SCALING_FACTOR - 1),
                            rect.right * (SCALING_FACTOR),
                            rect.bottom * SCALING_FACTOR + 1
                        )
                    }

                    cropDetectedFace(bitmap, faces)
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
        Log.d(TAG,"cropDetectedFaces")

        val rect=faces[closestMatch].boundingBox
        val x=Math.max(rect.left,0)
        val y=Math.max(rect.top,0)
        val width=rect.width()
        val height=rect.height()

        croppedBitmap=Bitmap.createBitmap(
            bitmap,
            x,
            y,
            if(x+width>bitmap.width) bitmap.width - x else width,
            if(y+height>bitmap.height)bitmap.height-y else height
        )

        croppedIv.setImageBitmap(croppedBitmap)
    }

//    private fun cropDetectedFace(bitmap: Bitmap,faces:List<Face>, closestMatch:Int){
//        Log.d(TAG,"cropDetectedFaces")
//
//        val rect=faces[closestMatch].boundingBox
//        val x=Math.max(rect.left,0)
//        val y=Math.max(rect.top,0)
//        val width=rect.width()
//        val height=rect.height()
//
//        croppedBitmap=Bitmap.createBitmap(
//            bitmap,
//            x,
//            y,
//            if(x+width>bitmap.width) bitmap.width - x else width,
//            if(y+height>bitmap.height)bitmap.height-y else height
//        )
//
//        croppedIv.setImageBitmap(croppedBitmap)
//    }

//    fun recognizeImage(bitmap: Bitmap) {
//
//        // set Face to Preview
//        face_preview.setImageBitmap(bitmap)
//
//        //Create ByteBuffer to store normalized image
//        val imgData: ByteBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
//        imgData.order(ByteOrder.nativeOrder())
//        intValues = IntArray(inputSize * inputSize)
//
//        //get pixel values from Bitmap to normalize
//        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
//        imgData.rewind()
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixelValue: Int = intValues.get(i * inputSize + j)
//                if (isModelQuantized) {
//                    // Quantized model
//                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
//                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
//                    imgData.put((pixelValue and 0xFF).toByte())
//                } else { // Float model
//                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                }
//            }
//        }
    //imgData is input to our model

//        val inputArray = arrayOf<Any>(imgData)
//        val outputMap: MutableMap<Int, Any> = HashMap()
//        embeedings = Array<FloatArray>(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
//        outputMap[0] = embeedings
//        tfLite.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
//        var distance_local = Float.MAX_VALUE
//        val id = "0"
//        val label = "?"
//
//        //Compare new face with saved Faces.
//        if (registered.size() > 0) {
//            val nearest: List<Pair<String, Float>?> =
//                findNearest(embeedings.get(0)) //Find 2 closest matching face
//            if (nearest[0] != null) {
//                val name = nearest[0]!!.first //get name and distance of closest matching face
//                // label = name;
//                distance_local = nearest[0]!!.second
//                if (developerMode) {
//                    if (distance_local < distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                        reco_name.setText(
//                            """
//                        ${
//                                "Nearest: $name\nDist: " + String.format(
//                                    "%.3f",
//                                    distance_local
//                                ) + "\n2nd Nearest: " + nearest[1]!!.first
//                            }
//                        Dist: ${java.lang.String.format("%.3f", nearest[1]!!.second)}
//                        """.trimIndent()
//                        ) else reco_name.setText(
//                        """
//                        ${
//                            """
//                            Unknown
//                            Dist:
//                            """.trimIndent() + String.format(
//                                "%.3f",
//                                distance_local
//                            ) + "\nNearest: " + name + "\nDist: " + String.format(
//                                "%.3f",
//                                distance_local
//                            ) + "\n2nd Nearest: " + nearest[1]!!.first
//                        }
//                        Dist: ${java.lang.String.format("%.3f", nearest[1]!!.second)}
//                        """.trimIndent()
//                    )
//
////                    System.out.println("nearest: " + name + " - distance: " + distance_local);
//                } else {
//                    if (distance_local < distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                        reco_name.setText(name) else reco_name.setText("Unknown")
//                    //                    System.out.println("nearest: " + name + " - distance: " + distance_local);
//                }
//            }
//        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );
//    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private suspend fun runModel( faces : List<Face> , cameraFrameBitmap : Bitmap ){
//        withContext( Dispatchers.Default ) {
//            val predictions = ArrayList<Prediction>()
//            for (face in faces) {
//                try {
//                    // Crop the frame using face.boundingBox.
//                    // Convert the cropped Bitmap to a ByteBuffer.
//                    // Finally, feed the ByteBuffer to the FaceNet model.
//                    val croppedBitmap = BitmapUtils.cropRectFromBitmap( cameraFrameBitmap , face.boundingBox )
//                    subject = model.getFaceEmbedding( croppedBitmap )
//
//                    // Perform face mask detection on the cropped frame Bitmap.
//                    var maskLabel = ""
//                    if ( isMaskDetectionOn ) {
//                        maskLabel = maskDetectionModel.detectMask( croppedBitmap )
//                    }
//
//                    // Continue with the recognition if the user is not wearing a face mask
//                    if (maskLabel == maskDetectionModel.NO_MASK) {
//                        // Perform clustering ( grouping )
//                        // Store the clusters in a HashMap. Here, the key would represent the 'name'
//                        // of that cluster and ArrayList<Float> would represent the collection of all
//                        // L2 norms/ cosine distances.
//                        for ( i in 0 until faceList.size ) {
//                            // If this cluster ( i.e an ArrayList with a specific key ) does not exist,
//                            // initialize a new one.
//                            if ( nameScoreHashmap[ faceList[ i ].first ] == null ) {
//                                // Compute the L2 norm and then append it to the ArrayList.
//                                val p = ArrayList<Float>()
//                                if ( metricToBeUsed == "cosine" ) {
//                                    p.add( cosineSimilarity( subject , faceList[ i ].second ) )
//                                }
//                                else {
//                                    p.add( L2Norm( subject , faceList[ i ].second ) )
//                                }
//                                nameScoreHashmap[ faceList[ i ].first ] = p
//                            }
//                            // If this cluster exists, append the L2 norm/cosine score to it.
//                            else {
//                                if ( metricToBeUsed == "cosine" ) {
//                                    nameScoreHashmap[ faceList[ i ].first ]?.add( cosineSimilarity( subject , faceList[ i ].second ) )
//                                }
//                                else {
//                                    nameScoreHashmap[ faceList[ i ].first ]?.add( L2Norm( subject , faceList[ i ].second ) )
//                                }
//                            }
//                        }
//
//                        // Compute the average of all scores norms for each cluster.
//                        val avgScores = nameScoreHashmap.values.map{ scores -> scores.toFloatArray().average() }
////                        Logger.log( "Average score for each user : $nameScoreHashmap" )
//
//                        val names = nameScoreHashmap.keys.toTypedArray()
//                        nameScoreHashmap.clear()
//
//                        // Calculate the minimum L2 distance from the stored average L2 norms.
//                        val bestScoreUserName: String = if ( metricToBeUsed == "cosine" ) {
//                            // In case of cosine similarity, choose the highest value.
//                            if ( avgScores.maxOrNull()!! > model.model.cosineThreshold ) {
//                                names[ avgScores.indexOf( avgScores.maxOrNull()!! ) ]
//                            }
//                            else {
//                                "Unknown"
//                            }
//                        } else {
//                            // In case of L2 norm, choose the lowest value.
//                            if ( avgScores.minOrNull()!! > model.model.l2Threshold ) {
//                                "Unknown"
//                            }
//                            else {
//                                names[ avgScores.indexOf( avgScores.minOrNull()!! ) ]
//                            }
//                        }
//
//                        if(bestScoreUserName!="Unknown") {
//                            CameraActivity.names.add(bestScoreUserName)
//                            CameraActivity.names_all[bestScoreUserName] = "present"
//                        }
//
////                        Logger.log( "Person identified as $bestScoreUserName" )
//                        predictions.add(
//                            Prediction(
//                                face.boundingBox,
//                                bestScoreUserName ,
//                                maskLabel
//                            )
//                        )
//                    }
//                    else {
//                        // Inform the user to remove the mask
//                        predictions.add(
//                            Prediction(
//                                face.boundingBox,
//                                "Please remove the mask" ,
//                                maskLabel
//                            )
//                        )
//                    }
//                }
//                catch ( e : Exception ) {
//                    // If any exception occurs with this box and continue with the next boxes.
//                    Log.e( "Model" , "Exception in FrameAnalyser : ${e.message}" )
//                    continue
//                }
//            }
//            withContext( Dispatchers.Main ) {
//                // Clear the BoundingBoxOverlay and set the new results ( boxes ) to be displayed.
//                boundingBoxOverlay.faceBoundingBoxes = predictions
//                boundingBoxOverlay.invalidate()
//
//                isProcessing = false
//            }
//        }
//    }
//
//
//    // Compute the L2 norm of ( x2 - x1 )
//    private fun L2Norm( x1 : FloatArray, x2 : FloatArray ) : Float {
//        return sqrt( x1.mapIndexed{ i , xi -> (xi - x2[ i ]).pow( 2 ) }.sum() )
//    }
//
//
//    // Compute the cosine of the angle between x1 and x2.
//    private fun cosineSimilarity( x1 : FloatArray , x2 : FloatArray ) : Float {
//        val mag1 = sqrt( x1.map { it * it }.sum() )
//        val mag2 = sqrt( x2.map { it * it }.sum() )
//        val dot = x1.mapIndexed{ i , xi -> xi * x2[ i ] }.sum()
//        return dot / (mag1 * mag2)
//    }


}