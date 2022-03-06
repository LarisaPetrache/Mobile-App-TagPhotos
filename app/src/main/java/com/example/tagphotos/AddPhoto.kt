package com.example.tagphotos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BitmapCompat
import androidx.room.Room
import com.example.tagphotos.database.AppDatabase
import com.example.tagphotos.entities.Imagini
import com.example.tagphotos.entities.TagImagini
import com.example.tagphotos.entities.Tags


@Suppress("DEPRECATION")
class AddPhoto : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var selectPhoto: Button
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var setTag: EditText
    lateinit var btnSave: Button
    lateinit var btnCancel: Button
    lateinit var backBtn: ImageView
    var array = arrayListOf<String>()
    private lateinit var checkbox_container: GridLayout
    var fileName: String = ""
    lateinit var bitmap: Bitmap

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
            val intent = Intent(this@AddPhoto, MainActivity::class.java)
            startActivity(intent)
        }

        // --- Pick photo from gallery ---
        selectPhoto = findViewById(R.id.selectPhoto)
        selectPhoto.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        setTag = findViewById(R.id.setTag)
        val count = checkbox_container.childCount

        // --- Save photo ---
        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            var i: Int
            var checkOK: Boolean
            var tagOK: Boolean

            if(imageView.getDrawable().constantState == resources.getDrawable(R.drawable.placeholder).constantState || imageView.drawable == null)
                Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
            else
            {
                i=0
                checkOK = false // checkbox is not checked

                if(!array.isEmpty()) // if we have tags in database
                {
                    // Verify in OK if a checkbox is checked
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
                }

                // Verify in tagOK if a tag is set manually
                tagOK = false //no tag entered
                if(!setTag.text.contentEquals(""))
                    tagOK = true

                if(tagOK || checkOK) // if a tag is entered or selected, add to database
                {
                    // compress bitmap if size >= 2MB
                    val numDigits = BitmapCompat.getAllocationByteCount(bitmap).toString().length
                    if(numDigits >= 8)
                    {
                        val maxHeight: Int
                        val maxWidth: Int
                        if(bitmap.width > bitmap.height)
                        {
                            maxHeight = 1280
                            maxWidth = 1920
                        }
                        else
                        {
                            maxHeight = 1920
                            maxWidth = 1280
                        }

                        val scale = Math.min(maxHeight.toFloat() / bitmap.width,
                            maxWidth.toFloat() / bitmap.height)

                        val matrix = Matrix()
                        matrix.postScale(scale, scale)

                        bitmap = Bitmap.createBitmap(bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            matrix,
                            true)
                    }

                    var imagini = db.imaginiDao().getALL()
                    val id_img = imagini.size + 1
                    db.imaginiDao().insertAll(Imagini(id_img, fileName, bitmap))

                    if(tagOK)
                    {
                        var OK = true // putem insera tag-ul in database
                        var tag = db.tagsDao().getALL()
                        var idTag = tag.size + 1

                        for(content in tag)
                            if(content.nume.contentEquals(setTag.text.trim().toString()))
                            {
                                idTag = content.id
                                OK = false // tag-ul exista in database
                            }

                        if(OK)
                        {
                            if(!tags.isEmpty())
                                idTag = tag.last().id + 1
                            db.tagsDao().insertAll(Tags(idTag, setTag.text.trim().toString()))
                        }

                        var tagImg = db.tagImaginiDao().getALL()
                        if(!tagImg.isEmpty())
                            db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, id_img, idTag))
                        else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, id_img, idTag))
                    }

                    if(checkOK)
                    {
                        i = 0
                        while(i <= count)
                        {
                            val box = checkbox_container.getChildAt(i)
                            if (box is CheckBox)
                                if(box.isChecked && !box.text.toString().contentEquals(setTag.text.toString()))
                                {
                                    var tagImg = db.tagImaginiDao().getALL()
                                    var idTag = db.tagsDao().findByName(box.text.toString())

                                    if(!tagImg.isEmpty())
                                        db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, id_img, idTag))
                                    else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, id_img, idTag))
                                }
                            i++
                        }
                    }

                    Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show()
                }

                if(!tagOK && !checkOK)
                    Toast.makeText(this, "Please enter or select a tag", Toast.LENGTH_SHORT).show()
                else
                {
                    finish()
                    startActivity(intent)
                }
            }
        }

        // --- Cancel button ---
        btnCancel = findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            imageView.setImageResource(R.drawable.placeholder)
            setTag.setText("")

            var i=0
            while(i <= count)
            {
                val box = checkbox_container.getChildAt(i)
                if (box is CheckBox)
                    if(box.isChecked)
                        box.isChecked = false
                i++
            }
        }

        btnSave.isEnabled = false
        btnCancel.isEnabled = false

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imageView = findViewById(R.id.ImageView)

        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)

            // Activam butoanele de Save/Cancel
            btnSave.isEnabled = true
            btnCancel.isEnabled = true

            // --- Convertim Uri la bitmap ----
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            fileName = imageUri?.lastPathSegment.toString()
        }
    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@AddPhoto, MainActivity::class.java)
        startActivity(intent)
    }
}