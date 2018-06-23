package com.example.danny.paymentreminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class EventObjectAdapter extends RecyclerView.Adapter<EventObjectAdapter.ViewHolder>{

   private Context context;
   private List<EventObject> eventObjects;
    public CustomClickListener listener;

    public EventObjectAdapter(Context context, List<EventObject> eventObjects, CustomClickListener listener) {
        this.context = context;
        this.eventObjects = eventObjects;
        this.listener = listener;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        EventObject eventObject = eventObjects.get(position);


        String d = convertLongToDate(eventObject.getEventDate());
        holder.txtEventName.setText(eventObject.getEventName());
        holder.txtEventType.setText(eventObject.getEventType());
        holder.txtEventDate.setText(d);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                 listener.onItemClick(position);
            }
        });



    }



    @Override
    public int getItemCount() {
        return eventObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtEventName,txtEventDate, txtEventType;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEventName = (TextView)itemView.findViewById(R.id.txt_list_item_event_name);
            txtEventDate= (TextView)itemView.findViewById(R.id.txt_list_item_event_date);
            txtEventType = (TextView)itemView.findViewById(R.id.txt_list_item_event_type);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relative_layout_single_item);
        }
    }


    private String convertLongToDate(Long l){
        CustomDateParser parser = new CustomDateParser(l) ;
        return parser.convertLongToDate();

    }
}
