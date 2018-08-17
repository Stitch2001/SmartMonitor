package com.gdbjzx.smartmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/8/17.
 */

public class SituationAdapter extends RecyclerView.Adapter<SituationAdapter.ViewHolder> {

    private List<mSituation> mSituationList;

    private RecordSituationActivity context;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView situationText;
        ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            situationText = (TextView) itemView.findViewById(R.id.situation_text);
            deleteButton = (ImageView) itemView.findViewById(R.id.delete_button);
        }
    }

    public SituationAdapter(List<mSituation> situationList,RecordSituationActivity context){
        mSituationList = situationList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.situation_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                mSituationList.remove(position);
                notifyDataSetChanged();
                context.listNum--;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mSituation aSituation = mSituationList.get(position);
        holder.situationText.setText(aSituation.getLocation()+aSituation.getEvent()+"扣"+aSituation.getScore()+"分");
    }

    @Override
    public int getItemCount() {
        return mSituationList.size();
    }
}
