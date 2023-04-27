package com.ashray.spotbot
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MyAdapter(private val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var mDataList = ArrayList<DataModel>()

    init {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Queries")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mDataList.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    mDataList.add(model!!)
                    notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error here
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mDataList[position]
        holder.nameTextView.text = model.Name


        holder.itemView.setOnClickListener {
            val intent = Intent(context, FaceDetectActivity::class.java)
            intent.putExtra("name", model.Name)
            intent.putExtra("description", model.Description)
            intent.putExtra("phone_number", model.Telephone)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.imageName)
    }
}