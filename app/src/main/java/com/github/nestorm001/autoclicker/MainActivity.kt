package com.github.nestorm001.autoclicker

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.EditText
import com.github.nestorm001.autoclicker.service.FloatingClickService
import com.github.nestorm001.autoclicker.service.autoClickService
import kotlinx.android.synthetic.main.activity_main.*


private const val PERMISSION_CODE = 110

class MainActivity : AppCompatActivity() {

    private var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val server=findViewById<EditText>(R.id.edtserver)
        val edtctime=findViewById<EditText>(R.id.edtclickingtime)
        val edtrefreshtime=findViewById<EditText>(R.id.edtrefreshtime)
        val edtserverurl=findViewById<EditText>(R.id.edtserverurl)

     //   edtserverurl.setText(Test.serverUrl+server.text.toString())
        server.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                edtserverurl.setText(Test.serverUrl+server.text.toString())
            }
        })

            button.setOnClickListener {
              //  edtserverurl.setText(Test.serverUrl+server.text.toString())
                Test.serverno = server.text.toString()
                Test.clickingtime = edtctime.text.toString().toLongOrNull()!!
                Test.refreshtime = edtrefreshtime.text.toString().toLongOrNull()!!
                Test.serverUrl = edtserverurl.text.toString()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                    || Settings.canDrawOverlays(this)) {
                serviceIntent = Intent(this@MainActivity,
                        FloatingClickService::class.java)
            //    intent.putExtra("server",server.text.toString())
                startService(serviceIntent)
                onBackPressed()
            } else {
                askPermission()
                shortToast("You need System Alert Window Permission to do this")
            }
        }
    }

    private fun checkAccess(): Boolean {
        val string = getString(R.string.accessibility_service_id)
        val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (id in list) {
            if (string == id.id) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        val hasPermission = checkAccess()
        "has access? $hasPermission".logd()
        if (!hasPermission) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onDestroy() {
        serviceIntent?.let {
            "stop floating click service".logd()
            stopService(it)
        }
        autoClickService?.let {
            "stop auto click service".logd()
            it.stopSelf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return it.disableSelf()
            autoClickService = null
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
