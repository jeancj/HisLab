package com.example.hislab.DAO;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hislab.Classes.Historico;
import com.example.hislab.Classes.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class HistoricoDAO {

    public static void insereHistorico(Historico historico, FirebaseUser usuarioAuth ){

        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase().child( "historico" );

            String idHistorico = reference.push().getKey();
            historico.setIdHistorico( idHistorico );

            reference.child( idHistorico ).setValue( historico );

        } catch ( Exception e ){
            e.printStackTrace();
        }

    }

    public static void removeHistorico( String idHistorico ){
        try{
            DatabaseReference reference = ConfiguracaoFireBase.getFireBase();
            reference.child( "historico/" + idHistorico ).setValue(null);

        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

}
