package com.example.hislab.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hislab.Classes.LinhaHistorico;
import com.example.hislab.DAO.HistoricoDAO;
import com.example.hislab.R;

import java.util.ArrayList;

public class HistoricoAdapter extends ArrayAdapter<LinhaHistorico> {

    private final Context context;
    private final ArrayList<LinhaHistorico> elementos;

    public HistoricoAdapter( Context context, ArrayList<LinhaHistorico> elementos ){
        super( context, R.layout.linhahistorico, elementos );
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( context.LAYOUT_INFLATER_SERVICE );
        View rowView = inflater.inflate( R.layout.linhahistorico, parent, false );

        TextView exame = (TextView) rowView.findViewById( R.id.txtLinhaExame );
        TextView data = (TextView) rowView.findViewById( R.id.txtLinhaData );
        TextView valor = (TextView) rowView.findViewById( R.id.txtLinhaValor );
        TextView refInf = (TextView) rowView.findViewById( R.id.txtLinhaRefInf );
        TextView refSup = (TextView) rowView.findViewById( R.id.txtLinhaRefSup );
        ImageButton btnExcluirHistorico = (ImageButton) rowView.findViewById( R.id.btnExcluirHistorico );

        btnExcluirHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder msgConfirmacao = new AlertDialog.Builder( context );
                msgConfirmacao.setTitle( "Excluindo..." );
                msgConfirmacao.setMessage( "Tem certeza que deseja excluir este item?" );
                msgConfirmacao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HistoricoDAO.removeHistorico( elementos.get(position).getIdHistorico() );
                        Toast.makeText(context, "Histórico excluído com sucesso!" , Toast.LENGTH_LONG).show();
                    }
                });
                msgConfirmacao.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                msgConfirmacao.show();
            }
        });

        exame.setText( elementos.get( position ).getExame() );
        data.setText( elementos.get( position ).getData() );
        valor.setText( elementos.get( position ).getValor() );
        refInf.setText( elementos.get( position ).getRefInf() );
        refSup.setText( elementos.get( position ).getRefSup() );

        return rowView;
    }
}
