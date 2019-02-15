package cn.tklvyou.appkeeplive

import android.content.BroadcastReceiver
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import cn.jpush.android.api.JPushInterface
import cn.tklvyou.appkeeplive.keeplive.foreground.DaemonService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        public val MESSAGE_RECEIVED_ACTION = "cn.tklvyou.appkeeplive.MESSAGE_RECEIVED_ACTION"
        public val KEY_TITLE = "title"
        public val KEY_MESSAGE = "message"
        public val KEY_EXTRAS = "extras"
        public var isForeground = false
    }


    private var mInit: Button? = null
    private var mSetting: Button? = null
    private var mStopPush: Button? = null
    private var mResumePush: Button? = null
    private var mGetRid: Button? = null
    private var mRegId: TextView? = null
    private var msgText: EditText? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(applicationContext, DaemonService::class.java))
        Log.d("MainActivity", "onCreate(): savedInstanceState = [$savedInstanceState]")
        Toast.makeText(this, "开启后台守护服务", Toast.LENGTH_SHORT).show()

        initView()
        registerMessageReceiver()  // used for receive msg

    }

    private fun initView() {
        val mImei = findViewById<TextView>(R.id.tv_imei)
        val udid = ExampleUtil.getImei(applicationContext, "")
        if (null != udid) mImei.text = "IMEI: " + udid

        val mAppKey = findViewById<TextView>(R.id.tv_appkey) as TextView
        var appKey = ExampleUtil.getAppKey(applicationContext)
        if (null == appKey) appKey = "AppKey异常"
        mAppKey.text = "AppKey: " + appKey

        mRegId = findViewById<TextView>(R.id.tv_regId) as TextView
        mRegId!!.setText("RegId:")

        val packageName = packageName
        val mPackage = findViewById<TextView>(R.id.tv_package) as TextView
        mPackage.text = "PackageName: " + packageName

        val deviceId = ExampleUtil.getDeviceId(applicationContext)
        val mDeviceId = findViewById<TextView>(R.id.tv_device_id) as TextView
        mDeviceId.text = "deviceId:" + deviceId

        val versionName = ExampleUtil.GetVersion(applicationContext)
        val mVersion = findViewById<TextView>(R.id.tv_version) as TextView
        mVersion.text = "Version: " + versionName

        mInit = findViewById<Button>(R.id.init) as Button
        mInit!!.setOnClickListener(this)

        mStopPush = findViewById<Button>(R.id.stopPush) as Button
        mStopPush!!.setOnClickListener(this)

        mResumePush = findViewById<Button>(R.id.resumePush) as Button
        mResumePush!!.setOnClickListener(this)

        mGetRid = findViewById<Button>(R.id.getRegistrationId) as Button
        mGetRid!!.setOnClickListener(this)

        mSetting = findViewById<Button>(R.id.setting) as Button
        mSetting!!.setOnClickListener(this)

        msgText = findViewById<EditText>(R.id.msg_rec) as EditText
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.init -> init()
            R.id.setting -> {
                val intent = Intent(this@MainActivity, PushSetActivity::class.java)
                startActivity(intent)
            }
            R.id.stopPush -> JPushInterface.stopPush(applicationContext)
            R.id.resumePush -> JPushInterface.resumePush(applicationContext)
            R.id.getRegistrationId -> {
                val rid = JPushInterface.getRegistrationID(applicationContext)
                if (!rid.isEmpty()) {
                    mRegId!!.setText("RegId:" + rid)
                } else {
                    Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private fun init() {
        JPushInterface.init(applicationContext)
    }


    override fun onResume() {
        isForeground = true
        super.onResume()
    }


    override fun onPause() {
        isForeground = false
        super.onPause()
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver!!)
        super.onDestroy()
    }


    //for receive customer msg from jpush server
    private var mMessageReceiver: MessageReceiver? = null

    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MESSAGE_RECEIVED_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver!!, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION == intent.action) {
                    val messge = intent.getStringExtra(KEY_MESSAGE)
                    val extras = intent.getStringExtra(KEY_EXTRAS)
                    val showMsg = StringBuilder()
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n")
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n")
                    }
                    setCostomMsg(showMsg.toString())
                }
            } catch (e: Exception) {
            }

        }
    }

    private fun setCostomMsg(msg: String) {
        if (null != msgText) {
            msgText!!.setText(msg)

            msgText!!.setVisibility(android.view.View.VISIBLE)
        }
    }

}
