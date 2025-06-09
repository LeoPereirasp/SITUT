package com.example.tccpuxxadados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class Form_User extends AppCompatActivity {

    private AppCompatButton btn_sair, btn_voltar;
    private TextView txtEmail, txtDataSistema, txtNomeUsuario;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView imgPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);

        // Configuração de padding para áreas de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Sistemas");


        // Inicializar componentes de exibição
        txtEmail = findViewById(R.id.txtEmail);
        txtDataSistema = findViewById(R.id.txtDataSistema);
        btn_sair = findViewById(R.id.btn_sair);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        btn_voltar = findViewById(R.id.btn_voltar);
        imgPerfil = findViewById(R.id.imgPerfil);


        // Obter o usuário autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Exibir o e-mail do usuário logado
            String email = user.getEmail();
            txtEmail.setText(" " + email);

            // Exibir nome do usuário
            String nomeUsuario = user.getDisplayName();
            if (nomeUsuario != null) {
                txtNomeUsuario.setText(" " + nomeUsuario);
            } else {
                txtNomeUsuario.setText(" Não disponível");
            }

            // Exibir a foto de perfil do Google, se disponível
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_person) // Ícone de placeholder
                        .error(R.drawable.ic_person)
                        .circleCrop()// Ícone em caso de erro
                        .into(imgPerfil);
            } else {
                // Opcional: definir um ícone de perfil padrão caso não haja foto
                imgPerfil.setImageResource(R.drawable.ic_person);
            }

            // Chamar o método para recuperar dados do sistema
            String userId = user.getUid();
            recuperarSistema(userId);
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
        }


        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_User.this, Form_home.class);
            startActivity(intent);
            finish();
        });
        btn_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Form_User.this, FormLogin.class);
                startActivity(intent);
            }
        });
    }


    private void recuperarSistema(String userId) {
        // Buscar dados do sistema do usuário no Firebase
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot sistemaSnapshot : dataSnapshot.getChildren()) {
                        Sistema sistema = sistemaSnapshot.getValue(Sistema.class);
                        if (sistema != null) {
                            // Exibir data de criação do sistema
                            txtDataSistema.setText(" " + sistema.data);
                        }
                    }
                } else {
                    // Caso não exista sistema para o usuário
                    Toast.makeText(Form_User.this, "Nenhum sistema encontrado para este usuário.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Form_User.this, "Erro ao recuperar dados do sistema.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Classe para representar um Sistema
    public static class Sistema {
        public String data;

        public Sistema() {
            // Construtor padrão necessário para o Firebase
        }

        public Sistema(String data) {
            this.data = data;
        }
    }
}