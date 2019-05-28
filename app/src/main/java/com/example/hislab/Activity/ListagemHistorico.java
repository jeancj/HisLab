package com.example.hislab.Activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.hislab.Classes.Exame;
import com.example.hislab.Classes.Historico;
import com.example.hislab.Classes.LinhaHistorico;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListagemHistorico extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;

    private ImageButton btnVoltarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFireBase.getFireBase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_historico);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltarPerfil = (ImageButton) findViewById( R.id.btnVoltarPerfil );
        btnVoltarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ListagemHistorico.this, ListagemPerfil.class );
                startActivity( intent );
                finish();
            }
        });


        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        //buscando historico
        reference.child("historico").orderByChild("dsEmail").equalTo( emailUsuarioLogado ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<LinhaHistorico> historico = new ArrayList<LinhaHistorico>();

                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){

                    final Historico dadosHistorico = postSnapshot.getValue( Historico.class );
                    final LinhaHistorico linha = new LinhaHistorico();

                    //buscando exame
                    reference.child("exames").orderByChild("idExame").equalTo( dadosHistorico.getIdExame() ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){

                                Exame dadosExame = postSnapshot.getValue( Exame.class );
                                linha.setExame( dadosExame.getDsExame() );
                                linha.setIdExame( dadosHistorico.getIdExame() );
                                linha.setData( dadosHistorico.getDtExame() );
                                linha.setIdHistorico( dadosHistorico.getIdHistorico() );
                                linha.setValor( "Valor: " + dadosHistorico.getVlExame() + " " + dadosExame.getDsMedida() );
                                if( dadosHistorico.getVlReferenciaInferior() != null ) {
                                    linha.setRefInf( "Ref. Inf: " + dadosHistorico.getVlReferenciaInferior() + " " + dadosExame.getDsMedida() );
                                }
                                if( dadosHistorico.getVlReferenciaSuperior() != null ) {
                                    linha.setRefSup( "Ref. Sup: " + dadosHistorico.getVlReferenciaSuperior() + " " + dadosExame.getDsMedida() );
                                }
                                historico.add( linha );
                            }

                            ListView lista = (ListView) findViewById( R.id.lvHistorico );
                            ArrayAdapter adapter = new HistoricoAdapter( ListagemHistorico.this, historico );
                            lista.setAdapter( adapter );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fabIncluirHistorico);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( ListagemHistorico.this, CadastroHistorico.class );
                startActivity( intent );
                finish();
            }
        });
    }

}
