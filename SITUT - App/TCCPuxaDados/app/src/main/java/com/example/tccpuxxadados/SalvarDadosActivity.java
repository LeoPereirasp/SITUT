package com.example.tccpuxxadados;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import java.util.concurrent.TimeUnit;

public class SalvarDadosActivity extends AppCompatActivity {

    private static final String TAG = "SalvarDadosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_home);

        // Agendar o trabalho para salvar os dados a cada 5 minutos
        agendarSalvarDados();
    }

    // Método para agendar a tarefa de salvar os dados periodicamente
    private void agendarSalvarDados() {
        // Criar uma solicitação periódica para salvar os dados
        WorkRequest salvarDadosRequest = new PeriodicWorkRequest.Builder(
                SalvarDadosWorker.class, // Classe que irá realizar o trabalho
                5, TimeUnit.MINUTES) // A tarefa será executada a cada 5 minutos
                .setInitialDelay(1, TimeUnit.MINUTES) // Espera 1 minuto antes de começar a execução
                .build();

        // Agendar o trabalho usando o WorkManager
        WorkManager.getInstance(this).enqueue(salvarDadosRequest);

        // Log para informar que a tarefa foi agendada
        Log.d(TAG, "Tarefa agendada para salvar os dados periodicamente.");
    }
}
