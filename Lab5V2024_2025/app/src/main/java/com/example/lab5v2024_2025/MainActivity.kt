package com.example.lab5v2024_2025

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tworzenie GLSurfaceView
        val renderer = MyRenderer()
        glSurfaceView = MyGLSurfaceView(this, renderer)

        setContentView(glSurfaceView)

        // Rejestracja sensora
        MySensor(renderer, this)
    }
}
