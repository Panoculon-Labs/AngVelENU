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
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*


class MainActivity : ComponentActivity(), SensorEventListener {

    // Defining a sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroSensor : Sensor
    private lateinit var accelSensor : Sensor
//    private lateinit var earthRotationVector : Sensor
//    private lateinit var step_detector : Sensor
    private lateinit var step_counter : Sensor
    private lateinit var linear_acceleration : Sensor
//    private lateinit var orientation : Sensor
    private lateinit var rotation_vector : Sensor
//    private lateinit var pressureSensor: Sensor

    // Arrays to hold sensor data

    private var accelData = FloatArray(3)
    private var gyroData = FloatArray(3)
    private var lin_accelData = FloatArray(3)
    private var rotation_vector_data = FloatArray(3)

//    private var pressureData = FloatArray(1)

       // Chat GPT
    // Mutable states to display sensor data
    private var accelValues = mutableStateOf(FloatArray(3))
    private var gyroValues = mutableStateOf(FloatArray(3))
    private var lin_accelValues = mutableStateOf(FloatArray(3))
    private var rotation_vector_values = mutableStateOf(FloatArray(3))
    private var step_counterValues = mutableStateOf(0f)
//    private var pressureValue = mutableStateOf(0f)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            angDisplay()
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        rotation_vector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
//        step_detector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!!
        step_counter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
        linear_acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!!
//        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)!!
        rotation_vector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!
//        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)!!
//        if (pressureSensor == null) {
//            // Handle the case where the sensor is not available (e.g., show a message or disable a feature)
//        }

        startSensorListening()
    }


    private fun startSensorListening() {
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, rotation_vector, SensorManager.SENSOR_DELAY_GAME)
//        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_GAME )
        sensorManager.registerListener(this, step_counter, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, linear_acceleration, SensorManager.SENSOR_DELAY_GAME)
//        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_GAME)
//        sensorManager.registerListener(this, rotation_vector, SensorManager.SENSOR_DELAY_GAME)
        // SENSOR_DELAY_GAME is the frequency used in gaming
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {


                Sensor.TYPE_GYROSCOPE -> {
                    System.arraycopy(event.values, 0, gyroData, 0, gyroData.size)
                    // Angular Velocity in Body Frame
                    gyroValues.value = gyroData.clone()
//                    convertToENU()
                }


                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    System.arraycopy(event.values, 0, lin_accelData, 0, lin_accelData.size)
                    lin_accelValues.value = lin_accelData.clone() // Update accelerometer values
                }

                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(event.values, 0, accelData, 0, accelData.size)
                    accelValues.value = accelData.clone() // Update accelerometer values
                }
//
                Sensor.TYPE_STEP_COUNTER -> {
                    step_counterValues.value = event.values[0]
                }

                Sensor.TYPE_ROTATION_VECTOR -> {
//                    SensorManager.getRotationMatrixFromVector(magRot, event.values)
                    System.arraycopy(event.values, 0, rotation_vector_data, 0, rotation_vector_data.size)
                    rotation_vector_values.value = rotation_vector_data.clone()
                }



//                Sensor.TYPE_PRESSURE -> {
//                    pressureValue.value = event.values[0] // Update pressure value
//                }



                else -> {}
            }

        }
    }

    /// TODO edit this function
    private fun convertToENU()
    {
        // Edit code here to use magRot & angVelBody to find angular velocity in ENU frame

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    @Composable
    fun angDisplay(){
//        Text("Angular Velocity North: ${angVelNEU.value[0]}, East: ${angVelNEU.value[1]}, Down: ${angVelNEU.value[2]})")

        // Display Accelerometer Values
//        Text("Accelerometer X: ${accelData.value[0]}")
//        Text("Accelerometer Y: ${accelData.value[1]}")
    Column{
        Text("Accelerometer X: ${accelData[0]}")
        Text("Accelerometer Y: ${accelData[1]}")
        Text("Accelerometer Z: ${accelData[2]}")

        Text("Linear Acceleration X: ${lin_accelData[0]}")
        Text("Linear Acceleration Y: ${lin_accelData[1]}")
        Text("Linear Acceleration Z: ${lin_accelData[2]}")

        Text("Gyro X: ${gyroData[0]}")
        Text("Gyro Y: ${gyroData[1]}")
        Text("Gyro Z: ${gyroData[2]}")

        Text("Step count: ${step_counterValues.value}")

        Text("x*sin(theta/2): ${rotation_vector_data[0]}")
        Text("y*sin(theta/2): ${rotation_vector_data[1]}")
        Text("z*sin(theta/2): ${rotation_vector_data[2]}")
    }

    }
}