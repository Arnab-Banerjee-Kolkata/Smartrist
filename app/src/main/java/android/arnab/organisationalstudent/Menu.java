package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu extends AppCompatActivity implements View.OnClickListener
{
    ImageView menuBg;
    RecyclerView recyclerView;
    RelativeLayout menuWait;
    ArrayList<Item> itemArrayList;
    ItemAdap adapter2;
    Button proceedBtn;
    long id;
    int len=0;
    ArrayList<String> itemName;
    ArrayList<Integer> quantity, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar=findViewById(R.id.toolbar6);
        toolbar.setTitle("Order Food");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuBg=findViewById(R.id.menuBg);
        recyclerView = findViewById(R.id.rec);
        menuWait=findViewById(R.id.menuWait);
        proceedBtn=findViewById(R.id.proceedBtn);

        SharedPreferences preferences=getSharedPreferences("Details",0);
        id=preferences.getLong("id",-1);


        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(menuBg);

        menuWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();
        getMenu(getApplicationContext());

        proceedBtn.setOnClickListener(this);
    }

    void getMenu(final Context mContext)
    {
        //Toast.makeText(mContext,"got:"+canId+"",Toast.LENGTH_SHORT).show();
        Response.Listener<String> menuResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                menuWait.setVisibility(View.GONE);
                makeWindowResponsive();
                JSONObject jsonResponse= null;
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    RecyclerView.LayoutManager layoutManager;
                    itemArrayList = new ArrayList<>();
                    len=array.length();
                    ItemAdap.initiateValues();

                    for (int i = 0; i < len; i++)
                    {
                        JSONObject menuItem = array.getJSONObject(i);
                        itemArrayList.add(new Item(menuItem.getString("imageUrl"), menuItem.getString("itemName"),
                                menuItem.getString("price"),menuItem.getInt("availableState")));

                    }
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(mContext);
                    adapter2 = new ItemAdap(itemArrayList);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter2);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String GET_MENU_URL= String.format("http://arnabbanerjee.dx.am/RequestGetMenu.php");
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(GET_MENU_URL,menuResponseListener);
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
    public void onClick(View v)
    {
        if(v.equals(proceedBtn))
        {
            ArrayList<String> values = adapter2.getValues();
            int x =0;
            String temp="";
            itemName=new ArrayList<String>();
            quantity=new ArrayList<Integer>();
            price=new ArrayList<Integer>();
            for (int i = 0; i < values.size(); i++)
            {
                x = Integer.parseInt(values.get(i));
                if(x!=0)
                {
                    itemName.add(itemArrayList.get(i).getMtxt1());
                    price.add(Integer.parseInt(itemArrayList.get(i).getMtxt2()));
                    quantity.add(x);
                }
            }
            if(itemName.size()>0)
            {
                menuWait.setVisibility(View.VISIBLE);
                makeScreenUnresponsive();
                postFoodOrder(getApplicationContext(), id, itemName, quantity, price);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"No item selected",Toast.LENGTH_SHORT).show();
            }
        }
    }

    void postFoodOrder(final Context mContext, final long id, ArrayList<String> itemName,
                       ArrayList<Integer> quantity, ArrayList<Integer> price)
    {
        Response.Listener<String> foodResponseListener=new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                Toast.makeText(mContext,response,Toast.LENGTH_SHORT).show();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Order Placed",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    finish();
//                    err.setText(e.toString());
                }


            }
        };
        RequestPostFood requestPostFood=new RequestPostFood(id, itemName,quantity,price,foodResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostFood);
    }
}
