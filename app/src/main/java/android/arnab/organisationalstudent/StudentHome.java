package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentHome extends AppCompatActivity implements View.OnClickListener
{
    EditText idNo,otp;
    Button verify;
    ImageView homeBg,homeFg;
    RelativeLayout homeWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        idNo=findViewById(R.id.idNo);
        otp=findViewById(R.id.otp);
        verify=findViewById(R.id.verify);
        homeBg=findViewById(R.id.homeBg);
        homeWait=findViewById(R.id.homeWait);
        homeFg=findViewById(R.id.homeFg);

        SharedPreferences preferences=getSharedPreferences("Details",0);
        Long id=preferences.getLong("id",-1);

        homeWait.setVisibility(View.GONE);
        makeWindowResponsive();

        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(homeBg);

        Glide.with(this).load(R.drawable.smartrist_logo).into(homeFg);

        if(id!=-1)
        {
            Intent intent=new Intent(StudentHome.this,StudentOptions.class);
            startActivity(intent);
            finish();
        }

        verify.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(verify))
        {
            String str1=idNo.getText().toString();
            String str2=otp.getText().toString();
            if(str1.equals("") || str1==null || str2.equals("") || str2==null)
            {
                Toast.makeText(StudentHome.this,"Enter all fields",Toast.LENGTH_SHORT).show();
            }
            else
            {
                homeWait.setVisibility(View.VISIBLE);
                makeScreenUnresponsive();
                final long enteredId=Long.parseLong(str1);
                final int enteredOtp=Integer.parseInt(str2);

                Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        JSONObject jsonResponse= null;
                        //msg.setText(response);
                        homeWait.setVisibility(View.GONE);
                        makeWindowResponsive();
                        try {
                            jsonResponse = new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success)
                            {
                                int activeState=jsonResponse.getInt("activeState");
                                if(enteredId==jsonResponse.getLong("id") && enteredOtp==jsonResponse.getInt("OTP") &&
                                        activeState==1)
                                {
                                    SharedPreferences preferences=getSharedPreferences("Details",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.putLong("id",enteredId);
                                    editor.putInt("grade",jsonResponse.getInt("grade"));
                                    editor.putString("department",jsonResponse.getString("department"));
                                    editor.putString("personName",jsonResponse.getString("personName"));
                                    editor.commit();

                                    Toast.makeText(getApplicationContext(),"Verification successful",Toast.LENGTH_SHORT).show();
                                    updateOTP(getApplicationContext(),enteredId);
                                    Intent intent=new Intent(StudentHome.this,StudentOptions.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    if(activeState==0)
                                    {
                                        Toast.makeText(getApplicationContext(),"Card not active. Contact office",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(),"Incorrect OTP",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Incorrect Id",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Some error occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                String CANDIDATE_DETAILS_URL= String.format("http://arnabbanerjee.dx.am/requestCandidateDetails.php?id=%1$d",enteredId);
                //msg.setText(CANDIDATE_DETAILS_URL);
                VolleyGetRequest volleyGetRequest=new VolleyGetRequest(CANDIDATE_DETAILS_URL,detailsResponseListener);
                RequestQueue queue=Volley.newRequestQueue(getApplicationContext());
                queue.add(volleyGetRequest);
            }
        }

    }

    private static void updateOTP(final Context mContext,long id)
    {
        Response.Listener<String> updateResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"OTP changed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };
        RequestUpdateOTP requestUpdateOTP=new RequestUpdateOTP(id,updateResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestUpdateOTP);
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
}
