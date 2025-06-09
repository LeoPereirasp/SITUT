package com.example.tccpuxxadados;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;

public class IntroPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] layouts;  // Array de layouts (IDs de recursos) para os slides

    // Construtor do adaptador que recebe o contexto e os layouts dos slides
    public IntroPagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Infla o layout do slide usando o LayoutInflater
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position], container, false);

        // Carrega o GIF correspondente para cada slide
        if (position == 0) {
            // Slide 1: Carregar o GIF para o primeiro slide
            ImageView imageView = view.findViewById(R.id.imagem_situt2);  // Referência ao ImageView no layout
            Glide.with(context)
                    .asGif()  // Especifica que o tipo de recurso a ser carregado é um GIF
                    .load(R.drawable.situtw)  // Substitua pelo nome do seu primeiro GIF
                    .into(imageView);  // Carrega o GIF na ImageView
        } else if (position == 1) {
            // Slide 2: Carregar o GIF para o segundo slide
            ImageView imageView = view.findViewById(R.id.imagem_plantairrigada); // ID do ImageView no slide 2
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.planta)  // Substitua pelo nome do seu segundo GIF
                    .into(imageView);
        } else if (position == 2) {
            // Slide 3: Carregar o GIF para o terceiro slide
            ImageView imageView = view.findViewById(R.id.imagem_clima); // ID do ImageView no slide 3
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.climagif)  // Substitua pelo nome do seu terceiro GIF
                    .into(imageView);
        } else if (position == 3) {
            // Slide 4: Carregar o GIF para o quarto slide
            ImageView imageView = view.findViewById(R.id.imagem_sensor); // ID do ImageView no slide 4
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.sensorplanta)  // Substitua pelo nome do seu quarto GIF
                    .into(imageView);
        } else if (position == 4) {
            // Slide 5: Carregar o GIF para o quinto slide
            ImageView imageView = view.findViewById(R.id.imagem_grafico); // ID do ImageView no slide 5
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.grafico)  // Substitua pelo nome do seu quinto GIF
                    .into(imageView);
        } else if (position == 5) {
            // Slide 6: Carregar o GIF para o sexto slide
            ImageView imageView = view.findViewById(R.id.imagem_home); // ID do ImageView no slide 6
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.homegif)  // Substitua pelo nome do seu sexto GIF
                    .into(imageView);
        } else if (position == 6) {
            // Slide 7: Carregar o GIF para o sétimo slide
            ImageView imageView = view.findViewById(R.id.imagem_situt); // ID do ImageView no slide 7
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.situtgif)  // Substitua pelo nome do seu sétimo GIF
                    .into(imageView);
        }

        // Adiciona o slide ao container do ViewPager
        container.addView(view);
        return view;  // Retorna a view inflada para o ViewPager
    }

    @Override
    public int getCount() {
        return layouts.length;  // Retorna o número total de slides
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;  // Verifica se a view passada é a mesma do objeto
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove a view do ViewPager
        View view = (View) object;
        container.removeView(view);
    }
}
