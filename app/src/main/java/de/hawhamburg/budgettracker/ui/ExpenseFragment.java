package de.hawhamburg.budgettracker.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.util.Date;

import de.hawhamburg.budgettracker.R;
import de.hawhamburg.budgettracker.ui.Model.Data;


public class ExpenseFragment extends Fragment {

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter;

    //text view result
    private TextView expenseTotalSum;

    //Update edit text
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnCancel;

    //Data variable

    private int amount;
    private String type;
    private String note;
    private String post_key;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            post_key = getRef(currentPosition).getKey();

                            Data currentModel = getItem(currentPosition);

                            amount = model.getAmount();
                            type = model.getType();
                            note = model.getNote();
                            updateDataItem();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {

                    Data data = mysnapshot.getValue(Data.class);

                    totalValue += data.getAmount();

                    String stTotalValue = String.valueOf(totalValue);

                    expenseTotalSum.setText(stTotalValue+".00€");
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type) {

            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);

        }
        private void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);

        }
        private void setDate(String date){

            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);

        }
        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_expense);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount+".00€");
        }


    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        btnUpdate = myview.findViewById(R.id.btn_update_Update);
        btnDelete = myview.findViewById(R.id.btn_update_Delete);
        btnCancel = myview.findViewById(R.id.btn_update_Cancel);


        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stamount = String.valueOf(amount);
                stamount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(stamount);

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmount, type, note, post_key, mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }
}