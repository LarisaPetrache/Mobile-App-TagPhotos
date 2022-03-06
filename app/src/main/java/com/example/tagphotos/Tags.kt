package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import com.example.tagphotos.entities.Tags

class Tags : AppCompatActivity() {

    private lateinit var myTags_container: GridLayout
    private lateinit var deleteTag_container: GridLayout
    lateinit var btnDelete: Button
    lateinit var btnSave: Button
    lateinit var btnAdd: Button
    lateinit var oldName: AutoCompleteTextView
    lateinit var newName: EditText
    lateinit var addTag: EditText
    var array = arrayListOf<String>()

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btnDelete = findViewById(R.id.btnDelete)
        btnSave = findViewById(R.id.btnSave)
        btnAdd = findViewById(R.id.btnAdd)
        oldName = findViewById(R.id.oldTagName)
        newName = findViewById(R.id.newTagName)
        addTag = findViewById(R.id.addTag)

        // --- connect DB ---
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        // --- Back button ---
        var backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
            val intent = Intent(this@Tags, MainActivity::class.java)
            startActivity(intent)
        }

        // --- Get Tags from database ---
        val tags = db.tagsDao().getALL()
        for(content in tags)
            array.add(content.nume.toString())

        myTags_container = findViewById(R.id.myTags_container)

        if(!array.isEmpty())
        {
            // Adaugare sugestii din baza de date pentru campul "Old name" (change tag name)
            val adapterOldName = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
            oldName.setAdapter(adapterOldName)

            // --- My Tags ---
            array.forEachIndexed { index, element ->
                val textView = TextView(this)
                textView.id = index
                textView.text = element
                textView.setPadding(0, 0, 60, 0);
                myTags_container.addView(textView)
            }

            // --- Delete Tags ---
            deleteTag_container = findViewById(R.id.deleteTag_container)

            // Display tags as checkbox
            array.forEachIndexed { index, element ->
                val checkbox = CheckBox(this)
                checkbox.id = index
                checkbox.text = element
                deleteTag_container.addView(checkbox)
            }

            // Delete checked tags
            btnDelete.setOnClickListener {

                var i = 0
                val count = deleteTag_container.childCount
                var checkOK = false

                while(i <= count)
                {
                    val box = deleteTag_container.getChildAt(i)
                    if (box is CheckBox)
                        if(box.isChecked)
                        {
                            checkOK = true
                            var numeTag = box.text.toString()
                            db.tagsDao().deleteTagByName(numeTag)
                        }
                    i++
                }

                if(!checkOK)
                    Toast.makeText(this,"Please select the tag you want to delete",
                        Toast.LENGTH_SHORT).show()
                else
                {
                    Toast.makeText(this,"Deleted!",
                        Toast.LENGTH_SHORT).show()

                    // Reload activity
                    finish()
                    startActivity(intent)
                }
            }

            // Change tag name
            btnSave.setOnClickListener {

                // Mesaje de eroare in caz ca nu sunt setate ambele/unul dintre campuri
                if(oldName.text.contentEquals("") && newName.text.contentEquals(""))
                {
                        Toast.makeText(this,"Please enter old and new tag name",
                            Toast.LENGTH_SHORT).show()
                }
                else if(oldName.text.contentEquals(""))
                    Toast.makeText(this,"Please enter old tag name",
                            Toast.LENGTH_SHORT).show()
                else if(newName.text.contentEquals(""))
                    Toast.makeText(this,"Please enter a new tag name",
                            Toast.LENGTH_SHORT).show()

                // Daca sunt setate, se face modificarea numelui tag-ului
                if(!oldName.text.contentEquals("") && !newName.text.contentEquals(""))
                {
                    val OK = db.tagsDao().findByName(oldName.text.toString())

                    if(OK == 0)
                        Toast.makeText(this,"Old name doesn't exist in tag list",
                            Toast.LENGTH_SHORT).show()
                    else
                    {
                        db.tagsDao().changeTagName(oldName.text.toString(), newName.text.toString())
                        Toast.makeText(this,"Tag name changed!",
                            Toast.LENGTH_SHORT).show()

                        // Reload activity
                        finish()
                        startActivity(intent)

                    }

                }
            }

            // Add new tag
            btnAdd.setOnClickListener {

                if(addTag.text.contentEquals(""))
                    Toast.makeText(this,"Please enter a name in 'Add tag'",
                        Toast.LENGTH_SHORT).show()
                else
                {
                    var i = 0
                    var tagAlreadyExist = false

                    for(i in array)
                        if(i.contentEquals(addTag.text.toString()))
                        {
                            tagAlreadyExist = true
                            break
                        }

                    if(!tagAlreadyExist)
                    {
                        var tag = db.tagsDao().getALL()
                        val idTag = tag.last().id + 1
                        db.tagsDao().insertAll(Tags(idTag, addTag.text.toString()))

                        Toast.makeText(this,"Tag added!",
                            Toast.LENGTH_SHORT).show()

                        // Reload activity
                        finish()
                        startActivity(intent)
                    }
                    else Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show()
                }

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
            myTags_container.addView(textView)
            btnSave.isEnabled = false
            btnDelete.isEnabled = false

            // Add new tag
            btnAdd.setOnClickListener {

                if(addTag.text.contentEquals(""))
                    Toast.makeText(this,"Please enter a name in 'Add tag'",
                        Toast.LENGTH_SHORT).show()
                else
                {
                    var tag = db.tagsDao().getALL()
                    db.tagsDao().insertAll(Tags(tag.size + 1, addTag.text.toString()))

                    Toast.makeText(this,"Tag added!",
                        Toast.LENGTH_SHORT).show()

                    // Reload activity
                    finish()
                    startActivity(intent)
                }

            }
        }

    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@Tags, MainActivity::class.java)
        startActivity(intent)
    }
}