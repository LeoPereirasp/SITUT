package com.example.tccpuxxadados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Form_status extends AppCompatActivity {

    // Declaração das variáveis de TextViews para exibir os dados dos sensores
    private TextView umidadeTextView;
    private TextView chuvaTextView;
    private TextView uvTextView;

    // Declaração do botão de voltar para a Activity anterior
    private AppCompatButton btn_voltar;

    // Declaração da referência ao banco de dados Firebase
    private DatabaseReference ref;

    // Declaração do Handler e Runnable para atualizar periodicamente os dados
    private Handler handler;
    private Runnable runnable;

    // Intervalo de atualização em milissegundos (5 minutos)
    private final long REFRESH_INTERVAL = 5 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_status);

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this);

        // Inicializa os TextViews com os IDs dos elementos no layout XML
        umidadeTextView = findViewById(R.id.umidadeTextView);
        chuvaTextView = findViewById(R.id.chuvaTextView);
        uvTextView = findViewById(R.id.uvTextView);

        // Configura o botão "Voltar" para retornar à Activity anterior
        btn_voltar = findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_status.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        // Configura a referência ao banco de dados do Firebase na localização "sensores"
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("sensores");

        // Inicializa o Handler e Runnable para atualizar os dados periodicamente
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Lê os dados do Firebase e agenda a próxima execução
                readDataFromFirebase();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };

        // Inicia a leitura dos dados do Firebase
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove o Runnable para evitar chamadas após a Activity ser destruída
        handler.removeCallbacks(runnable);
    }

    // Método para ler os dados do Firebase
    private void readDataFromFirebase() {
        // Adiciona um ValueEventListener para o campo "umidade" no Firebase
        ref.child("umidade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Log.d("Firebase", "Dados da umidade: " + snapshot.getValue());

                    // Verifica se o snapshot contém dados
                    if (snapshot.exists()) {
                        String umidadeStr = snapshot.getValue(String.class);
                        if (umidadeStr != null) {
                            try {
                                // Converte o valor para Integer
                                Integer umidade = Integer.parseInt(umidadeStr.trim());

                                // Define o texto do TextView de acordo com o valor
                                umidadeTextView.setText(umidade < 500 ? "Úmido" : "Não Úmido");
                            } catch (NumberFormatException e) {
                                umidadeTextView.setText("Erro ao converter a umidade");
                                Log.e("Firebase", "Erro ao converter a umidade: " + e.getMessage());
                            }
                        } else {
                            umidadeTextView.setText("Dados não disponíveis");
                        }
                    } else {
                        umidadeTextView.setText("Dados não disponíveis");
                    }
                } catch (Exception e) {
                    Log.e("Firebase", "Erro ao processar a umidade: " + e.getMessage());
                    umidadeTextView.setText("Erro ao processar a umidade");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log de erro caso a leitura do Firebase seja cancelada
                Log.e("Firebase", "Erro ao ler a umidade: " + error.getMessage());
                umidadeTextView.setText("Erro ao ler a umidade");
            }
        });

        // Adiciona um ValueEventListener para o campo "chuva" no Firebase
        // Adiciona um ValueEventListener para o campo "chuva" no Firebase
        ref.child("chuva").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Log.d("Firebase", "Dados da chuva: " + snapshot.getValue());

                    // Verifica se o snapshot contém dados
                    if (snapshot.exists()) {
                        String chuvaStr = snapshot.getValue(String.class);
                        if (chuvaStr != null) {
                            try {
                                // Converte o valor para Integer
                                Integer chuva = Integer.parseInt(chuvaStr.trim());

                                // Define o texto do TextView de acordo com o valor
                                if (chuva < 850) {
                                    chuvaTextView.setText("Chovendo");
                                } else {
                                    chuvaTextView.setText("Não chovendo");
                                }
                            } catch (NumberFormatException e) {
                                chuvaTextView.setText("Erro ao converter a chuva");
                                Log.e("Firebase", "Erro ao converter a chuva: " + e.getMessage());
                            }
                        } else {
                            chuvaTextView.setText("Dados não disponíveis");
                        }
                    } else {
                        chuvaTextView.setText("Dados não disponíveis");
                    }
                } catch (Exception e) {
                    Log.e("Firebase", "Erro ao processar a chuva: " + e.getMessage());
                    chuvaTextView.setText("Erro ao processar a chuva");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log de erro caso a leitura do Firebase seja cancelada
                Log.e("Firebase", "Erro ao ler a chuva: " + error.getMessage());
                chuvaTextView.setText("Erro ao ler a chuva");
            }
        });


        // Adiciona um ValueEventListener para o campo "uv" no Firebase
        ref.child("uv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Log.d("Firebase", "Dados do UV: " + snapshot.getValue());

                    // Verifica se o snapshot contém dados
                    if (snapshot.exists()) {
                        String uvStr = snapshot.getValue(String.class);
                        if (uvStr != null) {
                            try {
                                // Converte o valor para Integer e exibe no TextView
                                Integer uv = Integer.parseInt(uvStr.trim());
                                uvTextView.setText(uv > 800 ? "Extremo" : "Não extremo");
                            } catch (NumberFormatException e) {
                                uvTextView.setText("Erro ao converter o UV");
                                Log.e("Firebase", "Erro ao converter o UV: " + e.getMessage());
                            }
                        } else {
                            uvTextView.setText("Dados não disponíveis");
                        }
                    } else {
                        uvTextView.setText("Dados não disponíveis");
                    }
                } catch (Exception e) {
                    Log.e("Firebase", "Erro ao processar o UV: " + e.getMessage());
                    uvTextView.setText("Erro ao processar o UV");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log de erro caso a leitura do Firebase seja cancelada
                Log.e("Firebase", "Erro ao ler o UV: " + error.getMessage());
                uvTextView.setText("Erro ao ler o UV");
            }
        });
    }
}
