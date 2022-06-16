package com.example.caps;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountMyViewHolder> {

    private List<Pay> accountList;
    private Activity activity;

    public AccountAdapter(List<Pay> accountList, Activity activity) {
        this.accountList = accountList;
        this.activity = activity;
    }

    @NonNull @Override
    public AccountMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.account, parent, false);
        return new AccountMyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountMyViewHolder holder, int position) {
        String bankInfo = accountList.get(position).getBankInfo();
        String num = accountList.get(position).getBankId();
        String money = accountList.get(position).getMoney();
        holder.bank1.setVisibility(View.VISIBLE);
        holder.btn1.setVisibility(View.VISIBLE);
        holder.btn2.setVisibility(View.VISIBLE);
        holder.bankInfo.setVisibility(View.VISIBLE);
        holder.bankInfo.setVisibility(View.VISIBLE);
        holder.money.setVisibility(View.VISIBLE);
        holder.num.setText(num);
        holder.bankInfo.setText(bankInfo);
        holder.money.setText(money);

    }

    @Override public int getItemCount() {
        return accountList.size();
    }


    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener1;
    private OnItemClickListener mListener2;

    public void setOnItemClickListener1(OnItemClickListener listener){
        this.mListener1 = listener;
    }
    public void setOnItemClickListener2(OnItemClickListener listener){
        this.mListener2 = listener;
    }



    class AccountMyViewHolder extends RecyclerView.ViewHolder{
        TextView bankInfo;
        TextView money;
        TextView num;
        Button btn1;
        Button btn2;
        ImageView bank1;


        AccountMyViewHolder(@NonNull View itemView) {
            super(itemView);
            bankInfo = itemView.findViewById(R.id.bankInfo);
            money = itemView.findViewById(R.id.money);
            num = itemView.findViewById(R.id.num);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            bank1 = itemView.findViewById(R.id.bank1);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    mListener1.onItemClick(view, position);
                }
            });

            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    mListener2.onItemClick(view, position);
                }
            });

        }


    }
}

