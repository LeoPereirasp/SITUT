package com.example.tccpuxxadados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private IntroPagerAdapter adapter;
    private int[] layouts;
    private Button btn_passar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Inicializando os layouts do ViewPager
        layouts = new int[]{
                R.layout.intro_slide_0,
                R.layout.intro_slide_1,
                R.layout.intro_slide_2,
                R.layout.intro_slide_3,
                R.layout.intro_slide_4,
                R.layout.intro_slide_5,
                R.layout.intro_slide_6,
        };

        // Configurando o ViewPager e seu adaptador
        viewPager = findViewById(R.id.viewPager);
        adapter = new IntroPagerAdapter(this, layouts);
        viewPager.setAdapter(adapter);

        // Adicionando uma animação de transição personalizada
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        // Adicionando o listener para as mudanças de página
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Atualiza os dots na primeira inicialização
        updateDots(0);
    }

    private void updateDots(int position) {
        findViewById(R.id.dot1).setBackgroundResource(position == 0 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot2).setBackgroundResource(position == 1 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot3).setBackgroundResource(position == 2 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot4).setBackgroundResource(position == 3 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot5).setBackgroundResource(position == 4 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot6).setBackgroundResource(position == 5 ? R.drawable.branco : R.drawable.preta);
        findViewById(R.id.dot7).setBackgroundResource(position == 6 ? R.drawable.branco : R.drawable.preta);

        // Inicializa o botão apenas na posição 6
        if (position == 6) {
            btn_passar = findViewById(R.id.btn_avancar);
            if (btn_passar != null) { // Confirma se o botão existe
                btn_passar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(IntroActivity.this, Form_registro_sistema.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
