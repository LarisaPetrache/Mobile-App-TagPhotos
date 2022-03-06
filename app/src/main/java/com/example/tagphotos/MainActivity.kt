package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import com.example.tagphotos.entities.Imagini
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity(), Adapter.onItemClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var photos: List<Imagini>
    lateinit var adapter: Adapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var tabLayoutManager = findViewById<TabLayout>(R.id.TabLayoutManager)
        tabLayoutManager.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {

                    if (tab?.text == "tags") {
                        var intent = Intent(this@MainActivity, Tags::class.java)
                        finish()
                        startActivity(intent)
                    }

                    if (tab?.text == "add photo") {
                        var intent = Intent(this@MainActivity, AddPhoto::class.java)
                        finish()
                        startActivity(intent)
                    }

                    if (tab?.text == "search") {
                        var intent = Intent(this@MainActivity, Search::class.java)
                        finish()
                        startActivity(intent)
                    }

                    if (tab?.text == "email") {
                        var intent = Intent(this@MainActivity, Email::class.java)
                        finish()
                        startActivity(intent)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    onTabSelected(tab)
                }
            })

        // connect DB
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        // Afisare poze din baza de date in RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        var gridLayoutManager = GridLayoutManager(applicationContext, 2)
        recyclerView.layoutManager = gridLayoutManager

        var deletePhotos = findViewById<ImageView>(R.id.btnDelete)
        deletePhotos.setOnClickListener{

            val alertDialog: AlertDialog = this@MainActivity.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Are you sure you want to delete all photos?")
                    .setTitle("Delete Photos")
                builder.apply {
                    setPositiveButton("yes",
                        DialogInterface.OnClickListener{ dialog, id ->
                            // User clicked OK button
                            db.imaginiDao().clearTable()
                            finish()
                            startActivity(intent)
                        })
                    setNegativeButton("cancel",
                        DialogInterface.OnClickListener{ dialog, id ->
                            // User cancelled the dialog
                        })
                }
                // Create the AlertDialog
                builder.create()
            }
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }

        photos = db.imaginiDao().getALL()

        // Daca nu a fost aleasa nicio poza, sa se afiseze un mesaj
        val noPhotoTextView = findViewById<TextView>(R.id.noPhoto)
        if(photos.isEmpty())
            noPhotoTextView.isVisible = true

        adapter = Adapter(photos, this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    override fun onItemClick(position: Int) {
        val clickedItemId = photos[position].id

        val intent = Intent(this@MainActivity, PhotoDetails::class.java)
        intent.putExtra("imageID", clickedItemId.toString())
        startActivity(intent)
        finish()
    }
}