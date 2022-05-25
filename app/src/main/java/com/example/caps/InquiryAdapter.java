package com.example.caps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.InquiryMyViewHolder> {

    private List<InquiryData> useList;
    private Activity activity;


    public InquiryAdapter(List<InquiryData> useList, Activity activity) {
        this.useList = useList;
        this.activity = activity;
    }


    @NonNull @Override
    public InquiryMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.inquiry, parent, false);
        return new InquiryMyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryMyViewHolder holder, int position) {
        String inout_type = useList.get(position).getInout_type(); //이용금액
        String content= useList.get(position).getContent(); // 이용 내역
        String tran_amt =  useList.get(position).getTran_amt();


        holder.text.setVisibility(View.VISIBLE);
        holder.nameText.setVisibility(View.VISIBLE);
        holder.moneyText.setVisibility(View.VISIBLE);
        holder.nameText.setText(content);
        if(inout_type.equals("입금")){
            holder.moneyText.setText(tran_amt);
        }
        else{
            String cal = "-" + tran_amt;
            holder.moneyText.setText(cal);
        }



    }

    @Override public int getItemCount() {
        return useList.size();
    }

    class InquiryMyViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView nameText;
        TextView moneyText;


        InquiryMyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.temp);
            nameText = itemView.findViewById(R.id.useName);
            moneyText = itemView.findViewById(R.id.useMoney);
        }
    }

}


