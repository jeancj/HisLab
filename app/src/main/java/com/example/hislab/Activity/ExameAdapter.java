package com.example.hislab.Activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hislab.Classes.Exame;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExameAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Exame> lista;

//    public ExameAdapter( Context context, ArrayList<String> lista ){
    public ExameAdapter( Context context, ArrayList<Exame> lista ){
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position+1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tv = new TextView(context);
        tv.setTextSize(20);
        tv.setText(lista.get(position).getDsExame());

        return tv;
    }
}
