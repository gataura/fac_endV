package com.hfad.fac_endv.Fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.hfad.fac_endv.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InfoFragment : Fragment() {

    lateinit var text: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_info, container, false)

        text = view.findViewById(R.id.sys_info)

        askPerm()

        return view
    }

    fun askPerm() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                    1)
        } else {
            text.text = getSystemInfo(this.requireActivity())
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    text.text = getSystemInfo(this.requireActivity())
                } else {
                    this.requireActivity().finishAffinity()
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

    fun getSystemInfo(a: Activity) : String {
        var s = ""
        var wifiManager: WifiManager = this.requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAdress = android.text.format.Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        try {
            var pInfo = a.packageManager.getPackageInfo(a.packageName, PackageManager.GET_META_DATA)
            s += "\n APP Package Name: " + a.packageName
            s += "\n APP Version Name: " + pInfo.versionName
            s += "\n APP Version Code: " + pInfo.versionCode
            s += "\n"
        } catch (e: PackageManager.NameNotFoundException){
        }

        s+= "\n Ip Adress: $ipAdress"
        s += "\n OS Version: " + System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL + ")"
        s += "\n OS API Level: " + Build.VERSION.SDK
        s += "\n Device: " + Build.DEVICE
        s += "\n Model (and Product): " + Build.MODEL + " (" + Build.PRODUCT + ")"


        s += "\n Manufacturer: " + Build.MANUFACTURER
        s += "\n Other TAGS: " + Build.TAGS

        s += "\n screenWidth: " + a.window.windowManager.defaultDisplay.width
        s += "\n screenHeight: " + a.window.windowManager.defaultDisplay.width
        s += "\n Keyboard available: " + (a.resources.configuration.navigation == Configuration.NAVIGATION_TRACKBALL)
        s += "\n SD CArd state: " + Environment.getExternalStorageState()

        var p = System.getProperties()
        var keys = p.keys()
        var key: String

        while (keys.hasMoreElements()) {
            key = keys.nextElement().toString()
            s += "\n > " + key + " = " + p[key].toString()
        }

        return s

    }


}
