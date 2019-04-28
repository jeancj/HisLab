package com.example.hislab.DAO;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hislab.Classes.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioDAO {

    private static Usuario usuarioBusca;

    public static void insereUsuario(Usuario usuario, FirebaseUser usuarioAuth ){

        try{
            //Usa a referencia do firebase para inserir nós em usuarios
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase().child( "usuarios/" + usuarioAuth.getUid() );
            //adiciona os valores do objeto usuário como JSON
            reference.setValue( usuario );

        } catch ( Exception e ){
            e.printStackTrace();
        }

    }

    public static void alteraUsuario( Usuario usuario, FirebaseUser usuarioAuth ){
        try{
            //Usa a referencia do firebase para inserir nós em usuarios
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase().child( "usuarios/" + usuarioAuth.getUid() );
            //adiciona os valores do objeto usuário como JSON
            reference.setValue( usuario );

        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    public static void removeUsuario( FirebaseUser usuarioAuth ){
        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase();
            reference.child( "usuarios/" + usuarioAuth.getUid() ).setValue(null);

        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    public static Usuario buscaUsuario( String email ){

        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase();

            Log.d("DEBUGGGGGG", "Uid: " + email);
            reference.child("usuarios").orderByChild("dsEmail").equalTo( email ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DEBUGGGGGG", "111111111");
                    for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){

                        usuarioBusca = postSnapshot.getValue( Usuario.class );
                        Log.d("DEBUGGGGGG", "post: " + usuarioBusca.getDsEmail());
                        Log.d("DEBUGGGGGG", "post: " + usuarioBusca.getDsNome());
                        Log.d("DEBUGGGGGG", "post: " + usuarioBusca.getDsSenha());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return usuarioBusca;

        } catch ( Exception e ){
            e.printStackTrace();
        }

        return null;

    }

}
