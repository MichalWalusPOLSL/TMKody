package com.example.lab6_2024_2025_pl

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Prezenty : AppCompatActivity() {

    val gifts = ArrayList<String>()
    var photoUri: Uri? = null
    var photoPath: String? = null
    var path = ""
    var loc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //WebView - kontrolka wyswietlajaca html
        val page = WebView(this)

        //wlaczenie obslugi JS
        page.settings.javaScriptEnabled=true

        //dodanie interfejsu pomiędzy Kotlinem a JS
        //this - obiekt tej klasy dostarcza metody JSInterface, activity - nazwa widoczna w JS
        page.addJavascriptInterface(this, "activity")

        //zaladowanie zawartosci kontroli WebView - pliki z katalogu assests w projekcie
        page.loadUrl("file:///android_asset/Prezenty.html")

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }



        //wstawienie kontrolki WebView jako calej fasady aktywnosci
        setContentView(page)
    }


    fun getLocation(): String? {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        when(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 0x70)
            else -> return lm.getLastKnownLocation(lm.getBestProvider(Criteria(), false).toString()).toString()
        }

        return null


    }




    @JavascriptInterface
    fun addGift(gift: String)
    {
        if(gift.any())
        {
            gifts.add(gift)

            for(g in gifts){
                Log.i("Prezenty",g)
            }


        }

    }

    @JavascriptInterface
    fun generate()
    {
        take_photo()
        loc = getLocation()
    }

    fun take_photo(): String {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            Log.e("Prezenty", "Nie udało się stworzyć pliku do zapisu zdjęcia", ex)
            null
        }

        photoFile?.also {
            photoUri = FileProvider.getUriForFile(
                this,
                "com.example.lab6_2024_2025_pl.fileprovider",
                it
            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }

            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }

        path= photoFile?.path.toString()
        if (photoFile != null) {
            return photoFile.path
        }
        return ""
    }


    companion object {
        const val REQUEST_TAKE_PHOTO = 1
    }

    @Throws(Exception::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoPath = photoUri.toString()
            Log.i("80000", "Zdjęcie zapisane w: $photoPath")
            val intent = Intent(this, Kartka::class.java)
            intent.putStringArrayListExtra("gifts", gifts)
            //val path = take_photo()
            intent.putExtra("photo", photoPath)
            intent.putExtra("location", loc?: "no_data")
            //take_photo()
            startActivity(intent)
        }
    }


}
