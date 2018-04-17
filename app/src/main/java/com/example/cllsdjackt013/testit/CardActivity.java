package com.example.cllsdjackt013.testit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nxp.nfclib.CardType;
import com.nxp.nfclib.KeyType;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.desfire.DESFireFactory;
import com.nxp.nfclib.desfire.DESFireFile;
import com.nxp.nfclib.desfire.DESFireFile.LinearRecordFileSettings;
import com.nxp.nfclib.desfire.EV1ApplicationKeySettings;
import com.nxp.nfclib.desfire.EV2ApplicationKeySettings;
import com.nxp.nfclib.desfire.IDESFireEV1;
import com.nxp.nfclib.desfire.IDESFireEV2;


import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import static com.nxp.nfclib.desfire.IDESFireEV2.*;

public class CardActivity extends AppCompatActivity {

    private final byte[] NEW_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
            (byte)0x00,(byte)0x00
    };
    private final byte [] DEFAULT_KEY_2KTDES = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00
    };
    private final byte[] DEFAULT_AES_128 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00};
    private final byte[] VALID_2KTDES_FACTORY_KEY = {(byte)0x43,(byte)0x46,(byte)0x4F,(byte)0x49,(byte)0x4D,(byte)0x48,(byte)0x50,
                                                     (byte)0x4E,(byte)0x4C,(byte)0x43,(byte)0x59,(byte)0x45,(byte)0x4E,(byte)0x52,
            (byte)0x58,(byte)0x41};

    private final byte[] app0 = {0x00,0x00,0x00};
    private final byte read_access = 0x0e;
    private final byte change_access_rights = 0x0f;

    private String TAG = MainActivity.class.getSimpleName();
    private NxpNfcLib m_libInstance = null;
    private String m_stringKey = "8b9a693a12c1a87e55c5a40fcec92d12";
    private CardType cardType;
    IDESFireEV2 objDESFireEV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                //objDESFireEV2.getReader().close();
            }
            catch (Throwable t){
                t.printStackTrace();
            }
        }
    }

    /*This method is called once, to authenticate and change PCC key settings*/
    public void authenticatePCCLevelAndChangeKeys(View view){
        //format card
        //objDESFireEV2.selectApplication(app0);
        //Log.d("==>","Application exists");
/*        Log.d("=>","Authenticate application");
        authenticateApp(app0);
        Log.d("*****","Auth successful");*/
        //create first default key
        /*********************************************************************************************/

        /*Key default2k3desKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
        KeyData default2k3desData = new KeyData();
        default2k3desData.setKey(default2k3desKey);
        //next, authenticate the card at the PCC level,i.e Master Key
        objDESFireEV2.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.THREEDES,default2k3desData);
        Log.d("==>","Default authentication successfull");
        //next step is to change valid TWO_KEY_THREEDEES
        Key valid2k3desKey = new SecretKeySpec(NEW_KEY_2KTDES,"DESede");
        KeyData valid2k3desData = new KeyData();
        valid2k3desData.setKey(valid2k3desKey);
        //here, we are changing keys from default 2K3DES TO valid 2K3DES
        objDESFireEV2.changeKey(0,KeyType.THREEDES,DEFAULT_KEY_2KTDES,NEW_KEY_2KTDES,(byte)0);
        Log.d("=>","Key changed successfully");

        //now authenticate with valid TWO_KEY_THREEDEES
        objDESFireEV2.authenticate(0, IDESFireEV1.AuthType.Native,KeyType.THREEDES,valid2k3desData);
        Log.d("=>","Authentication with valid 2K3DES successful");*/
        /************************************************************************************************/

        /*****************************VALID CODE *********************************************************/
       Key default2k3desKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede");
        KeyData default2k3desData = new KeyData();
        default2k3desData.setKey(default2k3desKey);
        //next, authenticate the card at the PCC level,i.e Master Key with default key
        objDESFireEV2.authenticate(0, IDESFireEV1.AuthType.Native, KeyType.THREEDES,default2k3desData);
        Log.d("==>","Default authentication successfull");
        /*************************************************************************************************/
        Key valid2k3desFactoryKey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY,"DESede");
        KeyData valid2k3desfactorykeydata = new KeyData();
        valid2k3desfactorykeydata.setKey(valid2k3desFactoryKey);
        //here, we are changing keys from default 2K3DES TO valid 2K3DES
        objDESFireEV2.changeKey(0,KeyType.THREEDES,DEFAULT_KEY_2KTDES,VALID_2KTDES_FACTORY_KEY,(byte)0);
        objDESFireEV2.authenticate(0, AuthType.Native,KeyType.TWO_KEY_THREEDES,valid2k3desfactorykeydata);
        Log.d("=>","Authentication with valid factory key successfull");

    }

    /*method to authenticate PCC level before any operation on card*/
    public void authenticate(byte [] appId){
       // Key valid2k3desKey = new SecretKeySpec(NEW_KEY_2KTDES,"DESede"); //>>
        Key valid2k3desKey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY,"DESede");//<<
        KeyData valid2k3desData = new KeyData();
        valid2k3desData.setKey(valid2k3desKey);
        Log.d("==>","About to authenticate");
        //objDESFireEV2.authenticate(convertByteArrayToInt(appId), IDESFireEV1.AuthType.Native,KeyType.THREEDES,valid2k3desData);//>>
        objDESFireEV2.authenticate(convertByteArrayToInt(appId), IDESFireEV1.AuthType.Native,KeyType.TWO_KEY_THREEDES,valid2k3desData);
        Log.d("==>","Authentication successfull");
    }
    /*method to authenticate PCC to application level*/
    public void authenticateApp(byte [] appId){
        Key default2k3desKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede"); //>>
        //Key default2k3desKey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY,"DESede"); //<<
        KeyData default2k3desData = new KeyData();
        default2k3desData.setKey(default2k3desKey);
        Log.d("==>","App authentication init....\n");
        //objDESFireEV2.authenticate(convertByteArrayToInt(appId),AuthType.Native,KeyType.TWO_KEY_THREEDES,default2k3desData);
        objDESFireEV2.authenticate(convertByteArrayToInt(appId), IDESFireEV1.AuthType.Native,KeyType.THREEDES,default2k3desData);
        Log.d("=>sucess::","Application level authentication successful");
    }
    //method to create an application
    public void createApplication(byte [] appId){
        //authenticate to PCC level
        //objDESFireEV2.selectApplication(app0);
        authenticate(app0);
        /*next build application key settings.
        -since EV2ApplicationKeySettings extends EV1ApplicationKeySettings i.e subclass,
        we can use EV1ApplicationKeySettings to build application key settings
         */
        Log.d("=>=>","Building Application Key settings");
/*        EV2ApplicationKeySettings.Builder appsetbuilder = new EV2ApplicationKeySettings.Builder();
        EV2ApplicationKeySettings appsettings = (EV2ApplicationKeySettings) appsetbuilder.setAppKeySettingsChangeable(true).
                setAppMasterKeyChangeable(true).
                setAuthenticationRequiredForApplicationManagement(false).
                setAuthenticationRequiredForDirectoryConfigurationData(false).
                setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();*/
        EV1ApplicationKeySettings.Builder appsetbuilder = new EV1ApplicationKeySettings.Builder();
        EV1ApplicationKeySettings appsettings = appsetbuilder.
                setAppKeySettingsChangeable(true).setAppMasterKeyChangeable(true)
                .setAuthenticationRequiredForApplicationManagement(false)
                .setAuthenticationRequiredForDirectoryConfigurationData(false).
                setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();
/*        EV1ApplicationKeySettings appsettings = appsetbuilder.setAppKeySettingsChangeable(true)
                .setAppMasterKeyChangeable(true)
                .setAuthenticationRequiredForApplicationManagement(false)
                .setAuthenticationRequiredForDirectoryConfigurationData(false)
                .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();*/
        Log.d("==>","Application Keys built successfully");
        Log.d("=>","Now creating app");
        objDESFireEV2.createApplication(appId,appsettings);
    }

    /*This method creates a file in a given application
    -remember for value,Linear and Cyclic record files, file ID has to be between 0x00 and 0x07 in the selected application
    -significant file size is passed when creating Standard and BackupData files, otherwise pass zero for other files
     */
    public void createFile(byte[] appId,int fileId,byte readAccess,byte changeAccessRights,int fileType,int fileSize){
        DESFireFile.FileSettings fileSettings = null;
        //First step is to authenticate at PCC level
        Log.d("=>=>","File creation init...\n");
        //authenticate(app0);
        Log.d("=>","Authentication at file creation successful");
        //next, select application under which to create file
        //objDESFireEV2.selectApplication(appId);
        Log.d("==>","Application selected successfully");
        //next, authenticate selected application
       // authenticateApp(appId);
       // authenticate(appId);
        //next is to create file settings
        Log.d("==>","Building file settings...\n");
        switch (fileType){
            case 0:
                 fileSettings = new DESFireFile.StdDataFileSettings(IDESFireEV1.CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,fileSize);
                break;
            case 1:
                 fileSettings = new DESFireFile.BackupDataFileSettings(IDESFireEV2.CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,fileSize);
                         break;
            case 2:
                fileSettings = new DESFireFile.ValueFileSettings(IDESFireEV2.CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,0,816,0,true,false);
                break;
            case 3:
               // fileSettings = new DESFireFile.LinearRecordFileSettings(IDESFireEV1.CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,10,12);
                fileSettings = new DESFireFile.LinearRecordFileSettings(IDESFireEV2.CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,10,12,120);
                break;
            case 4:
                fileSettings = new DESFireFile.CyclicRecordFileSettings(CommunicationType.Plain,readAccess,(byte)0x00,(byte)0x00,changeAccessRights,10,10,100);
                break;
        }
        Log.d("=>=>","Creating file...");
        objDESFireEV2.createFile(fileId,fileSettings);
        Log.d("==>","File created successfully");
    }

    public void writeDataToFile(byte[]appId,int fileId,String dataToWrite){
        objDESFireEV2.selectApplication(app0);
        authenticate(app0);
        int applicationId;
        byte [] mockApp = {(byte)0x01,(byte)0x00,(byte)0x00};
        //createApplication(mockApp);
        objDESFireEV2.selectApplication(appId);
        Key valid2k3desKey = new SecretKeySpec(DEFAULT_KEY_2KTDES,"DESede"); //>>
        //Key valid2k3desKey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY,"DESede");//<<
        KeyData valid2k3desData = new KeyData();
        valid2k3desData.setKey(valid2k3desKey);
        objDESFireEV2.authenticate(convertByteArrayToInt(appId),AuthType.Native,KeyType.THREEDES,valid2k3desData);
        Log.d("==>Auth status::","selected app authenticated with default key successfuly");
        /************************VALID CODE **********************************************************/
        /*objDESFireEV2.changeKey(0,KeyType.THREEDES,DEFAULT_KEY_2KTDES,NEW_KEY_2KTDES,(byte)0);

        //Key validfactorykey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY,"DESede");
        Key validfactorykey = new SecretKeySpec(NEW_KEY_2KTDES,"DESede");
        KeyData validfactory2k3desdata = new KeyData();
        validfactory2k3desdata.setKey(validfactorykey);
        //get integer equivalent of appId byte array
        applicationId = convertByteArrayToInt(appId);
           -select and authenticate application.
          -remember first parameter of authentication method is the decimal equivalent of the application ID's byte array

        //objDESFireEV2.selectApplication(appId);
        Log.d("=>=>","selected app exists");
        Log.d(">>>","Authenticate App before writing");
        //authenticateApp(appId);


        //get key settings
        Log.d("<<Key settings>>",objDESFireEV2.getKeySettings().toString());
        //65536
        Log.d(">>>","Keys changed successfully");
        //authenticate with new key
        objDESFireEV2.authenticate(convertByteArrayToInt(appId),AuthType.Native,KeyType.TWO_KEY_THREEDES,validfactory2k3desdata);
        Log.d("<>><>>","card authentication success");*/
        /***************************************************************************************************************************/
        //next, write data to file
        objDESFireEV2.writeData(fileId,0,dataToWrite.getBytes());
        Log.d("==>check out","Data written successfully");
    }
    public String readData(byte[]appId,int fileId,int fileSize){
        //select and authenticate application
        objDESFireEV2.selectApplication(appId);
        authenticateApp(appId);
        Log.d("==>Auth:","Auth at read data success");
        byte [] dataFromCard = objDESFireEV2.readData(fileId,0,fileSize);
        //avaliable memory in card::
        Log.d("Free memory::",Integer.toString(objDESFireEV2.getFreeMemory()));
        String name = "Jonah";
        name.length();
        return new String(dataFromCard);
        //byte[] fpByte = Base64.decode(base64Image, Base64.DEFAULT);

    }
    public int convertByteArrayToInt(byte[] byteArray){
        double doubleEquivalent = 0;
        int finalEquivalent = 0;
        String finalEquivalentString;
        if(byteArray.length>0){
            for(int i =0;i<byteArray.length;i++){
                doubleEquivalent += byteArray[i] * Math.pow(16,2*(byteArray.length-(i+1)));
                System.out.println(byteArray[i]);
            }
        }
        System.out.println(Math.round(doubleEquivalent));
        finalEquivalentString = String.valueOf(Math.round(doubleEquivalent));
        Log.d("=>=>App ID::",finalEquivalentString);
        return Integer.parseInt(finalEquivalentString);
    }
    /**** below methods implement views ********/
    public void AuthenticateView(View view){
        authenticate(app0);
        Log.d("=>MF","Master File authentication successful");
    }
    public void CreateApplicationView(View view){
        //first authenticate card with PCC level authentication
        authenticate(app0);
        //create nhif application
        createApplication(NhifCard.nhifapp);
        Log.d("=>Success","Nhif app created successfully");
    }
    public void CreateFileView(View view){
        /*create files for nhifapp
        * -two mock files will be created,biodata and name files
        * */
        //first authenticate card to PCC level
        authenticate(app0);
        Log.d("**","PCC level auth success");
        //select app
       objDESFireEV2.selectApplication(NhifCard.nhifapp);
        Log.d("**","App selected success");
        //authenticate to application level
        //authenticateApp(NhifCard.nhifapp);
       Log.d("**","App authenticated successfully");
        //create biodata file, a standardData file with size 200 bytes
        createFile(NhifCard.nhifapp,NhifCard.biodata,read_access,change_access_rights,0,NhifCard.biodata_size);
        Log.d("==>","Biodata file created successfully");
        //create name file, standardData filewith 30 bytes
        createFile(NhifCard.nhifapp,NhifCard.name,read_access,change_access_rights,0,NhifCard.name_size);
        Log.d("==>Success","Name file created successfully");
        //create file templates

    }
    public void WriteDataView(View view){
        //first authenticate the card to application level
        //authenticate(app0);
        //next select host application
        //objDESFireEV2.selectApplication(NhifCard.nhifapp);
        //mock bio-data
        String bio_data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String name = "Jonah Kiprop Kimutai Kipkeu";
        //next write bio-data  to biodata file
        writeDataToFile(NhifCard.nhifapp,NhifCard.biodata,bio_data);
        Log.d("=>success","Biodata written successfully");
        //then write name to name file
        writeDataToFile(NhifCard.nhifapp,NhifCard.name,name);
        Log.d("==>success","Name written successfully");
        //write dummy templates


    }
    public void ReadDataView(View view){
        //first, authenticate PICC to application level
        //authenticateApp(NhifCard.nhifapp);
        //next read bio-data
        Log.d("=>Read data::Biodata",readData(NhifCard.nhifapp,NhifCard.biodata,NhifCard.biodata_size));
        //finally read name file
        Log.d("==>Read data::Name",readData(NhifCard.nhifapp,NhifCard.name,NhifCard.name_size));
    }

    //below method formats the card
    public void FormatCardView(View view){
        //Key valid2k3desKey = new SecretKeySpec(NEW_KEY_2KTDES, "DESede"); //>>
        Key valid2k3desKey = new SecretKeySpec(VALID_2KTDES_FACTORY_KEY, "DESede");
        KeyData valid2k3desData = new KeyData();
        valid2k3desData.setKey(valid2k3desKey);
        Log.d("=>Formatting","About to format");
        Log.d("=>selecting MF","About to select app0");
        objDESFireEV2.selectApplication(app0);
        authenticate(app0);
        Log.d(">>>","Auth success");
        objDESFireEV2.format();
        Log.d(">>Formatting..","Card formatted successfully");
        Toast.makeText(getApplicationContext(),"Card formatted Successfully",Toast.LENGTH_SHORT).show();
    }
    public void GetFpSizeView(View view){
        String fp = "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";
        byte[] fpBytes = fp.getBytes();
        int fpSize = fpBytes.length;
        Log.d(">>FP size::",Integer.toString(fpSize));
        objDESFireEV2.getCardUID();
    }



}
