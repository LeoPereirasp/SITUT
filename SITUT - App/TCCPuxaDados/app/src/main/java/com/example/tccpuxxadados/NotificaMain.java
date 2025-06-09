package com.example.tccpuxxadados;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class NotificaMain extends AppCompatActivity {

    private DatabaseReference mDatabase;  // Referência para o Firebase Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_home);  // Define o layout da activity

        // Inicializa o Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance("https://situt-258258-default-rtdb.firebaseio.com/")
                .getReference("status"); // Caminho para o nó 'status' no banco de dados

        // Listener para monitorar a variável "Irrigando" no Firebase
        mDatabase.child("Irrigando").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Pega o valor da variável "Irrigando" como String
                String irrigandoStatus = dataSnapshot.getValue(String.class);

                // Verifica se o valor é "Sim" (indicando que a irrigação está acontecendo)
                if ("Sim".equalsIgnoreCase(irrigandoStatus)) {
                    mostrarNotificacao();  // Chama o método para mostrar a notificação
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Em caso de erro ao acessar o banco de dados Firebase
                Toast.makeText(NotificaMain.this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();
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
}

