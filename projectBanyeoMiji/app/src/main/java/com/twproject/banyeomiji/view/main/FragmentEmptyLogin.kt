package com.twproject.banyeomiji.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class FragmentEmptyLogin : Fragment() {

    private lateinit var mContext: Context
    private lateinit var activity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Main).launch {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        val itemId = when(MyGlobals.instance!!.navState) {
            "category" -> R.id.fragmentCategory
            "map" -> R.id.fragmentStoreMap
            else -> R.id.fragmentStoreMap
        }
        bottomNavigationView.selectedItemId = itemId

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empty_login, container, false)
    }
}