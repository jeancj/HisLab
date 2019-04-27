package com.example.hislab.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hislab.Classes.Usuario;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private EditText edtEmailLogin;
    private EditText edtSenhaLogin;
    private Button btnLogin;
    private Button btnCadastrar;
    private Usuario usuario;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        edtEmailLogin = (EditText) findViewById( R.id.edtEmail );
        edtSenhaLogin = (EditText) findViewById( R.id.edtSenha );
        btnLogin = (Button) findViewById( R.id.btnLogin );
        btnCadastrar = (Button) findViewById( R.id.btnCadastrar );

        btnCadastrar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, CadastroUsuario.class );
                abrirNovaActivity( intent );
            }
        });

        if( usuarioLogado() ){

            Intent intent = new Intent( MainActivity.this, ListagemPerfil.class );
            abrirNovaActivity( intent );

        } else {

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!edtEmailLogin.getText().toString().equals("") && !edtSenhaLogin.getText().toString().equals("")) {
                        usuario = new Usuario();
                        usuario.setDsEmail(edtEmailLogin.getText().toString());
                        usuario.setDsSenha(edtSenhaLogin.getText().toString());
                        validarLogin();
                    } else {
                        Toast.makeText(MainActivity.this, "Preencha os campos de E-mail e senha", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public Boolean usuarioLogado(){

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if( usuario != null ){
            return true;
        } else {
            return false;
        }
    }

    private void validarLogin(){

        autenticacao = ConfiguracaoFireBase.getFireBaseAuth();
        autenticacao.signInWithEmailAndPassword( usuario.getDsEmail().toString(), usuario.getDsSenha().toString() ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    Intent intent = new Intent( MainActivity.this, ListagemPerfil.class );
                    startActivity( intent );
                    Toast.makeText( MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( MainActivity.this, "Usuário ou senha inválidos! Tente novamente!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

    }

    public void abrirNovaActivity( Intent intent ){
        startActivity( intent );
    }

}
