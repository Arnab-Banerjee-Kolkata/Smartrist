package android.arnab.organisationalstudent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookRequisition extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    Button subbut;
    long id;
    ArrayList<String> bookNames;
    ArrayList<String> bookAuthors;
    ArrayList<String> subjects;
    int len=0;
    Spinner dropdown;
    ImageView reqBg,foreImg,foreBg;
    RelativeLayout reqFore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requisition);
        Toolbar toolbar=findViewById(R.id.toolbar7);
        toolbar.setTitle("Book Requisition");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        subbut=findViewById(R.id.subbut);
        dropdown=findViewById(R.id.spinner);
        reqBg=findViewById(R.id.reqBg);
        reqFore=findViewById(R.id.reqFore);
        foreImg=findViewById(R.id.foreImg);
        foreBg=findViewById(R.id.foreBg);

        reqFore.setVisibility(View.GONE);

        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(reqBg);
        Glide.with(this).load(R.drawable.green_tick).into(foreImg);
        Glide.with(this).load(resid).into(foreBg);


        String[] items=new String[]{" ","1","2","3","4","5","6"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinnertext,items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);


        subbut.setOnClickListener(this);

        SharedPreferences preferences=getSharedPreferences("Details",0);
        id=preferences.getLong("id",-1);

    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(subbut))
        {
            bookNames=new ArrayList<String>();
            bookAuthors=new ArrayList<String>();
            subjects=new ArrayList<String>();

                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(date);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String yesterday=dateFormat.format(cal.getTime());

            LinearLayout l=findViewById(R.id.ll);
            int c=l.getChildCount();
            String x="";
            boolean isAdded=true;
            for(int i=0;i<c/2;i++) {
                EditText book = findViewById((i*10)+1);
                EditText author = findViewById((i*10)+2);
                EditText subject = findViewById((i*10)+3);

                if(!isEmpty(subject) && (!isEmpty(book) || !isEmpty(author)))
                {
                    bookNames.add(book.getText().toString());
                    bookAuthors.add(author.getText().toString());
                    subjects.add(subject.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Cannot identify some books",Toast.LENGTH_SHORT).show();
                    isAdded=false;
                    break;
                }
            }
            if(isAdded)
            {
                deleteRequisition(getApplicationContext(),yesterday);
                postBookRequisition(getApplicationContext(), id, bookNames, bookAuthors, subjects,
                        formattedDate);

                hideSoftKeyboard(BookRequisition.this,subbut);
                reqFore.setVisibility(View.VISIBLE);
                boolean chk=true;
                Handler handler=new Handler();
                for (int a = 5, j=1;  ;j++)
                {

                    final int temp=a;
                    if(a<=1000 && chk)
                    {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) foreImg.getLayoutParams();
                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                foreImg.setLayoutParams(layoutParams);

                                foreImg.getLayoutParams().height = temp;
                                foreImg.getLayoutParams().width = temp;
                                foreImg.requestLayout();
                            }
                        }, 3 * j);
                        a+=5;
                    }
                    else
                    {
                        chk=false;
                        a-=5;
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) foreImg.getLayoutParams();
                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                foreImg.setLayoutParams(layoutParams);

                                foreImg.getLayoutParams().height = temp;
                                foreImg.getLayoutParams().width = temp;
                                foreImg.requestLayout();
                            }
                        }, 3 * j);
                        if(a<=700)
                            break;
                    }
                }

            }

        }

    }

    private static void deleteRequisition(Context mContext, String yesterday)
    {
        Response.Listener<String> deleteResponseListener=new Response.Listener<String>() {
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
        RequestDeleteRequisition requestDeleteRequisition=new RequestDeleteRequisition(yesterday,deleteResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestDeleteRequisition);
    }

    private static  void postBookRequisition(final Context mContext, long id, ArrayList<String> bookNames,
                                             ArrayList<String> bookAuthors, ArrayList<String> subjects,
                                             String date)
    {
        Response.Listener<String> bookResponseListener=new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Posted",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
//                    err.setText(e.toString());
                }


            }
        };
        RequestPostBook requestPostBook=new RequestPostBook(id, bookNames, bookAuthors, subjects,
                date,bookResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostBook);
    }

    private static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String text=parent.getItemAtPosition(position).toString();
        int in=0,c;
        if(!text.equals(" "))
            in=Integer.parseInt(text);
        LinearLayout l=findViewById(R.id.ll);
        c=l.getChildCount();
        if(c>0&&in<c/2)
            l.removeViews(in*2,c-in*2);
        for(int i=c;i<in*2;i+=2) {
            TextInputLayout tl = new TextInputLayout(this);
            EditText my=new EditText(this);
            my.setHint("Book "+Integer.toString(i/2+1));
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setMargins(40,30,40,0);
            my.setLayoutParams(ll);
            my.setTextSize(18);
            Typeface font =Typeface.createFromAsset(getAssets(),"century.ttf");
            my.setTypeface(font);
            my.setBackgroundResource(R.drawable.textboxbg);
            my.setTextColor(getResources().getColor(android.R.color.white));
            my.setPadding(30,30,30,30);
            my.setId(((i/2)*10)+1);
            tl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tl.addView(my);
            l.addView(tl);
            TextInputLayout t2 = new TextInputLayout(this);
            EditText my2=new EditText(this);
            my2.setHint("Author");
            LinearLayout ll2=new LinearLayout(this);
            ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            ll2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.5f);
            lp.setMargins(20,0,20,40);
            my2.setLayoutParams(lp);
            my2.setTypeface(font);
            my2.setBackgroundResource(R.drawable.textboxbg);
            my2.setTextColor(getResources().getColor(android.R.color.white));
            my2.setPadding(30,30,30,30);
            my2.setId(((i/2)*10)+2);
            t2.setLayoutParams(lp);
            t2.addView(my2);
            ll2.addView(t2);
            TextInputLayout t3 = new TextInputLayout(this);
            EditText my3=new EditText(this);
            my3.setHint("Subject");
            my3.setLayoutParams(lp);
            my3.setTypeface(font);
            my3.setBackgroundResource(R.drawable.textboxbg);
            my3.setTextColor(getResources().getColor(android.R.color.white));
            my3.setPadding(30,30,30,30);
            my3.setId(((i/2)*10)+3);
            t3.setLayoutParams(lp);
            t3.addView(my3);
            ll2.addView(t3);
            l.addView(ll2);

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
