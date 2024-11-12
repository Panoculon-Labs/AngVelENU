package com.example.angvelenu


import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.OutputStream


class MainActivity : ComponentActivity(), SensorEventListener {
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 1
    // Defining a sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroSensor: Sensor
    private lateinit var accelSensor: Sensor
    private lateinit var step_counter: Sensor
    private lateinit var linear_acceleration: Sensor
    private lateinit var rotation_vector: Sensor

    // Arrays to hold sensor data
    private var accelData = FloatArray(3)
    private var gyroData = FloatArray(3)
    private var lin_accelData = FloatArray(3)
    private var rotation_vector_data = FloatArray(3)
    private var index = 0

    // Mutable states to display sensor data
    private var accelValues = mutableStateOf(FloatArray(3))
    private var gyroValues = mutableStateOf(FloatArray(3))
    private var lin_accelValues = mutableStateOf(FloatArray(3))
    private var rotation_vector_values = mutableStateOf(FloatArray(3))
    private var step_counterValues = mutableStateOf(0f)

    private var isListening by mutableStateOf(false)
    private val sensorDataList = mutableListOf<String>()

    private fun checkActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            angDisplay()
        }
        checkActivityRecognitionPermission()
        checkPermissions()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        rotation_vector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        step_counter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
        linear_acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!!
    }

    private fun startSensorListening() {
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, rotation_vector, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, step_counter, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, linear_acceleration, SensorManager.SENSOR_DELAY_GAME)
        isListening = true
    }

    private fun stopSensorListening() {
        sensorManager.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> {
                    System.arraycopy(event.values, 0, gyroData, 0, gyroData.size)
                    gyroValues.value = gyroData.clone()
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    System.arraycopy(event.values, 0, lin_accelData, 0, lin_accelData.size)
                    lin_accelValues.value = lin_accelData.clone()
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(event.values, 0, accelData, 0, accelData.size)
                    accelValues.value = accelData.clone()
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    step_counterValues.value = event.values[0]
                }
                Sensor.TYPE_ROTATION_VECTOR -> {
                    System.arraycopy(event.values, 0, rotation_vector_data, 0, rotation_vector_data.size)
                    rotation_vector_values.value = rotation_vector_data.clone()
                }
                else -> {}
            }
            val data = "Index=$index, ax=${accelData[0]}, ay=${accelData[1]}, az=${accelData[2]}, " +
                    "wx=${gyroData[0]}, wy=${gyroData[1]}, wz=${gyroData[2]}, " +
                    "mx=${lin_accelData[0]}, my=${lin_accelData[1]}, mz=${lin_accelData[2]}, " +
                    "q0=${rotation_vector_data[0]}, q1=${rotation_vector_data[1]}, " +
                    "q2=${rotation_vector_data[2]}, q3=${step_counterValues.value}"
            sensorDataList.add(data)
            index++
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun saveDataToText() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val fileName = "SensorData_$timestamp.txt"

        // Creating the content to save in the txt file
        val content = StringBuilder()
        sensorDataList.forEach { data ->
            content.append(data).append("\n")
        }

        try {
            val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, save to MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Data")
                }
                val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
                // For Android 9 and below, save in the app's external files directory
                val folder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Data")
                if (!folder.exists()) folder.mkdirs()
                val txtFile = File(folder, fileName)
                FileOutputStream(txtFile)
            }

            outputStream?.use { output ->
                output.write(content.toString().toByteArray())
                Toast.makeText(this, "Text file saved as $fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save text file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        }
    }
    @Composable
    fun angDisplay() {
        Column {
            Row {
                Button(onClick = { startSensorListening() }, enabled = !isListening) {
                    Text("Start")
                }
                Button(onClick = { stopSensorListening() }, enabled = isListening) {
                    Text("Stop")
                }
                Button(onClick = { saveDataToText() }, enabled= (index!=0)) {
                    Text("Save")
                }
            }
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
