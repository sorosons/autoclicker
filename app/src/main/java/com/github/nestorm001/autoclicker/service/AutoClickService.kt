package com.github.nestorm001.autoclicker.service

import android.R.attr
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.nestorm001.autoclicker.MainActivity
import com.github.nestorm001.autoclicker.bean.Event
import com.github.nestorm001.autoclicker.logd
import org.json.JSONException
import org.json.JSONObject
import android.R.attr.data
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.github.nestorm001.autoclicker.Server
import com.github.nestorm001.autoclicker.Test
import org.json.JSONArray


/**
 * Created on 2018/9/28.
 * By nesto
 */

var autoClickService: AutoClickService? = null

class AutoClickService : AccessibilityService() {

    internal val events = mutableListOf<Event>()

    override fun onInterrupt() {
        // NO-OP
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // NO-OP
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        "onServiceConnected".logd()
        autoClickService = this
        startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }






    var isclick:Boolean?=false
    private fun parsejMaho(serverNo: String)
    {
        AndroidNetworking.get(Test.serverUrl)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                 //   @SuppressLint("SetTextI18n")
                    override fun onResponse(response: JSONObject?) {
                     //   var withDrawBalance = response!!.getJSONObject("data").getDouble("balance")
                        Log.d("RESPONSE", response.toString())
                      isclick = response!!.getBoolean("is_click_for_waiting")

                     Log.d("isclick", isclick.toString())


                    }

                    override fun onError(anError: ANError?) {
                        Log.d("ERRORO", anError.toString())
                    }
                })
    }


    fun click(x: Int, y: Int) {
        "click $x $y".logd()


        Log.d("Değerler", Test.clickingtime.toString())
        Log.d("Değerler", Test.refreshtime.toString())
        Log.d("Değerler", Test.serverno)
        Log.d("Değerler", Test.serverUrl)
        parsejMaho(Test.serverno);
        if(isclick==true)
        {
            for (i in 1..2) {
                Log.d("CLİCKXX", "Tıklama  Olacak ")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
                val path = Path()
                path.moveTo(x.toFloat(), y.toFloat())
                val builder = GestureDescription.Builder()
                val gestureDescription = builder
                        .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
                        .build()
                dispatchGesture(gestureDescription, null, null)
                Thread.sleep(Test.clickingtime)
            }

        }
        Thread.sleep(Test.refreshtime)


    }

    fun run(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        events.toString().logd()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        "AutoClickService onUnbind".logd()
        autoClickService = null
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".logd()
        autoClickService = null
        super.onDestroy()
    }
}