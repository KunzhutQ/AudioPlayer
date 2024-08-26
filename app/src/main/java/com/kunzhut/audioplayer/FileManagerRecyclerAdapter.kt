package com.kunzhut.audioplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileManagerRecyclerAdapter(context: Context,private val data : List<File>) : RecyclerView.Adapter<FileManagerRecyclerAdapter.ViewHolder>()  {

       private val inflatorL : LayoutInflater = LayoutInflater.from(context)
       private lateinit var itemClickListener : ItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = inflatorL.inflate(R.layout.file_model, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = data[position]
        holder.dataTextView.text=folder.name
        holder.image.setImageResource(if(folder.isDirectory) R.drawable.folder else R.drawable.file)
    }

    override fun getItemCount(): Int {
        return data.size
    }


   inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val dataTextView : TextView = itemView.findViewById(R.id.file_name)
        val image : ImageView = itemView.findViewById(R.id.file_or_folder)

        init { itemView.setOnClickListener(this) }

        override fun onClick(v: View) {
            itemClickListener.onItemClick(v, adapterPosition)
        }

    }

    fun getFilePath(position : Int) : String{
        return data[position].absolutePath
    }
    fun getFile(position: Int) : File{
        return data[position]
    }

    fun setClickListener(listener : ItemClickListener){
           this.itemClickListener = listener
    }

    interface ItemClickListener{
        fun onItemClick(view: View, position : Int)
    }
}