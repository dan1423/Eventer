package com.example.danny.paymentreminder.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.danny.paymentreminder.Custom_Classes.CustomClickListener;
import com.example.danny.paymentreminder.Custom_Classes.CustomDateParser;
import com.example.danny.paymentreminder.R;
import com.example.danny.paymentreminder.third_party.SwipeRevealLayout;

import java.util.List;

public class EventObjectAdapter extends RecyclerView.Adapter<EventObjectAdapter.ViewHolder>{

   private Context context;
   private List<EventObject> eventObjects;
    public CustomClickListener listener;
    //public ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public EventObjectAdapter(Context context, List<EventObject> eventObjects, CustomClickListener listener) {
        this.context = context;
        this.eventObjects = eventObjects;
       this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.swipe_layout, parent,false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        EventObject eventObject = eventObjects.get(position);

        String d = convertLongToDate(eventObject.getEventDate());
        holder.txtEventName.setText(stringShortener(eventObject.getEventName()));
        holder.txtEventType.setText(eventObject.getEventType()+ " event");
        holder.txtEventDate.setText("Next date: "+d);




        //user wants to edit the event
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEditClick(position);
                }
            }
        });

        //user wants to delete the event
       holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onDeleteClick(position);
            }
        });

        //user wants know more about the event
        holder.imgAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onInfoClick(position);
            }
        });

    }



    @Override
    public int getItemCount() {
        return eventObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtEventName,txtEventDate, txtEventType;
        ImageView  imgRemove, imgEdit, imgAbout;
        SwipeRevealLayout swipeRevealLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEventName = (TextView)itemView.findViewById(R.id.txt_list_item_event_name);
            txtEventDate= (TextView)itemView.findViewById(R.id.txt_list_item_event_date);
            txtEventType = (TextView)itemView.findViewById(R.id.txt_list_item_event_type);
           swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout_single_item);
            imgEdit = (ImageView)itemView.findViewById( R.id.img_edit_event) ;
            imgRemove = (ImageView)itemView.findViewById(R.id.img_remove_event);
            imgAbout = (ImageView)itemView.findViewById(R.id.img_about_event);
        }
    }


    private String convertLongToDate(Long l){
        CustomDateParser parser = new CustomDateParser(l) ;
        return parser.convertLongToDate();

    }

    private String stringShortener(String str) {
        if (str.length() <= 5) {
            return str;
        }
        str = str.substring(0, 5);
        str = str + "...";
        return str;
    }
}
