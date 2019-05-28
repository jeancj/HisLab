package com.example.hislab.Activity;

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

                        String strURL = "https://chart.googleapis.com/chart?" +
                        "cht=lc&" + //define o tipo do gráfico "linha"
                                "chxt=x,y&" + //imprime os valores dos eixos X, Y
                                "chs=300x300&" + //define o tamanho da imagem
                                "chd=t:10,45,5,10,13,26&" + //valor de cada coluna do gráfico
                                "chl=Jan|Fev|Mar|Abr|Mai|Jun&" + //rótulo para cada coluna
                                "chdl=Vendas&" + //legenda do gráfico
                                "chxr=1,0,50&" + //define o valor de início e fim do eixo
                                "chds=0,50&" + //define o valor de escala dos dados
                                "chg=0,5,0,0&" + //desenha linha horizontal na grade
                                "chco=3D7930&" + //cor da linha do gráfico
                                "chtt=Vendas+x+1000&" + //cabeçalho do gráfico
                                "chm=B,0,0,0"; //fundo verde

                        WebView wvGrafico = (WebView)findViewById(R.id.webViewGrafico);
                        wvGrafico.loadUrl(strURL);

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
