package com.example.tccpuxxadados;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IrrigacaoWorker extends Worker {

    // Construtor da classe, chamado quando o worker é instanciado
    public IrrigacaoWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // Método principal do worker, que será executado em segundo plano
    @Override
    public Result doWork() {
        // Inicializa a referência do Firebase para o nó "status"
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://situt-258258-default-rtdb.firebaseio.com/")
                .getReference("status"); // Caminho para o nó 'status'

        // Adiciona um listener para monitorar a variável "Irrigando" no Firebase
        mDatabase.child("Irrigando").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtém o valor da variável "Irrigando" como String
                String irrigandoStatus = dataSnapshot.getValue(String.class);

                // Se o status for "Sim", exibe a notificação
                if ("Sim".equalsIgnoreCase(irrigandoStatus)) {
                    mostrarNotificacao(); // Chama o método para mostrar a notificação
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Se ocorrer um erro na leitura do Firebase, não faz nada aqui
            }
        });

        // Retorna sucesso após a execução da tarefa
        return Result.success();
    }

    // Método para exibir a notificação quando a irrigação está em andamento
    private void mostrarNotificacao() {
        String channelId = "irrigacao_channel";  // ID do canal de notificação
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Cria um canal de notificação, necessário para Android 8.0 ou superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Canal Irrigação";  // Nome do canal
            String description = "Notificações sobre o status da irrigação";  // Descrição do canal
            int importance = NotificationManager.IMPORTANCE_DEFAULT;  // Importância da notificação
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Define a cor do canal (utiliza a cor Cobalt Blue)
            channel.setLightColor(Color.parseColor("#0047AB"));  // Cobalt Blue

            // Cria o canal de notificação no sistema
            notificationManager.createNotificationChannel(channel);
        }

        // Criação da notificação com a cor personalizada
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle("ATENÇÃO!!!")  // Título da notificação
                .setContentText("Sua planta está recebendo irrigação no momento.")  // Texto da notificação
                .setSmallIcon(R.drawable.logoclara)  // Ícone da notificação
                .setColor(Color.parseColor("#0047AB"))  // Cor da notificação (Cobalt Blue)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Prioridade da notificação
                .build();

        // Exibe a notificação
        notificationManager.notify(1, notification);  // Notificação com ID 1
    }
}

