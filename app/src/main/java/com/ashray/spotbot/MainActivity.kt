package com.ashray.spotbot

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.ashray.spotbot.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var progressDialog:ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()

        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val signUpBtn=binding.signUpBtn
        val loginBtn=binding.loginBtn
        val loginEt=binding.emailEt
        val passEt=binding.passEt

        signUpBtn.setOnClickListener {
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            validateData()
        }
    }

    private var email=""
    private var pass=""
    private fun validateData() {
        email=binding.emailEt.text.toString().trim()
        pass=binding.passEt.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Enter Valid Email",Toast.LENGTH_SHORT).show()
        }
        else if (pass.isEmpty()){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show()
        }
        else{
            bhaiGhusaYaarIsse()
        }
    }

    private fun bhaiGhusaYaarIsse() {
        progressDialog.setMessage("Logging In..")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                checkUser()
//                startActivity(Intent(this@MainActivity,dashboardUserActivity::class.java))
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Login Failed: ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User")
        val firebaseUser=firebaseAuth.currentUser!!
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType=snapshot.child("userType").value
                    if(userType=="user"){
                        startActivity(Intent(this@MainActivity,dashboardUserActivity::class.java))
                        finish()
                    }
                    else if(userType =="admin"){
//                        startActivity(Intent(this@MainActivity,dashboardAdminActivity::class.java))
                        startActivity(Intent(this@MainActivity,ListActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}