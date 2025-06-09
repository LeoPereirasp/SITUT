package com.example.tccpuxxadados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class primeiro_acesso extends AppCompatActivity {

    private Button btn_umavez, btn_outra;  // Declaração dos botões

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeiro_acesso);  // Define o layout da activity

        // Configura o comportamento de ajuste de padding quando as barras de status e navegação são detectadas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());  // Obtém os insets das barras do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);  // Aplica os insets como padding
            return insets;  // Retorna os insets aplicados
        });

        // Inicializa os componentes da tela
        IniciarComponentes();

        // Configura o listener de clique para o botão "Primeira vez"
        btn_umavez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria uma intent para abrir a atividade "IntroActivity"
                Intent intent = new Intent(primeiro_acesso.this, IntroActivity.class);
                startActivity(intent);  // Inicia a nova activity
            }
        });

        // Configura o listener de clique para o botão "Outra vez"
        btn_outra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria uma intent para abrir a atividade "Form_home"
                Intent intent = new Intent(primeiro_acesso.this, Form_home.class);
                startActivity(intent);  // Inicia a nova activity
            }
        });
    }

    // Método para inicializar os componentes da tela
    private void IniciarComponentes() {
        btn_umavez = findViewById(R.id.btnPrimeiravez);  // Inicializa o botão "Primeira vez"
        btn_outra = findViewById(R.id.btnOutravez);  // Inicializa o botão "Outra vez"
    }
}
