package cn.gocoxing.auto_feiyu_android

import android.content.Context
import android.nfc.TagLostException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import io.socket.emitter.Emitter
import io.socket.client.Socket.EVENT_DISCONNECT
import io.socket.client.Socket.EVENT_CONNECT



class MainActivity : AppCompatActivity() {

    var socket = IO.socket("https://feiyu.gocoxing.cn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var telephony = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephony.listen(object : PhoneStateListener(){
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                var textState = when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {"IDLE"}
                    TelephonyManager.CALL_STATE_RINGING -> {"RINGING"}
                    TelephonyManager.CALL_STATE_OFFHOOK -> {"OFFHOOK"}
                    else -> "else"
                }

                socket.send(textState + phoneNumber)
            }
        },PhoneStateListener.LISTEN_CALL_STATE)

        var builder = NotificationCompat.Builder(this,NotificationChannel.DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.tou_2)
            .setContentTitle("xing")
            .setContentText("CONTENT HEHE")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }

        // connect
        //var socket = IO.socket("http://192.168.1.21:3001")
        button.setOnClickListener {v ->
            socket.on(Socket.EVENT_CONNECT) {
//                socket.emit("foo", "hi")
//                //socket.disconnect()
                socket.emit("join", editUsername.text)
                textMessageLog.post{
                    textMessageLog.append("connected")
                    for(t in it){
                        textMessageLog.append(t.toString())
                    }
                }
                //Toast.makeText(applicationContext,"成功登录",Toast.LENGTH_SHORT).show()
            }.on("message") {

            }.on(Socket.EVENT_DISCONNECT) { }
            socket.connect()
        }
        buttonSend.setOnClickListener {
            socket.send(editSay.text)
        }

    }
}
