package com.example.akshirsa.contactsexplorer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akshirsa.contactsexplorer.Model.DBContactsModel;
import com.example.akshirsa.contactsexplorer.R;
import com.example.akshirsa.contactsexplorer.View.DropListener;
import com.example.akshirsa.contactsexplorer.View.RemoveListener;
import com.example.akshirsa.contactsexplorer.View.SwipeListener;

import java.util.ArrayList;

/**
 * Created by akshirsa on 10/22/2014.
 */
public class ContactListAdapter extends BaseAdapter implements RemoveListener, DropListener, SwipeListener {
    private Context context;
    private ArrayList<DBContactsModel> Items;
    private int resID;

    private int[] mIds;
    private int[] mLayouts;

    private LayoutInflater mInflater;

    //Constructor to initialize values
    public ContactListAdapter(Context context, ArrayList<DBContactsModel> items, int resourceID) {
        this.context = context;
        this.Items = items;
        this.resID = resourceID;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // Number of times getView method call depends upon number of elements
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // Number of times getView method call depends upon gridValues.length
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(resID, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();

            // set value into text views
            holder.fname = (TextView) convertView.findViewById(R.id.first_name);
            holder.lname = (TextView) convertView.findViewById(R.id.last_name);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete_contact);

            convertView.setTag(holder);
        }
        else
        {
            // Get the ViewHolder back to get fast access to child views
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        holder.fname.setText(Items.get(position).getFName());
        holder.lname.setText(Items.get(position).getLName());
        holder.email.setText(Items.get(position).getEmail());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Items.remove(position);
                ((ImageView)v.findViewById(R.id.iv_delete_contact)).setVisibility(View.GONE);
                notifyDataSetChanged();

                /*Builder builder = new Builder(context);
                builder.setTitle("Alert!!!!");
                builder.setMessage("Are you sure you want to delete " + Items.get(position).getFName());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Items.remove(position);
                        v.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();*/
            }
        });

        return convertView;
    }




    // -------------------------------------

    static class ViewHolder {
        TextView fname;
        TextView lname;
        TextView email;
        ImageView ivDelete;
    }

    public void onRemove(int which) {
        if (which < 0 || which > Items.size())
            return;

        // Else remove the item form the list
        Items.remove(which);
    }

    public void onDrop(int from, int to) {
        DBContactsModel temp = Items.get(from);
        Items.remove(from);
        Items.add(to, temp);
    }

    @Override
    public void onSwipeDone(int which) {
        Log.d("Adapter", "Swipe on item : " + which);
    }
}
