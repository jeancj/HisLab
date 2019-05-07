package com.example.hislab.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hislab.Classes.Usuario;
import com.example.hislab.DAO.UsuarioDAO;
import com.example.hislab.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditaUsuario extends AppCompatActivity {

    private EditText edtEditNome;
    private EditText edtEditEmail;
    private Button btnEditarCadastro;
    private Button btnCancelarEdit;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private String tpSexo;
    private EditText dtNascimento;
    private Calendar calendario = Calendar.getInstance();

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

        edtEditNome = (EditText) findViewById( R.id.edtEditNome );
        edtEditEmail = (EditText) findViewById( R.id.edtEditEmail );
        btnEditarCadastro = (Button) findViewById( R.id.btnEditarCadastro );
        btnCancelarEdit = (Button) findViewById( R.id.btnCancelarEdit );
        dtNascimento = (EditText) findViewById( R.id.edtEditDtNascimento );
        tpSexo = null;

        Usuario dadosUsuario = UsuarioDAO.buscaUsuario( autenticacao.getCurrentUser().getEmail() );
        edtEditEmail.setText( dadosUsuario.getDsEmail() );
        edtEditNome.setText( dadosUsuario.getDsNome() );
        dtNascimento.setText( dadosUsuario.getDtNascimento() );
        tpSexo = dadosUsuario.getTpSexo();

        btnCancelarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( EditaUsuario.this, ListagemPerfil.class );
                startActivity( intent );
            }
        });

        btnEditarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !edtEditNome.getText().toString().equals( "" ) && !edtEditEmail.getText().toString().equals( "" ) ){
                    if( tpSexo != null ){

                        usuario = new Usuario();
                        usuario.setDsNome( edtEditNome.getText().toString() );
                        usuario.setDsEmail( edtEditEmail.getText().toString() );
                        usuario.setTpSexo( tpSexo );
                        usuario.setDtNascimento( dtNascimento.getText().toString() );

                        editarUsuario();

                    } else {
                        Toast.makeText( EditaUsuario.this, "Sexo não informado!", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( EditaUsuario.this, "Preencha os campos de E-mail e nome!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        dtNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditaUsuario.this, dataSelecionada,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public void editarUsuario(){
        UsuarioDAO.alteraUsuario( usuario, autenticacao.getCurrentUser() );
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        dtNascimento.setText(sdf.format(calendario.getTime()));
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

}
