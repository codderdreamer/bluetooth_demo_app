package com.example.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bluetooth.databinding.ActivityMainBinding
import java.util.UUID
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    // bluetooth adapter
    lateinit var  bAdapter:BluetoothAdapter
    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private val REQUEST_PERMISSION_CODE = 1
    val uuid: UUID = UUID.fromString("63DEF7AB-8BEB-6272-6430-B649F5E5CCEA") // RFCOMM UUID
    val iphone_uuid: UUID =  UUID.fromString("28FFECEA-665D-4289-9D90-584E939CBD2A") // RFCOMM UUID
    private lateinit var result: TextView

    var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*
        Uygulama ilk başladığında bluetoothun kontrol edilip icon'un durumu değiştirilir.
        Uygulama sırasında sürekli blueetooth'un açıp olup olmadığını 2 saniyede bir kontrol eder.
        */
        val changeIconThread = BluetoothChangeIconThread(this)
        changeIconThread.start()


        /*
        Bluetooth Adapter'ı al
         */
        bAdapter = BluetoothAdapter.getDefaultAdapter()

        /*
        Bluetooth açmak için butonun görevi
         */
        binding.turnOn.setOnClickListener{
            if(bAdapter.isEnabled){
                Toast.makeText(this,"Bluetooth zaten açık!",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Açılıyor",Toast.LENGTH_LONG).show()
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //resultLauncher.launch(intent)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT);
            }
        }

        /*
        Bluetooth'u kapatmak için butonun görevi
        Kapatma tam olarak çalışmıyor incelenecek sonrasında!
         */
        binding.turnOff.setOnClickListener{
            try {
                if(!bAdapter.isEnabled){
                    Toast.makeText(this,"Bluetooth zaten kapalı!",Toast.LENGTH_LONG).show()
                }else{
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        print("ActivityCompat#requestPermissions!!!")
                    }
                    bAdapter.disable()
                    binding.imageView.setImageResource(R.drawable.ic_bluetooth_off)
                    Toast.makeText(this,"Bluetooth turned off", Toast.LENGTH_LONG).show()

                }
            }catch (e: InterruptedException){
                e.printStackTrace()
            }
        }

        /*
        Daha önce eşleşmiş olan cihaz listesini döner
         */
        binding.pairedDevicesButton.setOnClickListener{
            if(!bAdapter.bondedDevices.isEmpty())
            {
                for(device: BluetoothDevice in bAdapter.bondedDevices) {
                    println("\n")
                    println(device.address)
                    println(device.name)

                    val deviceUUIDs = device.uuids
                    for (uuid in deviceUUIDs) {
                        println(uuid.uuid)
                    }

                    println("Bluetooth cihazı tanımlanması")
                    val device: BluetoothDevice? = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("60:55:F9:F5:23:3D")

                    println("Bluetooth soketi oluştur")
                    val uuid: UUID = UUID.fromString("63DEF7AB-8BEB-6272-6430-B649F5E5CCEA") // RFCOMM UUID
                    val socket: BluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)!!

                    println("Veri gönderme işlemi")
                    try {
                        // Bluetooth bağlantısını başlat
                        socket.connect()

                        // Veriyi göndermek için OutputStream oluştur
                        val outputStream: OutputStream = socket.outputStream

                        // Göndermek istediğiniz veriyi byte dizisine çevirin
                        val dataToSend = ("GET Sensor").toByteArray()

                        // Veriyi gönder
                        outputStream.write(dataToSend)

                        // Gönderim tamamlandıktan sonra soketi kapat
                        outputStream.close()
                        socket.close()
                    } catch (e: IOException) {
                        // Hata durumunda işlemleri yönet
                        e.printStackTrace()
                    }





                }
            }
            else
            {
                println("isEmpty  !!!!")
            }
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_ENABLE_BT)
        {
            if (resultCode == RESULT_OK){
                binding.imageView.setImageResource(R.drawable.ic_bluetooth_on)
            }
            else if(resultCode == RESULT_CANCELED){
                binding.imageView.setImageResource(R.drawable.ic_bluetooth_off)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }





}



