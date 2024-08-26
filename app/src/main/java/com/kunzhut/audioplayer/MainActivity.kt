package com.kunzhut.audioplayer

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ViewPropertyAnimator
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileFilter
import java.util.Arrays
import java.util.stream.Stream

class MainActivity : ComponentActivity() {


    private var Press : Long = 0
    private val supportedAudioFormats : List<String> = arrayOf("mp3","m4a","3gp","mid","xmf","mxmf","rtttl","rtx","ota","imy","ogg","wav").toList()
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var currTrackOrSelectFolderElement : ViewGroupChildHorizontallyCentered
    private var folderWithAudioFiles : Array<File>? = null
    private var positionInFolder : Int = 0
    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.hide()
        window.statusBarColor =getColor(R.color.dark_gray)
        setContentView(R.layout.mainlayout)

        //check perms
        val permToCheck = if(Build.VERSION.SDK_INT>32) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE
        if(ContextCompat.checkSelfPermission(this, permToCheck)==PackageManager.PERMISSION_DENIED) requestPermissions(arrayOf(permToCheck), 95)
        //
        //
        sharedPreferences= getSharedPreferences("General", MODE_PRIVATE)
        positionInFolder=sharedPreferences.getInt("PositionInFolder", 0)

        currTrackOrSelectFolderElement= findViewById<ViewGroupChildHorizontallyCentered>(R.id.current_track_or_select_folder_element)
        audioPlayerStartPreparation()


        //file manager call on click
        findViewById<ImageView>(R.id.select_folder_clickable_icon).setOnClickListener { callFileManager() }
        //


        //animations and listeners init (mediaPlayer start, stop forward, change music track, select folder)
        val playButton = findViewById<ImageView>(R.id.play_button)
        val resetButton = findViewById<ImageView>(R.id.reset_button)
        val viewPropertyResetButtonAnimator : ViewPropertyAnimator = resetButton.animate()
        viewPropertyResetButtonAnimator.duration=500

        resetButton.setOnClickListener {
            viewPropertyResetButtonAnimator.rotation(360f).start()
            mediaPlayer.seekTo(0)
        }

        viewPropertyResetButtonAnimator.setListener(object : AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {resetButton.rotation=0f;resetButton.isClickable=true; resetButton.setColorFilter(getColor(R.color.crimson)) }
            override fun onAnimationStart(animation: Animator) { resetButton.isClickable=false;resetButton.setColorFilter(getColor(R.color.gold))}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        val valueAnimator : ValueAnimator = AnimatorInflater.loadAnimator(this,R.animator.button_anim) as ValueAnimator

        valueAnimator.addUpdateListener { playButton.rotationY= it.animatedValue as Float }

        valueAnimator.doOnEnd {
            playButton.isClickable=true
            playButton.setImageResource(if(playButton.rotationY==180f) R.drawable.pausebutton else R.drawable.playbutton)
        }

         playButton.setOnClickListener {
             playButton.isClickable=false
             if(playButton.rotationY==0f) {
                 valueAnimator.start()
                 mediaPlayer.start()
             } else {valueAnimator.reverse(); mediaPlayer.pause()}

         }
        val lambdas = arrayOf<()->(Unit)>({mediaPlayer.seekTo(mediaPlayer.currentPosition-10000)},
            {mediaPlayer.seekTo(mediaPlayer.currentPosition+10000)},{ changeSoundtrack(positionInFolder-1) },{ changeSoundtrack(positionInFolder+1) })

         intArrayOf(R.id.ten_seconds_back,R.id.ten_seconds_forward,R.id.left_arrow,R.id.right_arrow).forEachIndexed { index,id ->
             val view = findViewById<ImageViewTextPosImpl>(id)

             view.setOnClickListener {
                 view.setColorToSource(getColor(R.color.gold))
                 lambdas[index].invoke()
                 view.postDelayed(Runnable { view.setColorToSource(getColor(R.color.crimson)) },150)}
         }
         //


    }

    private fun audioPlayerStartPreparation(){
        folderWithAudioFiles=getFileArray()
        if(folderWithAudioFiles.isNullOrEmpty()){
            if (folderWithAudioFiles!=null) currTrackOrSelectFolderElement.setText("Папка не содержит аудио-файлов")
            return
        }

        setTrackName()
        mediaPlayer.setDataSource(folderWithAudioFiles!![positionInFolder].absolutePath)
        mediaPlayer.prepare()
    }

    private fun changeSoundtrack(pos : Int){
        if (folderWithAudioFiles.isNullOrEmpty()) return

        positionInFolder= when(pos){
            -1-> folderWithAudioFiles!!.lastIndex
            folderWithAudioFiles!!.lastIndex+1-> 0
            else -> pos
        }
        setTrackName()
        val b = mediaPlayer.isPlaying
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(folderWithAudioFiles!![positionInFolder].absolutePath)
        mediaPlayer.prepare()
        if (b)mediaPlayer.start()
    }

    private fun setTrackName(){
        currTrackOrSelectFolderElement.setImage(R.drawable.audiosquare,0)
        currTrackOrSelectFolderElement.setText(folderWithAudioFiles!![positionInFolder].nameWithoutExtension)

    }
    private fun getFileArray() : Array<File>?{
        val path = sharedPreferences.getString("pathToMusicFolder", null)

        return if(path!=null) File(path).listFiles(FileFilter {f -> f.isFile && supportedAudioFormats.contains(f.extension) })?: arrayOf<File>()
        else null
    }

    private fun callFileManager(){
        val intent = Intent(this, FileManagerActivity::class.java)
        intent.putExtra("path", Environment.getExternalStorageDirectory().absolutePath)
        startActivityForResult(intent,80)
    }

    override fun onDestroy() {
        sharedPreferences.edit().putInt("PositionInFolder", positionInFolder).apply()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode==80){
            sharedPreferences.edit().putString("pathToMusicFolder", data!!.getStringExtra("path")!!).apply()
                positionInFolder=0
            mediaPlayer.stop()
            mediaPlayer.reset()
            audioPlayerStartPreparation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]==-1){
            toastMessage("Разрешение необходимо для корректной работы",Toast.LENGTH_LONG)
            finish()
        }

    }

    override fun onBackPressed() {
        if((System.currentTimeMillis()-Press)>2000 || Press==0.toLong()){
            Press=System.currentTimeMillis()
            toastMessage("Нажмите \"Назад\" еще раз, чтобы выйти", Toast.LENGTH_SHORT)
        }else{
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val conf = Configuration(newBase!!.resources.configuration)
        conf.fontScale=1.0f
        applyOverrideConfiguration(conf)
        super.attachBaseContext(newBase)
    }

    private fun toastMessage(text : String, length : Int){
        Toast.makeText(this@MainActivity,text, length).show()
    }

}