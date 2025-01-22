package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etContraseña;
    private Button btnLogin;
    private TextView tvResultado;
    private RequestQueue requestQueue;
    private final String URL_API = "http://10.0.2.2/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = findViewById(R.id.etUsuario);
        etContraseña = findViewById(R.id.etContraseña);
        btnLogin = findViewById(R.id.btnLogin);
        tvResultado = findViewById(R.id.tvResultado);
        requestQueue = Volley.newRequestQueue(this);

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
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario", usuario);
            jsonBody.put("contraseña", contraseña);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_API,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                String message = response.getString("message");
                                if (success) {
                                    tvResultado.setText("Login exitoso: " + message);
                                } else {
                                    tvResultado.setText("Error: " + message);
                                }
                            } catch (Exception e) {
                                tvResultado.setText("Error al procesar la respuesta: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tvResultado.setText("Error de conexión: " + error.getMessage());
                        }
                    }
            );

            requestQueue.add(request);
        } catch (Exception e) {
            tvResultado.setText("Error al crear la solicitud: " + e.getMessage());
        }
    }
}