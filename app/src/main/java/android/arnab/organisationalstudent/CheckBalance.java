package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
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

import java.util.HashMap;
import java.util.Map;

public class CheckBalance extends AppCompatActivity
{
    RelativeLayout balanceWait;
    TextView balanceTxt;
    ImageView balanceBg;
    long id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        Toolbar toolbar=findViewById(R.id.toolbar4);
        toolbar.setTitle("Balance");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        balanceWait=findViewById(R.id.balanceWait);
        balanceTxt=findViewById(R.id.balanceTxt);
        balanceBg=findViewById(R.id.balanceBg);

        SharedPreferences preferences=getSharedPreferences("Details",0);
        id=preferences.getLong("id",-1);

        Glide.with(this).load(R.drawable.stubg).into(balanceBg);

        makeScreenUnresponsive();
        getBalance(getApplicationContext(),id);

    }

    void getBalance(final Context mContext, final long id)
    {
        Response.Listener<String> balanceResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //info.setText(response);
                Map<String,String> params=new HashMap<>();
                JSONObject jsonResponse= null;
                balanceWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //msg.setText(response);
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        balanceTxt.setText("Rs. "+jsonResponse.getLong("balance"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    params.put("message","Failed");
                }
            }
        };

        String BALANCE_URL= String.format("http://arnabbanerjee.dx.am/RequestGetBalance.php?id=%1$d",id);
        //msg.setText(CANDIDATE_DETAILS_URL);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(BALANCE_URL,balanceResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
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
}
