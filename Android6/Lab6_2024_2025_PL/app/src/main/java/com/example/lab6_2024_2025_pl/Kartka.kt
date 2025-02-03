package com.example.lab6_2024_2025_pl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class Kartka : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //WebView - kontrolka wyswietlajaca html
        val page = WebView(this)

        val gifts = intent.getStringArrayListExtra("gifts")

        val photo = intent.getStringExtra("photo")

        val location = intent.getStringExtra("location")

        //wlaczenie obslugi JS
        page.settings.javaScriptEnabled=true

        //dodanie interfejsu pomiędzy Kotlinem a JS
        //this - obiekt tej klasy dostarcza metody JSInterface, activity - nazwa widoczna w JS
        page.addJavascriptInterface(this, "activity") //ODKOMENTOWAC DLA JS

        //zaladowanie zawartosci kontroli WebView - pliki z katalogu assests w projekcie
        page.loadUrl("file:///android_asset/Kartka.html")

        //wstawienie kontrolki WebView jako calej fasady aktywnosci
        setContentView(page)

        if (gifts != null) {
            for(g in gifts){
                Log.i("Odczytane", g)
            }
        }

        if (photo != null) {
            Log.i("Odczytane", photo)
        }



        // Przekazanie danych do JavaScript
        page.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val jsonGifts = gifts?.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                page.evaluateJavascript("displayGifts($jsonGifts);", null)
                if (photo != null) {
                    page.evaluateJavascript("document.getElementById('image').src = '$photo';", null)
                    page.evaluateJavascript("document.getElementById('l').innerText = '$location';", null)
                    //page.evaluateJavascript("document.getElementById('test').innerText = '$photo';", null)
                }
            }
        }

    }
}