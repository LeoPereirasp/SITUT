package com.example.tccpuxxadados;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;



public class Form_registro_sistema extends AppCompatActivity {

    private TextInputEditText editName, editData;
    private TextInputLayout txtInputLayoutName, txtInputLayoutData;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private AppCompatButton btn_jacriei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_registro_sistema);

        // Inicialização dos componentes
        editName = findViewById(R.id.editName);
        editData = findViewById(R.id.editData);
        txtInputLayoutName = findViewById(R.id.txtInputLayoutName);
        txtInputLayoutData = findViewById(R.id.txtInputLayoutData);
        btn_jacriei = findViewById(R.id.btnjacriei);

        // Inicializar o Firebase Authentication e o Firebase Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Sistemas");

        verificarSistemaCadastrado();

        btn_jacriei.setOnClickListener(v -> {
            Intent intent = new Intent(Form_registro_sistema.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        // Configurar o botão de cadastro
        findViewById(R.id.btnCadastrar).setOnClickListener(v -> {
            // Verificar se os campos estão preenchidos
            String nome = editName.getText().toString().trim();
            String data = editData.getText().toString().trim();

            if (nome.isEmpty()) {
                txtInputLayoutName.setError("Nome é obrigatório");
                return;
            }

            if (data.isEmpty()) {
                txtInputLayoutData.setError("Data é obrigatória");
                return;
            }

            // Se os campos estiverem válidos, salvar os dados no Firebase
            salvarSistema(nome, data);
        });

        // Configurar o campo de data para abrir o DatePicker
        editData.setOnClickListener(v -> {
            // Obter a data atual
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Criar o DatePicker
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Form_registro_sistema.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Formatar a data no formato dd/MM/yyyy
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(selectedDate.getTime());
                        // Preencher o campo com a data selecionada
                        editData.setText(formattedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        // Adicionar um TextWatcher para formatar automaticamente a data enquanto o usuário digita
        editData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Evitar formatação excessiva
                if (charSequence.length() == 2 || charSequence.length() == 5) {
                    // Inserir a barra automaticamente após o dia e o mês
                    if (charSequence.length() == 2) {
                        editData.append("/");
                    } else if (charSequence.length() == 5) {
                        editData.append("/");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Verificar se a entrada ultrapassou o limite de caracteres
                if (editable.length() > 10) {
                    editData.setText(editable.subSequence(0, 10));
                    editData.setSelection(editable.length());
                    Toast.makeText(Form_registro_sistema.this, "Formato de data inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void salvarSistema(String nome, String data) {
        String userId = mAuth.getCurrentUser().getUid(); // ID do usuário logado

        // Caminho para os sistemas do usuário
        DatabaseReference sistemasRef = mDatabase.child(userId);

        // Verificar se já existe um sistema com o mesmo nome
        sistemasRef.orderByChild("nome").equalTo(nome).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Sistema com o mesmo nome já existe
                    Toast.makeText(Form_registro_sistema.this, "Já existe um sistema com esse nome", Toast.LENGTH_SHORT).show();
                } else {
                    // Nenhum sistema encontrado, criar novo
                    String sistemaId = sistemasRef.push().getKey();
                    Sistema sistema = new Sistema(nome, data);

                    if (sistemaId != null) {
                        sistemasRef.child(sistemaId).setValue(sistema)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Form_registro_sistema.this, "Sistema cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Form_registro_sistema.this, Form_home.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Form_registro_sistema.this, "Erro ao cadastrar sistema", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Erro ao acessar o Firebase
                Toast.makeText(Form_registro_sistema.this, "Erro ao verificar sistemas: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void verificarSistemaCadastrado() {
        String userId = mAuth.getCurrentUser().getUid(); // UID do usuário logado

        mDatabase.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Sistema já cadastrado, o botão pode funcionar
                    btn_jacriei.setEnabled(true);
                } else {
                    // Nenhum sistema registrado, desabilitar o botão
                    btn_jacriei.setEnabled(false);
                    Toast.makeText(Form_registro_sistema.this, "Você precisa cadastrar um sistema primeiro", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Form_registro_sistema.this, "Erro ao verificar o sistema", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Classe para representar um Sistema
    public static class Sistema {
        public String nome;
        public String data;

        public Sistema() {
            // Constructor default necessário para o Firebase
        }

        public Sistema(String nome, String data) {
            this.nome = nome;
            this.data = data;
        }
    }
}
