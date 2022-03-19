package com.example.covidtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class modelAdapter extends RecyclerView.Adapter<modelAdapter.ViewHolder> {

    @NonNull
    private ArrayList<Statemodel>statemodelArrayList;
    private Context context;

    public modelAdapter(@NonNull ArrayList<Statemodel> statemodelArrayList, Context context) {
        this.statemodelArrayList = statemodelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.statrelayout, parent, false);
        return new modelAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     Statemodel statemodel= statemodelArrayList.get(position);
     holder.RVname.setText(statemodel.getSname());
     holder.RVdname.setText(statemodel.getDname());
     holder.RVConfirmed.setText(""+Math.toIntExact((Long) statemodel.getConfirmed()));
     holder.RVDecreased.setText(""+Math.toIntExact((Long) statemodel.getDecreased()));
     holder.RVRecovered.setText(""+Math.toIntExact((Long) statemodel.getRecovered()));
     holder.RVactive.setText(""+Math.toIntExact((Long) statemodel.getActive()));

//


    }

    @Override
    public int getItemCount() {
        return statemodelArrayList.size();
    }

    public void filterList(ArrayList<Statemodel> filteredlist) {
        statemodelArrayList=filteredlist;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView  RVname;
        private  TextView  RVdname;
        private TextView RVactive;
        private TextView  RVRecovered;
        private TextView  RVDecreased;
        private TextView  RVConfirmed;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            RVdname=itemView.findViewById(R.id.idlistdistrict);
            RVactive= itemView.findViewById(R.id.idActivecases);
            RVRecovered= itemView.findViewById(R.id.idRecoveredcases);
            RVDecreased= itemView.findViewById(R.id.idDecreasedcases);
            RVConfirmed= itemView.findViewById(R.id.idConfirmedcases);
            RVname= itemView.findViewById(R.id.idlistState);



        }


    }
}
