package com.example.embebidos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Giroscopio extends AppCompatActivity implements SensorEventListener {

    private ImageButton btnConnect;
    private BluetoothSocket socket;
    private static final int REQUEST_PERMISSION = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accelerationX, accelerationY, accelerationZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giroscopio);

        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupBluetooth();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no ha sido concedido, se solicita al usuario
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            // El permiso ya ha sido concedido
            getBondedDevices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBondedDevices();
            } else {
                // Handle the case where the user denies the permission
            }
        }
    }

    private void getBondedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            // Aquí puedes realizar las acciones necesarias con los dispositivos emparejados
        }
    }

    private void setupBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (bluetoothAdapter.isEnabled()) {
                checkBluetoothPermission();
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice targetDevice = buscarDispositivo(pairedDevices, "WERICHA");
            if (targetDevice != null) {
                try {
                    socket = targetDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();
                    Toast.makeText(this, "Conexión Bluetooth establecida", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al establecer la conexión Bluetooth", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private BluetoothDevice buscarDispositivo(Set<BluetoothDevice> devices, String name) {
        checkBluetoothPermission();
        for (BluetoothDevice device : devices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerationX = event.values[0];
            accelerationY = event.values[1];
            accelerationZ = event.values[2];

            // Puedes realizar las acciones necesarias con los valores del acelerómetro
            if (accelerationX>15) {
                if (socket != null && socket.isConnected()) {
                    try {
                        String signal = "1";
                        socket.getOutputStream().write(signal.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(accelerationX<-15){
                if (socket != null && socket.isConnected()) {
                    try {
                        String signal = "2";
                        socket.getOutputStream().write(signal.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(accelerationY>15){
                if (socket != null && socket.isConnected()) {
                    try {
                        String signal = "3";
                        socket.getOutputStream().write(signal.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(accelerationY< -15){
                if (socket != null && socket.isConnected()) {
                    try {
                        String signal = "4";
                        socket.getOutputStream().write(signal.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if (socket != null && socket.isConnected()) {
                try {
                    String signal = "5";
                    socket.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se utiliza en este caso
    }
}
