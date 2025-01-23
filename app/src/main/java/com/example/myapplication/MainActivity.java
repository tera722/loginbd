package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerUsuarios;
    private EditText etUsuario, etContraseña;
    private Button btnLogin;
    private TextView tvResultado;
    private RequestQueue requestQueue;
    private final String URL_API = "http://10.0.2.2/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerUsuarios = findViewById(R.id.spinnerUsuarios);

        etUsuario = findViewById(R.id.etUsuario);
        etContraseña = findViewById(R.id.etContraseña);
        btnLogin = findViewById(R.id.btnLogin);
        tvResultado = findViewById(R.id.tvResultado);

        requestQueue = Volley.newRequestQueue(this);

        spinnerUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el usuario seleccionado
                Usuario usuarioSeleccionado = (Usuario) parent.getItemAtPosition(position);
                etUsuario.setText(usuarioSeleccionado.getUser());
                etContraseña.setText(usuarioSeleccionado.getContrasena());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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

    private void cargarUsuarios() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray usuariosJson = response.getJSONArray("usuarios");
                        List<Usuario> usuarios = new ArrayList<>();

                        for (int i = 0; i < usuariosJson.length(); i++) {
                            JSONObject usuarioJson = usuariosJson.getJSONObject(i);
                            Usuario usuario = new Usuario(
                                    usuarioJson.getInt("id"),
                                    usuarioJson.getString("user"),
                                    usuarioJson.getString("contrasena")
                            );
                            usuarios.add(usuario);
                        }

                        // Configurar adaptador para el Spinner
                        ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                usuarios
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerUsuarios.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Manejar error
                }
        );
        requestQueue.add(request);
    }

    // Clase Usuario para manejar los datos
    public class Usuario {
        private int id;
        private String user;
        private String contrasena;

        public Usuario(int id, String user, String contrasena) {
            this.id = id;
            this.user = user;
            this.contrasena = contrasena;
        }

        public String getUser() {
            return user;
        }

        public String getContrasena() {
            return contrasena;
        }

        @Override
        public String toString() {
            return user; // Texto que se mostrará en el Spinner
        }
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

                                    // Obtener el usuario de la respuesta
                                    String usuarioBD = response.getString("usuario");

                                    // Encontrar el EditText EdBD
                                    EditText edBD = findViewById(R.id.EdBD);

                                    // Establecer el texto del usuario
                                    edBD.setText(usuarioBD);

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