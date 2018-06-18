package com.example.danny.paymentreminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventObjectAdapter extends RecyclerView.Adapter<EventObjectAdapter.ViewHolder>{

   private Context context;
   private List<EventObject> eventObjects;

    public EventObjectAdapter(Context context, List<EventObject> eventObjects) {
        this.context = context;
        this.eventObjects = eventObjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_list_item, null);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EventObject eventObject = eventObjects.get(position);


        String d = convertLongToDate(eventObject.getEventDate());
        holder.txtEventName.setText(eventObject.getEventName());
        holder.txtEventType.setText(eventObject.getEventType());
        holder.txtEventDate.setText(d);
    }

    @Override
    public int getItemCount() {
        return eventObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtEventName,txtEventDate, txtEventType;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEventName = (TextView)itemView.findViewById(R.id.txt_list_item_event_name);
            txtEventDate= (TextView)itemView.findViewById(R.id.txt_list_item_event_date);
            txtEventType = (TextView)itemView.findViewById(R.id.txt_list_item_event_type);
        }
    }


    private String convertLongToDate(Long l){
        CustomDateParser parser = new CustomDateParser(l) ;
        return parser.convertLongToDate();

    }
}
