package com.example.songchiyun.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Device list adapter.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class DeviceListAdapter extends BaseAdapter {

	BluetoothGattService mBluetoothGattService;
	BluetoothGatt mBluetoothGatt;
	BluetoothGattCallback mGattCallback;
	boolean ver = true;
	String buff1 = "";
	String buff2 = "";
	private LayoutInflater mInflater;
	private List<BluetoothDevice> mData;
	private OnPairButtonClickListener mListener;
	static final private String SERIAL_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private ExecutorService mWriteExecutor;
	private ExecutorService mReadExecutor;
	String TAG = "debug";
	private UUID mUUID = UUID.fromString(SERIAL_UUID);
	private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
	Context c;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;

	public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

	public void setData(List<BluetoothDevice> data) {
		mData = data;
	}

	public void setListener(OnPairButtonClickListener listener) {
		mListener = listener;
	}

	public int getCount() {
		return (mData == null) ? 0 : mData.size();
	}

	public Object getItem(int position) {
		return null;
	}
	public void setThis(Context c){
		this.c = c;
	}
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		mReadExecutor = Executors.newSingleThreadExecutor();
		mWriteExecutor = Executors.newSingleThreadExecutor();
		if (convertView == null) {
			convertView			=  mInflater.inflate(R.layout.list_item_device, null);

			holder 				= new ViewHolder();

			holder.nameTv		= (TextView) convertView.findViewById(R.id.tv_name);
			holder.addressTv 	= (TextView) convertView.findViewById(R.id.tv_address);
			holder.pairBtn		= (Button) convertView.findViewById(R.id.btn_pair);
			holder.connectBtn	= (Button) convertView.findViewById(R.id.connect);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BluetoothDevice device	= mData.get(position);

		holder.nameTv.setText(device.getName());
		holder.addressTv.setText(device.getAddress());
		holder.pairBtn.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair" : "Pair");
		holder.pairBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onPairButtonClick(position);
				}
			}
		});
		holder.connectBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mmSocket == null) {
					try {
						Set<BluetoothDevice> pairedDevices = BLmode.mBluetoothAdapter.getBondedDevices();
						if (pairedDevices.size() > 0) {
							for (BluetoothDevice device : pairedDevices) {
								if (device.getName().equals("ARDUINO") || device.getName().equals("Arduino")) {
									mmDevice = device;
									Log.d("debug", "check paired :" + mmDevice);
								}
							}
						}
						openBT();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Intent intent = new Intent("Setting");
						Bundle b = new Bundle();
						b.putString("Type", "ppg");
						b.putString("arduino","disconnect");
						intent.putExtras(b);
						c.sendBroadcast(intent);
						mmOutputStream.write("s".getBytes());
						mmInputStream.close();
						mmOutputStream.close();
						mmSocket.close();
						mmSocket = null;
						mmInputStream = null;
						mmOutputStream = null;
					} catch (IOException e) {
						Log.d("error", "error");
					} catch (NullPointerException e2) {
						Log.d("error", "null");
						mmSocket = null;
						mmInputStream = null;
						mmOutputStream = null;
					}

				}
			}
		});

		return convertView;

	}


	static class ViewHolder {
		TextView nameTv;
		TextView addressTv;
		TextView pairBtn;
		Button connectBtn;
	}
	void openBT() throws IOException
	{
		UUID uuid = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID

		mmSocket = mmDevice.createRfcommSocketToServiceRecord(mUUID);
		if(mmSocket!=null){
			Log.d(TAG,"socket created");
		}else{
			Log.d(TAG,"socket is null");
		}

		mmSocket.connect();

		Log.d(TAG, mmDevice.toString());
		byte[] buffer = new byte[1024];
		mmInputStream = null;
		mmOutputStream = null;
		int count = 0;
		if(mmSocket!=null){
			try {
				if(mmInputStream == null){
					mmInputStream = mmSocket.getInputStream();
				}
				else{
					Log.e(TAG, "input not null ");

				}
				if(mmOutputStream == null){
					mmOutputStream = mmSocket.getOutputStream();
				}
				else{
					Log.e(TAG, "output not null "+count);

				}

				mmOutputStream.write("c".getBytes());
				beginListenForData();

			} catch (IOException e3) {
				Log.e(TAG, "disconnected "+e3.toString());


			}

		}


	}
	void beginListenForData()
	{
		final Handler handler = new Handler(Looper.getMainLooper() );
		final byte delimiter = 8; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{

				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{

					try
					{

						int bytesAvailable = mmInputStream.available();
						if(bytesAvailable > 0) {

							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);

							for (int i = 0; i < bytesAvailable; i++) {
								byte b = packetBytes[i];

								if (readBufferPosition == 2) {
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									Log.d("result", data);
									readBufferPosition = 0;

									handler.post(new Runnable() {
										public void run() {
											Intent intent = new Intent("Setting");
											Bundle b = new Bundle();
											b.putString("Type", "ppg");
											b.putString("arduino",data);
											intent.putExtras(b);
											c.sendBroadcast(intent);
										}
									});
								} else {
									readBuffer[readBufferPosition] = b;
									readBufferPosition++;
								}

							}
						}

					}
					catch (IOException ex)
					{
						stopWorker = true;
						try {
							if(mmOutputStream != null)
								mmOutputStream.close();
						} catch (IOException e) {

						}
						try {
							if(mmInputStream != null)
								mmInputStream.close();
						} catch (IOException e) {

						}
						try {
							if(mmSocket != null)
								mmSocket.close();
						} catch (IOException e) {

						}
						mmOutputStream = null;
						mmInputStream = null;
						mmSocket = null;
					}

				}


			}
		});

		workerThread.start();
	}


	public interface OnPairButtonClickListener {
		public abstract void onPairButtonClick(int position);
	}
}