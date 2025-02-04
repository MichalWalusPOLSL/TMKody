package com.example.lab5v2024_2025

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MySensor(private val renderer: MyRenderer, context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            var accelerationX = -event.values[1] // Odczyt przyspieszenia w osi X
            if (accelerationX < 0.5 && accelerationX > -0.5 ){
                accelerationX = 0f
            }
            renderer.updateBallVelocity(-accelerationX) // Aktualizacja prędkości piłki
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
