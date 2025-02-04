package com.example.lab5v2024_2025

import android.content.Context
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyRenderer() : GLSurfaceView.Renderer {
    private var ballX = 0f // Pozycja piłki w osi X
    private val ballY = 0f // Pozycja piłki w osi Y (stała)
    private val ballRadius = 0.1f // Promień piłki
    private var velocityX = 0f // Prędkość piłki w osi X
    private var leftBoundary = -1f
    private var rightBoundary = 1f
    private var bottomBoundary = -1f
    private var topBoundary = 1f
    public lateinit var context: Context
    private lateinit var pilkaPlayer: MediaPlayer
    var licznik:Int = 0


    // Funkcja wywoływana przy tworzeniu płótna
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.glClearColor(0f, 1f, 0f, 1f) // Zielone tło
        pilkaPlayer = MediaPlayer.create(context, R.raw.pilka)
        licznik = 0
    }

    // Funkcja wywoływana przy zmianie i tworzeniu płótna
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.glViewport(0, 0, width, height)
        gl?.glMatrixMode(GL10.GL_PROJECTION)
        gl?.glLoadIdentity()

        val aspectRatio = width.toFloat() / height.toFloat()
        if (width > height) {
            // Ekran szerszy niż wysoki - granice w osi X zależą od aspectRatio
            leftBoundary = -aspectRatio
            rightBoundary = aspectRatio
            bottomBoundary = -1f
            topBoundary = 1f
            gl?.glOrthof(leftBoundary, rightBoundary, bottomBoundary, topBoundary, -1f, 1f)
        } else {
            // Ekran wyższy niż szeroki - granice w osi Y zależą od aspectRatio
            leftBoundary = -1f
            rightBoundary = 1f
            bottomBoundary = -1f / aspectRatio
            topBoundary = 1f / aspectRatio
            gl?.glOrthof(leftBoundary, rightBoundary, bottomBoundary, topBoundary, -1f, 1f)
        }

        // Przełączenie na macierz modelu-widoku
        gl?.glMatrixMode(GL10.GL_MODELVIEW)
        gl?.glLoadIdentity()
    }




    // Funkcja wywoływana w pętli odpowiedzialna za renderowanie
    override fun onDrawFrame(gl: GL10?) {
        gl?.glClear(GL10.GL_COLOR_BUFFER_BIT)

        // Przełączenie na macierz model-widoku
        gl?.glMatrixMode(GL10.GL_MODELVIEW)
        gl?.glLoadIdentity()

        // Aktualizacja pozycji piłki
        ballX += velocityX

        // Sprawdzanie kolizji z prawą krawędzią
        if (ballX + ballRadius > rightBoundary) {
            ballX = rightBoundary - ballRadius
            velocityX = 0f // zatrzymanie przy prawej krawędzi

            if (pilkaPlayer != null && !pilkaPlayer.isPlaying) {
                // MediaPlayer nie odtwarza dźwięku, możesz np. rozpocząć odtwarzanie
                pilkaPlayer.start()
                licznik++
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, licznik.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
        // Sprawdzanie kolizji z lewą krawędzią
        if (ballX - ballRadius < leftBoundary) {
            ballX = leftBoundary + ballRadius
            velocityX = 0f // zatrzymanie przy lewej krawędzi

            if (pilkaPlayer != null && !pilkaPlayer.isPlaying) {
                // MediaPlayer nie odtwarza dźwięku, możesz np. rozpocząć odtwarzanie
                pilkaPlayer.start()
                licznik++

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, licznik.toString(), Toast.LENGTH_SHORT).show()
                }
            }


        }

        // Używamy macierzy model-widoku do translacji i rysowania
        gl?.glTranslatef(ballX, ballY, 0f)
        drawCircle(gl)
    }



    private fun drawCircle(gl: GL10?) {
        // Tablica wierzchołków
        val circleVertices = FloatArray(362 * 2) // 360 stopni + 1 punkt początkowy dla zamknięcia koła
        for (i in 0..360) {
            val angle = Math.toRadians(i.toDouble()) // Kąt w radianach
            circleVertices[i * 2] = (ballRadius * Math.cos(angle)).toFloat() // Współrzędna X
            circleVertices[i * 2 + 1] = (ballRadius * Math.sin(angle)).toFloat() // Współrzędna Y
        }

        // Bufor wierzchołków
        val buffer: FloatBuffer = ByteBuffer.allocateDirect(circleVertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(circleVertices).position(0)

        // Konfiguracja i rysowanie
        gl?.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl?.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer)
        gl?.glColor4f(0f, 0f, 1f, 1f) // Kolor niebieski
        gl?.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 361) // Rysowanie pełnego koła
        gl?.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }


    // Funkcja aktualizująca prędkość piłki na podstawie przyspieszenia
    fun updateBallVelocity(accelerationX: Float) {
        velocityX = accelerationX / 100 // Zmniejszamy efekt przyspieszenia dla płynności
    }
}
