package com.example.hislab.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
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

                        //TODO repetir os dados alimentando o grafico

                        final Long idExameSelecionado = parent.getSelectedItemId();
                        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

                        reference.child("historico")
                                .orderByChild("dsEmail").equalTo(emailUsuarioLogado)
//                                .orde("dsEmail").equalTo(emailUsuarioLogado)
//                                .orderByChild("idExame").equalTo(idExameSelecionado)
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                grafico = (LineChart) findViewById(R.id.graficoLinha);
                                grafico.setDragEnabled(true);
                                grafico.setScaleEnabled(false);

                                final ArrayList<String> legendaX = new ArrayList<>();
                                final ArrayList<Entry> vlResultados = new ArrayList<>();
                                final ArrayList<Entry> vlLimiteSup = new ArrayList<>();
                                final ArrayList<Entry> vlLimiteInf = new ArrayList<>();

                                int index = 0;

                                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                                    Historico dadosHistorico = postSnapshot.getValue( Historico.class );

                                    if( dadosHistorico.getIdExame().equals( idExameSelecionado + "" ) ) {

                                        Log.d("GRAFF", "##################");
                                        Log.d("GRAFF", "" + dadosHistorico.getIdExame());
                                        Log.d("GRAFF", "" + dadosHistorico.getDtExame());
                                        Log.d("GRAFF", "" + dadosHistorico.getVlExame());
                                        Log.d("GRAFF", "" + dadosHistorico.getVlReferenciaInferior());
                                        Log.d("GRAFF", "" + dadosHistorico.getVlReferenciaSuperior());

                                        vlResultados.add( new Entry(index, dadosHistorico.getVlExame().floatValue() ) );

                                        if( dadosHistorico.getVlReferenciaSuperior() != null ) {
                                            vlLimiteSup.add(new Entry(index, dadosHistorico.getVlReferenciaSuperior().floatValue()));
                                        }
                                        if( dadosHistorico.getVlReferenciaSuperior() != null ) {
                                            vlLimiteInf.add( new Entry(index, dadosHistorico.getVlReferenciaInferior().floatValue() ) );
                                        }

                                        legendaX.add( dadosHistorico.getDtExame() );
                                        index++;
                                    }

                                }

                                LineDataSet resultados = new LineDataSet( vlResultados, "Resultado" );
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
                                x.setEnabled(true);
                                x.setSpaceMax(vlResultados.size());
                                x.setSpaceMin(vlResultados.size());
                                x.setValueFormatter(new IAxisValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, AxisBase axis) {
                                        return legendaX.get( (int) value );
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                        //TODO fim

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
