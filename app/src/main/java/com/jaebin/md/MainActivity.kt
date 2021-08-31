package com.jaebin.md

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private val OPEN_GALLERY = 1
    private val PICK_FROM_ALBUM = 2
    private var imageView: ImageView? = null
    private var btn : Button ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById<ImageView>(R.id.img)
        btn.apply { findViewById<Button>(R.id.testImg).setOnClickListener {
            test()
            
        } }

        val temp = SPF.prefs.getString("img","")

        Log.d("TAG", "bitmap:${StringToBitMap(temp)} ")

        imageView?.setImageBitmap(StringToBitMap(temp))


    }


    private fun test(){


        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        filterActivityLauncher.launch(intent)


    }

    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK && it.data !=null) {
                var currentImageUri = it.data?.data


                try {
                    currentImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                currentImageUri
                            )
                            imageView?.setImageBitmap(bitmap)
                        } else {

                            val source = ImageDecoder.createSource(this.contentResolver, currentImageUri)
                            val bitmap = ImageDecoder.decodeBitmap(source)
                            Log.d("TAG", "source:${source} ")
                            Log.d("TAG", "bitmap:${bitmap} ")
                            imageView?.setImageBitmap(bitmap)
                            SPF.prefs.setString("img",bitMaptoString(bitmap)!!)

                        }
                    }


                }catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }else{
                Log.d("ActivityResult","something wrong")
            }
        }

    fun bitMaptoString(bitmap: Bitmap): String? {

        val byte = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,this)
        }.toByteArray()

        val bitMapString = Base64.encodeToString(byte,Base64.DEFAULT)

        return bitMapString
    }

    fun StringToBitMap(encodeString: String): Bitmap? {
        val encodeByte = Base64.decode(encodeString,Base64.DEFAULT)
        val bitMap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)
        return bitMap
    }
}

