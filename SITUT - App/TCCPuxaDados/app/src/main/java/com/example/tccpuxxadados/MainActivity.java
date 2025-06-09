package com.example.tccpuxxadados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tccpuxxadados.api.Forecast;
import com.example.tccpuxxadados.api.Results;
import com.example.tccpuxxadados.api.WeatherModel;
import com.example.tccpuxxadados.api.WeatherService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Integer currentTemperature;
    String currentCondition;
    private AppCompatButton btn_voltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_clima);


        btn_voltar = findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Form_home.class);
            startActivity(intent);
            finish();
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.hgbrasil.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherModel> call = service.getWeather("12b81fef","São Bernardo do Campo, SP");

        call.enqueue(new Callback<WeatherModel>() {
                         @Override
                         public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                             assert response.body() != null;
                             Results results = response.body().getResults();

                             setDataToView(R.id.temp_hoje, formatTempToC(results.getTemp()));
                             setDataToView(R.id.condicao_hoje, results.getDescription());
                             setDataToView(R.id.text_dataHoje,results.getDate());
                             Integer codeHoje = Integer.valueOf(results.getConditionCode());
                             getImageForCondition0(codeHoje);

                             List<Forecast> forecasts = results.getForecast();

                             String dayOfWeek1 = forecasts.get(1).getWeekday();
                             setDataToView(R.id.next_day1, dayOfWeek1);
                             String description1 = forecasts.get(1).getDescription();
                             setDataToView(R.id.cond_nextDay1, description1);
                             Integer minTemperature1 = forecasts.get(1).getMin();
                             setDataToView(R.id.minTemp_d1, formatTempToC(minTemperature1));
                             Integer maxTemperature1 = forecasts.get(1).getMax();
                             setDataToView(R.id.maxTemp_d1, formatTempToC(maxTemperature1));
                             String conditionCode1 = forecasts.get(1).getCondition();
                             getImageForCondition1(conditionCode1);
                             String dayOfWeek2 = forecasts.get(2).getWeekday();
                             setDataToView(R.id.next_day2, dayOfWeek2);
                             String descripition2 = forecasts.get(2).getDescription();
                             setDataToView(R.id.cond_nextDay2, descripition2);
                             Integer minTemperature2 = forecasts.get(2).getMin();
                             setDataToView(R.id.minTemp_d2, formatTempToC(minTemperature2));
                             Integer maxTemperature2 = forecasts.get(2).getMax();
                             setDataToView(R.id.maxTemp_d2, formatTempToC(maxTemperature2));
                             String conditionCode2 = forecasts.get(2).getCondition();
                             getImageForCondition(conditionCode2);

                         }

                         @Override
                         public void onFailure(Call<WeatherModel> call, Throwable t) {
                             t.printStackTrace();
                         }
                     }
        );
    }
    DatabaseReference referenceClima= FirebaseDatabase.getInstance("https://situt-258258-default-rtdb.firebaseio.com/").getReference("Clima/Previsao");
    private void getImageForCondition0(Integer condition) {

        ImageView imageCondition0 = findViewById(R.id.image_today);

        if (condition <=18 || (condition>=37 && condition<=43) || (condition>=45) || ((condition==35))) { //chuva
            imageCondition0.setImageResource(R.drawable.chuvaimg);
            referenceClima.setValue(1);

        } else if ( condition <=30) { //nublado
            imageCondition0.setImageResource(R.drawable.nubladoimg);
            referenceClima.setValue(0);
        } else if (condition==33){ //Ensolarado
            imageCondition0.setImageResource(R.drawable.noite);
            referenceClima.setValue(0);
        }else{
            imageCondition0.setImageResource(R.drawable.solimage);
            referenceClima.setValue(0);
        }

    }

    private void getImageForCondition1(String condition) {

        ImageView imageCondition1 = findViewById(R.id.icon_nextDay1);

        if (condition.equals("storm")||condition.equals("snow")||condition.equals("hail")||condition.equals("rain")) { //chuva
            imageCondition1.setImageResource(R.drawable.chuvaicon);

        } else if (condition.equals("fog")||condition.equals("cloud")||condition.equals("cloudly_day")||condition.equals("cloudly_night")||condition.equals("none_night")) { //nublado
            imageCondition1.setImageResource(R.drawable.nubladoicon);
        } else { //Ensolarado
            imageCondition1.setImageResource(R.drawable.solicon);
        }

    }
    private void getImageForCondition(String condition) {

        ImageView imageCondition2 = findViewById(R.id.icon_nextDay2);

        if (condition.equals("storm")||condition.equals("snow")||condition.equals("hail")||condition.equals("rain")) { //chuva
            imageCondition2.setImageResource(R.drawable.chuvaicon);

        } else if (condition.equals("fog")||condition.equals("cloud")||condition.equals("cloudly_day")||condition.equals("cloudly_night")||condition.equals("none_night")) { //nublado
            imageCondition2.setImageResource(R.drawable.nubladoicon);
        } else { //Ensolarado
            imageCondition2.setImageResource(R.drawable.solicon);
        }

    }

    private void setDataToView(int resourceId, String value) {
        TextView view = findViewById(resourceId);
        view.setText(value);
    }

    private String formatTempToC(Integer temperature) {
        return new DecimalFormat("#.#").format(temperature) + "\u2103";
    }
}