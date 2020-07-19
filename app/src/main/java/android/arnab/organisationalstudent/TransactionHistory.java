package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionHistory extends AppCompatActivity
{
    ImageView transBg;
    RelativeLayout transWait;
    RecyclerView recyclerView2;
    TranAdap adapter3;
    RecyclerView.LayoutManager layoutManager2;
    ArrayList<TranItem> tranArrayList;
    long id;
    int len=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        Toolbar toolbar=findViewById(R.id.toolbar5);
        toolbar.setTitle("Transaction History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        transBg=findViewById(R.id.transBg);
        transWait=findViewById(R.id.transWait);
        recyclerView2 = findViewById(R.id.rec2);

        Glide.with(this).load(R.drawable.stubg).into(transBg);

        transWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();

        SharedPreferences preferences=getSharedPreferences("Details",0);
        id=preferences.getLong("id",-1);

        getTransactions(getApplicationContext(),id);



         tranArrayList= new ArrayList<>();
        String[] dummytran = {"porbona", "doctor hobona", "seta ki", "fwtyjhrt", "Srthr", "rstb", "crth", "Cwtgh45", "cvweth", "dryndt", "dryhrd"},
                dummydate = {"12/12/12", "11/01/16", "12/12/12", "11/01/16", "12/12/12", "11/01/16", "12/12/12", "11/01/16", "12/12/12", "11/01/16", "01/01/19"},
                dummytime = {"10:12 PM", "09:59 AM", "10:12 PM", "09:59 AM", "10:12 PM", "09:59 AM", "10:12 PM", "09:59 AM", "10:12 PM", "09:59 AM", "12:01 PM"};
        int[] dummybal = {4586, 456, 10, 58, 45, 45, 4586, 456, 10, 58, 45}, dummychange = {-5, 8, 65, -84, 54, -65, -48, -10, 200, -5, -100};
        for (int i = 0; i < dummytran.length; i++)
            tranArrayList.add(new TranItem(dummytran[i], dummydate[i], dummytime[i], dummybal[i], dummychange[i]));


    }

    void getTransactions(final Context mContext, final long id)
    {
        Response.Listener<String> tranResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                transWait.setVisibility(View.GONE);
                makeWindowResponsive();
                JSONObject jsonResponse= null;
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    RecyclerView.LayoutManager layoutManager;
                    tranArrayList = new ArrayList<>();
                    len=array.length();
                    ItemAdap.initiateValues();

                    for (int i = 0; i < len; i++)
                    {
                        JSONObject tranItem = array.getJSONObject(i);
                        String details=tranItem.getString("details");
                        details=details.toUpperCase();
                        String date=tranItem.getString("transDate");
                        String time=tranItem.getString("transTime");
                        int balance=tranItem.getInt("balance");
                        int transAmt=tranItem.getInt("transAmt");
                        String type=tranItem.getString("transType");
                        if(type.equalsIgnoreCase("debit"))
                            transAmt=transAmt*(-1);




                        tranArrayList.add(new TranItem(details,date,time,balance,transAmt));

                        //tranArrayList.add(new TranItem(dummytran[i], dummydate[i], dummytime[i], dummybal[i], dummychange[i]));

                    }
                    recyclerView2.setHasFixedSize(true);
                    layoutManager2 = new LinearLayoutManager(mContext);
                    adapter3 = new TranAdap(tranArrayList);
                    recyclerView2.setLayoutManager(layoutManager2);
                    recyclerView2.setAdapter(adapter3);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String GET_TRANSACTION_URL= String.format("http://arnabbanerjee.dx.am/RequestGetTransaction.php?id=%1$d",id);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(GET_TRANSACTION_URL,tranResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
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
