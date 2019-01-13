@file:Suppress("DEPRECATION")

package com.hfad.fac_endv.Fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.SensorListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast

import com.hfad.fac_endv.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
@Suppress("DEPRECATION")
class SensorsFragment : Fragment(), SensorListener {

    lateinit var xViewA: TextView
    lateinit var yViewA: TextView
    lateinit var zViewA: TextView
    lateinit var xViewO: TextView
    lateinit var yViewO: TextView
    lateinit var zViewO: TextView
    lateinit var sm: SensorManager
    var s =""
    var lis: SensorListener = this
    var myTag = ""
    lateinit var saveButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sensors, container, false)

        saveButton = view.findViewById(R.id.detect_button)
        saveButton.isEnabled = false

        askPerm()

        xViewA = view.findViewById(R.id.xViewA)
        yViewA = view.findViewById(R.id.yViewA)
        zViewA = view.findViewById(R.id.zViewA)
        xViewO = view.findViewById(R.id.xViewO)
        yViewO = view.findViewById(R.id.yViewO)
        zViewO = view.findViewById(R.id.zViewO)

        sm = this.requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager

        return view
    }

    fun askPerm() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
        } else {
            saveButton.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveButton.isEnabled = true
                } else {
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(sensor: Int, values: FloatArray) {
        synchronized(this) {
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
                xViewO.text = "\n Orientation X: " + values[0]
                yViewO.text = "\n Orientation Y: " + values[1]
                zViewO.text = "\n Orientation Z: " + values[2]
            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                xViewA.text = "\n Accel X: " + values[0]
                yViewA.text = "\n Accel Y: " + values[1]
                zViewA.text = "\n Accel Z: " + values[2]
            }
        }
    }


    override fun onAccuracyChanged(sensor: Int, accuracy: Int) {
        Log.d(tag, "onAccuracyChanged: $sensor, accuracy: $accuracy")
    }

    fun onSaveButtonClick(v: View) {
        var now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {
            var mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg"
            var v1: View = this.requireActivity().window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            var bitmap: Bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false

            var imageFile = File(mPath)

            var outputStream = FileOutputStream(imageFile)
            var quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            var toast = Toast.makeText(this.requireContext(), "Скриншот сохранен", Toast.LENGTH_SHORT)
            toast.show()
            //openScreenshot(imageFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun openScreenshot(imageFile: File) {
        var intent = Intent()
        intent.action = Intent.ACTION_VIEW
        var uri = Uri.fromFile(imageFile)
        intent.setDataAndType(uri, "image/*")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        sm.registerListener(lis, SensorManager.SENSOR_ORIENTATION or SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        super.onStop()
        sm.unregisterListener(lis)
    }

}
