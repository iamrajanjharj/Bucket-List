package com.zybooks.to_dolist

import ToDoList
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

const val SHAKE_THRESHOLD = 500

class MainActivity : AppCompatActivity(), SensorEventListener, DeleteAllDialog.OnYesClickListener {
    private var toDoList = ToDoList(this)
    private lateinit var itemEditText: EditText
    private lateinit var listTextView: TextView

    private lateinit var soundEffects: SoundEffects

    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemEditText = findViewById(R.id.todo_item)
        listTextView = findViewById(R.id.item_list)
        findViewById<Button>(R.id.add_button).setOnClickListener { addButtonClick() }
        findViewById<Button>(R.id.settings_button).setOnClickListener { onClickSettings() }
        findViewById<Button>(R.id.clear_button).setOnClickListener { clearButtonClick() }

        soundEffects = SoundEffects.getInstance(applicationContext)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

         // For testing in the emulator
        listTextView.setOnClickListener { onYesClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEffects.release()
    }

    override fun onResume() {
        super.onResume()

        // Attempt to load a previously saved list
        toDoList.readFromFile()
        displayList()

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()

        // Save list for later
        toDoList.saveToFile()

        sensorManager.unregisterListener(this, accelerometer)
    }

    private fun addButtonClick() {
        // Ignore any leading or trailing spaces
        val item = itemEditText.text.toString().trim()

        // Clear the EditText so it's ready for another item
        itemEditText.setText("")

        // Add the item to the list and display it
        if (item.isNotEmpty()) {
            soundEffects.playTone()

            toDoList.addItem(item)
            displayList()
        }
    }

    private fun displayList() {

        // Display a numbered list of items
        val itemText = StringBuffer()
        val items = toDoList.getItems()
        val lineSeparator = System.getProperty("line.separator")

        for (i in items.indices) {
            itemText.append(i + 1).append(". ").append(items[i]).append(lineSeparator)
        }

        listTextView.text = itemText.toString()
    }

    fun clearToDoList() {
        toDoList.clear()
        displayList()
    }

    private fun clearButtonClick() {
        val dialog = DeleteAllDialog()
        dialog.show(supportFragmentManager, "warningDialog")
    }

    private fun onClickSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onYesClick() {

        // Animate down off screen
        val screenHeight = this.window.decorView.height.toFloat()
        val moveBoardOff = ObjectAnimator.ofFloat(
            listTextView, "translationX", screenHeight)
        moveBoardOff.duration = 700
        moveBoardOff.start()

        moveBoardOff.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                clearToDoList()

                 // Animate from above the screen down to default location
                val moveBoardOn = ObjectAnimator.ofFloat(
                    listTextView, "translationX", -screenHeight, 0f)
                moveBoardOn.duration = 700
                moveBoardOn.start()
            }
        })
    }

    override fun onSensorChanged(event: SensorEvent) {
        // get values in x,y,z using accelerometer
        val x: Float = event.values[0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]

        // get magnitude of acceleration
        val currentAcceleration: Float = x * x + y * y + z * z

        // get the difference between current acceleration & previous acceleration to get the displacement
        val delta = currentAcceleration - lastAcceleration
        lastAcceleration = currentAcceleration

        // if phone is shook
        if (abs(delta) > SHAKE_THRESHOLD) {
            onYesClick()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}
