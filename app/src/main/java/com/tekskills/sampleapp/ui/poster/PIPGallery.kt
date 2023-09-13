package com.tekskills.sampleapp.ui.poster

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.helper.GalleryCollection
import com.tekskills.sampleapp.ui.poster.recyleradapter.GalleryAdapter
import java.io.File

class PIPGallery : AppCompatActivity() {
    var mRecyclerViewGallery: RecyclerView? = null
    var mLayoutManager: RecyclerView.LayoutManager? = null
    var mAdapter: GalleryAdapter? = null
    var mGalleryCollection: ArrayList<GalleryCollection?>? = null
    var textView: TextView? = null
    override fun onStart() {
        super.onStart()
        settingUpGallery()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipgallery)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        if (mGalleryCollection == null) {
            mGalleryCollection = ArrayList<GalleryCollection?>()
        }
        mRecyclerViewGallery = findViewById(R.id.gallery_recycler_view)
        textView = findViewById(R.id.text_view)
        mRecyclerViewGallery!!.hasFixedSize()
        mLayoutManager = GridLayoutManager(this@PIPGallery, 2, RecyclerView.HORIZONTAL, false)
        mRecyclerViewGallery!!.layoutManager = mLayoutManager
        mGalleryCollection = ArrayList()
    }

    private fun settingUpGallery() {
        if (mGalleryCollection == null) {
            mGalleryCollection = ArrayList<GalleryCollection?>()
        }
        mGalleryCollection!!.clear()
        if (pickImagesFromFolder()) {
            mAdapter = GalleryAdapter(mGalleryCollection) { position ->
                val intent = Intent(this@PIPGallery, GalleryFullImage::class.java)
                intent.putExtra("position", position as Int)
                startActivity(intent)
            }
        }
        if (mGalleryCollection!!.size == 0) {
            textView!!.visibility = View.VISIBLE
            mRecyclerViewGallery!!.visibility = View.GONE
        } else {
            mRecyclerViewGallery!!.adapter = mAdapter
        }
    }

    private fun pickImagesFromFolder(): Boolean {
        val dirPath = getExternalFilesDir(null)!!.absolutePath + "/PIPCamera"
        val filePath = File(dirPath)
        if (!filePath.exists()) {
            filePath.mkdir()
        }
        if (filePath.isDirectory) {
            val imageList = filePath.listFiles()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}