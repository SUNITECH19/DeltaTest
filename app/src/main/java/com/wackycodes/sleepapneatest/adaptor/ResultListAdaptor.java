package com.wackycodes.sleepapneatest.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wackycodes.sleepapneatest.R;
import com.wackycodes.sleepapneatest.model.ResultItem;

import java.util.ArrayList;

public class ResultListAdaptor extends RecyclerView.Adapter<ResultListAdaptor.ViewHolderList> {

    Context context;
    ArrayList<ResultItem> resultItems;
    ResultItem resultItem = null;
    private OnItemClickListener CardClickListener;

    public ResultListAdaptor(Context context, ArrayList<ResultItem> items){
        this.context = context;
        this.resultItems = items;
    }
    @NonNull
    @Override
    public ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent,false);
        ViewHolderList VH = new ViewHolderList(V, CardClickListener );
        return VH;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderList holder, int position) {
        resultItem = resultItems.get(position);
        holder.recording_name.setText("Record date: " + String.valueOf(resultItem.getVideoName()));
        holder.recording_length.setText("Duration: "+String.valueOf(resultItem.getLength())+" seconds");
    }

    @Override
    public int getItemCount() {
        return resultItems.size();
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    //Setting up clicklistener for every card
    public void setOnItemClickListener(OnItemClickListener listener){
        CardClickListener = listener;
    }
    //Extending viewholder class for our specific need
    public class ViewHolderList extends RecyclerView.ViewHolder{
        public TextView recording_name;
        public TextView recording_length;


        public ViewHolderList(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            recording_name = itemView.findViewById(R.id.recording_name);
            recording_length = itemView.findViewById(R.id.recording_length);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
