package com.example.cllsdjackt013.testit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by CLLSDJACKT013 on 07/12/2017.
 */

public class MyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        int i;
        Toast.makeText(context,"Intent detected.",Toast.LENGTH_LONG).show();
        //meanwhile do something like count 1 to 30
        for(i=0;i<50;i++){
            Log.d("##broadcast receiver",Integer.toString(i));
            if(i == 20){
                break;
            }
        }
    }
}
