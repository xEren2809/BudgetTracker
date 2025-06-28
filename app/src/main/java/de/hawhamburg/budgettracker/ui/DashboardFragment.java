package de.hawhamburg.budgettracker.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import de.hawhamburg.budgettracker.R;
import de.hawhamburg.budgettracker.ui.Model.Data;


public class DashboardFragment extends Fragment {

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean
    private boolean isFabOpen = false;

    //Animation
    private Animation FadeOpen;
    private Animation FadeClose;

    //Dashboard income and expense result
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase:
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    private FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter;
    private FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        //Unique UserID info
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //Connect floating buttons to layout
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        //Connect floating text to layout
        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //Connect animation to layout
        FadeOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getContext(), R.anim.fade_close);

        //Total income and expense result
        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        //Recycler
        mRecyclerIncome = myview.findViewById(R.id.recycler_id_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_id_expense);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isFabOpen) {

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isFabOpen = false;

                } else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isFabOpen = true;
                }
            }
        });

        //Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalSum = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data data = dataSnapshot.getValue(Data.class);

                    totalSum += data.getAmount();

                    String stTotalSum = String.valueOf(totalSum);

                    totalIncomeResult.setText(stTotalSum+".00€");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalSum = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data data = dataSnapshot.getValue(Data.class);

                    totalSum += data.getAmount();

                    String stTotalSum = String.valueOf(totalSum);

                    totalExpenseResult.setText(stTotalSum+".00€");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler view
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);




        return myview;
    }

    // Floating button animation
    private void ftAnimation() {

        if (isFabOpen) {

            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isFabOpen = false;

        } else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isFabOpen = true;
        }

    }

    private void addData() {

        //Fab Button income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incomeDataInsert();

            }

        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myviewm = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myviewm);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount = myviewm.findViewById(R.id.amount_edt);
        EditText editType = myviewm.findViewById(R.id.type_edt);
        EditText editNote = myviewm.findViewById(R.id.note_edt);

        Button btnSave = myviewm.findViewById(R.id.btnSave);
        Button btnCancel = myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = editAmount.getText().toString().trim();
                String type = editType.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Amount is required");
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    editType.setError("Type is required");
                    return;
                }

                int outamountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Note is required");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(outamountint, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data inserted successfully", Toast.LENGTH_SHORT).show();

                ftAnimation(); //remove floating button
                dialog.dismiss();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation(); //remove floating button
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);

        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false); //click outside doesnt cancel

        EditText amount = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Amount is required");
                    return;
                }
                int outamountint = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Type is required");
                    return;
                }
                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Note is required");
                    return;
                }

                //für Datenbank einfügen
                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(outamountint, tmType, tmNote, id, mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data inserted successfully", Toast.LENGTH_SHORT).show();

                ftAnimation(); //remove floating button
                dialog.dismiss();


            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation(); //remove floating button
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder incomeHolder, int position, @NonNull Data model) {
                incomeHolder.setIncomeType(model.getType());
                incomeHolder.setIncomeAmount(model.getAmount());
                incomeHolder.setIncomeDate(model.getDate());
            }
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder expenseHolder, int position, @NonNull Data model) {
                expenseHolder.setExpenseType(model.getType());
                expenseHolder.setExpenseAmount(model.getAmount());
                expenseHolder.setExpenseDate(model.getDate());
            }
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();

    }

    //For Income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type){

            TextView mType = mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        public void setIncomeAmount(int amount){

            TextView mAmount = mIncomeView.findViewById(R.id.amount_Income_ds);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount+".00€");
        }
        public void setIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }
    }

    //For Expense Data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type){

            TextView mType = mExpenseView.findViewById(R.id.type_Expense_ds);
            mType.setText(type);
        }

        public void setExpenseAmount(int amount){

            TextView mAmount = mExpenseView.findViewById(R.id.amount_Expense_ds);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount+".00€");
        }
        public void setExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }
    }


}