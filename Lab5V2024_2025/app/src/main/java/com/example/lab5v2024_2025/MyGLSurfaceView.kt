package com.example.lab5v2024_2025

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context, renderer: MyRenderer) : GLSurfaceView(context) {
    init {
        renderer.context = context
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
