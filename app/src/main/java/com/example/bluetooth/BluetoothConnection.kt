package com.example.bluetooth

import android.content.ContentValues
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.widget.TextView
import com.example.bluetooth.databinding.ActivityMainBinding
import java.lang.Exception

class BluetoothChangeIconThread(val mainActivity:  MainActivity):Thread() {
    override fun run() {
        // VERSION 1b
        // Running a task in a separate thread
        // update text from separate thread - can you?
        val thread = Thread {
            Log.i(ContentValues.TAG, "Runnable Started")
            try {
                while(true){
                    mainActivity.bAdapter = BluetoothAdapter.getDefaultAdapter()
                    print(mainActivity.bAdapter.isEnabled)
                    if (mainActivity.bAdapter==null)
                    {
                        mainActivity.runOnUiThread { mainActivity.binding.result.text = "Bluetooth is not available" }
                    }
                    else
                    {
                        mainActivity.runOnUiThread { mainActivity.binding.result.text = "Bluetooth is available" }

                        if (mainActivity.bAdapter.isEnabled)
                        {
                            mainActivity.runOnUiThread { mainActivity.binding.result.text = "Turn on" }
                            mainActivity.runOnUiThread { mainActivity.binding.imageView.setImageResource(R.drawable.ic_bluetooth_on)}
                        }
                        else
                        {
                            mainActivity.runOnUiThread { mainActivity.binding.result.text = "Turn off" }
                            mainActivity.runOnUiThread { mainActivity.binding.imageView.setImageResource(R.drawable.ic_bluetooth_off)}
                        }
                    }


                    Thread.sleep(2000)
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Log.i(ContentValues.TAG, "Runnable Finished")
        }
        thread.start()
    }
}