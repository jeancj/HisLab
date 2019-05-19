package com.example.hislab.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadastroUsuario extends AppCompatActivity {

    private EditText edtCadNome;
    private EditText edtCadEmail;
    private EditText edtCadSenha;
    private EditText edtCadConfirmaSenha;
    private Button btnSalvarCadastro;
    private Button btnCancelarCadastro;
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
        setContentView(R.layout.activity_cadastro_usuario);

        edtCadNome = (EditText) findViewById( R.id.edtCadNome );
        edtCadEmail = (EditText) findViewById( R.id.edtCadEmail );
        edtCadSenha = (EditText) findViewById( R.id.edtCadSenha );
        edtCadConfirmaSenha = (EditText) findViewById( R.id.edtCadConfirmaSenha );
        btnSalvarCadastro = (Button) findViewById( R.id.btnSalvarCadastro );
        btnCancelarCadastro = (Button) findViewById( R.id.btnCancelarCadastro );
        dtNascimento = (EditText) findViewById( R.id.edtDtNascimento );
        tpSexo = null;

        btnCancelarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CadastroUsuario.this, MainActivity.class );
                startActivity( intent );
            }
        });

        btnSalvarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !edtCadNome.getText().toString().equals( "" ) && !edtCadEmail.getText().toString().equals( "" ) ){
                    if( !edtCadSenha.getText().toString().equals( "" ) && !edtCadConfirmaSenha.getText().toString().equals( "" ) ){
                        if( edtCadSenha.getText().toString().equals( edtCadConfirmaSenha.getText().toString() ) ){
                            if( tpSexo != null ){

                                usuario = new Usuario();
                                usuario.setDsNome( edtCadNome.getText().toString() );
                                usuario.setDsEmail( edtCadEmail.getText().toString() );
                                usuario.setDsSenha( edtCadSenha.getText().toString() );
                                usuario.setTpSexo( tpSexo );
                                usuario.setDtNascimento( dtNascimento.getText().toString() );

                                cadastrarUsuario();

                            } else {
                                Toast.makeText( CadastroUsuario.this, "Sexo não informado!", Toast.LENGTH_SHORT ).show();
                            }
                        } else {
                            Toast.makeText( CadastroUsuario.this, "Senha e confirmação estão diferentes!", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( CadastroUsuario.this, "Preencha os senha e confirmação da senha!", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( CadastroUsuario.this, "Preencha os campos de E-mail e nome!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        dtNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(CadastroUsuario.this, dataSelecionada,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        dtNascimento.setText(sdf.format(calendario.getTime()));
    }

    private void cadastrarUsuario(){

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getDsEmail(),
                usuario.getDsSenha()
        ).addOnCompleteListener(CadastroUsuario.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful() ){
                    try{
                        UsuarioDAO.insereUsuario( usuario, autenticacao.getCurrentUser() );
                        Toast.makeText( CadastroUsuario.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG ).show();

                        Intent intent = new Intent( CadastroUsuario.this, MainActivity.class );
                        startActivity( intent );

                    } catch( Exception e ){
                        Toast.makeText( CadastroUsuario.this, "Erro ao gravar o usuário!", Toast.LENGTH_LONG ).show();
                    }
                } else {
                    String erroExcessao = "";
                    try{
                        throw  task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erroExcessao = "Digite uma senha mais forte, contendo no mínimo 8 caracteres e que contenha letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcessao = "O e-mail digitado é inválido, digite um novo e-mail!";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroExcessao = "Esse e-mail já está cadastrado!";
                    } catch (Exception e) {
                        erroExcessao = "Erro ao efetuar o cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText( CadastroUsuario.this, "Erro: " + erroExcessao, Toast.LENGTH_LONG ).show();
                }

            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

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
