package com.example.cllsdjackt013.testit;


import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.security.keystore.KeyInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.nxp.nfclib.CardType;
import com.nxp.nfclib.KeyType;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.defaultimpl.Utilities;
import com.nxp.nfclib.desfire.DESFireFactory;
import com.nxp.nfclib.desfire.DESFireFile;
//import com.nxp.nfclib.desfire.EV2ApplicationKeySettings;
import com.nxp.nfclib.desfire.EV1ApplicationKeySettings;
import com.nxp.nfclib.desfire.EV1KeySettings;
import com.nxp.nfclib.desfire.EV2ApplicationKeySettings;
import com.nxp.nfclib.desfire.IDESFireEV1;
import com.nxp.nfclib.desfire.IDESFireEV2;
import com.nxp.nfclib.interfaces.IKeyData;
//import com.nxp.nfclib.ndef.Uri;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private TextView m_textView = null;
    private String m_stringKey = "8b9a693a12c1a87e55c5a40fcec92d12";
    private NxpNfcLib m_libInstance = null;
    private IKeyData objKEY_2KTDES = null;

    byte [] keydata;
    KeyData kd = null;
    CardType cardType;
    IDESFireEV2 objDESFireEV2;
    Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //these two buttons implement action listerners that execute intents
        b1 = (Button)findViewById(R.id.button4);
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                startActivity(i);
            }
        });
        b2 = (Button)findViewById(R.id.button5);
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:0706103800"));
                startActivity(i);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logName();
        initializeLibrary();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void logName(){
        int i =0;
        for(i =0;i<10;i++){
            Log.d("##Logger action::",Integer.toString(i));
        }
    }
    public void startService(View view){
            startService(new Intent(getBaseContext(),MyService.class));
    }
    public void stopService(View view){
            stopService(new Intent(getBaseContext(),MyService.class));
    }
    //broadcast a custom intent
    public void broadcastIntent(View view){
        Intent intent = new Intent();
        intent.setAction("com.tutorial.CUSTOM_INTENT");
        sendBroadcast(intent);
    }
    public void myIntent(View view){
        Intent intent = new Intent();
        intent.setAction("com.kipkeu.CUSTOM_INTENT");
        sendBroadcast(intent);
    }
    /**
     * below section handles all about MIFARE cards
     */
    private void initializeLibrary(){
        m_libInstance = NxpNfcLib.getInstance();
        m_libInstance.registerActivity(this,m_stringKey);
    }
    @Override
    protected void onResume(){    //method called if app becomes active
        m_libInstance.startForeGroundDispatch();
        super.onResume();
    }
    @Override
    protected void onPause(){  //method called if app becomes inactive
        m_libInstance.stopForeGroundDispatch();
        super.onPause();
    }
    @Override
    public void onNewIntent(final Intent intent){
        Log.d(TAG,"onNewIntent");
       CardLogic(intent);
        super.onNewIntent(intent);
    }
    public void CardLogic(final Intent intent){
         cardType = m_libInstance.getCardType(intent);
        Log.d("##Card Type Found",cardType.getTagName());
       // m_textView.setText("Card Type::" +cardType.getTagName());
        if(CardType.DESFireEV2 == cardType){
            //IDESFireEV2 objDESFireEV2 = DESFireFactory.getInstance().getDESFireEV2(m_libInstance.getCustomModules());
             objDESFireEV2 = DESFireFactory.getInstance().getDESFireEV2(m_libInstance.getCustomModules());
            try{
                objDESFireEV2.getReader().connect();
                objDESFireEV2.getReader().close();
            }
            catch (Throwable t){
                t.printStackTrace();
            }
        }
    }
    public void generateAES(){
        byte[] byAesKey = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
        Key key = new SecretKeySpec(byAesKey,"AES");
        kd = new KeyData();
        kd.setKey(key);
        keydata = key.getEncoded();
        Log.d("##About to::","dump bytes");
        Log.d(TAG,"Key verification:" + com.nxp.nfclib.utils.Utilities.dumpBytes(keydata));

    }
    public void manipulateCard(View view){
        Log.d("Message::","card manipulation here");
        Intent i = new Intent(Intent.ACTION_VIEW);
        //CardLogic(i);

    }

    //function to demo intent
    public void showSecond(View view){
        Intent i = new Intent(MainActivity.this,SecondActivity.class);
        startActivity(i);
    }

    public void heisenberg(final Intent intent){
        Log.d("###SAY MY NAME::","WALTER WHITE HEISENBERG");
    }

    public void getFileId(View view){
        Log.d("TAG TYPE::",cardType.getTagName());
        byte[] MF = new byte[]{0x00,0x00,0x00};
        byte[] DF1 = new byte[] {0x1,0x00,0x00};
        byte[] DF2 = new byte[] {0x00,0x00,0x01};
        final int appId0 = 0x000000;
        if(CardType.DESFireEV2 == cardType){
            //objDESFireEV2 = DESFireFactory.getInstance().getDESFireEV2(m_libInstance.getCustomModules());
            try{
                //first connect with the card reader
                objDESFireEV2.getReader().connect();
                Log.d("**Action**","Getting file ids..\n");
                //to get Application IDs, you need to select MF first
                //objDESFireEV2.selectApplication(MF);// this is selection of PICC level, all other commands can now be executed
                Log.d("Total memory::",Integer.toString(objDESFireEV2.getTotalMemory()));
                //objDESFireEV2.selectApplication(appId0);
                int[] fileID = objDESFireEV2.getApplicationIDs();
                int x =  fileID.length;
                Log.d("IDs length::",Integer.toString(x));
                objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.AES128,objKEY_2KTDES);
                Log.d("CARD INFO::",objDESFireEV2.getVersion().toString());
                int[] fileIDs = objDESFireEV2.getApplicationIDs();
                for(int i = 0;i<fileIDs.length;i++){
                    Log.d("##File IDs::",Integer.toString(fileIDs[i]));
                }
               // objDESFireEV2.createApplication(0x000001,0x01,0x11,0x00,);
               // EV2ApplicationKeySettings.Builder appKeyBuilder = new EV2ApplicationKeySettings.Builder();
                EV2ApplicationKeySettings.Builder mesh = new EV2ApplicationKeySettings.Builder();
                EV2ApplicationKeySettings appSettings = (EV2ApplicationKeySettings) mesh.setAppKeySettingsChangeable(true)
                        .setAppMasterKeyChangeable(true)
                        .setAuthenticationRequiredForApplicationManagement(false).setAuthenticationRequiredForDirectoryConfigurationData(false)
                        .setKeyTypeOfApplicationKeys(KeyType.AES128).build();
                //objDESFireEV2.createApplication(0x000001,appSettings);


                objDESFireEV2.createApplication(DF2,appSettings);
                //objDESFireEV2.createApplication(DF2,appSettings,2,KeyType.AES128);
                objDESFireEV2.getReader().close();
            }
            catch (Throwable err){
                err.printStackTrace();
            }
        }
    }
    public void Kenya(View view){
        final int appId0 = 0x000000;
        byte [] app1 = {(byte)0x01,(byte)0x00,(byte)0x00};
        objDESFireEV2.getReader().connect();
        Log.d("##CARD TYPE::",cardType.getTagName());
        ///objDESFireEV2.selectApplication(appId0);
        //get application ids on card

       // EV2ApplicationKeySettings keySettings = new EV2ApplicationKeySettings();
        //objDESFireEV2.format();
        objDESFireEV2.selectApplication(0);
        Log.d("##Command Set::",objDESFireEV2.getCommandSet().toString());
        Log.d("##Key settings",objDESFireEV2.getKeySettings().toString());
        EV1KeySettings  keySets = objDESFireEV2.getKeySettings();
        Log.d("**Settings",convertByteArray(keySets.toByteArray()));
        Log.d("##Auth status::",objDESFireEV2.getAuthStatus());
        Log.d("Key version",objDESFireEV2.getVersion().toString());
        Log.d("Key version",new String(objDESFireEV2.getVersion()));
        Log.d("++Card version",convertByteArray(objDESFireEV2.getVersion()));
        //objDESFireEV2.getKeySetVersion();
        byte[] keyVersion = objDESFireEV2.getKeyVersion(0);
        Log.d("##Key version::",convertByteArray(keyVersion));
        String uidhex = new String();
        Log.d("Floating::",Integer.toHexString(11));
        Log.d("","Converting byte to string....\n\n");
        Log.d("#After conversion::",convertIntTobyte(Math.round(Math.round(Math.random()*10)),Math.round(Math.round(Math.random()*10)),Math.round(Math.round(Math.random()*10))));
        byte[] uid = objDESFireEV2.getUID();
        for (int i = 0; i < uid.length; i++) {
            String x = Integer.toHexString(((int) uid[i] & 0xff));
            if (x.length() == 1) {
                x = '0' + x;
            }
            uidhex += x;
        }
        Log.d("UID:::",uidhex);
        Log.d("##GENERATING AES....","Generating Keys");
        //generateAES();
        //authenticatePCC();
        Log.d("Next..","Authenticate with AES");
        //authenticatePCCAES();
        //authenticate,change keys and authenticate
        //ChangeDefaultKeys();
        /*****Authenticate and create an app ***********/
       // createApplication(app1);
        Log.d("##Authent success","Authentication tick");
        //objDESFireEV2.authenticate(appId0, IDESFireEV1.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);
        //objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.THREEDES,objKEY_2KTDES);
        int [] appIdArray = objDESFireEV2.getApplicationIDs();
        if(appIdArray.length>0){
            Log.d("##Tick","Application Id exists");
        }
        else{
            Log.d("**Nope","No application exists");
        }
        //fetch number of apps and retun their ids
        Log.d("##Fetching apps","returning ids");
        Log.d("##App Ids",countApps());
        //createFiles();
        //writeDataToFile();
        readData();
    }
    public String convertIntTobyte(int x,int y,int z){
        String byteEquivalent = "";
        int [] receivedNumber = {x,y,z};
        Log.d("String to convert::",Integer.toString(x)+""+Integer.toString(y)+""+Integer.toString(z));
        for(int i =0;i<receivedNumber.length;i++) {
            //convert individual numbers
            String intermediateEq = Integer.toHexString(receivedNumber[i]);
            if (intermediateEq.length() == 1) {
                byteEquivalent += "0" + intermediateEq;
            } else {
                byteEquivalent += intermediateEq;
            }

        }
        return byteEquivalent;
    }
    public String convertByteArray(byte[] byteArray){
        String final_bytes = "";
        if(byteArray.length>0){
            for(int i =0;i<byteArray.length;i++){
                byte byteElement = byteArray[i];
                final_bytes += byteElement;
            }

        }
        return final_bytes;
    }
/*    public void getkeyInfo(){
        desFireEV1.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.THREEDES, objKEY_2KTDES);

        objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.AES,KeyType.AES128,keydata);
    }*/
public void authenticatePCC(){
    //default 2k three dees
     byte [] DEFAULT_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                                    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                                    (byte)0x00,(byte)0x00
     };
     objDESFireEV2.selectApplication(0);
    Key defKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
    KeyData defKeyData = new KeyData();
    defKeyData.setKey(defKey);
    objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.THREEDES,defKeyData);
    Log.d("##Auth status","Very successful");
    int [] appIds = objDESFireEV2.getApplicationIDs();
    if(appIds.length>0){
        Log.d("###Tick","Application Id exists");
    }
    else{
        Log.d("##Nope","No application exists");
    }
}

public void authenticatePCCAES(){
    byte[] DEFAULT_AES_128 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                              (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                              (byte)0x00,(byte)0x00};
    Key aesKey = new SecretKeySpec(DEFAULT_AES_128,"AES");
    KeyData authKey = new KeyData();
    authKey.setKey(aesKey);
    objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.AES128,authKey);
    Log.d("##Auth logger","Authentication success");
}
public void ChangeDefaultKeys(){
    /*
    -Authenticate to PCC Master key
    -Change 16 byte zero key to 16 byte non-zero key
    -Authenticate to PICC level with the new key
    -Change 16 byte non-zero 3DES to 16 byte AES
    -Authenticate with 16 byte AES
     */
    //objDESFireEV2.format();
    byte[] NEW_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                             (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
                             (byte)0x00,(byte)0x00
    };
    byte [] DEFAULT_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00
    };
    byte[] DEFAULT_AES_128 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00};

    Key defKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
    KeyData defKeyData = new KeyData();
    defKeyData.setKey(defKey);
    objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.THREEDES,defKeyData);
    Log.d("##Auth 1::","Auth with 2K3DES default key success");
    //change default 16 byte zero DES to 16 non zero bytes
    objDESFireEV2.changeKey(0,KeyType.THREEDES,DEFAULT_KEY_2KTDES,NEW_KEY_2KTDES,(byte)0);
    Log.d("Key change","Change to valid 2K3DES(non-zeros)");
    //prepare new 2k3DES for authentication
    Key valid2K3des = new SecretKeySpec(NEW_KEY_2KTDES,"DESede");
    KeyData validKeydata = new KeyData();
    validKeydata.setKey(valid2K3des);
    //authenticate with new key
    objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.THREEDES,validKeydata);
    Log.d("Valid auth","Valid authentication with 2K3DES");
    //NEXT change 16 bytes non-zero key to AES
   // objDESFireEV2.changeKey(0,KeyType.AES128,NEW_KEY_2KTDES,DEFAULT_AES_128,(byte)0);
    Log.d("Key change","Key changed to AES");
    //then authenticate PCC with DEFAULT AES
    Key aesKey = new SecretKeySpec(DEFAULT_AES_128,"AES");
    KeyData aesKeyData = new KeyData();
    aesKeyData.setKey(aesKey);
    //objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.AES,KeyType.AES128,aesKeyData);
    Log.d("Auth status::","finally authenticated with AES");
}
public void createApplication(byte []appId){
    byte[] NEW_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
            (byte)0x00,(byte)0x00
    };
    Key valid2K3DES = new SecretKeySpec(NEW_KEY_2KTDES,"DESede");
    KeyData keyData = new KeyData();
    keyData.setKey(valid2K3DES);
    objDESFireEV2.authenticate(0,IDESFireEV2.AuthType.Native,KeyType.THREEDES,keyData);
    Log.d("Auth message","Authentication successful");
    Log.d("Generating keys builder","Building keys....\n");
    EV2ApplicationKeySettings.Builder ev2ApplicationKeySettings = new EV2ApplicationKeySettings.Builder();
    EV2ApplicationKeySettings appSettings = (EV2ApplicationKeySettings) ev2ApplicationKeySettings.setAppKeySettingsChangeable(true).setAppMasterKeyChangeable(true)
            .setAuthenticationRequiredForApplicationManagement(false).setAuthenticationRequiredForDirectoryConfigurationData(false)
            .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();
    //ev1 app key settings
    EV1ApplicationKeySettings.Builder appsetbuilder = new EV1ApplicationKeySettings.Builder();
    EV1ApplicationKeySettings appsettings = appsetbuilder.setAppKeySettingsChangeable(true)
            .setAppMasterKeyChangeable(true)
            .setAuthenticationRequiredForApplicationManagement(false)
            .setAuthenticationRequiredForDirectoryConfigurationData(false)
            .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();
    Log.d("Init..","creating application");
    objDESFireEV2.createApplication(appId,appsettings);
    Log.d("success","App created successfully");
}
public String countApps(){
    String finalApps = "";
    authenticate();
    int [] appids = objDESFireEV2.getApplicationIDs();
    if(appids.length>0){
        for(int i =0;i<appids.length;i++){
            finalApps += appids[i];
        }
    }
    return finalApps;
}
public void createFiles(){
    byte [] app1 = {(byte)0x01,(byte)0x00,(byte)0x00};
    final int fileId = 0x08;
    final int fileId2 = 0x0c;
    byte param1 = 0x0e;
    byte param2 = 0x0f;
    byte parama = 0x0c;
    authenticate();
    Log.d("=>Authentication","At file creation successfull");
    objDESFireEV2.selectApplication(app1);
    Log.d("=>","Application selected successfully");
    DESFireFile.FileSettings fileSettings = new DESFireFile.StdDataFileSettings(IDESFireEV1.CommunicationType.Plain,(byte)0,(byte)0,(byte)0,(byte)0,50);
    //param1 and param 4 define file access,params 2 and 3 are the application ids
    DESFireFile.FileSettings fileSettings1 = new DESFireFile.StdDataFileSettings(IDESFireEV2.CommunicationType.Plain,param1,(byte)0x00,(byte)0x00,param2,50);
    objDESFireEV2.createFile(fileId2,fileSettings1);
    //objDESFireEV2.createFile(6,fileSettings);
    Log.d("creation success","File created successfully");
}
public void authenticate() {
    byte[] NEW_KEY_2KTDES = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x00, (byte) 0x00
    };
    Key validAesKey = new SecretKeySpec(NEW_KEY_2KTDES, "DESede");
    KeyData validKeyData = new KeyData();
    validKeyData.setKey(validAesKey);
    objDESFireEV2.authenticate(0, IDESFireEV2.AuthType.Native, KeyType.TWO_KEY_THREEDES, validKeyData);
}

public void writeDataToFile(){
    byte [] app1 = {(byte)0x01,(byte)0x00,(byte)0x00};
    final int appOne = 0x01;
    final int fileId = 0x08;
    final int fileId2 = 0x0c;
    byte [] DEFAULT_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00};
    Log.d("##WRITING","How about we write data to a file");
    //authenticate app
   // authenticate();
    //select application
    objDESFireEV2.selectApplication(app1);
    //authenticate app1
    Key default2k3des = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
    KeyData default2k3desData = new KeyData();
    default2k3desData.setKey(default2k3des);
    Log.d("-->","Authenticating selected application");
    objDESFireEV2.authenticate(65536, IDESFireEV1.AuthType.Native,KeyType.TWO_KEY_THREEDES,default2k3desData);
    String myName = "Jonah Kiprop Kimutai";
    Log.d("Str to Bytes","Converting name to bytes");
    myName.getBytes();
    Log.d("=>Name final","String equivalent of name bytes");
    Log.d("=>dumping..",convertByteArray(myName.getBytes()));
    Log.d("=>","Writing data to file 0x08");
    //authenticate();
    objDESFireEV2.writeData(fileId2,0,myName.getBytes());
    Log.d("-->","Data written successfully");

}
public void readData(){
    byte [] app1 = {(byte)0x01,(byte)0x00,(byte)0x00};
    final int appOne = 0x01;
    final int fileId = 0x08;
    byte [] DEFAULT_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00};

    Log.d("Auth","Authenticating app1");
    Key default2k3des = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
    KeyData default2k3desData = new KeyData();
    default2k3desData.setKey(default2k3des);
    objDESFireEV2.selectApplication(app1);
    objDESFireEV2.authenticate(65536,IDESFireEV2.AuthType.Native,KeyType.TWO_KEY_THREEDES,default2k3desData);
    Log.d("=>","App authentication successful");
    Log.d("-->","Reading file 0x08");
    byte [] dataFromCard = objDESFireEV2.readData(12,0,50);
    if(dataFromCard.length>0){
        Log.d("==>","Data exists in file 0x08");
        Log.d("->Data length",Integer.toString(dataFromCard.length));
    }
    else{
        Log.d("==>","No data was written");
    }
    Log.d("==>Name obtained::",dataFromCard.toString());
    Log.d("==>Byte equivalent",convertByteArray(dataFromCard));
    String derivedName = new String(dataFromCard);
    Log.d("=>Dumping Name..",derivedName);

}

}
