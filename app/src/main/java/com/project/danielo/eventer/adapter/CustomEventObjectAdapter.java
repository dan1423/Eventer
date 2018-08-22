package com.project.danielo.eventer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.danielo.eventer.Custom_Classes.CustomClickListener;
import com.project.danielo.eventer.Custom_Classes.CustomDateParser;
import com.project.danielo.eventer.R;
import com.project.danielo.eventer.third_party.SwipeRevealLayout;

import java.util.Arrays;
import java.util.List;

public class CustomEventObjectAdapter extends RecyclerView.Adapter<CustomEventObjectAdapter.ViewHolder>{

   private Context context;
   private List<CustomEventObject> customEventObjects;
    public CustomClickListener listener;


    //accompanying the usal recyclerview adapter, we add a custom click listener interface
    public CustomEventObjectAdapter(Context context, List<CustomEventObject> customEventObjects, CustomClickListener listener) {
        this.context = context;
        this.customEventObjects = customEventObjects;
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

        CustomEventObject customEventObject = customEventObjects.get(position);
        CustomDateParser dateParser = setDateAndTime(customEventObject.getEventDate());

        holder.txtEventName.setText(stringShortener(customEventObject.getEventName()));
        holder.txtEventType.setText(customEventObject.getEventType().toLowerCase()+ " event");
        holder.txtEventDate.setText("On " + dateParser.getDate() +"at "+dateParser.getTime());

        String eventType = customEventObject.getEventType().toLowerCase();
        eventType =  eventType.replace("event","").trim();
        String[] arrayOfEventTypes =  context.getResources().getStringArray(R.array.event_type_array);

        arrayOfEventTypes = replaceAllInArray(arrayOfEventTypes,"");

        Drawable yellowBackground = context.getResources().getDrawable(R.drawable.layout_border_yellow);
        Drawable greenBackground = context.getResources().getDrawable(R.drawable.layout_border_green);
        Drawable blueBackground = context.getResources().getDrawable(R.drawable.layout_border_blue);
        Drawable purpleBackground = context.getResources().getDrawable(R.drawable.layout_border_purple);
        Drawable pinkBackground = context.getResources().getDrawable(R.drawable.layout_border_pink);




        //set border color based on event
        if(eventType.contains(arrayOfEventTypes[0].trim())){
            holder.swipeRevealLayout.setBackground(pinkBackground);
            //Log.i("COLORS","yellow");
        }else if(eventType.contains(arrayOfEventTypes[1].trim())){
            holder.swipeRevealLayout.setBackground(yellowBackground);
          //  Log.i("COLORS","green");
        }else if(eventType.contains(arrayOfEventTypes[2].trim())){
            holder.swipeRevealLayout.setBackground(greenBackground);
            //Log.i("COLORS","blue");
        }else if(eventType.contains(arrayOfEventTypes[3].trim())){
            holder.swipeRevealLayout.setBackground(blueBackground);
           // Log.i("COLORS","purple");
        }else if(eventType.contains(arrayOfEventTypes[4].trim())){
            holder.swipeRevealLayout.setBackground(purpleBackground);
        }





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
        return customEventObjects.size();
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


    private CustomDateParser setDateAndTime(Long l){
        CustomDateParser parser = new CustomDateParser(l) ;
        parser.setDateAndTime();

        return parser;

    }


    //we use this method to shorten event names so we can fit it on a list widget
    private String stringShortener(String str) {
        if (str.length() <= 5) {
            return str;
        }
        str = str.substring(0, 5);
        str = str + "...";
        return str;
    }

    private String[] replaceAllInArray(String[] arr, String rep){
        for(int i = 0; i < arr.length; i++){
            arr[i] = arr[i].trim().replace("event",rep);
            arr[i] = arr[i].toLowerCase();
        }

        return arr;
    }


}
