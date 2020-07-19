package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.EncrypterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentOptions extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener
{

    ImageView ImageView1;
    private DrawerLayout drawer;
    TextView studentName, courseName, year,navId;
    String name,course;
    int yr;
    long id;
    Toolbar toolbar;
    RelativeLayout optionsWait;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_options);
        Button bcred=findViewById(R.id.creditb);
        bcred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Credits.class);
                startActivity(intent);
            }
        });

        LayoutInflater l=getLayoutInflater();
        View v=l.inflate(R.layout.start_screen,null);
        FrameLayout f=(FrameLayout)findViewById(R.id.frag_container);
        f.addView(v);

        toolbar = findViewById(R.id.toolbar);
        final RelativeLayout r=findViewById(R.id.rel);
        drawer = findViewById(R.id.drawer_layout);
        ImageView1=findViewById(R.id.glideImageView);
        studentName=findViewById(R.id.studentName);
        courseName=findViewById(R.id.courseName);
        year=findViewById(R.id.year);
        NavigationView navigationView=findViewById(R.id.nav_view);
        optionsWait=findViewById(R.id.optionsWait);

        optionsWait.setVisibility(View.GONE);
        makeWindowResponsive();



        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(ImageView1);


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences preferences=getSharedPreferences("Details",0);
        yr=preferences.getInt("grade",-1);
        course=preferences.getString("department","");
        name=preferences.getString("personName","");
        id=preferences.getLong("id",-1);

        //Drawer Listener

        drawer.addDrawerListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("Smartrist");

        studentName.setText(name);
        courseName.setText(course);
        year.setText(yr+"");

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        menuItem.setChecked(true);
        LayoutInflater l=getLayoutInflater();
        Toolbar toolbar=findViewById(R.id.toolbar);
        ImageView ImageView2;
        int resid = R.drawable.cardbg;
        switch (menuItem.getItemId()){
            case R.id.chk_att: {
                //toolbar.setTitle("Your Attendance");

                Intent intent = new Intent(getApplicationContext(), CheckAttendence.class);
                startActivity(intent);

                break;
            }
            case R.id.smart: {
                //toolbar.setTitle("Student Card");

                Intent intent = new Intent(getApplicationContext(), FingerprintAuth.class);
                startActivity(intent);

                break;
            }
            case R.id.bal: {
                //toolbar.setTitle("Check Balance");
                Intent intent = new Intent(getApplicationContext(), CheckBalance.class);
                startActivity(intent);
                break;
            }
            case R.id.tran_his:
            {
                Intent intent=new Intent(getApplicationContext(),TransactionHistory.class);
                startActivity(intent);
                break;
            }
            case R.id.order:
            {
                //toolbar.setTitle("Order Food");

                Intent intent=new Intent(getApplicationContext(),Menu.class);
                startActivity(intent);
                break;
            }
            case R.id.lib_book: {
                //toolbar.setTitle("Book Requisition");

                Intent intent = new Intent(getApplicationContext(), BookRequisition.class);
                startActivity(intent);
//                View v8=l.inflate(R.layout.lib_book,null);
//                FrameLayout f8=(FrameLayout)findViewById(R.id.frag_container);
//                f8.removeAllViews();
//                f8.addView(v8);
                break;
            }
            case R.id.refresh:
                //toolbar.setTitle("Late Fine");
            {
                refreshDetails(getApplicationContext(), id);
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if(hasFocus)
        {
            studentName.setText(name);
            courseName.setText(course);
            year.setText(yr+"");
        }
    }


    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view)
    {
        navId = findViewById(R.id.navId);
        navId.setText("ID: "+id+"");
        ImageView navIcon=findViewById(R.id.navIcon);
        Glide.with(this).load(R.drawable.smartrist_logo).into(navIcon);

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    void refreshDetails(final Context mContext, final long id)
    {
        optionsWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();
        Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                optionsWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //info.setText(response);
                Map<String,String> params=new HashMap<>();
                JSONObject jsonResponse= null;
                //msg.setText(response);
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                            SharedPreferences preferences=getSharedPreferences("Details",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putLong("id",jsonResponse.getLong("id"));
                            editor.putInt("grade",jsonResponse.getInt("grade"));
                            editor.putString("department",jsonResponse.getString("department"));
                            editor.putString("personName",jsonResponse.getString("personName"));
                            editor.commit();


                        yr=preferences.getInt("grade",-1);
                        course=preferences.getString("department","");
                        name=preferences.getString("personName","");

                        studentName.setText(name);
                        courseName.setText(course);
                        year.setText(yr+"");
                    }
                    else{
                        Toast.makeText(mContext,"Could not refresh",Toast.LENGTH_SHORT).show();
                        params.put("message","Failed");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    params.put("message","Failed");
                }
            }
        };

        String CANDIDATE_DETAILS_URL= String.format("http://arnabbanerjee.dx.am/requestCandidateDetails.php?id=%1$d",id);
        //msg.setText(CANDIDATE_DETAILS_URL);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(CANDIDATE_DETAILS_URL,detailsResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }
}
