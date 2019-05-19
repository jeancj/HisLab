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
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase().child( "usuarios/" + usuarioAuth.getUid() );
            reference.setValue( usuario );

        } catch ( Exception e ){
            e.printStackTrace();
        }

    }

    public static void alteraUsuario( Usuario usuario, FirebaseUser usuarioAuth ){
        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase().child( "usuarios/" + usuarioAuth.getUid() );
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
        Log.d("DEBUGGGGGG", "entrei no busca usuario");
        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase();

            Log.d("DEBUGGGGGG", "vou pesquisar com: " + email);
            reference.child("usuarios").orderByChild("dsEmail").equalTo( email ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DEBUGGGGGG", "1111111");
                    for( DataSnapshot postSnapshot : dataSnapshot.getChildren() ){
                        Log.d("DEBUGGGGGG", "2222222");
                        usuarioBusca = postSnapshot.getValue( Usuario.class );
                        Log.d("DEBUGGGGGG", "post: " + usuarioBusca.getDsEmail());
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
