package com.example.hislab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.hislab.Classes.Usuario;
import com.example.hislab.DAO.ConfiguracaoFireBase;
import com.example.hislab.DAO.UsuarioDAO;
import com.example.hislab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ListagemPerfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    DatabaseReference reference;
    private String email;
    private String senha;

    private String txtOrigem = "";
    private String txtNome = "";
    private String txtSexo = "";
    private String txtDataNascimento = "";
    private String txtEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFireBase.getFireBase();

        email = getIntent().getExtras().getString("email");
        senha = getIntent().getExtras().getString("senha");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        atualizaNavHeader();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listagem_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_edit) {
            editarUsuario();
        } else if( id == R.id.nav_removeUser ){
            removerUsuario();
        } else if( id == R.id.nav_logout ) {
            deslogarUsuario();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void editarUsuario() {

        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        //Busca de usuarios
        reference.child("usuarios").orderByChild("dsEmail").equalTo( emailUsuarioLogado ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DEBUGGGGGG", "1111111");
                for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                    Log.d("DEBUGGGGGG", "2222222");
                    Usuario dadosUsuario = postSnapshot.getValue( Usuario.class );
                    Log.d("DEBUGGGGGG", "post: " + dadosUsuario.getDsEmail());

                    final Intent intent = new Intent( ListagemPerfil.this, EditaUsuario.class );
                    final Bundle bundle = new Bundle();

                    bundle.putString("origem", "editarUsuario");
                    bundle.putString("nome", dadosUsuario.getDsNome());
                    bundle.putString("email", dadosUsuario.getDsEmail());
                    bundle.putString("sexo", dadosUsuario.getTpSexo());
                    bundle.putString("dataNascimento", dadosUsuario.getDtNascimento());

                    intent.putExtras( bundle );
                    startActivity( intent );
                    finish();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removerUsuario() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, senha);

        UsuarioDAO.removeUsuario( autenticacao.getCurrentUser() );
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MSG", "Conta deletada");
                        }
                    }
                });
            }
        });

//        autenticacao.getCurrentUser().delete();
        autenticacao.signOut();

        Intent intent = new Intent( ListagemPerfil.this, MainActivity.class );
        startActivity( intent );
        finish();

    }

    public void deslogarUsuario(){

        autenticacao.signOut();

        Intent intent = new Intent( ListagemPerfil.this, MainActivity.class );
        startActivity( intent );
        finish();
    }

    public void atualizaNavHeader(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0 );
        TextView navName = headerView.findViewById(R.id.nomeUsuarioLogado);
        TextView navEmail = headerView.findViewById(R.id.emailUsuarioLogado);

        navName.setText( autenticacao.getCurrentUser().getDisplayName() );
        navEmail.setText( autenticacao.getCurrentUser().getEmail() );

    }

}
