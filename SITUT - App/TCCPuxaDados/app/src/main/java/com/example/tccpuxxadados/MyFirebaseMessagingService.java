package com.example.tccpuxxadados;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Verifica se a mensagem recebida contém dados
        if (remoteMessage.getData().size() > 0) {
            // Extrai o valor da chave "Irrigando" dos dados da mensagem
            String irrigandoStatus = remoteMessage.getData().get("Irrigando");

            // Se o valor de "Irrigando" for "Sim", chama o método para mostrar a notificação
            if ("Sim".equalsIgnoreCase(irrigandoStatus)) {
                mostrarNotificacao();  // Chama o método que exibe a notificação
            }
        }
    }

    // Método para exibir a notificação
    private void mostrarNotificacao() {
        String channelId = "irrigacao_channel";  // ID único do canal de notificação
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  // Gerenciador de notificações

        // Criação do canal de notificação, necessário para Android 8.0 (API nível 26) ou superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Canal Irrigação";  // Nome do canal
            String description = "Notificações sobre o status da irrigação";  // Descrição do canal
            int importance = NotificationManager.IMPORTANCE_DEFAULT;  // Importância da notificação
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);  // Cria o canal de notificação
            channel.setDescription(description);  // Define a descrição do canal

            // Definindo a cor do canal de notificação (para efeitos de luz no dispositivo)
            channel.setLightColor(Color.parseColor("#0047AB"));  // Cor personalizada (Cobalt Blue)

            notificationManager.createNotificationChannel(channel);  // Cria o canal de notificações
        }

        // Criação da notificação com uma cor personalizada e ícone
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("ATENÇÃO!!!")  // Título da notificação
                .setContentText("Sua planta está recebendo irrigação no momento.")  // Texto da notificação
                .setSmallIcon(R.drawable.logoclara)  // Ícone da notificação (personalizado)
                .setColor(Color.parseColor("#0047AB"))     // Cor personalizada (Cobalt Blue) para a notificação
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Prioridade padrão da notificação
                .build();  // Constrói a notificação

        // Exibe a notificação
        notificationManager.notify(1, notification);  // ID único para exibir a notificação
    }
}
