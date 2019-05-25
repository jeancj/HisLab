package com.example.hislab.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hislab.Classes.Usuario;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.DAO.UsuarioDAO;
import com.example.hislab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditaUsuario extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;

    private EditText edtEditNome;
    private Button btnEditarCadastro;
    private Button btnCancelarEdit;
    private String tpSexo;
    private RadioButton edtRadioFeminino;
    private RadioButton edtRadioMasculino;
    private EditText edtDtNascimento;
    private EditText edtEditSenha;
    private EditText edtEditConfirmaSenha;
    private Calendar calendario = Calendar.getInstance();

    private String txtOrigem = "";
    private String txtNome = "";
    private String txtSexo = "";
    private String txtDataNascimento = "";
    private String txtEmail = "";

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
        setContentView(R.layout.activity_edita_usuario);

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();

        edtEditNome = (EditText) findViewById( R.id.edtEditNome );
        edtRadioMasculino = (RadioButton) findViewById( R.id.edtRadioMasculino );
        edtRadioFeminino = (RadioButton) findViewById( R.id.edtRadioFeminino );
        tpSexo = null;
        edtDtNascimento = (EditText) findViewById( R.id.edtEditDtNascimento );
        edtEditSenha = (EditText) findViewById( R.id.edtEditSenha );
        edtEditConfirmaSenha = (EditText) findViewById( R.id.edtEditConfirmaSenha );

        btnEditarCadastro = (Button) findViewById( R.id.btnEditarCadastro );
        btnCancelarEdit = (Button) findViewById( R.id.btnCancelarEdit );

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        txtOrigem = bundle.getString("origem");
        if( txtOrigem.equals("editarUsuario") ){
            txtNome = bundle.getString("nome");
            txtEmail = bundle.getString("email");
            txtSexo = bundle.getString("sexo");
            txtDataNascimento = bundle.getString("dataNascimento");

            edtEditNome.setText( txtNome );
            edtDtNascimento.setText( txtDataNascimento );

            if( txtSexo.equals("M") ){
                edtRadioMasculino.setChecked(true);
            } else if( txtSexo.equals("F") ){
                edtRadioFeminino.setChecked(true);
            }

        }

        btnCancelarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( EditaUsuario.this, ListagemPerfil.class );
                startActivity( intent );
                finish();
            }
        });

        btnEditarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( edtEditSenha.getText().toString().equals( edtEditConfirmaSenha.getText().toString() ) ){

                    Usuario usuario = new Usuario();
                    usuario.setDsNome( edtEditNome.getText().toString() );
                    usuario.setDtNascimento( edtDtNascimento.getText().toString() );
                    usuario.setDsSenha( edtEditSenha.getText().toString() );

                    if( edtRadioMasculino.isChecked() ){
                        usuario.setTpSexo("M");
                    } else if( edtRadioFeminino.isChecked() ) {
                        usuario.setTpSexo("F");
                    }

                    //chave por isso não é atualizado
                    usuario.setDsEmail( txtEmail );

                    atualizaDados( usuario );

                } else {
                    Toast.makeText(EditaUsuario.this, "As senhas não conferem", Toast.LENGTH_LONG ).show();
                }

            }
        });

        edtDtNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditaUsuario.this, dataSelecionada,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        edtDtNascimento.setText(sdf.format(calendario.getTime()));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_masculino:
                if (checked)
                    tpSexo = "M";
                break;
            case R.id.radio_feminino:
                if (checked)
                    tpSexo = "F";
                break;
        }
    }

    private boolean atualizaDados( final Usuario usuario ){

        btnEditarCadastro.setEnabled(false);

        try{

//            reference = ConfiguracaoFireBase.getFireBase().child("usuarios");
            if( !usuario.getDsSenha().trim().equals( "" ) ){
                atualizarSenha(usuario.getDsSenha());
            }
            UsuarioDAO.alteraUsuario(usuario, FirebaseAuth.getInstance().getCurrentUser());

            Toast.makeText(EditaUsuario.this, "Dados Alterados com sucesso!", Toast.LENGTH_LONG ).show();
            Intent intent = new Intent( EditaUsuario.this, ListagemPerfil.class );
            startActivity(intent);
            finish();
//            abrirTelaPrincipal();
//            finish();

        } catch ( Exception e ){
            e.printStackTrace();
        }

        return true;
    }

    private void atualizarSenha( String senhaNova ){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword( senhaNova ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if( task.isSuccessful() ){
                    Log.d( "NOVA_SENHA_ATUALIZADA", "Senha atualizada com sucesso!" );
                }

            }
        });

    }

    private void abrirTelaPrincipal(){



    }

}
