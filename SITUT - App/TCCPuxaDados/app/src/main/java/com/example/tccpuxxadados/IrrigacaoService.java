package com.example.tccpuxxadados;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IrrigacaoService extends android.app.Service {

    private DatabaseReference mDatabase;  // Referência ao banco de dados do Firebase

    // Método chamado quando o serviço é criado
    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa a referência para o nó 'status' no Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance("https://situt-258258-default-rtdb.firebaseio.com/")
                .getReference("status");  // Caminho para o nó 'status'

        // Adiciona um listener para monitorar as mudanças na variável "Irrigando"
        mDatabase.child("Irrigando").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtém o valor da variável "Irrigando" como String
                String irrigandoStatus = dataSnapshot.getValue(String.class);

                // Se o valor de "Irrigando" for "Sim", exibe uma notificação
                if ("Sim".equalsIgnoreCase(irrigandoStatus)) {
                    mostrarNotificacao();  // Chama o método para exibir a notificação
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Em caso de erro na leitura do Firebase, não faz nada
            }
        });
    }

    // Método para exibir a notificação
    private void mostrarNotificacao() {
        String channelId = "irrigacao_channel";  // ID do canal de notificação
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Cria um canal de notificação para Android 8.0 ou superior (necessário)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Canal Irrigação";  // Nome do canal
            String description = "Notificações sobre o status da irrigação";  // Descrição do canal
            int importance = NotificationManager.IMPORTANCE_DEFAULT;  // Importância da notificação
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Define a cor do canal (Cobalt Blue)
            channel.setLightColor(Color.parseColor("#0047AB"));

            // Cria o canal de notificação no sistema
            notificationManager.createNotificationChannel(channel);
        }

        // Criação da notificação com a cor personalizada
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("ATENÇÃO!!!")  // Título da notificação
                .setContentText("Sua planta está recebendo irrigação no momento.")  // Texto da notificação
                .setSmallIcon(R.drawable.logoclara)  // Ícone da notificação
                .setColor(Color.parseColor("#0047AB"))  // Cor da notificação (Cobalt Blue)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Prioridade da notificação
                .build();

        // Exibe a notificação
        notificationManager.notify(1, notification);  // Exibe a notificação com ID 1
    }

    // Método chamado quando o serviço é iniciado
    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        return START_STICKY;  // Mantém o serviço em execução (recomeça automaticamente se o serviço for interrompido)
    }

    // Método necessário para serviços, mas não utilizado neste caso
    @Override
    public android.os.IBinder onBind(android.content.Intent intent) {
        return null;  // Este serviço não permite vinculação
    }
}
