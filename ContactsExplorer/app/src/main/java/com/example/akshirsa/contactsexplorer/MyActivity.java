package com.example.akshirsa.contactsexplorer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.akshirsa.contactsexplorer.Adapter.ContactListAdapter;
import com.example.akshirsa.contactsexplorer.Model.DBContactsModel;
import com.example.akshirsa.contactsexplorer.View.ContactsListView;
import com.example.akshirsa.contactsexplorer.View.DragListener;
import com.example.akshirsa.contactsexplorer.View.DropListener;
import com.example.akshirsa.contactsexplorer.View.RemoveListener;
import com.example.akshirsa.contactsexplorer.View.SwipeListener;
import com.example.akshirsa.contactsexplorer.database.ContactsTableOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MyActivity extends ListActivity {

    ArrayList<DBContactsModel> contactsRecords;
    String TAG = "ContactsMainActivity";
    private ContactListAdapter adapter;
    ContactsTableOperations dOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        DisplayContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    // Logic starts

    // Display the contacts list from reading contact details
    public void DisplayContacts()
    {
        //Get all semester records from DB
        dOperation = new ContactsTableOperations(getApplicationContext());
        try {
            dOperation.open();
            contactsRecords = dOperation.getAllContacts();
            dOperation.close();

            Log.d(TAG, "Total items added = " + contactsRecords.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // If there are records found then display as a list
        if(contactsRecords != null && contactsRecords.size() > 0)
        {
            ImageView iv = (ImageView) findViewById(R.id.iv_contacts_image);
            iv.setVisibility(View.GONE);
            TextView tv = (TextView) findViewById(R.id.tv_empty_msg);
            tv.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.list_layout);
            ll.setVisibility(View.VISIBLE);

            //setting the list adapter
            adapter = new ContactListAdapter(getApplicationContext(), contactsRecords, R.layout.list_item_view);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();


            ListView listView = getListView();
            if (listView instanceof ContactsListView) {
                ((ContactsListView) listView).setDropListener(mDropListener);
                ((ContactsListView) listView).setRemoveListener(mRemoveListener);
                ((ContactsListView) listView).setDragListener(mDragListener);
                ((ContactsListView) listView).setSwipeListener(mSwipeListener);
            }
        }
        // Else if no records found then show empty
        else
        {
            ImageView iv = (ImageView) findViewById(R.id.iv_contacts_image);
            iv.setVisibility(View.VISIBLE);
            TextView tv = (TextView) findViewById(R.id.tv_empty_msg);
            tv.setVisibility(View.VISIBLE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.list_layout);
            ll.setVisibility(View.GONE);
        }
    }

    // Add new contact if button clicked
    public void AddNewContact(View view)
    {
        // Show dialog to add new contacts
        ShowAddContactsDialog();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.d(TAG, "Clicked item is  = " + position + " : contact ID  : " + contactsRecords.get(position).getID());

        //Set delete buttons invisible if any
        InvisibleDeleteBtns();

        ShowContactsDetails(position);
    }


    private void InvisibleDeleteBtns() {
        //Set delete buttons invisible if any
        int firstVisibleChild = getListView().getFirstVisiblePosition();
        Log.d(TAG, "First child id === " + firstVisibleChild);

        for (int i = 0; i < getListView().getChildCount(); i++) {
            ViewGroup vv = (ViewGroup) getListView().getChildAt(i);

            ImageView delBtn = (ImageView) vv.findViewById(R.id.iv_delete_contact);
            if (delBtn != null)
                delBtn.setVisibility(View.GONE);
        }
    }

    public void ShowContactsDetails(int position) {
        Log.d(TAG, "Display contact details");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.display_contact_details, null);
        final PopupWindow popupWindow  = new PopupWindow(popupView, width - 200, height - 350);

        final TextView fname = (TextView) popupView.findViewById(R.id.tv_first_name);
        final TextView lname = (TextView) popupView.findViewById(R.id.tv_last_name);
        final TextView email = (TextView) popupView.findViewById(R.id.tv_email);


        TextView cancel = (TextView) popupView.findViewById(R.id.tv_cancel_dialog);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        fname.setText(contactsRecords.get(position).getFName());
        lname.setText(contactsRecords.get(position).getLName());
        email.setText(contactsRecords.get(position).getEmail());

        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(Animation.ZORDER_TOP);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 50);
    }



    public void ShowAddContactsDialog() {
        Log.d(TAG, "Adding contacts");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.add_contact_view, null);
        final PopupWindow popupWindow  = new PopupWindow(popupView, width - 200, height - 350);

        final EditText fname = (EditText) popupView.findViewById(R.id.et_first_name);
        final EditText lname = (EditText) popupView.findViewById(R.id.et_last_name);
        final EditText email = (EditText) popupView.findViewById(R.id.et_email);


        TextView cancel = (TextView) popupView.findViewById(R.id.tv_cancel_dialog);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        TextView save = (TextView) popupView.findViewById(R.id.tv_save_data);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send entered values to save in table.
                if(TextUtils.isEmpty(fname.getText().toString()) || TextUtils.isEmpty(lname.getText().toString()) || TextUtils.isEmpty(email.getText().toString()))
                {
                    new AlertDialog.Builder(MyActivity.this)
                        .setTitle("Invalid Input")
                        .setMessage("Please fill all input values")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }
                else if (!isValidEmail(email.getText().toString()))
                {
                    new AlertDialog.Builder(MyActivity.this)
                            .setTitle("Invalid Email")
                            .setMessage("Please correct your email ID")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    popupWindow.dismiss();
                    AddContactValues(fname.getText().toString().trim(), lname.getText().toString().trim(), email.getText().toString().trim());
                }
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(Animation.ZORDER_TOP);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 50);
    }


    // Check if email id is valid format
    public final boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    // Add new contacts
    public void AddContactValues(String fname, String lname, String email)
    {
        int index = 0;
        if(contactsRecords == null || contactsRecords.size() == 0)
            index = 1;
        else
            index = contactsRecords.size() + 1;

        // Add new record to the database
        try {
            dOperation.open();
            dOperation.addContactRecord(new DBContactsModel(fname, lname, email, index));
            dOperation.close();

            // Call to display the records
            DisplayContacts();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    // Sort the contacts
    public void SortContacts(View view)
    {
        // Now sort the collection by writing custom sort method
        Collections.sort(contactsRecords, new Comparator<DBContactsModel>() {
            @Override
            public int compare(DBContactsModel dbContactsModel, DBContactsModel dbContactsModel2) {
                // Compare two records based on first name
                int flag = dbContactsModel.getFName().compareToIgnoreCase(dbContactsModel2.getFName());

                // If first name is matching then comparing for last name
                if(flag == 0)
                {
                    return dbContactsModel.getLName().compareToIgnoreCase(dbContactsModel2.getLName());
                }
                else
                    return flag;
            }
        });

        /*for(DBContactsModel rec : contactsRecords)
        {
            Log.d(TAG, "Value :: " + rec.getFName() + " " + rec.getLName());
        }*/


        // Now save the contacts in DB in the sorted order
        SaveSortedContacts(null);
    }


    // Save manually sorted contacts
    public void SaveSortedContacts(View view)
    {
        // Now update this sorting in DB
        try {
            dOperation.open();
            dOperation.updateContactRecord(contactsRecords);
            dOperation.close();

            // Call to display the records
            DisplayContacts();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    // -----------------------------------------------------
    // Drag and drop detection

    private DropListener mDropListener =
        new DropListener() {
            public void onDrop(int from, int to) {
                ListAdapter adapter = getListAdapter();
                if (adapter instanceof ContactListAdapter) {
                    ((ContactListAdapter)adapter).onDrop(from, to);
                    getListView().invalidateViews();
                }
            }
        };

    private RemoveListener mRemoveListener =
        new RemoveListener() {
            public void onRemove(int which) {
                ListAdapter adapter = getListAdapter();
                if (adapter instanceof ContactListAdapter) {
                    ((ContactListAdapter)adapter).onRemove(which);
                    getListView().invalidateViews();
                }
            }
        };

    private DragListener mDragListener =
        new DragListener() {

            int backgroundColor = 0xe0fffacd;
            int defaultBackgroundColor;

            public void onDrag(int x, int y, ListView listView) {
                // TODO Auto-generated method stub
            }

            public void onStartDrag(View itemView) {
                itemView.setVisibility(View.INVISIBLE);
                defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
                itemView.setBackgroundColor(backgroundColor);
                ImageView iv = (ImageView)itemView.findViewById(R.id.dragndropimage);
                if (iv != null) iv.setVisibility(View.INVISIBLE);
            }

            public void onStopDrag(View itemView) {
                itemView.setVisibility(View.VISIBLE);
                itemView.setBackgroundColor(defaultBackgroundColor);
                ImageView iv = (ImageView)itemView.findViewById(R.id.dragndropimage);
                if (iv != null) iv.setVisibility(View.VISIBLE);
            }

        };


    private SwipeListener mSwipeListener =
        new SwipeListener() {
            public void onSwipeDone(int which) {
                ListAdapter adapter = getListAdapter();
                if (adapter instanceof ContactListAdapter) {
                    ((ContactListAdapter) adapter).onSwipeDone(which);
                    View wantedView = getListView().getChildAt(which);
                    InvisibleDeleteBtns();

                    if(wantedView != null) {
                        if (((ImageView) wantedView.findViewById(R.id.iv_delete_contact)) != null) {
                            if (((ImageView) wantedView.findViewById(R.id.iv_delete_contact)).getVisibility() == View.VISIBLE)
                                ((ImageView) wantedView.findViewById(R.id.iv_delete_contact)).setVisibility(View.GONE);
                            else
                                ((ImageView) wantedView.findViewById(R.id.iv_delete_contact)).setVisibility(View.VISIBLE);
                        }
                    }
                    getListView().invalidateViews();
                }
            }
        };
}

