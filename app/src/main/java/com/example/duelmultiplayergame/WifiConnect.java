package com.example.duelmultiplayergame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.media.Image;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.duelmultiplayergame.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WifiConnect extends Activity implements View.OnClickListener {

    Context context;
    Button btnOnOff, btnDiscover, btnSend, btnChat;
    ListView listView;
    TextView read_msg_box, connectionStatus, myChat, opponentChat;
    EditText writeMsg, chatMsg;

    LinearLayout messchat;

    LinearLayout redSurrenderBtn, blueSurrenderBtn, redBackwardBtn, blueBackwardBtn;
    RelativeLayout BackgroundGame;

    Boolean identityPlayer;
    HistoryPlay historyPlay;

    boolean onlinePlayer; //true: p1, false: p2

    boolean restartGame;
    boolean restartGameTouched;
    String restartOnlineStatus;

    private int shortAnimationDuration;
    LinearLayout endGameLinear;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    
    MyView[] myViews;
    GridLayout myGridLayout;
    
    ImageView endGameImg;
    TextView endGameTextView;
    LinearLayout endGameSubLinear;
    ImageButton endGameBtnYes;
    ImageButton endGameBtnNo;

    List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;
    boolean turn = true; //kiem tra luot choi
    boolean isOnline; // online mode
    ChessSquare currentSquare;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isOnline = extras.getBoolean("IS_ONLINE"); //kiem tra che do online hay offline
        }
        
        if (isOnline) {
            setContentView(R.layout.activity_wifi_connect);
            context = this;
            initialWork(); // ham khoi tao bien
            exqListener();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1
                );
            }
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess() {
                    connectionStatus.setText("Finding Opponents");
                }

                @Override
                public void onFailure(int i) {
                    connectionStatus.setText("Finding Failed");
                }
            });
        } else {
            setContentView(R.layout.activity_game_play);
            initialWork(); //khoi tao cac bien
            createBoard(); // tao ban co
        }
    }
    ///Massage handler
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ: //flag = 1: danh co, flag = 2: chat, flag = 3: ket noi status
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if (tempMsg.length() > 1){
                        if (tempMsg.charAt(0) == '1'){
                            tempMsg = tempMsg.substring(1);
                            String[] pos = tempMsg.split(",",2);
                            if(pos.length<2){
                                break;
                            }
                            try {
                                int xPos = Integer.valueOf(pos[0]);
                                int yPos = Integer.valueOf(pos[1]);
                                int player;
                                if (totalTurns % 2 == 0) {
                                    player = 1;
                                }
                                else {
                                    player = 2;
                                }
                                myViews[yPos * numOfRow + xPos].setOn(true, player);
                                myViews[yPos * numOfRow + xPos].invalidate();
                                turn = true;
                                if(currentSquare != null) {
                                    myViews[currentSquare.idY*numOfCol + currentSquare.idX].setOffClicking();
                                }
                                currentSquare = new ChessSquare(true, player, myViews[yPos * numOfRow + xPos].getIdX(),myViews[yPos * numOfRow + xPos].getIdY());

                                totalTurns++;
                                int status = checkWinner(myViews[yPos * numOfRow + xPos].getIdX(),myViews[yPos * numOfRow + xPos].getIdY());

                                if (status!=0){
                                    resultHandler(status);
                                    if (status == Constant_Player.RED.getValue()){  // the winner is red, red will start firstly
                                        identityPlayer = true;
                                    }
                                    else if (status == Constant_Player.BLUE.getValue()){    // otherwise
                                        identityPlayer = false;
                                    }
                                }
                            }
                            catch (Exception e){

                            }
                        }
                        else if (tempMsg.charAt(0) == '2') {
                            tempMsg = tempMsg.substring(1);
                            if (tempMsg.length() < 1) {
                                break;
                            }
                            opponentChat.setText(tempMsg);
                        }
                        ////////////////////////////flag
                        else if (tempMsg.charAt(0) == '3')
                        {
                            tempMsg = tempMsg.substring(1);
                            if (tempMsg.length() < 1) {
                                break;
                            }
                            restartOnlineStatus = tempMsg;
                            if (restartOnlineStatus == "yes"){
                                if(restartGameTouched) {
                                    if(restartGame){
                                        setResetGame();
                                        endGameLinear.setVisibility(View.GONE);
                                    }
                                }
                            }
                            else if (restartOnlineStatus == "no"){
                                Toast.makeText(context,"Opponent disconnected", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(WifiConnect.this, MenuNavigationActivity.class));
                                finish();
                            }



                        }
                    }
                    //read_msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });
    //setup connect
    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifiManager.isWifiEnabled())
                {
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("ON");
                }else {
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1
                    );
                }
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Finding Opponents");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionStatus.setText("Finding Failed");
                    }
                });
            }
        });
        //click vao 1 thiet bi tren list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device=deviceArray[i];
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1
                    );
                }
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to "+device.deviceName,Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        ////phan nay bo
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=writeMsg.getText().toString();
                sendReceive.write(msg.getBytes());
            }
        });
    }
    // khoi tao bien
    private void initialWork() {
        this.historyPlay = new HistoryPlay();
        this.identityPlayer = (Constant_Player.RED.getValue() == Constant_Player.RED.getValue());   // emulator that we start with red


        restartGameTouched = false;
        restartOnlineStatus = "";
        btnOnOff=(Button) findViewById(R.id.onOff);
        btnDiscover=(Button) findViewById(R.id.discover);
        btnSend=(Button) findViewById(R.id.sendButton);
        listView=(ListView) findViewById(R.id.peerListView);
        read_msg_box=(TextView) findViewById(R.id.readMsg);
        connectionStatus=(TextView) findViewById(R.id.connectionStatus);
        writeMsg=(EditText) findViewById(R.id.writeMsg);

        endGameLinear=(LinearLayout) findViewById(R.id.endGameLayout);
        endGameImg=(ImageView) findViewById(R.id.endGameImg);
        endGameTextView=(TextView) findViewById(R.id.endGameTextView);
        endGameSubLinear=(LinearLayout) findViewById(R.id.endGameSubLayout);
        endGameBtnYes=(ImageButton) findViewById(R.id.yesBtn);
        endGameBtnNo=(ImageButton) findViewById(R.id.noBtn);

        redSurrenderBtn = (LinearLayout)this.findViewById(R.id.redSurrenderBtn);
        redBackwardBtn = (LinearLayout) this.findViewById(R.id.redBackwardBtn);
        blueSurrenderBtn = (LinearLayout)this.findViewById(R.id.blueSurrenderBtn);
        blueBackwardBtn = (LinearLayout) this.findViewById(R.id.blueBackwardBtn);

        BackgroundGame = (RelativeLayout) this.findViewById(R.id.BackgroundGame);
        this.setupBackgroundColor(this.identityPlayer?Constant_Player.RED.getValue():Constant_Player.BLUE.getValue());

        redSurrenderBtn.setOnClickListener(this);
        redBackwardBtn.setOnClickListener(this);
        blueSurrenderBtn.setOnClickListener(this);
        blueBackwardBtn.setOnClickListener(this);

        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=mManager.initialize(this,getMainLooper(),null);

        mReceiver=new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void setupBackgroundColor(int typePlayer){
        if (typePlayer == Constant_Player.RED.getValue()){
            this.BackgroundGame.setBackgroundColor(Color.argb(255,252, 62, 48));
        }
        else if (typePlayer == Constant_Player.BLUE.getValue()){
            this.BackgroundGame.setBackgroundColor(Color.argb(255,48, 150, 252));
        }
    }

    // list thiet bi duoc tim thay
    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers))
            {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray=new String[peerList.getDeviceList().size()];
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                int index=0;

                for(WifiP2pDevice device : peerList.getDeviceList())
                {
                    deviceNameArray[index]=device.deviceName;
                    deviceArray[index]=device;
                    index++;
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                listView.setAdapter(adapter);
            }

            if(peers.size()==0)
            {
                Toast.makeText(getApplicationContext(),"No Device Found",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
    //Xu ly khi thiet lap ket noi
    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress=wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
            {
                connectionStatus.setText("Host");
                setContentView(R.layout.activity_game_play);
                createBoard();
                serverClass=new ServerClass();
                serverClass.start();
            }else if(wifiP2pInfo.groupFormed)
            {
                connectionStatus.setText("Client");
                setContentView(R.layout.activity_game_play);
                createBoard();
                clientClass=new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        final int idRedSurrenderBtn = R.id.redSurrenderBtn;
        final int idBlueSurrenderBtn = R.id.blueSurrenderBtn;
        final int idRedBackwardBtn = R.id.redBackwardBtn;
        final int idBlueBackwardBtn = R.id.blueBackwardBtn;
        switch (v.getId()){
            case idRedSurrenderBtn:
                this.resultHandler(Constant_Player.RED.getValue());
                break;
            case idBlueSurrenderBtn:
                this.resultHandler(Constant_Player.BLUE.getValue());
                break;
            case idRedBackwardBtn:
                /* is in blue turn: person who start is blue and turn is even and otherwise ==> red want to take cell again*/
                if ((!this.identityPlayer && (this.totalTurns % 2 == 0)) || (this.identityPlayer && (this.totalTurns % 2 == 1))){
//                    Log.d("CheckBackward", "onClick: Blue");
                    Cell cell = this.historyPlay.getLastItem();

                    if (cell != null){
                        myViews[cell.getY() * numOfRow + cell.getX()].reset();
                        this.historyPlay.pop();
                        totalTurns -= 1;

                        /* change to red turn */
                        this.setupBackgroundColor(Constant_Player.RED.getValue());
                    }
                }
                break;
            case idBlueBackwardBtn:
                /* is in red turn: person who start is red and turn is even and otherwise ==> Blue want to take cell again*/
                if ((this.identityPlayer && (this.totalTurns % 2 == 0)) || (!this.identityPlayer && (this.totalTurns % 2 == 1))){
//                    Log.d("CheckBackward", "onClick: Red");
                    Cell cell = this.historyPlay.getLastItem();

                    if (cell != null){
                        myViews[cell.getY() * numOfRow + cell.getX()].reset();
                        this.historyPlay.pop();
                        totalTurns -= 1;

                        /* change to red turn */
                        this.setupBackgroundColor(Constant_Player.BLUE.getValue());
                    }
                }
                break;
        }
    }
    ////////////////////////////
    ///Socket - server, client
    ////////////////////////////
    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket=new ServerSocket(8888);
                socket=serverSocket.accept();
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt)
        {
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];
            int bytes;

            while (socket!=null)
            {
                try {
                    bytes=inputStream.read(buffer);
                    if(bytes>0)
                    {
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes)
        {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public  ClientClass(InetAddress hostAddress)
        {
            hostAdd=hostAddress.getHostAddress();
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //////////////////////////////////////////////////////
    private void setResetGame()
    {
        totalTurns = 0;
        restartGameTouched = false;
        restartOnlineStatus = "";
        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++) {
                myViews[yPos*numOfRow + xPos].reset();
            }
        }
    }

    private  void restartHandler()
    {
        this.setupBackgroundColor(this.identityPlayer?Constant_Player.RED.getValue():Constant_Player.BLUE.getValue());

        ////////flag
        if (isOnline == true) {
            if(restartOnlineStatus.length()!=0) {
                if(restartGame && restartOnlineStatus == "yes"){
                    setResetGame();
                    endGameLinear.setVisibility(View.GONE);
                }
                else if (restartGame && restartOnlineStatus == "no"){
                    Toast.makeText(context,"Opponent disconnected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WifiConnect.this, MenuNavigationActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(WifiConnect.this, MenuNavigationActivity.class));
                    finish();
                }
            }
            else{
                if(restartGame){
                    String msg = "3yes";
                    sendReceive.write(msg.toString().getBytes());
                    endGameTextView.setText("Waiting for opponent");
                    endGameBtnYes.setVisibility(View.GONE);
                    endGameBtnNo.setVisibility(View.GONE);
                    endGameImg.setVisibility(View.GONE);
                }
                else{
                    String msg = "3no";
                    sendReceive.write(msg.toString().getBytes());
                    startActivity(new Intent(WifiConnect.this, MenuNavigationActivity.class));
                    finish();
                }
            }

        }
        else {
            if(restartGame){
                setResetGame();
                endGameLinear.setVisibility(View.GONE);
            }
            else{
                startActivity(new Intent(WifiConnect.this, MenuNavigationActivity.class));
                finish();
            }
        }

    }

    //////////////////////////////////////////////////////////////////
    private ScaleGestureDetector mScaleGestureDetector; //phat hien scale
    int pWidth;
    int pHeight;
    int totalTurns = 0;
    int numOfCol;
    int numOfRow;
    
    /////////////////
    /// xu ly ban co (ve, setonlick)
    /////////////////
    private void createBoard(){
        myGridLayout = (GridLayout)findViewById(R.id.myGrid);
        myGridLayout.setUseDefaultMargins(false);
        myGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        myGridLayout.setRowOrderPreserved(false);
        numOfCol = myGridLayout.getColumnCount();
        numOfRow = myGridLayout.getRowCount();
        myViews = new MyView[numOfCol*numOfRow];

        myChat = (TextView) findViewById(R.id.my_chat);
        opponentChat = (TextView) findViewById(R.id.opponent_chat);
        chatMsg = (EditText) findViewById(R.id.chatMsg);
        btnChat = (Button) findViewById(R.id.chatButton);
        messchat = (LinearLayout) this.findViewById(R.id.messchat);
        ////Ve view cua tung che do online hay offline
        if (isOnline == true) {
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myChat.setText(chatMsg.getText());
                    String msg = "2" + chatMsg.getText().toString();
                    sendReceive.write(msg.getBytes());
                    chatMsg.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
        else{
            myChat.setVisibility(View.INVISIBLE);
            opponentChat.setVisibility(View.INVISIBLE);
            chatMsg.setVisibility(View.INVISIBLE);
            btnChat.setVisibility(View.INVISIBLE);
            messchat.setVisibility(View.GONE);
        }
        /// tao ban co kich thuoc = numOfRow * numOfCol
        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++){
                final MyView tView = new MyView(myGridLayout.getContext(), xPos, yPos);// 1 o tren ban co
                final int x = xPos;
                final int y = yPos;
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tView.getToggle() == false && turn == true){
                            int player; //  nhan dien player
                            int firstPlayer = (identityPlayer?1:0) * Constant_Player.RED.getValue() + (identityPlayer?0:1) * Constant_Player.BLUE.getValue();
                            int secondPlayer = (identityPlayer?1:0) * Constant_Player.BLUE.getValue() + (identityPlayer?0:1) * Constant_Player.RED.getValue();
                            if (totalTurns % 2 == 0) {
//                                player = 1;
                                /* identityPlayer presents for who start the game*/
                                /* true means red and false means blue*/
                                player = firstPlayer;
                                setupBackgroundColor(secondPlayer);
                            }
                            else {
//                                player = 2;
                                player = secondPlayer;
                                setupBackgroundColor(firstPlayer);
                            }

                            boolean isToggled = true;
                            historyPlay.add(new Cell(x, y, player));
//                            historyPlay.showList();
                            if(currentSquare != null) {
                                myViews[currentSquare.idY*numOfCol + currentSquare.idX].setOffClicking();
                            }
                            currentSquare = new ChessSquare(isToggled, player, tView.getIdX(),tView.getIdY());
                            tView.setOn(isToggled, player);

                            int status = checkWinner(tView.getIdX(),tView.getIdY());

                            if (status!=0){
                                resultHandler(status);
                                if (status == Constant_Player.RED.getValue()){  // the winner is red, red will start firstly
                                    identityPlayer = true;
                                }
                                else if (status == Constant_Player.BLUE.getValue()){    // otherwise
                                    identityPlayer = false;
                                }
                            }

                            ////////////////////////////
                            //Send
                            ///////////////////////////
                            if (isOnline == true) {
                                String msg = "1" + String.valueOf(tView.getIdX()) + "," + String.valueOf(tView.getIdY());
                                sendReceive.write(msg.toString().getBytes());
                                turn = false; //doi luot choi
                            }

                            if(totalTurns == 0) {
                                onlinePlayer = true;
                            }
                            else if(totalTurns == 1) {
                                onlinePlayer = false;
                            }
                            totalTurns++;
                            myGridLayout.invalidate();
                        }
                    }
                });

                myViews[yPos*numOfCol + xPos] = tView;
                myGridLayout.addView(tView);
            }
        }
        
        // Ve ban co
        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    public void onGlobalLayout() {

                        final int MARGIN = 1;

                        pWidth = myGridLayout.getWidth();
                        pHeight = myGridLayout.getHeight();

                        int numOfCol = myGridLayout.getColumnCount();
                        int numOfRow = myGridLayout.getRowCount();
                        int w = pWidth/numOfCol;
                        int h = pHeight/numOfRow;

                        for(int yPos=0; yPos<numOfRow; yPos++){
                            for(int xPos=0; xPos<numOfCol; xPos++){
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams)myViews[yPos*numOfCol + xPos].getLayoutParams();

                                params.width = pWidth/numOfCol;
                                params.height = pWidth/numOfRow;
                                myViews[yPos*numOfCol + xPos].setLayoutParams(params);
                            }
                        }
                    }
                }
        );

        endGameBtnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restartGame = true;
                restartGameTouched = true;
                restartHandler();
            }
        });

        endGameBtnNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restartGame = false;
                restartGameTouched = true;
                restartHandler();
            }
        });

    }

    private int checkWinner(int currentPosX, int currentPosY)
    {
        int status = 0;
        int check;

        //Create check board
        ArrayList<ChessSquare> tempSquares;
        ChessSquare[][] myBoard = new ChessSquare[numOfRow][numOfCol];
        for(int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                myBoard[yPos][xPos] = new ChessSquare(myViews[yPos*numOfCol + xPos].getToggle(),myViews[yPos*numOfCol + xPos].getPlayer(), xPos, yPos);
            }
        }

        /////////////////////////
        //set player to check
        if(myBoard[currentPosY][currentPosX].player == 1)
        {
            status = 1;
        }
        else if (myBoard[currentPosY][currentPosX].player == 2)
        {
            status = 2;
        }

        ////////////////////////////////////
        //check horizontal

        tempSquares = new ArrayList<ChessSquare>();
        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countLeft = 0;
        int countRight = 0;
        int posLeft = currentPosX;
        int posRight = currentPosX;

        for (int i = currentPosX - 1; i >= 0; i--) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[currentPosY][i]);
                posLeft--;
                countLeft++;
            }
            else{
                break;
            }
        }

        for (int i = currentPosX + 1; i < numOfCol; i++) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[currentPosY][i]);
                posRight++;
                countRight++;
            }
            else{
                break;
            }
        }

        check = countLeft + countRight + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if((countLeft + countRight + 1 == 5) && !(
                ((myBoard[currentPosY][posLeft - 1].player!=myBoard[currentPosY][currentPosX].player) && (posLeft - 1 >= 0) && (myBoard[currentPosY][posLeft - 1].touchOn))
                        && ((myBoard[currentPosY][posRight + 1].player!=myBoard[currentPosY][currentPosX].player) && (posRight + 1 < numOfCol) && (myBoard[currentPosY][posRight + 1].touchOn))
        )) {
            setWinnerSquares(tempSquares);
            return status;
        }
        tempSquares.clear();

        ////////////////////////
        //check vertical

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countTop = 0;
        int countBottom = 0;
        int posTop = currentPosY;
        int posBottom = currentPosY;

        for (int i = currentPosY - 1; i >= 0; i--) {
            if (myBoard[i][currentPosX].touchOn == true &&
                    myBoard[i][currentPosX].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[i][currentPosX]);
                posTop--;
                countTop++;
            }
            else{
                break;
            }
        }

        for (int i = currentPosY + 1; i < numOfRow; i++) {
            if (myBoard[i][currentPosX].touchOn == true &&
                    myBoard[i][currentPosX].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[i][currentPosX]);
                posBottom++;
                countBottom++;
            }
            else{
                break;
            }
        }

        check = countTop + countBottom + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if((check == 5) && !(
                ((myBoard[posTop - 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posTop - 1 >= 0) && (myBoard[posTop - 1][currentPosX].touchOn))
                        && ((myBoard[posBottom + 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posBottom + 1 < numOfCol) && (myBoard[posBottom + 1][currentPosX].touchOn))
        )) {
            setWinnerSquares(tempSquares);
            return status;
        }

        tempSquares.clear();

        ///////////////////////////////
        //Check Primary Diagonal

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countPriPrev = 0;
        int countPriAft = 0;
        int posPrevX = currentPosX;
        int posPrevY = currentPosY;
        int posAftX = currentPosX;
        int posAftY = currentPosY;


        for (int i = numOfRow - 1; i >= 0; i--) {
            if((posPrevX - 1 < 0) || (posPrevY - 1 < 0)) {
                break;
            }
            if (myBoard[posPrevY - 1][posPrevX - 1].touchOn == true &&
                    myBoard[posPrevY - 1][posPrevX - 1].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posPrevY - 1][posPrevX - 1]);
                posPrevX--;
                posPrevY--;
                countPriPrev++;
            }
            else{
                break;
            }
        }

        for (int i = 0; i < numOfRow; i++) {
            if((posAftX + 1 >= numOfCol) || (posAftY + 1 >= numOfRow)) {
                break;
            }
            if (myBoard[posAftY + 1][posAftX + 1].touchOn == true &&
                    myBoard[posAftY + 1][posAftX + 1].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posAftY + 1][posAftX + 1]);
                posAftX++;
                posAftY++;
                countPriAft++;
            }
            else{
                break;
            }
        }

        check = countPriPrev + countPriAft + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY - 1 < 0) || (posAftX + 1 >= numOfCol) || (posAftY + 1 >= numOfRow)) {
                setWinnerSquares(tempSquares);
                return status;
            }
            else {
                if(!(myBoard[posPrevY - 1][posPrevX - 1].touchOn) || !(myBoard[posAftY + 1][posAftX + 1].touchOn)){
                    setWinnerSquares(tempSquares);
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY - 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY + 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        setWinnerSquares(tempSquares);
                        return status;
                    }
                }
            }
        }
        tempSquares.clear();

        /////////////////////////////////
        //Check Sub Diagonal

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countSubPrev = 0;
        int countSubAft = 0;
        posPrevX = currentPosX;
        posPrevY = currentPosY;
        posAftX = currentPosX;
        posAftY = currentPosY;


        for (int i = numOfRow; i >= 0; i--) {
            if((posPrevX - 1 < 0) || (posPrevY + 1 >= numOfRow)) {
                break;
            }
            if (myBoard[posPrevY + 1][posPrevX - 1].touchOn == true &&
                    myBoard[posPrevY + 1][posPrevX - 1].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posPrevY + 1][posPrevX - 1]);
                posPrevY++;
                posPrevX--;
                countSubPrev++;
            }
            else{
                break;
            }
        }

        for (int i = 0; i < numOfRow; i++) {
            if((posAftX + 1 >= numOfCol) || (posAftY - 1 < 0)) {
                break;
            }
            if (myBoard[posAftY - 1][posAftX + 1].touchOn == true &&
                    myBoard[posAftY - 1][posAftX + 1].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posAftY - 1][posAftX + 1]);
                posAftY--;
                posAftX++;
                countSubAft++;
            }
            else{
                break;
            }
        }

        check = countSubPrev + countSubAft + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY + 1 >= numOfRow) || (posAftX + 1 >= numOfCol) || (posAftY - 1 < 0)) {
                setWinnerSquares(tempSquares);
                return status;
            }
            else {
                if(!(myBoard[posPrevY + 1][posPrevX - 1].touchOn) || !(myBoard[posAftY - 1][posAftX + 1].touchOn)){
                    setWinnerSquares(tempSquares);
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY + 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY - 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        setWinnerSquares(tempSquares);
                        return status;
                    }
                }
            }
        }

        tempSquares.clear();


        status = 0;
        return status;
    }

    private void setWinnerSquares(List<ChessSquare> tempSquares)
    {
        for(int i = 0; i < tempSquares.size(); i++) {
            ChessSquare temp = tempSquares.get(i);
            myViews[temp.getIdY()*numOfCol + temp.getIdX()].setWinnerOn();
        }
    }

    private void resultHandler(int winner)
    {
        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++) {
                myViews[yPos*numOfRow + xPos].setTouched();
            }
        }

        endGameTextView.setText("(Do you want to play again?)");
        endGameBtnYes.setVisibility(View.VISIBLE);
        endGameBtnNo.setVisibility(View.VISIBLE);
        endGameImg.setVisibility(View.VISIBLE);

        endGameLinear.setAlpha(0f);
        endGameLinear.setVisibility(View.VISIBLE);

        endGameLinear.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        if (isOnline){
            if(winner == 1 && onlinePlayer == true) {
                endGameImg.setImageResource(R.drawable.youwin);
            }
            else if(winner == 1 && onlinePlayer == false) {
                endGameImg.setImageResource(R.drawable.youlose);
            }
            else if(winner == 2 && onlinePlayer == true) {
                endGameImg.setImageResource(R.drawable.youlose);
            }
            else if(winner == 2 && onlinePlayer == false) {
                endGameImg.setImageResource(R.drawable.youwin);
            }
        }
        else{
            if(winner == 1){
                endGameImg.setImageResource(R.drawable.redwin);
            }
            else if(winner == 2){
                endGameImg.setImageResource(R.drawable.bluewin);
            }
        }

    }

//    private boolean isEndHorizontal(int currentPosX)
//    {
//
//    }

}
