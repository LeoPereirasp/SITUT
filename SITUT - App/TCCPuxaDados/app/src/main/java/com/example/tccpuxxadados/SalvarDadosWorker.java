package com.example.tccpuxxadados;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalvarDadosWorker extends Worker {

    private static final String TAG = "SalvarDadosWorker";

    // Construtor que recebe o contexto e os parâmetros do worker
    public SalvarDadosWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // Método que executa o trabalho do worker
    @NonNull
    @Override
    public Result doWork() {
        // Inicializa o Firebase
        FirebaseApp.initializeApp(getApplicationContext());

        // Referências para as pastas do Firebase (sensores e historico)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("sensores");
        DatabaseReference historicoRef = ref.getParent().child("Historico");

        // Lê os dados da pasta 'sensores' no Firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Cria um mapa para os dados que serão salvos no histórico
                    Map<String, Object> historicoData = new HashMap<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        historicoData.put(data.getKey(), data.getValue());
                    }

                    // Adiciona a data de expedição (data e hora atual)
                    String currentDate = getCurrentDate();
                    historicoData.put("dataExpedicao", currentDate);

                    // Verifica quantos dados já estão na pasta 'Historico'
                    historicoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot historicoSnapshot) {
                            int size = (int) historicoSnapshot.getChildrenCount();

                            if (size >= 10) {
                                // Se houver 10 ou mais registros, remove o dado mais antigo
                                String keyToRemove = null;
                                for (DataSnapshot data : historicoSnapshot.getChildren()) {
                                    keyToRemove = data.getKey(); // Pega a chave do primeiro item
                                    break; // Sai após pegar o primeiro
                                }
                                if (keyToRemove != null) {
                                    // Remove o dado mais antigo
                                    historicoRef.child(keyToRemove).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Dado mais antigo removido com sucesso!"))
                                            .addOnFailureListener(e -> Log.e(TAG, "Erro ao remover dado: " + e.getMessage()));
                                }
                            }

                            // Salva os novos dados na pasta 'Historico'
                            historicoRef.push().setValue(historicoData)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Dados salvos com sucesso!"))
                                    .addOnFailureListener(e -> Log.e(TAG, "Erro ao salvar os dados: " + e.getMessage()));
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Caso ocorra erro na leitura dos dados do histórico
                            Log.e(TAG, "Erro ao ler os dados do histórico: " + error.getMessage());
                        }
                    });
                } else {
                    // Caso não existam dados na pasta 'sensores'
                    Log.e(TAG, "Dados não encontrados na pasta sensores");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Caso ocorra erro na leitura dos dados da pasta 'sensores'
                Log.e(TAG, "Erro ao ler os dados: " + error.getMessage());
            }
        });

        // Retorna um resultado bem-sucedido
        return Result.success();
    }

    // Método para obter a data e hora atual formatada
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());  // Retorna a data no formato "ano-mês-dia hora:minuto:segundo"
    }
}
