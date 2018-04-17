package com.example.cllsdjackt013.testit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by CLLSDJACKT013 on 07/12/2017.
 */

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int  onStartCommand(Intent intent,int flags,int startId){
        Toast.makeText(this,"service started",Toast.LENGTH_LONG).show();
        Log.d("service::","starting services\n\n");
        return START_STICKY;
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d("##","Now stopping services....\n\n");
        Toast.makeText(this,"service destroyed",Toast.LENGTH_LONG).show();
    }

}
