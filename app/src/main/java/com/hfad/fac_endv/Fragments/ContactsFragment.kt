package com.hfad.fac_endv.Fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import com.hfad.fac_endv.MainActivity
import com.hfad.fac_endv.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ContactsFragment : Fragment() {

    lateinit var l1:ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_contacts, container, false)
        l1 = view.findViewById(R.id.contacts_view)
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getPerm()
    }

    fun getPerm(){
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    1)
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getContacts()
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

    @SuppressLint("Recycle")
    fun getContacts() {
        var cursor: Cursor = this.requireContext().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        this.requireActivity().startManagingCursor(cursor)

        var from = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID)

        var to:IntArray = intArrayOf(android.R.id.text1, android.R.id.text2)

        var simpleCursorAdapter = SimpleCursorAdapter(
                this.requireContext(),
                android.R.layout.simple_list_item_2,
                cursor,
                from,
                to
        )
        l1.adapter = simpleCursorAdapter
        l1.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

}
