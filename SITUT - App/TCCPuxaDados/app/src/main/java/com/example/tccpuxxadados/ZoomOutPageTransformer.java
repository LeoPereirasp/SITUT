package com.example.tccpuxxadados;

import android.view.View;
import androidx.viewpager.widget.ViewPager;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    // Define os limites para o efeito de zoom e opacidade.
    private static final float MIN_SCALE = 0.85f;  // Escala mínima das páginas durante a transição.
    private static final float MIN_ALPHA = 0.5f;  // Opacidade mínima das páginas durante a transição.

    @Override
    public void transformPage(View view, float position) {
        // Obtém a largura e altura da página
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        // Se a posição da página for menos que -1 (fora da tela à esquerda)
        if (position < -1) {
            view.setAlpha(0f);  // A página é invisível (completamente opaca).

            // Se a posição estiver dentro da faixa de visibilidade (-1 a 1)
        } else if (position <= 1) {
            // Calcula o fator de escala com base na posição (quanto mais perto de 0, maior a escala)
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));

            // Margens verticais e horizontais para aplicar o efeito de zoom
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            // Se a página está à esquerda (posição negativa), aplica a tradução à esquerda
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
                // Se a página está à direita (posição positiva), aplica a tradução à direita
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Aplica o efeito de escala na página
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Aplica a opacidade, dependendo do fator de escala
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            // Se a posição da página estiver fora da tela à direita (+1)
        } else {
            view.setAlpha(0f);  // A página é invisível (completamente opaca).
        }
    }
}
