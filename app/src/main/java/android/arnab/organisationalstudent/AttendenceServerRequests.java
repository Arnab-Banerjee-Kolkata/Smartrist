package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AttendenceServerRequests
{
    Map<String,Integer> classTable;
    Map<String,Integer> attendedTable;
    ProgressBar totalClassProgress;
    void getTotalClasses(final Context mContext, final long id, int grade, String department, int group,
                         final ProgressBar totalClassProgress)
    {
        this.totalClassProgress=totalClassProgress;
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //erMsg.setText(response);
                try
                {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        String msg="";
                        classTable=new HashMap<>();
                        float totalClass=0;
                        for (String key : iterate(jsonResponse.keys()))
                        {
                            if(!key.equalsIgnoreCase("success"))
                            {
                                classTable.put(key, jsonResponse.optInt(key));
                                totalClass += jsonResponse.optInt(key);
                                //msg += key + ":" + classTable.get(key);
                            }
                        }
                        //Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                        AttendenceServerRequests.this.getCandidateAttendence(mContext,id,totalClass);

                    }
                    else
                        Toast.makeText(mContext,"false",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String TOTAL_CLASSES_URL= String.format("http://arnabbanerjee.dx.am/RequestTotalClasses.php?id=%1$d&classGroup=%2$d",id,group);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(TOTAL_CLASSES_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }


    void getCandidateAttendence(final Context mContext, long id, final float totalClass)
    {
        Response.Listener<String> attendedResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                totalClassProgress.setVisibility(View.GONE);
                JSONObject jsonResponse= null;
                //erMsg.setText(response);
                try
                {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        String msg="";
                        attendedTable=new HashMap<>();
                        float totalAttended=0;
                        ArrayList<String> topics=new ArrayList<String>();
                        ArrayList<Float> attended=new ArrayList<Float>();
                        ArrayList<String> attendedPart=new ArrayList<String>();
                        for (String key : iterate(jsonResponse.keys()))
                        {
                            if(!key.equalsIgnoreCase("success"))
                            {
                                attendedTable.put(key, jsonResponse.optInt(key));
                                totalAttended += jsonResponse.optInt(key);
                                topics.add(key);
                                attended.add((float)(jsonResponse.optInt(key)/totalClass)*100);
                                    attendedPart.add(jsonResponse.optInt(key)+"/"+classTable.get(key));


                                //msg+=key+":"+attendedTable.get(key);
                            }
                        }
                        //Toast.makeText(mContext,totalAttended+" "+totalClass,Toast.LENGTH_LONG).show();
                        topics.add("Absent");
                        attended.add((float)((totalClass-totalAttended)/totalClass)*100);
                        attendedPart.add(0+"/"+(int)(totalClass-totalAttended));
                        String total=(int)totalAttended+"/"+(int)totalClass;
                        float totalAttendence=(float)(totalAttended/totalClass)*100;

                        CheckAttendence.setUpPieChart(topics,attended,totalAttendence,attendedPart,total);




                    }
                    else
                        Toast.makeText(mContext,"false",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String ATTENDED_CLASSES_URL= String.format("http://arnabbanerjee.dx.am/RequestTotalAttended.php?candidateId=%1$d",id);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(ATTENDED_CLASSES_URL,attendedResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }



    private <T> Iterable<T> iterate(final Iterator<T> i){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return i;
            }
        };
    }
}
