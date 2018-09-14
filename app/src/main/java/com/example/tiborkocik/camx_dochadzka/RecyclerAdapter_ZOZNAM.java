package com.example.tiborkocik.camx_dochadzka;

import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import java.util.ArrayList;

public class RecyclerAdapter_ZOZNAM extends RecyclerView.Adapter<RecyclerAdapter_ZOZNAM.RecyclerViewHolder_ZOZNAM> {
    ArrayList<ZOZNAM_ZAMESTNANCOV> arrayList = new ArrayList<>();
    RecyclerAdapter_ZOZNAM(ArrayList<ZOZNAM_ZAMESTNANCOV> arrayList)
    {
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerViewHolder_ZOZNAM onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_workers,parent,false);
        RecyclerViewHolder_ZOZNAM recyclerViewHolder = new RecyclerViewHolder_ZOZNAM(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter_ZOZNAM.RecyclerViewHolder_ZOZNAM holder, int position) {

        ZOZNAM_ZAMESTNANCOV zamestnanci = arrayList.get(position);
        if (position == 0){
            holder.headerTopLine.setVisibility(View.VISIBLE);
            holder.header.setVisibility(View.VISIBLE);
            holder.headerBottomLine.setVisibility(View.VISIBLE);
        }
        else {
            holder.headerTopLine.setVisibility(View.GONE);
            holder.header.setVisibility(View.GONE);
            holder.headerBottomLine.setVisibility(View.GONE);
        }
        holder.Name.setText(zamestnanci.getMeno());
        holder.Number.setText(zamestnanci.getCislo());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static  class RecyclerViewHolder_ZOZNAM extends  RecyclerView.ViewHolder
    {
        TextView Name,Number;
        LinearLayout header;
        ImageView headerTopLine, headerBottomLine;
        RecyclerViewHolder_ZOZNAM(View view)
        {
            super(view);
            headerTopLine = (ImageView) view.findViewById(R.id.headerTopLine);
            header = (LinearLayout) view.findViewById(R.id.header);
            headerBottomLine = (ImageView) view.findViewById(R.id.headerBottomLine);
            Name = (TextView)view.findViewById(R.id.workName);
            Number = (TextView)view.findViewById(R.id.workNumber);

        }
    }


}
