package com.example.hislab.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hislab.Classes.Exame;
import com.example.hislab.Classes.Historico;
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
import java.util.Collections;

public class VisualizaGrafico extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private Spinner spExameGrafico;
    private LineChart grafico;

    ArrayList<String> legendaX;
    ArrayList<Entry> vlResultados;
    ArrayList<Entry> vlLimiteSup;
    ArrayList<Entry> vlLimiteInf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_grafico);

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();
        reference = ConfiguracaoFireBase.getFireBase();

        reference.child("exames").orderByChild("idExame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<Exame> arrayExames = new ArrayList<Exame>();

                Exame e = new Exame();
                e.setIdExame("99999");
                e.setDsExame("Selecionar");
                arrayExames.add(e);

                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                    Exame dadosExame = postSnapshot.getValue( Exame.class );
                    arrayExames.add( dadosExame );
                }

                spExameGrafico = new Spinner( VisualizaGrafico.this );

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                spExameGrafico.setLayoutParams( lp );
                spExameGrafico.setAdapter( new ExameAdapter( VisualizaGrafico.this, arrayExames ) );

                spExameGrafico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        final Exame exameSelecionado = (Exame) parent.getSelectedItem();
                        final String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

                        reference.child("historico")
                                .orderByChild("dsEmail").equalTo(emailUsuarioLogado)
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                grafico = (LineChart) findViewById(R.id.graficoLinha);
                                grafico.setDragEnabled(true);
                                grafico.setScaleEnabled(false);

//                                final ArrayList<String> legendaX = new ArrayList<>();
//                                ArrayList<Entry> vlResultados = new ArrayList<>();
//                                ArrayList<Entry> vlLimiteSup = new ArrayList<>();
//                                ArrayList<Entry> vlLimiteInf = new ArrayList<>();

                                legendaX = new ArrayList<>();
                                vlResultados = new ArrayList<>();
                                vlLimiteSup = new ArrayList<>();
                                vlLimiteInf = new ArrayList<>();

//                                legendaX.clear();
//                                vlResultados.clear();
//                                vlLimiteSup.clear();
//                                vlLimiteInf.clear();
                                int index = 0;

                                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                                    Historico dadosHistorico = postSnapshot.getValue( Historico.class );

                                    if( dadosHistorico.getIdExame().equals( exameSelecionado.getIdExame() ) ) {

                                        vlResultados.add( new Entry(index, dadosHistorico.getVlExame().floatValue() ) );

                                        if( dadosHistorico.getVlReferenciaSuperior() != null ) {
                                            vlLimiteSup.add(new Entry(index, dadosHistorico.getVlReferenciaSuperior().floatValue()));
                                        } else {
                                            if( exameSelecionado.getVlReferenciaSuperior() != null ) {
                                                vlLimiteSup.add(new Entry(index, exameSelecionado.getVlReferenciaSuperior().floatValue()));
                                            }
                                        }
                                        if( dadosHistorico.getVlReferenciaSuperior() != null ) {
                                            vlLimiteInf.add( new Entry(index, dadosHistorico.getVlReferenciaInferior().floatValue()));
                                        } else {
                                            if( exameSelecionado.getVlReferenciaInferior() != null ) {
                                                vlLimiteInf.add(new Entry(index, exameSelecionado.getVlReferenciaInferior().floatValue()));
                                            }
                                        }

                                        legendaX.add( dadosHistorico.getDtExame() );
                                        index++;
                                    }

                                }

                                if( !vlResultados.isEmpty() ){

                                    LineDataSet resultados = new LineDataSet( vlResultados, "Resultado em " + exameSelecionado.getDsMedida() );
                                    LineDataSet limiteSuperior = new LineDataSet( vlLimiteSup, "Limite Sup." );
                                    LineDataSet limiteInferior = new LineDataSet( vlLimiteInf, "Limite Inf." );

                                    resultados.setFillAlpha(110);
                                    resultados.setColor(Color.GREEN);
                                    resultados.setLineWidth(7f);
                                    resultados.setValueTextSize(15f);

                                    limiteSuperior.setFillAlpha(110);
                                    limiteSuperior.setColor(Color.RED);
                                    limiteSuperior.setLineWidth(5f);
                                    limiteSuperior.setValueTextSize(12f);

                                    limiteInferior.setFillAlpha(110);
                                    limiteInferior.setColor(Color.RED);
                                    limiteInferior.setLineWidth(5f);
                                    limiteInferior.setValueTextSize(12f);

                                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                    dataSets.add( resultados );
                                    dataSets.add( limiteSuperior );
                                    dataSets.add( limiteInferior );

                                    LineData data = new LineData( dataSets );
                                    data.setValueTextSize(12f);
                                    grafico.setData(data);

                                    XAxis x = grafico.getXAxis();
                                    x.setGranularity(1f);
                                    x.setValueFormatter(new IAxisValueFormatter() {
                                        @Override
                                        public String getFormattedValue(float value, AxisBase axis) {
                                            Log.d("TAMANHO", "value: " + value + " Axis: " + axis);

                                            if( value < 0  ){
                                                value = 0;
                                            }

                                            if( value < legendaX.size() ){
                                                return legendaX.get((int) value);
                                            } else {
                                                return "";
                                            }

                                        }
                                    });

                                } else {
                                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                    LineData data = new LineData( dataSets );
                                    data.setValueTextSize(12f);
                                    grafico.setData(data);
//                                    Toast.makeText(VisualizaGrafico.this, "Não foram encontrados históricos para o exame selecionado", Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
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
