package com.example.tccpuxxadados;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormSobrenos extends AppCompatActivity {

    private AppCompatButton btn_voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sobrenos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_voltar = findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(FormSobrenos.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        // Configurando o clique do botão btnsaibamais
        findViewById(R.id.btn_saibamais).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://situt-tpg.web.app/sobre.html"));
                startActivity(intent);
            }
        });
    }
}
