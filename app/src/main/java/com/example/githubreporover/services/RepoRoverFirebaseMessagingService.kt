package com.example.githubreporover.services

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.githubreporover.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class RepoRoverFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        super.onMessageReceived(remoteMessage)
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            if (it) {
                getFireBaseMessage(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            }
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getFireBaseMessage(title: String?, body: String?) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "notify")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(123, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle new or refreshed FCM registration token
        Log.d(TAG, "Refreshed token: $token")
        // You may want to send this token to your server for further use
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}