package android.arnab.organisationalstudent;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemAdap extends RecyclerView.Adapter<ItemAdap.itemViewHolder> {
    ArrayList<Item> itemArrayList;
    public static ArrayList<String> values = new ArrayList<String>();

    public static class itemViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodicon;
        public TextView foodname;
        public TextView foodprice;
        public TextView foodcount;
        public Button fooddecr, foodincr;
        RelativeLayout itemFade;

        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodicon = itemView.findViewById(R.id.foodicon);
            foodname = itemView.findViewById(R.id.foodname);
            foodprice = itemView.findViewById(R.id.foodprice);
            fooddecr = itemView.findViewById(R.id.fooddecr);
            foodincr = itemView.findViewById(R.id.foodincr);
            foodcount = itemView.findViewById(R.id.foodcount);
            itemFade=itemView.findViewById(R.id.itemFade);

            fooddecr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int c = Integer.parseInt(foodcount.getText().toString());
                    if (c > 0)
                        c--;
                    foodcount.setText(Integer.toString(c));
                }
            });
            foodincr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int c = Integer.parseInt(foodcount.getText().toString());
                    c++;
                    foodcount.setText(Integer.toString(c));
                }
            });

            foodcount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    values.set(getAdapterPosition(), s.toString());
                }
            });
        }
    }

    public ItemAdap(ArrayList<Item> itemlist) {
        itemArrayList = itemlist;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);
        itemViewHolder ivh = new itemViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder itemViewHolder, int i)
    {
        values.add("0");
        Item currentItem = itemArrayList.get(i);
        Glide.with(itemViewHolder.foodicon.getContext()).load(currentItem.getMimageres()).into(itemViewHolder.foodicon);
        itemViewHolder.foodname.setText(currentItem.getMtxt1());
        itemViewHolder.foodprice.setText("Rs. " + currentItem.getMtxt2());
        itemViewHolder.foodcount.setText(values.get(i));


        if(currentItem.getAvailableState()==0)
        {
            itemViewHolder.itemFade.setVisibility(View.VISIBLE);
            itemViewHolder.fooddecr.setEnabled(false);
            itemViewHolder.foodincr.setEnabled(false);
        }
        else
        {
            itemViewHolder.itemFade.setVisibility(View.GONE);
            itemViewHolder.fooddecr.setEnabled(true);
            itemViewHolder.foodincr.setEnabled(true);
        }

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public static void initiateValues()
    {
        values=new ArrayList<String>();
    }
}