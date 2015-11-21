package com.dsi.darshan.samplegcm_chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by darshan on 13/11/15.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

    Context context;
    public static String USER_ID = "user_id";

    public Adapter(Context context){
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_user_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tv.setText(FetchUsers.mList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context,ChatActivity.class);
                in.putExtra(Constants.RECEIVED_REG_ID,FetchUsers.mList.get(position).id);
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return FetchUsers.mList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView tv;

        public Holder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

}
