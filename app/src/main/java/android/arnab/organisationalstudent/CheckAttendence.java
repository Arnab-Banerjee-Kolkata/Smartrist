package android.arnab.organisationalstudent;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class CheckAttendence extends AppCompatActivity implements OnChartValueSelectedListener
{
    float attendenceVal[]={25,75};
    String status[]={"Present","Absent"};
    int grade,group;
    String department;
    long id;
    static final int TOTAL_STUDENTS=60;
    ProgressBar totalClassProgress;
    static PieChart chart;
    static TextView attendenceInfo;
    static String total;
    ImageView attendenceBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendence);

        totalClassProgress=findViewById(R.id.totalClassProgress);
        chart=findViewById(R.id.attendenceChart);
        attendenceInfo=findViewById(R.id.attendenceInfo);
        attendenceBg=findViewById(R.id.attendenceBg);
        Toolbar toolbar=findViewById(R.id.toolbar3);
        toolbar.setTitle("Attendance");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int resid = R.drawable.stubg;
        Glide
                .with(this)
                .load(resid).into(attendenceBg);

        SharedPreferences preferences=getSharedPreferences("Details",0);
        id=preferences.getLong("id",-1);
        grade=preferences.getInt("grade",-1);
        department=preferences.getString("department","");

        totalClassProgress.setVisibility(View.VISIBLE);
        chart.setOnChartValueSelectedListener(CheckAttendence.this);

        int temp=(int)(id%100);
        if(temp<=30)
            group=1;
        else
            group=2;


        AttendenceServerRequests atnReq=new AttendenceServerRequests();
        atnReq.getTotalClasses(getApplicationContext(),id,grade,department,group,totalClassProgress);
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

    static void setUpPieChart(ArrayList<String> topics, ArrayList<Float> attended, float value, ArrayList<String> attendedPart, String total)
    {

        CheckAttendence.total=total;
        attendenceInfo.setText("Attended: "+total);
        //populating a list of pie entries
        List<PieEntry> pieEntries=new ArrayList<>();
        for (int i=0;i<topics.size();i++)
        {
            pieEntries.add(new PieEntry(attended.get(i),topics.get(i),attendedPart.get(i)));
        }
        value=(float)Math.round(value*100)/100;

        PieDataSet dataSet=new PieDataSet(pieEntries,"Attendence %");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data=new PieData(dataSet);

        data.setValueTextSize(20);
        data.setValueFormatter(new PercentFormatter());

        //Get the chart

        chart.setData(data);
        chart.animateY(1000);
        chart.setUsePercentValues(true);
        chart.setCenterText(value+"%");
        chart.setCenterTextSize(30);
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h)
    {
        attendenceInfo.setText("Attended: "+e.getData().toString());

    }

    @Override
    public void onNothingSelected()
    {
        attendenceInfo.setText("Attended: "+total);

    }

}
