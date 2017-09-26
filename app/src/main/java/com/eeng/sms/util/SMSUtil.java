package com.eeng.sms.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.eeng.sms.model.Customer;

import java.util.ArrayList;

/**
 * Created by Caca Rusmanas on 04/03/2017.
 */

public class SMSUtil {

    public static void sendLongSMS(Context context) {
        SmsManager sms = SmsManager.getDefault();
        String msg = "Aslkum Sahabat, Smg sehat Brlimpah Rjeki, Kmi dr pandu utomo solo-Jateng, menawarkan program investasi online, kerja cukup 2 jam per hari.";

        ArrayList<String> parts = sms.divideMessage(msg);
        sms.sendMultipartTextMessage("087823989459", null, parts, null, null);
    }


    // send Short SMS using Pending Intent
    public static void sendSMS(final Context context, String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    // send Long SMS using Pending Intent
    public static void sendLongSMS(final Context context, Customer customer, String greetings, String contentMessage) {
        String DELIVERED = "SMS_DELIVERED_" + customer.getPhoneNumber();

        SmsManager sms = SmsManager.getDefault();

        String msg = greetings.trim() + " " + customer.getTitle() + " " + customer.getCustomerName()
                + ",\n" + contentMessage.trim();

        ArrayList<String> parts = sms.divideMessage(msg.trim());
        int messageCount = parts.size();

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
        Intent deliveredIntent = new Intent(DELIVERED);
        deliveredIntent.putExtra("phoneNumber", customer.getPhoneNumber());
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, deliveredIntent, 0);

        for (int j = 0; j < messageCount; j++) {
            deliveryIntents.add(deliveredPI);
        }

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.i("==DELIVERED", arg1.getExtras().getString("phoneNumber"));
                        context.unregisterReceiver(this);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("==NOT DELIVERED", arg1.getExtras().getString("phoneNumber"));
                        context.unregisterReceiver(this);
                        break;

                }
            }
        }, new IntentFilter(DELIVERED));

        sms.sendMultipartTextMessage(customer.getPhoneNumber(), null, parts, null, deliveryIntents);
    }
}
