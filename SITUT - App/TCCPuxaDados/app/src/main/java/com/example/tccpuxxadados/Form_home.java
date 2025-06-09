package com.example.tccpuxxadados;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Form_home extends AppCompatActivity {

    private AppCompatButton btclima, btirriga, btstatus, btinfo, btnuser, btnConfig, btnBook;
    private TextView txtNomeSistema;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_home);

        // Configuração do Edge-to-Edge para layout fullscreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes e Firebase
        IniciarComponentes();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Sistemas");

        // Obter o usuário autenticado e carregar o nome do sistema
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarNomeSistema(userId);
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
        }

        // Monitorar o status de irrigação no Firebase
        monitorarIrrigacaoStatus();

        // Configuração dos botões
        configurarBotoes();
    }


    private void IniciarComponentes() {
        btclima = findViewById(R.id.btn_clima);
        btirriga = findViewById(R.id.btn_irrigacao);
        btinfo = findViewById(R.id.btn_info);
        btstatus = findViewById(R.id.btn_status);
        btnuser = findViewById(R.id.btnuser);
        btnConfig = findViewById(R.id.btnConfig);
        btnBook = findViewById(R.id.btnbook);
        txtNomeSistema = findViewById(R.id.txtNomeSistema);
    }

    private void configurarBotoes() {
        btclima.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, MainActivity.class);
            startActivity(intent);
        });

        btirriga.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, Form_irrigacao.class);
            startActivity(intent);
        });

        btstatus.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, Form_status.class);
            startActivity(intent);
        });

        btinfo.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, Form_info.class);
            startActivity(intent);
        });

        btnBook.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, IntroActivity.class);
            startActivity(intent);
        });

        btnuser.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, Form_User.class);
            startActivity(intent);
        });

        btnConfig.setOnClickListener(view -> {
            Intent intent = new Intent(Form_home.this, FormSobrenos.class);
            startActivity(intent);
        });
    }

    private void carregarNomeSistema(String userId) {
        // Buscar o nome do sistema do usuário no Firebase
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot sistemaSnapshot : dataSnapshot.getChildren()) {
                        Sistema sistema = sistemaSnapshot.getValue(Sistema.class);
                        if (sistema != null) {
                            txtNomeSistema.setText(sistema.nome);
                        }
                    }
                } else {
                    Toast.makeText(Form_home.this, "Nenhum sistema encontrado para este usuário.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Form_home.this, "Erro ao recuperar dados do sistema.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para monitorar o status de irrigação no Firebase
    private void monitorarIrrigacaoStatus() {
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("status/Irrigando");
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String irrigandoStatus = dataSnapshot.getValue(String.class);
                if ("Sim".equalsIgnoreCase(irrigandoStatus)) {
                    mostrarNotificacao();  // Exibe a notificação
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Form_home.this, "Erro ao acessar o status de irrigação.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para exibir a notificação
    private void mostrarNotificacao() {
        String channelId = "irrigacao_channel";  // ID único para o canal de notificações
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  // Gerenciador de notificações

        // Criação do canal de notificação (necessário para Android 8.0 ou superior)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Canal Irrigação";  // Nome do canal
            String description = "Notificações sobre o status da irrigação";  // Descrição do canal
            int importance = NotificationManager.IMPORTANCE_DEFAULT;  // Importância da notificação
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);  // Define a descrição do canal

            // Definindo a cor do canal de notificações
            channel.setLightColor(Color.parseColor("#0047AB"));  // Cor personalizada (Cobalt Blue)

            notificationManager.createNotificationChannel(channel);  // Cria o canal de notificações
        }

        // Criação da notificação
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("ATENÇÃO!!!")  // Título da notificação
                .setContentText("Sua planta está recebendo irrigação no momento.")  // Texto da notificação
                .setSmallIcon(R.drawable.logoclara)  // Ícone da notificação (ícone customizado)
                .setColor(Color.parseColor("#0047AB"))     // Define a cor da notificação (Cobalt Blue)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Prioridade da notificação
                .build();  // Cria a notificação

        // Exibe a notificação
        notificationManager.notify(1, notification);  // ID único para a notificação
    }

    // Classe interna para representar um Sistema
    public static class Sistema {
        public String nome;

        public Sistema() {
            // Constructor default necessário para o Firebase
        }

        public Sistema(String nome, String data) {
            this.nome = nome;
        }
    }
}
