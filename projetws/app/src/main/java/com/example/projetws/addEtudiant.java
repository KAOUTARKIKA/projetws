package com.example.projetws;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addEtudiant extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button add, dateButton, selectImage;
    private ImageView imageView;
    private DatePickerDialog datePickerDialog;
    private Bitmap imageBitmap;
    private String photoName;
    private RequestQueue requestQueue;
    private final String insertUrl = "http://192.168.100.73/projet/ws/createEtudiant.php";
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        // Initialisation des vues
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        add = findViewById(R.id.add);
        dateButton = findViewById(R.id.dateButton);
        selectImage = findViewById(R.id.selectImage);
        imageView = findViewById(R.id.imageView);

        initDatePicker();
        selectedDate = getTodaysDate();  // Initialiser avec la date d'aujourd'hui
        dateButton.setText(selectedDate);

        add.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        selectImage.setOnClickListener(this);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        return makeDateString(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                selectedDate = makeDateString(day, month, year);
                dateButton.setText(selectedDate);
            }
        };

        Calendar cal = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private String makeDateString(int day, int month, int year) {
        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }

    @Override
    public void onClick(View v) {
        if (v == add) addEtudiant();
        else if (v == dateButton) datePickerDialog.show();
        else if (v == selectImage) selectImage();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
                photoName = "img_" + System.currentTimeMillis() + ".jpg";
            } catch (IOException e) {
                Toast.makeText(this, "Erreur chargement image", Toast.LENGTH_SHORT).show();
                Log.e("ImageError", e.getMessage());
            }
        }
    }

    private void addEtudiant() {
        final String nomText = nom.getText().toString().trim();
        final String prenomText = prenom.getText().toString().trim();
        final String villeText = ville.getSelectedItem().toString();
        final String sexeText = m.isChecked() ? "homme" : "femme";

        if (nomText.isEmpty() || prenomText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                Toast.makeText(addEtudiant.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(addEtudiant.this, "Erreur: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(addEtudiant.this, "Réponse serveur invalide", Toast.LENGTH_SHORT).show();
                            Log.e("JSON_ERROR", "Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = "Erreur de connexion";
                        if (error.networkResponse != null) {
                            message += " (Code: " + error.networkResponse.statusCode + ")";
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (jsonResponse.has("message")) {
                                    message += ": " + jsonResponse.getString("message");
                                }
                            } catch (Exception e) {
                                Log.e("ERROR", "Error parsing error response", e);
                            }
                        }
                        Toast.makeText(addEtudiant.this, message, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nom", nomText);
                params.put("prenom", prenomText);
                params.put("ville", villeText);
                params.put("sexe", sexeText);
                params.put("dateNaissance", selectedDate);

                if (imageBitmap != null) {
                    String encodedImage = encodeImage(imageBitmap);
                    params.put("photo", encodedImage);
                    params.put("photoName", photoName);
                    Log.d("IMAGE", "Image encoded and added to params");
                } else {
                    Log.d("IMAGE", "No image selected");
                }

                // Log pour déboguer
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Log.d("PARAMS", entry.getKey() + ": " +
                            (entry.getKey().equals("photo") ? "ENCODED_IMAGE" : entry.getValue()));
                }

                return params;
            }
        };

        Log.d("REQUEST", "Sending request to: " + insertUrl);
        requestQueue.add(request);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
} 