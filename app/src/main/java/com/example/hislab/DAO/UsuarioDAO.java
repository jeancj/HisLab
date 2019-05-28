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
}
