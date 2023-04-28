package com.ashray.spotbot

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.ashray.spotbot.databinding.ActivityDashboardUserBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class dashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    lateinit var ImageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDashboardUserBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
//        detector = FaceDetection.getClient()(highAccuracyOpts)

        val gallery = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                binding.img.setImageURI(it)
            }
        )
        binding.submitApp.setOnClickListener {
            if(!binding.name.text.toString().isEmpty() && !binding.telephoneEt.text.toString().isEmpty() && !binding.desc.text.toString().isEmpty()){
                uploadImage()
                binding.desc.text.clear()
                binding.name.text.clear()
                binding.telephoneEt.text.clear()
                binding.img.setImageResource(R.drawable.ic)
            }
            else{
                Toast.makeText(this,"One of the feild is empty",Toast.LENGTH_SHORT).show()
            }
        }
        binding.button2.setOnClickListener {
            //gallery.launch("images/*")
        selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            ImageUri = data?.data!!
            binding.img.setImageURI(ImageUri)
        }
    }

    private fun uploadImage() {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Image....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val filename = binding.name.text.toString() + ".jpg"
        val storage = FirebaseStorage.getInstance().getReference(filename)
        val realtime = FirebaseDatabase.getInstance().reference

        val data = hashMapOf(
            "Name" to binding.name.text.toString(),
            "Telephone" to binding.telephoneEt.text.toString(),
            "Description" to binding.desc.text.toString()
        )
        realtime.child("Queries").child(binding.name.text.toString()).setValue(data)
        storage.putFile(ImageUri)
            .addOnSuccessListener {

//                binding.img.setImageURI(null)
                Toast.makeText(this,"Image successfully Uploaded",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            .addOnFailureListener{e->
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }
    }

//    private fun selectImage() {
//       val intent = Intent()
//        intent.type = "images/"
//        intent.action = Intent.ACTION_GET_CONTENT
//
//        startActivityForResult(intent,100)
//
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == 100 && resultCode == RESULT_OK){
//            ImageUri = data?.data!!
//            binding.img.setImageURI(ImageUri)
//        }
//    }
//
//    private fun uploadImage() {
//        var progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("Uploading Image....")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//
//        val filename = binding.name.text.toString() + ".jpg"
//        val storage = FirebaseStorage.getInstance().getReference(filename)
//
//        storage.putFile(ImageUri)
//            .addOnSuccessListener {
//                binding.img.setImageURI(null)
//                Toast.makeText(this,"Image successfully Uploaded",Toast.LENGTH_SHORT).show()
//                progressDialog.dismiss()
//            }
//            .addOnFailureListener{
//                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
//                progressDialog.dismiss()
//            }
//    }
}