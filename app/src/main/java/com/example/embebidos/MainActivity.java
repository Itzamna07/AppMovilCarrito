package com.example.embebidos;

// Importar las clases necesarias

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.Manifest;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ImageButton front, back, left, right, connectButton, lock;
    private BluetoothSocket socket;
    private static final int REQUEST_PERMISSION = 1;

    // ID único para la comunicación Bluetooth
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Bloquear modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        front = findViewById(R.id.btnFren);
        back = findViewById(R.id.btnDown);
        left = findViewById(R.id.btnIzq);
        right = findViewById(R.id.btnDer);
        lock = findViewById(R.id.btnLock);

        connectButton = findViewById(R.id.btnCon);

        connectButton.setOnClickListener(view -> {
            setupBluetooth();
        });

        // Configurar la conexión Bluetooth
        setupBluetooth();

        front.setOnTouchListener((view, event) -> {
            if (socket != null && socket.isConnected()) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String signal = "1";
                        socket.getOutputStream().write(signal.getBytes());
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        String signal = "5";
                        socket.getOutputStream().write(signal.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        back.setOnTouchListener((view, event) -> {
            if (socket != null && socket.isConnected()) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String signal = "2";
                        socket.getOutputStream().write(signal.getBytes());
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        String signal = "5";
                        socket.getOutputStream().write(signal.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        left.setOnTouchListener((view, event) -> {
            if (socket != null && socket.isConnected()) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String signal = "3";
                        socket.getOutputStream().write(signal.getBytes());
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        String signal = "5";
                        socket.getOutputStream().write(signal.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        right.setOnTouchListener((view, event) -> {
            if (socket != null && socket.isConnected()) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String signal = "4";
                        socket.getOutputStream().write(signal.getBytes());
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        String signal = "5";
                        socket.getOutputStream().write(signal.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        lock.setOnClickListener(view -> {
            if (socket != null && socket.isConnected()) {
                try {
                    String signal = "5";
                    socket.getOutputStream().write(signal.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no ha sido concedido, se solicita al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            // El permiso ya ha sido concedido
            getBondedDevices();
        }
    }

    // Manejar el resultado de la solicitud de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso ha sido concedido
                getBondedDevices();
            } else {
                // El permiso ha sido denegado
                // Aquí puedes manejar el caso en que el usuario deniegue el permiso
            }
        }
    }

    // Obtener los dispositivos Bluetooth emparejados
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
            checkBluetoothPermission();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice targetDevice = buscarDispositivo(pairedDevices, "WERICHA");
            if (targetDevice != null) {
                try {
                    socket = targetDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();
                    // La conexión Bluetooth se estableció correctamente, mostrar un Toast
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
}



