package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import com.example.tagphotos.entities.Imagini

class Search : AppCompatActivity(), Adapter.onItemClickListener{

    lateinit var backBtn: ImageView
    lateinit var btnSearch: Button
    private lateinit var checkbox_container: GridLayout
    var array = arrayListOf<String>()
    lateinit var recyclerView: RecyclerView
    lateinit var photos: List<Imagini>
    lateinit var adapter: Adapter

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btnSearch = findViewById(R.id.btnSearch)

        // --- connect DB ---
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        // --- Generate Check Box ---
        val tags = db.tagsDao().getALL()
        for(content in tags) // add content of checkbox in array (the tags from database)
            array.add(content.nume.toString())

        checkbox_container = findViewById(R.id.checkbox_container)

        if(!array.isEmpty())
        {
            array.forEachIndexed { index, element ->
                val checkbox = CheckBox(this)
                checkbox.id = index
                checkbox.text = element
                checkbox_container.addView(checkbox)
            }
        }
        else
        {
            val textView = TextView(this)
            val id = 1
            textView.id = id
            textView.text = "No tags added yet"
            textView.setPadding(40,30,0,20)
            textView.typeface = ResourcesCompat.getFont(this, R.font.ptserif)
            checkbox_container.addView(textView)
        }

        // --- Back button ---
        backBtn = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
            val intent = Intent(this@Search, MainActivity::class.java)
            startActivity(intent)
        }

        // --- Search button ---

        if(array.isEmpty()) // if we don't have tags in database
            btnSearch.isEnabled = false

        var tagIdsArray = arrayListOf<Int>()
        btnSearch.setOnClickListener {

            // Adaugam id-urile tag-urilor in lista tagIdsArray
            var i = 0
            val count = checkbox_container.childCount
            var checkOK = false

            while(i <= count)
            {
                val box = checkbox_container.getChildAt(i)
                if (box is CheckBox)
                    if(box.isChecked)
                    {
                        checkOK = true
                        var idTag = db.tagsDao().findByName(box.text.toString())
                        tagIdsArray.add(idTag)
                    }
                i++
            }

            if(!checkOK)
                Toast.makeText(this,"Please select a tag",
                    Toast.LENGTH_SHORT).show()

            // Luam din database imaginile care au tag-urile din tagIdsArray
            photos = db.tagsDao().findImagesByTagIds(tagIdsArray)

            // Afisare poze cu tag-urile alese in RecyclerView
            recyclerView = findViewById(R.id.recyclerView)
            var gridLayoutManager = GridLayoutManager(applicationContext, 2)
            recyclerView.layoutManager = gridLayoutManager

            adapter = Adapter(photos, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            tagIdsArray.clear()
        }

    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@Search, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        val clickedItemId = photos[position].id

        val intent = Intent(this@Search, PhotoDetails::class.java)
        intent.putExtra("imageID", clickedItemId)
        startActivity(intent)
        finish()
    }
}