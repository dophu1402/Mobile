package com.example.connectapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.connectapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    Context context;
    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, connectionStatus;
    EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;
    boolean turn = true;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        initialWork();
        exqListener();
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ:
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
                                boolean player = (totalTurns % 2 == 0);
                                myViews[yPos * numOfRow + xPos].setOn(true, player);
                                myViews[yPos * numOfRow + xPos].invalidate();
                                turn = true;
                                totalTurns++;
                            }
                            catch (Exception e){

                            }
                        }
                    }
                    //read_msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1
                    );
                }
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionStatus.setText("Discovery Starting Failed");
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device=deviceArray[i];
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=writeMsg.getText().toString();
                sendReceive.write(msg.getBytes());
            }
        });
    }

    private void initialWork() {
        btnOnOff=(Button) findViewById(R.id.onOff);
        btnDiscover=(Button) findViewById(R.id.discover);
        btnSend=(Button) findViewById(R.id.sendButton);
        listView=(ListView) findViewById(R.id.peerListView);
        read_msg_box=(TextView) findViewById(R.id.readMsg);
        connectionStatus=(TextView) findViewById(R.id.connectionStatus);
        writeMsg=(EditText) findViewById(R.id.writeMsg);

        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=mManager.initialize(this,getMainLooper(),null);

        mReceiver=new WiFiDirectBroadcastReceiver(mManager, mChannel,this);

        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

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

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress=wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
            {
                connectionStatus.setText("Host");
                setContentView(R.layout.game_play);
                createBoard();
                serverClass=new ServerClass();
                serverClass.start();
            }else if(wifiP2pInfo.groupFormed)
            {
                connectionStatus.setText("Client");
                setContentView(R.layout.game_play);
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


    //////////////////////////////////////////////////////////////////
    private ScaleGestureDetector mScaleGestureDetector;
    int pWidth;
    int pHeight;
    int totalTurns = 0;
    int numOfCol;
    int numOfRow;
    boolean isOnline = true;

    private void createBoard(){
        myGridLayout = (GridLayout)findViewById(R.id.myGrid);
        myGridLayout.setUseDefaultMargins(false);
        myGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        myGridLayout.setRowOrderPreserved(false);
        numOfCol = myGridLayout.getColumnCount();
        numOfRow = myGridLayout.getRowCount();
        myViews = new MyView[numOfCol*numOfRow];

        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++){
                final MyView tView = new MyView(myGridLayout.getContext(), xPos, yPos);
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tView.getToggle() == false){
                            boolean player = (totalTurns % 2 == 0);
                            boolean isToggled = true;
                            tView.setOn(isToggled, player);

                            int status = checkWinner(tView.getIdX(),tView.getIdY());

                            if (status!=0){
                                resultHandler(status);
                            }
                            ////////////////////////////
                            //Send
                            ///////////////////////////
                            String msg = "1" + String.valueOf(tView.getIdX()) +","+ String.valueOf(tView.getIdY());
                            sendReceive.write(msg.toString().getBytes());
                            totalTurns++;
                            turn = false;
                        }
                    }
                });

                myViews[yPos*numOfCol + xPos] = tView;
                myGridLayout.addView(tView);
            }
        }
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


    }

    MyView[] myViews;

    GridLayout myGridLayout;

    private int checkWinner(int currentPosX, int currentPosY)
    {
        int status;
        int check;

        ChessSquare[][] myBoard = new ChessSquare[numOfRow][numOfCol];
        for(int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                myBoard[yPos][xPos] = new ChessSquare(myViews[yPos*numOfCol + xPos].getToggle(),myViews[yPos*numOfCol + xPos].getPlayer());
            }
        }

        if(myBoard[currentPosY][currentPosX].player)
        {
            status = 1;
        }
        else
        {
            status = 2;
        }

        //check horizontal
        int countLeft = 0;
        int countRight = 0;
        int posLeft = currentPosX;
        int posRight = currentPosX;
        for (int i = currentPosX - 1; i >= 0; i--) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player==myBoard[currentPosY][currentPosX].player) {
                posLeft--;
                countLeft++;
            }
            else{
                break;
            }
        }

        for (int i = currentPosX + 1; i < numOfCol; i++) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player==myBoard[currentPosY][currentPosX].player) {
                posRight++;
                countRight++;
            }
            else{
                break;
            }
        }

        check = countLeft + countRight + 1;
        if(check > 5){
            return status;
        }
        else if((countLeft + countRight + 1 == 5) && !(
                ((myBoard[currentPosY][posLeft - 1].player!=myBoard[currentPosY][currentPosX].player) && (posLeft - 1 >= 0) && (myBoard[currentPosY][posLeft - 1].touchOn))
                        && ((myBoard[currentPosY][posRight + 1].player!=myBoard[currentPosY][currentPosX].player) && (posRight + 1 < numOfCol) && (myBoard[currentPosY][posRight + 1].touchOn))
        )) {
            return status;
        }

        //check vertical
        int countTop = 0;
        int countBottom = 0;
        int posTop = currentPosX;
        int posBottom = currentPosX;
        for (int i = currentPosY - 1; i >= 0; i--) {
            if (myBoard[i][currentPosX].touchOn == true &&
                    myBoard[i][currentPosX].player==myBoard[currentPosY][currentPosX].player) {
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
                posBottom++;
                countBottom++;
            }
            else{
                break;
            }
        }

        check = countTop + countBottom + 1;
        if(check > 5){
            return status;
        }
        else if((check == 5) && !(
                ((myBoard[posTop - 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posTop - 1 >= 0) && (myBoard[posTop - 1][currentPosX].touchOn == true))
                        && ((myBoard[posBottom + 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posBottom + 1 < numOfCol) && (myBoard[posBottom + 1][currentPosX].touchOn == true))
        )) {
            return status;
        }

//        int currentPosMin = 0;
//        int currentPosMax = numOfCol;
//        if(currentPosX < currentPosY){
//            currentPosMin = currentPosX;
//            currentPosMax = currentPosY;
//        }
//        else {
//            currentPosMin = currentPosY;
//            currentPosMax = currentPosX;
//        }

        //Check Primary Diagonal
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
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY - 1 < 0) || (posAftX + 1 >= numOfCol) || (posAftY + 1 >= numOfRow)) {
                return status;
            }
            else {
                if(!(myBoard[posPrevY - 1][posPrevX - 1].touchOn) || !(myBoard[posAftY + 1][posAftX + 1].touchOn)){
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY - 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY + 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        return status;
                    }
                }
            }
        }


        //Check Sub Diagonal
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
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY + 1 >= numOfRow) || (posAftX + 1 >= numOfCol) || (posAftY - 1 < 0)) {
                return status;
            }
            else {
                if(!(myBoard[posPrevY + 1][posPrevX - 1].touchOn) || !(myBoard[posAftY - 1][posAftX + 1].touchOn)){
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY + 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY - 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        return status;
                    }
                }
            }
        }


        status = 0;
        return status;
    }

    private void resultHandler(int winner)
    {
        if(winner == 1){
            Toast.makeText(this, "P1 WIN", Toast.LENGTH_SHORT).show();
        }
        else if(winner == 2){
            Toast.makeText(this, "P2 WIN", Toast.LENGTH_SHORT).show();
        }
    }

//    private boolean isEndHorizontal(int currentPosX)
//    {
//
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
