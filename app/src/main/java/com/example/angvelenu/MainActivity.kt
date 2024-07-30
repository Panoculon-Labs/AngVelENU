package com.example.angvelenu


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity(), SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private lateinit var gyroSensor : Sensor
    private lateinit var accelSensor : Sensor
    private lateinit var earthRotationVector : Sensor
    private var magRot = FloatArray(9)
    private var angVelBody = FloatArray(3)

    private var angVelNEU = mutableStateOf(FloatArray(3))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            angDisplay()
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        earthRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!


        startSensorListening()
    }


    private fun startSensorListening() {
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, earthRotationVector, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {


                Sensor.TYPE_GYROSCOPE -> {
                    System.arraycopy(event.values, 0, angVelBody, 0, angVelBody.size)
                    // Angular Velocity in Body Frame

                    convertToNED()
                }

                Sensor.TYPE_ROTATION_VECTOR -> {
                    SensorManager.getRotationMatrixFromVector(magRot, event.values)
                }

                else -> {}
            }

        }
    }

    /// TODO edit this function
    private fun convertToNED()
    {
        // Edit code here to use magRot & angVelBody to find angular velocity in ENU frame
        angVelNEU.value = FloatArray(3)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    @Composable
    fun angDisplay(){
        Text("Angular Velocity North: ${angVelNEU.value[0]}, East: ${angVelNEU.value[1]}, Down: ${angVelNEU.value[2]})")
    }
}