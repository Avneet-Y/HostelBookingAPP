package com.example.hostelappnitj.MBH_B_Hostel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.example.hostelappnitj.Acitvity.RegisterationActivity;
import com.example.hostelappnitj.Acitvity.RoomConfirmer;
import com.example.hostelappnitj.Acitvity.SeatmatrixMBHBoys;
import com.example.hostelappnitj.ModelResponse.HostelRegisterationResponse;
import com.example.hostelappnitj.ModelResponse.PreRegisterResponse;
import com.example.hostelappnitj.ModelResponse.hostel;
import com.example.hostelappnitj.ModelResponse.statusModel;
import com.example.hostelappnitj.R;
import com.example.hostelappnitj.RetrofitClient;
import com.example.hostelappnitj.SharedPrefManager;
import com.example.hostelappnitj.databinding.ActivityFloor2SeatMatrixBinding;
import com.example.hostelappnitj.databinding.ActivitySeatmatrixMbhboysBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Floor_2_SeatMatrix extends AppCompatActivity {

    private ActivityFloor2SeatMatrixBinding binding;
    AppCompatButton room301;
    AppCompatButton btnBook3;
    GestureView gestureView;
    SharedPrefManager sharedPrefManager;
    String hostelName, username, rollNumber, email, branch;
    TextView display;
    List<hostel> hostelList;
    List<statusModel>hostelStatusList;
    ProgressDialog progressDialog ;   //this will give the background box also


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFloor2SeatMatrixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(Floor_2_SeatMatrix.this);
        username = sharedPrefManager.getUser().getUsername();
        email = sharedPrefManager.getUser().getEmail();
        rollNumber = sharedPrefManager.getUser().getRollNumber();
        branch = sharedPrefManager.getUser().getBranch();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Updating Seat Matrix..");
        progressDialog.show();
        progressDialog.setCancelable(false);
//        intent from MegaBoysB_activity
        Intent intent = getIntent();
        hostelName = intent.getStringExtra("hostelName");


        gestureView = findViewById(R.id.gestureview);
        gestureView.getController().getSettings()
                .setMaxZoom(2f)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(true)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(false)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

//        loadRooms();
        //to load the color of rooms in matrix
        loadStatus();
//        TO PASS THE INTENT TO NEXT REGISTER ACTIVITY
        binding.btnRoomBook3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Floor_2_SeatMatrix.this, "Register here", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Floor_2_SeatMatrix.this, RoomConfirmer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("hostelName", hostelName);
                intent.putExtra("username", username);
                intent.putExtra("rollNumber", rollNumber);
                intent.putExtra("email", email);
                intent.putExtra("branch", branch);
                startActivity(intent);
            }
        });

        Handler handler= new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
//                loadRooms();
                loadStatus();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable,2000);
    }

    private void loadStatus() {
        //        API call for Status verification
        Call<PreRegisterResponse>call1 = RetrofitClient.getInstance().getApi().fetchAllHostelsStatus();
        call1.enqueue(new Callback<PreRegisterResponse>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<PreRegisterResponse> call, Response<PreRegisterResponse> response) {
                PreRegisterResponse responseFromAPI1 = response.body();
                if(response.isSuccessful()){
                    progressDialog.dismiss();  //if any error occurs then it need to be returned

                    hostelStatusList=responseFromAPI1.getHostelStatusList();
                    int n = hostelStatusList.size();
                    String status_received,room,hostel_name,nums;
                    String room2 = "";
                    for (int i =0 ; i<n;i++){
                        status_received=hostelStatusList.get(i).getStatus();
                        room=hostelStatusList.get(i).getRoomNumber();
                        hostel_name=hostelStatusList.get(i).getHostelName();
                        nums=hostelStatusList.get(i).getNums();

//                        room2=room+" ";

//                            if condition for evaluating the hostel name
                        //concept
//                        vacancy    status   output
//                           0        0         no
//                           1        0         light blue
//                           2        0         blue
//                           0        1       red
//                           1        1       red
//                           2        1       red
                        if(hostel_name.equals("Mega Boys Hostel B")){
                            if(room!=null) {
                                if (status_received.equals("1")) {   //temporary blocked
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_temporarily_blocked);
                                    }catch (NullPointerException e){
                                        progressDialog.dismiss();  //if any error occurs then it need to be returned
                                        e.printStackTrace();
                                    }
                                }
                                if (status_received.equals("0")&& nums.equals("1")) {   //unBlock room num people =1
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_occupied_partially);
                                    }catch (NullPointerException e){
                                        progressDialog.dismiss();  //if any error occurs then it need to be returned
                                        e.printStackTrace();
                                    }
                                }
                                if (status_received.equals("0")&& nums.equals("2")) {   //unBlock room  , num people 2
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_occupied_full);
                                    }catch (NullPointerException e){
                                        progressDialog.dismiss();  //if any error occurs then it need to be returned
                                        e.printStackTrace();
                                    }
                                }
                                if (status_received.equals("0")&& nums.equals("0")) {   //unBlock room  , num people 2
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_gradient2);
                                    }catch (NullPointerException e){
                                        progressDialog.dismiss();  //if any error occurs then it need to be returned
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PreRegisterResponse> call, Throwable t) {
                progressDialog.dismiss();  //if any error occurs then it need to be returned
            }
        });
    }

    public void loadRooms(){
        Call<HostelRegisterationResponse> call = RetrofitClient.getInstance().getApi().fetchAllHostels();
        call.enqueue(new Callback<HostelRegisterationResponse>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<HostelRegisterationResponse> call, Response<HostelRegisterationResponse> response) {
                HostelRegisterationResponse responseFromAPI= response.body();
                if(response.isSuccessful()){
                    progressDialog.dismiss();  //if any error occurs then it need to be returned

                    hostelList=  responseFromAPI.getHostelList();
                    int n = hostelList.size();


//                            List<String>usernames = new ArrayList<>();
                    List<String>roomNumberFullyFilled = new ArrayList<>();
                    String rollNumber1 , rollNumber2,room , room2 = "",hostelName;



                    for (int i =0 ; i<n;i++){
                        rollNumber1 = hostelList.get(i).getRollNumber1();
                        rollNumber2 = hostelList.get(i).getRollNumber2();
                        room = hostelList.get(i).getRoomNumber();
                        room2=room+" ";

//                            if condition for evaluating the hostel name
                        hostelName = hostelList.get(i).getHostelName();

                        if(hostelName.equals("Mega Boys Hostel B")){
                            if(room!=null) {
                                if (rollNumber1 != null && rollNumber2 != null) {
//                                    both ARE present
//                            roomNumber FullyFilled. add(room);
//                            Toast.makeText(SeatmatrixMBHBoys.this, room+" fully filled", Toast.LENGTH_SHORT).show();
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_occupied_full);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }

//                            b.setTextColor(R.color.white);
                                } else if (rollNumber1 != null && rollNumber2 == null) {
//                            student 1 is present only partially filled
//                            roomNumber.add(room);
//                            Toast.makeText(SeatmatrixMBHBoys.this, room+" partially filled", Toast.LENGTH_SHORT).show();
                                    try{
                                        String btnid = "room" + room;
                                        int resId = getResources().getIdentifier(btnid, "id", getPackageName());  //to get the ID of resource at runtime
                                        Button b = (Button) findViewById(resId);
                                        b.setBackgroundResource(R.drawable.room_occupied_partially);
                                    }catch (NullPointerException e){
                                        progressDialog.dismiss();  //if any error occurs then it need to be returned
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
//                    binding.display.setText("total "+n);
//                            hosteltxt.setText("names : "+usernames);

                }else{
                    progressDialog.dismiss();  //if any error occurs then it need to be returned
                    Toast.makeText(Floor_2_SeatMatrix.this, "Something went Wrong..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HostelRegisterationResponse> call, Throwable t) {
                progressDialog.dismiss();  //if any error occurs then it need to be returned
                Toast.makeText(Floor_2_SeatMatrix.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
        loadStatus();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        this.recreate();
    }
}