package com.tekskills.sampleapp.ui.poster

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.helper.GalleryCollection
import com.tekskills.sampleapp.ui.poster.recyleradapter.SlidingImage_Adapter
import java.io.File

class GalleryFullImage : AppCompatActivity() {
    private var path: String? = null
    private val position = 0
    var defPosition = 0
    var mGalleryCollection: ArrayList<GalleryCollection>? = null
    private fun makeBuilder() {
        val builder = AlertDialog.Builder(this@GalleryFullImage)
        builder.setMessage("Are You Sure ?")
            .setPositiveButton("Yes") { dialog, which -> delImage() }
            .setNegativeButton("No") { dialog, which -> }.create().show()
    }

    private fun delImage() {
        path = mGalleryCollection!![mPager!!.currentItem].imagePath
        val file = File(path)
        if (file.delete()) {
            defPosition = mPager!!.currentItem
            mGalleryCollection!!.clear()
            pickImagesFromFolder()
            if (mGalleryCollection!!.isEmpty()) {
                finish()
            } else {
                init()
            }
        } else {
            Toast.makeText(this, "Image not deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_full_image)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        defPosition = intent.getIntExtra("position", 0)
        mGalleryCollection = ArrayList()
        mPager = findViewById(R.id.pager_gallery_full_image)
        pickImagesFromFolder()
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.gallery_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_del -> {
                makeBuilder()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {
        mPager!!.adapter = SlidingImage_Adapter(this@GalleryFullImage, mGalleryCollection!!)
        mPager!!.currentItem = defPosition
    }

    private fun pickImagesFromFolder(): Boolean {
        val dirPath = getExternalFilesDir(null)!!.absolutePath + "/PIPCamera"
        val filePath = File(dirPath)
        if (!filePath.exists()) {
            filePath.mkdir()
        }
        if (filePath.isDirectory) {
            val imageList = filePath.listFiles() ?: return false
            for (i in imageList.indices.reversed()) {
                mGalleryCollection!!.add(GalleryCollection(imageList[i].absolutePath))
            }
            return true
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private var mPager: ViewPager? = null
    }
}