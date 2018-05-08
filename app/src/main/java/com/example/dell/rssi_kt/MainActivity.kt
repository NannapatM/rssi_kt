package com.example.dell.rssi_kt

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import org.w3c.dom.Text

import java.util.ArrayList
import java.util.Date
import java.util.concurrent.Delayed

class MainActivity : AppCompatActivity() {

    private var nets: Array<Element>? = null
    private var wifiManager: WifiManager? = null
    private var wifiList: List<ScanResult>? = null
    //internal var dialog: Dialog
    internal var list: MutableList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            detectWiFi()
            Snackbar.make(view, "Scanning....", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }
    }

    /*public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            mWifiListener.getScanningResults();
        }
    }*/

    fun detectWiFi() {
        //TODO: Permission!!!
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            //getScanningResults();
            //do something, permission was previously granted; or legacy device

            this.wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            this.wifiManager.startScan();
            this.wifiList = this.wifiManager.getScanResults();
        }*/
        this.wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        this.wifiManager!!.startScan()
        this.wifiList = this.wifiManager!!.scanResults

        Log.d("TAG", wifiList!!.toString())

        this.nets = arrayOfNulls<Element>(wifiList!!.size)
        for (i in wifiList!!.indices) {
            val item = wifiList!![i].toString()
            val vector_item = item.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val item_ssid = vector_item[0]
            val item_capabilities = vector_item[2]
            val item_level = vector_item[3]
            val ssid = item_ssid.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val security = item_capabilities.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val level = item_level.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            nets[i] = Element(ssid, security, level)
        }

        val adapterElements = AdapterElements(this)
        val netlist = findViewById(R.id.listItem) as ListView
        val info = findViewById(R.id.info) as TextView
        netlist.adapter = adapterElements

        //  Toast.makeText(MainActivity.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
        netlist.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //  Toast.makeText(MainActivity.this,"Send",Toast.LENGTH_SHORT).show();
            var c = 0
            var k: Int
            val name = nets!![i].getTitle()
            //  list.add("abc");

            //   Toast.makeText(getApplicationContext(), "This is my Toast message!", Toast.LENGTH_SHORT).show();
            while (c < 10) {
                k = 0
                while (k < wifiList!!.size && name.compareTo(nets!![k].getTitle()) != 0) {
                    k++
                }

                list.add(nets!![k].getLevel())
                //   String name = nets[i].getTitle();

                Log.d("round", "roundc" + c + nets!![k].getTitle() + nets!![k].getLevel())
                c++
                SystemClock.sleep(2000)
                // Toast.makeText(getApplicationContext(), "This is my Toast message!", Toast.LENGTH_SHORT).show();
                detectWiFi()
                //  Toast.makeText(MainActivity.this,"Send", Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(MainActivity.this,"Send", Toast.LENGTH_SHORT).show();

            for (z in list.indices) {

                Log.d("melist", list[z])
            }
         /*   val move = Intent(this@MainActivity, Main2Activity::class.java)
            move.putExtra("demo", "demo")
            startActivity(move)*/
            list.clear()
        }


    }

    /*fun move(view: View) {
        val move = Intent(this@MainActivity, Main2Activity::class.java)

        move.putExtra("demo", "demo")
        startActivity(move)
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    internal inner class AdapterElements(var context: Activity) : ArrayAdapter<Any>(context, R.layout.items, nets) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.layoutInflater
            val item = inflater.inflate(R.layout.items, null)

            val tvSsid = item.findViewById(R.id.tvSSID) as TextView
            tvSsid.setText(nets!![position].getTitle())

            val tvSecurity = item.findViewById(R.id.tvSecurity) as TextView
            tvSecurity.setText(nets!![position].getSecurity())

            val tvLevel = item.findViewById(R.id.tvLevel) as TextView
            tvLevel.text = "Signal Level: " + nets!![position].getLevel()
            //String level = nets[position].getLevel();

            /* try{
                int i = Integer .parseInt(level);
                if (i>-50){
                    tvLevel.setText("High");
                }else if(i<=-50 && i>=-80){
                    tvLevel.setText("Average");
                }else if (i<=-80){
                    tvLevel.setText("Low");
                }
            } catch(NumberFormatException e){
                Log.d("TAG","Incorrect Format Line");
            }*/
            return item
        }
    }

 /*   fun showdialow(ten_Wifi: String) {
        dialog = Dialog(this@MainActivity)
        dialog.setTitle("Enter Wifi Password")
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_layout)

        val btn_DongY = dialog.findViewById(R.id.btn_dongy) as Button
        val btn_Huy = dialog.findViewById(R.id.btn_huy) as Button
        val edt_Password = dialog.findViewById(R.id.edt_password) as EditText

        val checkBox = dialog.findViewById(R.id.cb_show) as CheckBox
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                edt_Password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                edt_Password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        btn_DongY.setOnClickListener {
            val matkhau = edt_Password.text.toString()

            if (TextUtils.isEmpty(matkhau)) {
                edt_Password.error = "No password Yet"
            } else {
                //Toast.makeText(MainActivity.this, "Name of wifi: " + ten_wifi + " Password " + matkhau, Toast.LENGTH_SHORT).show();
                //  dialog.dismiss();

            }
        }
        btn_Huy.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }*/

    private fun connectToWifi(networkSSID: String, networkPassword: String) {
        if (!wifiManager!!.isWifiEnabled) {
            wifiManager!!.isWifiEnabled = true
        }
        val conf = WifiConfiguration()
        conf.SSID = String.format("\"%s\"", networkSSID)
        conf.preSharedKey = String.format("\"%s\"", networkPassword)
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        //Toast.makeText(MainActivity.this, "Name of wifi: " + networkSSID + " Password " + networkPassword, Toast.LENGTH_SHORT).show();


        val netId = wifiManager.addNetwork(conf)

        val list = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                wifiManager.reconnect()

                break
            }
        }

        /*wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();*/
    }
}