package com.ashray.spotbot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import com.google.mlkit.common.sdkinternal.ModelInfo
//import com.practice.attendanceusingfacerecognition.Logger
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

// Utility class for FaceNet model
class FaceNetModelImproved( context : Context ) {

    // TFLiteInterpreter used for running the FaceNet model.
    private var interpreter : Interpreter

    // Input image size for FaceNet model.
    private val imgSize = 160

    // Image Processor for preprocessing input images.
    private val imageTensorProcessor = ImageProcessor.Builder()
        .add( ResizeOp( imgSize , imgSize , ResizeOp.ResizeMethod.BILINEAR ) )
        .add( NormalizeOp( 127.5f , 127.5f ) )
        .build()

    init {
        // Initialize TFLiteInterpreter
        val interpreterOptions = Interpreter.Options().apply {
            setNumThreads( 4 )
        }
        interpreter = Interpreter(FileUtil.loadMappedFile(context, "facenet_int8_quant.tflite") , interpreterOptions )
    }

    // Gets an face embedding using FaceNet, use the `crop` rect.
    fun getFaceEmbedding(image : Bitmap, crop : Rect, preRotate: Boolean ) : FloatArray {
        return runFaceNet(
            convertBitmapToBuffer(
                cropRectFromBitmap( image , crop , preRotate )
            )
        )[0]
    }

    // Gets an face embedding using the FaceNet model, given the cropped images.
    fun getFaceEmbeddingWithoutBBox( image : Bitmap ) : FloatArray {
        return runFaceNet( convertBitmapToBuffer( image ) )[0]
    }

    // Run the FaceNet model.
    private fun runFaceNet(inputs: Any): Array<FloatArray> {
        val t1 = System.currentTimeMillis()
        val outputs = Array(1) { FloatArray(128 ) }
        interpreter.run(inputs, outputs)
        Log.i( "Performance" , "FaceNet Inference Speed in ms : ${System.currentTimeMillis() - t1}")
        return outputs
    }

    // Resize the given bitmap and convert it to a ByteBuffer
    private fun convertBitmapToBuffer( image : Bitmap) : ByteBuffer {
        val imageTensor = imageTensorProcessor.process( TensorImage.fromBitmap( image ) )
        return imageTensor.buffer
    }

    // Crop the given bitmap with the given rect.
    private fun cropRectFromBitmap(source: Bitmap, rect: Rect , preRotate : Boolean ): Bitmap {
        var width = rect.width()
        var height = rect.height()
        if ( (rect.left + width) > source.width ){
            width = source.width - rect.left
        }
        if ( (rect.top + height ) > source.height ){
            height = source.height - rect.top
        }
        val croppedBitmap = Bitmap.createBitmap(
            if ( preRotate ) rotateBitmap( source , -90f )!! else source,
            rect.left,
            rect.top,
            width,
            height )
        //saveBitmap( croppedBitmap , "image")
        return croppedBitmap
    }

    private fun saveBitmap(image: Bitmap, name: String) {
        val fileOutputStream =
            FileOutputStream(File( Environment.getExternalStorageDirectory()!!.absolutePath + "/$name.png"))
        image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate( angle )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix , false )
    }
}