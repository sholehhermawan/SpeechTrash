package com.d3iftelu.gooddayteam.speechtrash;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d3iftelu.gooddayteam.speechtrash.adapter.DeviceListAdapter;
import com.d3iftelu.gooddayteam.speechtrash.model.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowChartFragment extends Fragment {
    private static final String TAG = "ShowChartFragment";
    private ListView mListViewDevice;
    private TextView mTextViewDataIsEmpty;
    private ProgressBar loadingData;
    private FirebaseUser mCurrentUser;
    private DeviceListAdapter mAdapter;

    public ShowChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_chart, container, false);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mListViewDevice = rootView.findViewById(R.id.list_view_device);
        mTextViewDataIsEmpty = rootView.findViewById(R.id.text_view_empty_view);
        loadingData = (ProgressBar) rootView.findViewById(R.id.item_progres_bar);

        final ArrayList<Device> devices = readDeviceData();

        mAdapter = new DeviceListAdapter(getContext(), devices);
        mListViewDevice.setAdapter(mAdapter);
        mListViewDevice.setEmptyView(mTextViewDataIsEmpty);

        mListViewDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), FinishActivity.class);
                intent.putExtra(DetailActivity.ARGS_DEVICE_NAME, devices.get(i).getDeviceName());
                intent.putExtra(DetailActivity.ARGS_DEVICE_ID, devices.get(i).getDeviceId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    private ArrayList<Device> readDeviceData() {
        final ArrayList<Device> devicesData = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        loadingData.setVisibility(View.VISIBLE);

        final DatabaseReference myRef = database.getReference();
        myRef.keepSynced(true);
        myRef.child("list_device").child("petugas").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                devicesData.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String deviceName = userSnapshot.getValue(String.class);
                    final String deviceId = userSnapshot.getKey();

                    final Device device = new Device(deviceName, deviceId);
                    devicesData.add(device);

                    myRef.child("device").child(deviceId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                boolean status = dataSnapshot.child("status").getValue(boolean.class);
                                device.setStatus(status);

                            } catch (NullPointerException e){
                                myRef.child("device").child(deviceId).child("status").setValue(false);
                                device.setStatus(false);

                                int x = 0;
                                ProcessingHelper processingHelper = new ProcessingHelper();
                                long time = processingHelper.getDateNow();
                                myRef.child("device").child(deviceId).child("monitoring").child("volume").setValue(x);
                                myRef.child("device").child(deviceId).child("monitoring").child("berat").setValue(x);
                                myRef.child("device").child(deviceId).child("monitoring").child("time").setValue(String.valueOf(time));
                            }
                            loadingData.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                loadingData.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        return devicesData;
    }
}
