package com.example.tiborkocik.camx_dochadzka;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    ArrayList<ZAMESTNANCI> arrayList = new ArrayList<>();
    RecyclerAdapter(ArrayList<ZAMESTNANCI> arrayList)
    {
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        ZAMESTNANCI zamestnanci = arrayList.get(position);
        holder.Name.setText(zamestnanci.getMeno());
        holder.Prich.setText(zamestnanci.getPrichod());
        holder.OdchObed.setText(zamestnanci.getOdchod_na_obed());
        holder.PrichObed.setText(zamestnanci.getPrichod_z_obeda());
        holder.OdchObed.setText(zamestnanci.getOdchod());
        holder.poznamka.setText(zamestnanci.getPoznamka());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static  class RecyclerViewHolder extends  RecyclerView.ViewHolder
    {
        TextView Name,Prich,OdchObed,PrichObed,odcho,poznamka;
        RecyclerViewHolder(View view)
        {
            super(view);
            Name = (TextView)view.findViewById(R.id.viewPrichod);
            Prich = (TextView)view.findViewById(R.id.viewPrichod);
            OdchObed = (TextView)view.findViewById(R.id.viewOdchodNaObed);
            PrichObed = (TextView)view.findViewById(R.id.viewPrichodZObeda);
            odcho = (TextView)view.findViewById(R.id.viewOdchod);
            poznamka = (TextView)view.findViewById(R.id.viewPoznamka);
        }
    }
}