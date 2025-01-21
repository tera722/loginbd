package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etContraseña;
    private Button btnLogin;
    private TextView tvResultado;
    private final String URL_API = "http:/localhost/login.php"; // Cambia por la URL de tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlazar vistas
        etUsuario = findViewById(R.id.etUsuario);
        etContraseña = findViewById(R.id.etContraseña);
        btnLogin = findViewById(R.id.btnLogin);
        tvResultado = findViewById(R.id.tvResultado);

        // 2. Configurar el botón de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String contraseña = etContraseña.getText().toString().trim();

                if (!usuario.isEmpty() && !contraseña.isEmpty()) {
                    realizarLogin(usuario, contraseña);
                } else {
                    tvResultado.setText("Por favor, completa todos los campos.");
                }
            }
        });
    }

    private void realizarLogin(String usuario, String contraseña) {
        // 1. Crear cliente OkHttp
        OkHttpClient client = new OkHttpClient();

        // 2. Crear cuerpo JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usuario", usuario);
            jsonObject.put("contraseña", contraseña);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 3. Crear RequestBody
        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // 4. Crear Request
        Request request = new Request.Builder()
                .url(URL_API)
                .post(body)
                .build();

        // 5. Realizar la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvResultado.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();

                    try {
                        JSONObject responseJson = new JSONObject(responseString);
                        boolean success = responseJson.getBoolean("success");
                        String message = responseJson.getString("message");

                        runOnUiThread(() -> {
                            if (success) {
                                tvResultado.setText("Login exitoso: " + message);
                            } else {
                                tvResultado.setText("Error: " + message);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> tvResultado.setText("Error: " + response.message()));
                }
            }
        });
    }
}
