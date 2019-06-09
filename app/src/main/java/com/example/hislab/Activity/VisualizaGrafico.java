package com.example.hislab.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hislab.Classes.Exame;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VisualizaGrafico extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Spinner spExameGrafico;
    private LineChart grafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_grafico);

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();
        reference = ConfiguracaoFireBase.getFireBase();

        reference.child("exames").orderByChild("idExame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> arrayExames = new ArrayList<String>();

                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                    Exame dadosExame = postSnapshot.getValue( Exame.class );
                    arrayExames.add( dadosExame.getDsExame() );
                }

                spExameGrafico = new Spinner( VisualizaGrafico.this );

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                spExameGrafico.setLayoutParams( lp );
                spExameGrafico.setAdapter( new ExameAdapter( VisualizaGrafico.this, arrayExames ) );

                spExameGrafico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        idExameSelecionado = parent.getSelectedItemId();

//                        String strURL = "https://chart.googleapis.com/chart?" +
//                        "cht=lc&" + //define o tipo do gráfico "linha"
//                                "chxt=x,y&" + //imprime os valores dos eixos X, Y
//                                "chs=300x300&" + //define o tamanho da imagem
//                                "chd=t:10,45,5,10,13,26&" + //valor de cada coluna do gráfico
//                                "chl=Jan|Fev|Mar|Abr|Mai|Jun&" + //rótulo para cada coluna
//                                "chdl=Vendas&" + //legenda do gráfico
//                                "chxr=1,0,50&" + //define o valor de início e fim do eixo
//                                "chds=0,50&" + //define o valor de escala dos dados
//                                "chg=0,5,0,0&" + //desenha linha horizontal na grade
//                                "chco=3D7930&" + //cor da linha do gráfico
//                                "chtt=Vendas+x+1000&" + //cabeçalho do gráfico
//                                "chm=B,0,0,0"; //fundo verde
//
//                        WebView wvGrafico = (WebView)findViewById(R.id.webViewGrafico);
//                        wvGrafico.loadUrl(strURL);

                        grafico = (LineChart) findViewById(R.id.graficoLinha);

//                        grafico.setOnChartGestureListener(VisualizaGrafico.this);
//                        grafico.setOnChartValueSelectedListener(VisualizaGrafico.this);
                        grafico.setDragEnabled(true);
                        grafico.setScaleEnabled(false);

                        ArrayList<Entry> valorY = new ArrayList<>();
                        valorY.add( new Entry(0, 60f ) );
                        valorY.add( new Entry(1, 89.5f ) );
                        valorY.add( new Entry(2, 42.1f ) );
                        valorY.add( new Entry(3, 50f ) );
//                        valorY.add( new Entry(4, 23.8f ) );

                        final ArrayList<String> xAxis = new ArrayList<>();
                        xAxis.add("01/2019");
                        xAxis.add("02/2019");
                        xAxis.add("03/2019");
                        xAxis.add("04/2019");
                        xAxis.add("05/2019");
                        xAxis.add("06/2019");
                        xAxis.add("07/2019");
                        xAxis.add("08/2019");

                        LineDataSet set1 = new LineDataSet( valorY, "Resultado" );
                        set1.setFillAlpha(110);
                        set1.setColor(Color.GREEN);
                        set1.setLineWidth(3f);
                        set1.setValueTextSize(10f);

                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add( set1 );

                        LineData data = new LineData( dataSets );
                        grafico.setData(data);

                        XAxis x = grafico.getXAxis();
                        x.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return xAxis.get( (int) value );
                            }
                        });

                        Toast.makeText( VisualizaGrafico.this, "Selecionei " + parent.getSelectedItem() + ", vou gerar o gráfico", Toast.LENGTH_LONG ).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                LinearLayout ll = (LinearLayout) findViewById( R.id.linearlayoutGrafico );
                ll.addView( spExameGrafico, 0 );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
}
