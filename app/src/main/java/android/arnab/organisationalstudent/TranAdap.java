package android.arnab.organisationalstudent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TranAdap extends RecyclerView.Adapter<TranAdap.tranViewHolder> {
    private ArrayList<TranItem> tranArrayList;

    public static class tranViewHolder extends RecyclerView.ViewHolder {
        public TextView itemname;
        public TextView timedate;
        public TextView change;
        public TextView balance;
        public ImageView trantype, tranwall;

        public tranViewHolder(@NonNull final View tranView) {
            super(tranView);
            itemname = tranView.findViewById(R.id.itemname);
            timedate = tranView.findViewById(R.id.time);
            balance = tranView.findViewById(R.id.balance);
            change = tranView.findViewById(R.id.change);
            trantype = tranView.findViewById(R.id.trantype);
            tranwall = tranView.findViewById(R.id.tranwall);

        }
    }

    public TranAdap(ArrayList<TranItem> itemlist) {
        tranArrayList = itemlist;
    }

    @NonNull
    @Override
    public tranViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_item, viewGroup, false);
        tranViewHolder ivh = new tranViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull tranViewHolder tranViewHolder, int i) {
        TranItem currentItem = tranArrayList.get(i);
        tranViewHolder.timedate.setText(currentItem.getMdate() + "\t\t" + currentItem.getMtime());
        tranViewHolder.balance.setText("Closing Balance : Rs. "+Integer.toString(currentItem.getMbalance()));
        if (currentItem.getMchange() < 0)
        {
            tranViewHolder.trantype.setImageResource(R.drawable.deduct_red);
            tranViewHolder.tranwall.setImageResource(R.drawable.balance_deduct);


            tranViewHolder.change.setText("- Rs. "+Integer.toString(Math.abs(currentItem.getMchange())));
            tranViewHolder.itemname.setText("Paid to "+currentItem.getMtxt1());
        }
        else
        {
            tranViewHolder.trantype.setImageResource(R.drawable.add_green);
            tranViewHolder.tranwall.setImageResource(R.drawable.balance_add);


            tranViewHolder.change.setText("+ Rs. " + Integer.toString(currentItem.getMchange()));
            tranViewHolder.itemname.setText(currentItem.getMtxt1());
        }
    }

    @Override
    public int getItemCount() {
        return tranArrayList.size();
    }

}