package com.example.songchiyun.myapplication;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Device list.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class DeviceListActivity extends Activity {
	private ListView mListView;
	private DeviceListAdapter mAdapter;
	private ArrayList<BluetoothDevice> mDeviceList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_paired_devices);
		
		mDeviceList		= getIntent().getExtras().getParcelableArrayList("device.list");
		
		mListView		= (ListView) findViewById(R.id.lv_paired);
		
		mAdapter		= new DeviceListAdapter(this);
		mAdapter.setThis(this);
		
		mAdapter.setData(mDeviceList);
		
		/*mScanBtn.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				//mBluetoothAdapter.startDiscovery();
			}
		});*/
		
		mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {			
			@Override
			public void onPairButtonClick(int position) {
				BluetoothDevice device = mDeviceList.get(position);
				
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					unpairDevice(device);
				} else {
					showToast("Pairing...");
					
					pairDevice(device);
				}
			}
		});
		
		/*mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {			
			@Override
			public void onPairButtonClick(int position) {
				BluetoothDevice device = mDeviceList.get(position);
				
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					unpairDevice(device);
				} else {
					showToast("Pairing...");
					
					pairDevice(device);
				}
			}
		});*/
		
		mListView.setAdapter(mAdapter);
		
		registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
	}
	
    // start game1
    public void start_v_eeg_f1(View view) {
			showToast("SINGULARITY!!!!...");   	
			BluetoothDevice device = mDeviceList.get(0);
			if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
				unpairDevice(device);
			} else {
				showToast("Pairing...");
				
				pairDevice(device);
			}
			
    }
	
    
	@Override
	public void onDestroy() {
		unregisterReceiver(mPairReceiver);
		
		super.onDestroy();
	}
	
	
	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        
	        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
	        	 final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
	        	 final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
	        	 
	        	 if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
	        		 showToast("Paired");
	        	 } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
	        		 showToast("Unpaired");
	        	 }
	        	 
	        	 mAdapter.notifyDataSetChanged();
	        }
	    }
	};
}