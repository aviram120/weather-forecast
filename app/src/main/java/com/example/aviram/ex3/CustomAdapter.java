package com.example.aviram.ex3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class CustomAdapter extends BaseAdapter{
    private String [] dateArr;
    private String [] timeArr;
    private String [] descArr;
    private String [] tempArr;
    private String [] picArr;
    private Context context;
    private final String SERVER="http://openweathermap.org/img/w/";
    private static LayoutInflater inflater=null;

    public CustomAdapter(Activity mainActivity, String[] prgmNameList,String[] timeList,String[] descList,String[] tempList,String[] picList) {
    //the function get all array from main and make the listView

        //init array
        dateArr=prgmNameList;
        timeArr=timeList;
        descArr=descList;
        tempArr=tempList;
        picArr=picList;

        context=mainActivity;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public class Holder{
        //object-hold the Components in the layout

        TextView tvData;
        TextView tvTime;
        TextView tvDesc;
        TextView tvTemp;
        ImageView img;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        // the function put the data from arrays to listView

        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_view_custom, null);

        //list view
        holder.tvData=(TextView) rowView.findViewById(R.id.data);
        holder.tvTime=(TextView) rowView.findViewById(R.id.time);
        holder.tvDesc=(TextView) rowView.findViewById(R.id.desc);
        holder.tvTemp=(TextView) rowView.findViewById(R.id.temp);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView);

        //the text
        holder.tvData.setText(dateArr[position]);
        holder.tvTime.setText(timeArr[position]);
        holder.tvDesc.setText(descArr[position]);
        holder.tvTemp.setText(tempArr[position]);

        //get the icon from API-server
        String url = SERVER+picArr[position]+".png";
        Picasso.with(context).load(url).into(holder.img);//put the icon

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

    @Override
    public int getCount() {
        return dateArr.length;
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
}//end