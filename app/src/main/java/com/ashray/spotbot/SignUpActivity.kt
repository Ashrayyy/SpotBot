package com.ashray.spotbot

import android.app.ProgressDialog
import android.content.Intent
import android.media.tv.TvContract.Programs
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ashray.spotbot.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        val signUpBtn=binding.signUpBtn
        val passEt=binding.passEt
        val passEtCfm=binding.PassEtCfm

        firebaseAuth=FirebaseAuth.getInstance()
        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        signUpBtn.setOnClickListener {
            validateData()
        }

    }
    private var name=""
    private var email=""
    private var pass=""
    private var passcfm=""
    private fun validateData() {
        name=binding.nameEt.text.toString().trim()
        email=binding.emailEt.text.toString().trim()
        pass=binding.passEt.text.toString().trim()
        passcfm=binding.PassEtCfm.text.toString().trim()

        if(name.isEmpty()){
            Toast.makeText(this,"Enter Name...",Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Enter Valid Email...",Toast.LENGTH_SHORT).show()
        }
        else if(pass.isEmpty()){
            Toast.makeText(this,"Enter Password...",Toast.LENGTH_SHORT).show()
        }
        else if(passcfm.isEmpty()){
            Toast.makeText(this,"Please Confirm Password...",Toast.LENGTH_SHORT).show()
        }
        else if(pass!=passcfm){
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
        }
        else{
            banaDeYaarIskaAccount()
        }
    }

    private fun banaDeYaarIskaAccount() {
        progressDialog.setMessage("Creating account, Please Wait")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                Toast.makeText(this,"Account created.. \n redirecting to login page",Toast.LENGTH_SHORT).show()
                updateUserInfo()
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Error Occured ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving User Information")
        val timestamp=System.currentTimeMillis()
        val uid=firebaseAuth.uid

        val hashMap:HashMap<String,Any?> = HashMap()
        hashMap["uid"]=uid
        hashMap["email"]=email
        hashMap["name"]=name
        hashMap["userType"]="user"
        hashMap["timestamp"]=timestamp

        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account Created",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Error Occured ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
}