package com.example.tccpuxxadados;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.IOException;
import java.util.ArrayList;

public class Form_info extends AppCompatActivity {
    private DatabaseReference historicoRef, sensoresRef;
    private BarChart barChart;
    private PieChart pieChart;
    private AppCompatButton btn_voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_info);

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Referências para as pastas no Firebase
        historicoRef = database.getReference("Historico");
        sensoresRef = database.getReference("sensores");

        // Configura o BarChart
        barChart = findViewById(R.id.barChart);
        carregarDadosEConstruirGrafico();

        // Configura o PieChart
        pieChart = findViewById(R.id.pieChart);
        carregarDadosEConstruirGraficoPizza();

        btn_voltar = findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_info.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        // Configura o botão para gerar o PDF
        AppCompatButton button = findViewById(R.id.btn_download);
        button.setOnClickListener(v -> {
            solicitarPermissao();
            gerarPdf();
        });
    }

    // Método para solicitar permissão de escrita no armazenamento externo
    private void solicitarPermissao() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    // Método para carregar dados e construir o gráfico de barras
    private void carregarDadosEConstruirGrafico() {
        sensoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("Firebase", "Dados recebidos: " + snapshot.getValue());
                if (snapshot.exists()) {
                    ArrayList<BarEntry> chuvaEntries = new ArrayList<>();
                    ArrayList<BarEntry> umidadeEntries = new ArrayList<>();
                    ArrayList<BarEntry> uvEntries = new ArrayList<>();

                    String chuvaStr = snapshot.child("chuva").getValue(String.class);
                    String umidadeStr = snapshot.child("umidade").getValue(String.class);
                    String uvStr = snapshot.child("uv").getValue(String.class);

                    if (chuvaStr != null && umidadeStr != null && uvStr != null) {
                        try {
                            float chuva = Float.parseFloat(chuvaStr);
                            float umidade = Float.parseFloat(umidadeStr);
                            float uv = Float.parseFloat(uvStr);

                            chuvaEntries.add(new BarEntry(0, chuva));
                            umidadeEntries.add(new BarEntry(1, umidade));
                            uvEntries.add(new BarEntry(2, uv));
                        } catch (NumberFormatException e) {
                            Log.e("Firebase", "Erro ao converter valores: " + e.getMessage());
                        }
                    } else {
                        Log.e("Firebase", "Valores nulos para chuva, umidade ou UV.");
                    }

                    if (!chuvaEntries.isEmpty() && !umidadeEntries.isEmpty() && !uvEntries.isEmpty()) {
                        // Defina um conjunto de dados e cor para cada variável
                        BarDataSet chuvaDataSet = new BarDataSet(chuvaEntries, "Chuva");
                        chuvaDataSet.setColor(Color.BLUE);
                        chuvaDataSet.setValueTextColor(Color.parseColor("#f5f5f5"));
                        chuvaDataSet.setValueTextSize(15f);

                        BarDataSet umidadeDataSet = new BarDataSet(umidadeEntries, "Umidade");
                        umidadeDataSet.setColor(Color.GREEN);
                        umidadeDataSet.setValueTextColor(Color.parseColor("#f5f5f5"));
                        umidadeDataSet.setValueTextSize(15f);

                        BarDataSet uvDataSet = new BarDataSet(uvEntries, "UV");
                        uvDataSet.setColor(Color.RED);
                        uvDataSet.setValueTextColor(Color.parseColor("#f5f5f5"));
                        uvDataSet.setValueTextSize(15f);

                        BarData data = new BarData(chuvaDataSet, umidadeDataSet, uvDataSet);
                        barChart.setData(data);
                        barChart.animateY(2000);

                        // Configura as cores do texto dos eixos X e Y
                        barChart.getXAxis().setTextColor(Color.parseColor("#f5f5f5"));
                        barChart.getAxisLeft().setTextColor(Color.parseColor("#f5f5f5"));
                        barChart.getAxisRight().setTextColor(Color.parseColor("#f5f5f5"));

                        // Configura a cor do texto da legenda
                        barChart.getLegend().setTextColor(Color.parseColor("#f5f5f5"));

                        // Configura o texto e a cor da descrição
                        barChart.getDescription().setText("Valor dos sensores");
                        barChart.getDescription().setTextColor(Color.parseColor("#f5f5f5"));

                        barChart.invalidate();
                    } else {
                        Log.e("Firebase", "Nenhum dado válido encontrado.");
                    }
                } else {
                    Log.e("Firebase", "Nenhum dado encontrado na pasta Sensores");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Erro ao ler os dados: " + error.getMessage());
            }
        });
    }

    // Método para carregar dados e construir o gráfico de pizza
    private void carregarDadosEConstruirGraficoPizza() {
        sensoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("Firebase", "Dados recebidos: " + snapshot.getValue());
                if (snapshot.exists()) {
                    ArrayList<PieEntry> entries = new ArrayList<>();

                    String chuvaStr = snapshot.child("chuva").getValue(String.class);
                    String umidadeStr = snapshot.child("umidade").getValue(String.class);
                    String uvStr = snapshot.child("uv").getValue(String.class);

                    if (chuvaStr != null && umidadeStr != null && uvStr != null) {
                        try {
                            float chuva = Float.parseFloat(chuvaStr);
                            float umidade = Float.parseFloat(umidadeStr);
                            float uv = Float.parseFloat(uvStr);

                            entries.add(new PieEntry(chuva, "Chuva"));
                            entries.add(new PieEntry(umidade, "Umidade"));
                            entries.add(new PieEntry(uv, "UV"));
                        } catch (NumberFormatException e) {
                            Log.e("Firebase", "Erro ao converter valores: " + e.getMessage());
                        }
                    } else {
                        Log.e("Firebase", "Valores nulos para chuva, umidade ou UV.");
                    }

                    if (!entries.isEmpty()) {
                        PieDataSet dataSet = new PieDataSet(entries, "");

                        // Defina cores específicas para cada entrada
                        ArrayList<Integer> colors = new ArrayList<>();
                        colors.add(Color.BLUE);   // Cor para chuva
                        colors.add(Color.GREEN);  // Cor para umidade
                        colors.add(Color.RED);    // Cor para UV
                        dataSet.setColors(colors);

                        dataSet.setValueTextColor(Color.parseColor("#f5f5f5")); // Cor dos valores off-white
                        dataSet.setValueTextSize(15f);

                        PieData data = new PieData(dataSet);
                        pieChart.setData(data);
                        pieChart.animateY(2000);

                        // Ativar o "miolo" e definir a cor "cobalt_blue"
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setHoleColor(ContextCompat.getColor(getApplicationContext(), R.color.cobalt_blue));

                        // Configura a cor do texto da legenda
                        pieChart.getLegend().setTextColor(Color.parseColor("#f5f5f5"));

                        // Configura o texto e a cor da descrição
                        pieChart.getDescription().setText("Valor dos sensores");
                        pieChart.getDescription().setTextColor(Color.parseColor("#f5f5f5"));

                        // Configura as cores do texto dos labels
                        pieChart.setEntryLabelColor(Color.parseColor("#f5f5f5"));

                        pieChart.invalidate();
                    } else {
                        Log.e("Firebase", "Nenhum dado válido encontrado.");
                    }
                } else {
                    Log.e("Firebase", "Nenhum dado encontrado na pasta Sensores");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Erro ao ler os dados: " + error.getMessage());
            }
        });
    }

    // Método para gerar o PDF
    private void gerarPdf() {
        // Ler os dados da pasta 'Historico' no Firebase
        historicoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> dados = new ArrayList<>();

                // Iterar sobre os registros salvos no histórico
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StringBuilder registro = new StringBuilder();

                    // Pegar os dados de cada item
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String chave = data.getKey();  // Nome da variável (umidade, uv, clima...)
                        String valor = data.getValue(String.class);  // Valor da variável

                        // Adicionar ao registro
                        registro.append(chave).append(": ").append(valor).append("\n");
                    }

                    // Adicionar o registro completo à lista
                    dados.add(registro.toString());
                }

                // Gerar o PDF com os dados coletados
                if (!dados.isEmpty()) {
                    criarPdf(dados);
                } else {
                    Toast.makeText(Form_info.this, "Nenhum dado encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Form_info.this, "Erro ao buscar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para criar o PDF com os dados coletados
    private void criarPdf(ArrayList<String> dados) {
        // Caminho para a pasta de downloads
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/historico_sensores.pdf";

        try {
            // Criar o PdfWriter e o PdfDocument
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Adicionar título ao PDF
            document.add(new Paragraph("Histórico dos Sensores"));

            // Adicionar os dados coletados ao PDF
            for (String dado : dados) {
                document.add(new Paragraph(dado));
            }

            // Fechar o documento
            document.close();
            Toast.makeText(this, "PDF gerado com sucesso: " + filePath, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criar PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
