package com.example.tccpuxxadados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;
import android.widget.Button;

public class Form_irrigacao extends AppCompatActivity {
    Firebase meuFirebase;
    DatabaseReference dados;
    int intervalo_int;
    int duracao_int;
    AppCompatButton btSalvar, btApagar, btnIrrigar,btn_voltar;
    TextView txtIrrigando ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_irrigacao);

        // Inicializa o Firebase
        dados = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(this);

        // Encontrando os elementos da interface
        btSalvar = findViewById(R.id.btn_salvar);
        btApagar = findViewById(R.id.btn_descartar);  // O botão de apagar
        btnIrrigar = findViewById(R.id.btnIrrigar);
        EditText edtI = findViewById(R.id.edit_intervalo);
        EditText edt2 = findViewById(R.id.edit_duracao);
        txtIrrigando = findViewById(R.id.Irrigando);
        btn_voltar = findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_irrigacao.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        // Referência para o Firebase
        meuFirebase = new Firebase("https://situt-258258-default-rtdb.firebaseio.com/");

        // Listener para a variável de status no Firebase
        dados.child("status").child("Irrigando").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recupera o status da irrigação (ex: "ativo", "inativo")
                    String status = dataSnapshot.getValue(String.class);

                    // Atualiza a TextView de acordo com o valor
                    if ("Sim".equals(status)) {
                        txtIrrigando.setText("Irrigando");
                        txtIrrigando.setBackgroundResource(R.drawable.irrigandobt);
                    } else if ("Não".equals(status)) {
                        txtIrrigando.setText("Não irrigando");
                        txtIrrigando.setBackgroundResource(R.drawable.buttons_red);
                    } else {
                        txtIrrigando.setText("Status Desconhecido");
                    }
                } else {
                    txtIrrigando.setText("Status não encontrado");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Implementação do método onCancelled
                Toast.makeText(Form_irrigacao.this, "Erro ao recuperar status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Botão Salvar - já presente no seu código
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervalo_int = Integer.parseInt(edtI.getText().toString());
                duracao_int = Integer.parseInt(edt2.getText().toString());
            }
        });

        btnIrrigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Atualizando os valores no Firebase
                Firebase info = meuFirebase.child("/Irriga/Intervalo");
                info.setValue(intervalo_int);

                Firebase info2 = meuFirebase.child("/Irriga/Duracao");
                info2.setValue(duracao_int);

                edtI.setText("0");
                edt2.setText("0");
            }
        });

        // Botão Apagar - Nova funcionalidade que zera os valores e envia para o Firebase
        btApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definindo intervalo e duração para 0
                intervalo_int = 0;
                duracao_int = 0;

                // Enviando para o Firebase
                Firebase info = meuFirebase.child("/Irriga/Intervalo");
                info.setValue(intervalo_int);

                Firebase info2 = meuFirebase.child("/Irriga/Duracao");
                info2.setValue(duracao_int);

                // Atualizando os campos EditText para 0
                edtI.setText("0");
                edt2.setText("0");

                txtIrrigando.setText("Não irrigando");
            }
        });
    }

    // Referência para a chave "Irriga_a" no Firebase
    DatabaseReference referenceIrriga_a = FirebaseDatabase.getInstance("https://situt-258258-default-rtdb.firebaseio.com/").getReference("Irriga/Irriga_a");
}
