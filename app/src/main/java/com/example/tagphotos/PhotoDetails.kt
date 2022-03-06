package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BitmapCompat
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import com.example.tagphotos.entities.Imagini
import com.example.tagphotos.entities.TagImagini
import com.example.tagphotos.entities.Tags

class PhotoDetails : AppCompatActivity(){

    lateinit var photo: List<Imagini>
    lateinit var btnAdd: Button
    lateinit var btnDelete: Button
    lateinit var addTag: EditText
    var array = arrayListOf<String>()
    var arrayTags = arrayListOf<String>()
    private lateinit var deleteTag_container: GridLayout
    private lateinit var checkbox_container: GridLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        // --- connect DB ---
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        // --- Back button ---
        var backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
            val intent = Intent(this@PhotoDetails, MainActivity::class.java)
            startActivity(intent)
        }

        var id = intent.getStringExtra("imageID")
        if (id != null) {
            photo = db.imaginiDao().getImageById(id.toInt()) // get photo info
        }

        // Set ImageView
        var imageView = findViewById<ImageView>(R.id.ImageView)
        imageView.setImageBitmap(photo.last().bitmap)

        // --- Generate Check Box for Delete Container---
        val tags = db.tagsDao().findNameTagByImgId(photo.last().id)
        for(content in tags) // add content of checkbox in array (the tags from database)
            array.add(content.nume.toString())

        deleteTag_container = findViewById(R.id.deleteTag_container)

        if(!array.isEmpty())
        {
            array.forEachIndexed { index, element ->
                val checkbox = CheckBox(this)
                checkbox.id = index
                checkbox.text = element
                deleteTag_container.addView(checkbox)
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
            deleteTag_container.addView(textView)
        }

        // --- Generate Check Box for Add Tag Container ---
        val allTags = db.tagsDao().getALL()
        for(content in allTags) // add content of checkbox in array (the tags from database)
            arrayTags.add(content.nume.toString())

        checkbox_container = findViewById(R.id.checkbox_container)
        if(!arrayTags.isEmpty())
        {
            arrayTags.forEachIndexed { index, element ->
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

        // Add tag
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {

            addTag = findViewById(R.id.addTag) // EditText cu tag-ul de adaugat

            var i=0
            var checkOK = false // checkbox is not checked
            val count = checkbox_container.childCount

            while(i <= count)
            {
                val box = checkbox_container.getChildAt(i)
                if (box is CheckBox)
                    if(box.isChecked)
                    {
                        checkOK = true
                        break
                    }
                i++
            }

            if(addTag.text.contentEquals("") && !checkOK)
                Toast.makeText(this, "Please enter a tag", Toast.LENGTH_SHORT).show()
            else
            {
                // Verify in tagOK if a tag is set manually
                var tagOK = false //no tag entered
                if(!addTag.text.contentEquals(""))
                    tagOK = true

                if(tagOK || checkOK) // if a tag is entered or selected, add to database
                {
                    if(tagOK)
                    {
                        var OK = true // putem insera tag-ul in database
                        var tag = db.tagsDao().getALL()
                        var idTag = tag.size + 1

                        for(content in tags)
                            if(content.nume.contentEquals(addTag.text.trim().toString()))
                            {
                                idTag = content.id
                                OK = false // tag-ul exista in database
                            }

                        if(OK)
                        {
                            if(!tags.isEmpty())
                                idTag = tag.last().id + 1
                            db.tagsDao().insertAll(Tags(idTag, addTag.text.trim().toString()))

                            var tagImg = db.tagImaginiDao().getALL()
                            if(!tagImg.isEmpty())
                                db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, photo.last().id, idTag))
                            else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, photo.last().id, idTag))

                            Toast.makeText(this, "Tag added!", Toast.LENGTH_SHORT).show()
                        }
                        else Toast.makeText(this, "Photo already has this tag!", Toast.LENGTH_SHORT).show()


                    }

                    if(checkOK)
                    {
                        var addedOK = false
                        i = 0
                        while(i <= count)
                        {
                            val box = checkbox_container.getChildAt(i)
                            if (box is CheckBox)
                                if(box.isChecked)
                                {
                                    var OK = true // putem adauga in database
                                    for(i in array)
                                        if(i.contentEquals(box.text.trim().toString()))
                                            OK = false

                                    if(OK)
                                    {
                                        addedOK = true // s-a adaugat macar un nou tag din checkbox
                                        var idTag = db.tagsDao().findByName(box.text.trim().toString())
                                        var tagImg = db.tagImaginiDao().getALL()
                                        if(!tagImg.isEmpty())
                                            db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, photo.last().id, idTag))
                                        else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, photo.last().id, idTag))

                                        Toast.makeText(this, "Tag added!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            i++
                        }

                        if(!addedOK)
                            Toast.makeText(this, "Photo already has this tag!", Toast.LENGTH_SHORT).show()
                    }
                }

                if(!tagOK && !checkOK)
                    Toast.makeText(this, "Please enter or select a tag", Toast.LENGTH_SHORT).show()

                finish()
                startActivity(intent)
            }
        }

        // Delete tag Button
        btnDelete = findViewById(R.id.btnDelete)
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
                        db.tagsDao().deleteByTagName(numeTag)

                        Toast.makeText(this,"Deleted!",
                            Toast.LENGTH_SHORT).show()

                        // Reload activity
                        finish()
                        startActivity(intent)
                    }
                i++
            }

            if(!checkOK)
                Toast.makeText(this,"Please select the tag you want to delete",
                    Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@PhotoDetails, MainActivity::class.java)
        startActivity(intent)
    }
}