package com.example.hislab.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hislab.Classes.Exame;
import com.example.hislab.Classes.Historico;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.DAO.HistoricoDAO;
import com.example.hislab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CadastroHistorico extends AppCompatActivity {

    private EditText edtVlExame;
    private EditText edtVlRefInf;
    private EditText edtVlRefSup;
    private Button btnSalvarHistorico;
    private Button btnVoltarHistorico;
    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private EditText dtExame;
    private Spinner spExameHistorico;
    private Calendar calendario = Calendar.getInstance();
    private Historico historico;
    private Long idExameSelecionado;

    DatePickerDialog.OnDateSetListener dataSelecionada = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, monthOfYear);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_historico);

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();
        reference = ConfiguracaoFireBase.getFireBase();

        edtVlExame = (EditText) findViewById( R.id.edtVlExame );
        edtVlRefInf = (EditText) findViewById( R.id.edtVlRefInf );
        edtVlRefSup = (EditText) findViewById( R.id.edtVlRefSup );
        btnSalvarHistorico = (Button) findViewById( R.id.btnSalvarHistorico );
        btnVoltarHistorico = (Button) findViewById( R.id.btnVoltarHistorico );
        dtExame = (EditText) findViewById( R.id.edtDtExame );

        dtExame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(CadastroHistorico.this, dataSelecionada,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        reference.child("exames").orderByChild("idExame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Exame> arrayExames = new ArrayList<Exame>();

                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                    Exame dadosExame = postSnapshot.getValue( Exame.class );
                    arrayExames.add( dadosExame );
                }

                spExameHistorico = new Spinner( CadastroHistorico.this );

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                spExameHistorico.setLayoutParams( lp );
                spExameHistorico.setAdapter( new ExameAdapter( CadastroHistorico.this, arrayExames ) );

                spExameHistorico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idExameSelecionado = parent.getSelectedItemId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                LinearLayout ll = (LinearLayout) findViewById( R.id.linearlayoutGrafico );
                ll.addView( spExameHistorico, 1 );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        btnVoltarHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CadastroHistorico.this, ListagemHistorico.class );
                startActivity( intent );
                finish();
            }
        });

        btnSalvarHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !dtExame.getText().toString().equals( "" ) ){
                    if( !edtVlExame.getText().toString().equals( "" ) ){
                        if( "" != null ){

                                historico = new Historico();
                                historico.setDsEmail( autenticacao.getCurrentUser().getEmail() );
                                historico.setDtExame( dtExame.getText().toString() );
                                historico.setIdExame( Long.toString( idExameSelecionado ) );
                                historico.setVlExame( Double.parseDouble( edtVlExame.getText().toString() ) );

                                if( !edtVlRefInf.getText().toString().equals( "" ) ) {
                                    historico.setVlReferenciaInferior( Double.parseDouble( edtVlRefInf.getText().toString() ) );
                                }
                                if( !edtVlRefSup.getText().toString().equals( "" ) ) {
                                    historico.setVlReferenciaSuperior( Double.parseDouble( edtVlRefSup.getText().toString() ) );
                                }

                                cadastrarHistorico();

                        } else {
                            Toast.makeText( CadastroHistorico.this, "Selecione um exame!", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( CadastroHistorico.this, "Preencha o valor do exame!", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( CadastroHistorico.this, "Preencha o campo de data de realização do exame!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        dtExame.setText(sdf.format(calendario.getTime()));
    }

    private void cadastrarHistorico(){

        try{
            HistoricoDAO.insereHistorico( historico, autenticacao.getCurrentUser() );
            Toast.makeText( CadastroHistorico.this, "Histórico cadastrado com sucesso!", Toast.LENGTH_LONG ).show();

            limpaCampos();

        } catch( Exception e ){
            Toast.makeText( CadastroHistorico.this, "Erro ao gravar o histórico!", Toast.LENGTH_LONG ).show();
        }

    }

    private void limpaCampos(){

        edtVlExame.setText( "" );
        edtVlRefInf.setText( "" );
        edtVlRefSup.setText( "" );
        dtExame.setText( "" );

    }

}
