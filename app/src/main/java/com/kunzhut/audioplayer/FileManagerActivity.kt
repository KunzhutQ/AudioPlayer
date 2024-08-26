package com.kunzhut.audioplayer

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileManagerActivity : ComponentActivity() {

    private val dirList = ArrayList<File>()
    private lateinit var fileManagerRecyclerAdapter : FileManagerRecyclerAdapter
    private lateinit var fileManagerLayout: FileManagerLayout
    private var defaultPath : String = "/"
    private var currPath : String = "/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.hide()
        window.statusBarColor =getColor(R.color.dark_gray)
        setContentView(R.layout.filemanagerlayout)
        fileManagerLayout = findViewById<FileManagerLayout>(R.id.file_manager_layout)
        fileManagerRecyclerAdapter= FileManagerRecyclerAdapter(applicationContext,dirList)
        defaultPath = intent.getStringExtra("path")?:throw PathNotFoundException()
        currPath=defaultPath

        File(currPath).addDirsToList(dirList) //user default catalog

        val recView = findViewById<RecyclerView>(R.id.select_folder_list)
        recView.layoutManager= LinearLayoutManager(applicationContext)
        recView.addItemDecoration(DividerItemDecoration(recView.context, resources.configuration.orientation))

        fileManagerRecyclerAdapter.setClickListener(object : FileManagerRecyclerAdapter.ItemClickListener{
            override fun onItemClick(view: View, position: Int) {
               fileManagerRecyclerAdapter.getFile(position).addDirsToList(dirList)

            }
        })
        recView.adapter=fileManagerRecyclerAdapter
        findViewById<Button>(R.id.confirm_folder_path).setOnClickListener {
            setResult(RESULT_OK,Intent().putExtra("path", currPath))
            finish()
        }

    }

    private fun File.addDirsToList(list : ArrayList<File>){
        val array = this.listFiles()

        if(array!=null) {
            list.clear()
            array.forEach { list.add(it) }
            currPath=this.absolutePath
            fileManagerLayout.setFolderPath(this.absolutePath)
            fileManagerRecyclerAdapter.notifyDataSetChanged()
        }else if (!this.isFile && this.exists()) Toast.makeText(this@FileManagerActivity,"Нет доступа к папке", Toast.LENGTH_LONG).show()

    }

    override fun onBackPressed() {
        if (!defaultPath.equals(currPath)) File(currPath.substring(0, currPath.lastIndexOf("/")+1)).addDirsToList(dirList)
        else finish()
    }

    override fun attachBaseContext(newBase: Context?) {
        val conf = Configuration(newBase!!.resources.configuration)
        conf.fontScale=1.0f
        applyOverrideConfiguration(conf)
        super.attachBaseContext(newBase)
    }
}