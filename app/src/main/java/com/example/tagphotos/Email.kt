package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import java.io.File


class Email : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // --- Back button ---
        var backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
            val intent = Intent(this@Email, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun export(view: View){
        // connect DB
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        // Adaugam datele tabelului in variabila data
        var asociere = db.tagImaginiDao().getALL()
        var data = StringBuilder()
        data.append("Id Imagine, Id Tag")
        for(rand in asociere)
            data.append("\n"+ rand.id_imagine.toString() + ", " + rand.id_tag.toString())

        try {
            //saving the file into device
            val out = openFileOutput("tag-imagine.csv", Context.MODE_PRIVATE)
            out.write(data.toString().toByteArray())
            out.close()

            //exporting
            val context: Context = applicationContext
            val filelocation = File(filesDir, "tag-imagine.csv")
            val path: Uri = FileProvider.getUriForFile(context,
                "com.example.tagphotos.fileprovider",
                filelocation)

            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Asociere tag-imagine")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Send mail"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@Email, MainActivity::class.java)
        startActivity(intent)
    }
}