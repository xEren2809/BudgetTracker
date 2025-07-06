package de.hawhamburg.budgettracker.ui.View;

import android.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.Locale;

import de.hawhamburg.budgettracker.R;
import de.hawhamburg.budgettracker.ui.Model.Data;


public class IncomeFragment extends Fragment {

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;

    //Text view result
    private TextView incomeTotalSum;

    //Update edit text
    private EditText edtAmount;
    private Spinner edtType;  // statt EditText

    private EditText edtNote;

    //Button for Update and Delete
    private Button btnUpdate;
    private Button btnDelete;

    private Button btnCancel;

    //Data item value
    private int amount;
    private String type;
    private String note;

    private String post_key;

    private FloatingActionButton fabAddIncome;

    private String currentSortField = "date"; // Standard-Sortierung
    private boolean sortAscending = true; // Start mit aufsteigend


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        //Unique UserID info
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        //Income Summe
        incomeTotalSum = myview.findViewById(R.id.income_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_income);

        Button btnSortAmount = myview.findViewById(R.id.btn_sort_amount);
        Button btnSortType = myview.findViewById(R.id.btn_sort_type);
        Button btnSortDate = myview.findViewById(R.id.btn_sort_date);

        btnSortAmount.setOnClickListener(v -> loadDataSorted("amount"));
        btnSortType.setOnClickListener(v -> loadDataSorted("type"));
        btnSortDate.setOnClickListener(v -> loadDataSorted("date"));

        recyclerView = myview.findViewById(R.id.recycler_id_income);

        initAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(!sortAscending); // <- wichtig
        layoutManager.setStackFromEnd(true);

        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        fabAddIncome = myview.findViewById(R.id.income_ft_btn);

        fabAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIncomeDialog(); // Methode zum Hinzufügen neuer Daten
            }
        });

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, final Data model) {
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totalvalue += data.getAmount();
                }

                String stTotalvalue = String.valueOf(totalvalue);
                incomeTotalSum.setText(stTotalvalue + ".00€");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: Fehlerbehandlung
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

            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);

        }
        private void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);

        }
        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date parsedDate = sdf.parse(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("d. MMMM yyyy", Locale.GERMANY);
                String formattedDate = outputFormat.format(parsedDate);
                mDate.setText(formattedDate);
            } catch (ParseException e) {
                mDate.setText(date); // Fallback
            }
        }

        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount+".00€");
        }


    }

    private void updateDataItem() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_spinner); // Spinner statt EditText
        edtNote = myview.findViewById(R.id.note_edt);

        // Spinner mit Kategorien füllen
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(),
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtType.setAdapter(adapterSpinner);

        // Daten setzen
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        int spinnerPosition = adapterSpinner.getPosition(type);
        edtType.setSelection(spinnerPosition);

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        btnUpdate = myview.findViewById(R.id.btn_update_Update);
        btnDelete = myview.findViewById(R.id.btn_update_Delete);
        btnCancel = myview.findViewById(R.id.btn_update_Cancel);

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mdamount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(mdamount);

                String myType = edtType.getSelectedItem().toString();
                String myNote = edtNote.getText().toString().trim();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String mDate = sdf.format(new Date());

                Data data = new Data(myAmount, myType, myNote, post_key, mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAddIncomeDialog() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.amount_edt);
        Spinner spinnerType = myview.findViewById(R.id.type_spinner); // Spinner statt EditText
        EditText note = myview.findViewById(R.id.note_edt);
        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        // Spinner mit Kategorien füllen
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(),
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSpinner);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmount = amount.getText().toString().trim();
                String tmType = spinnerType.getSelectedItem().toString();
                String tmNote = note.getText().toString().trim();

                if (tmAmount.isEmpty()) {
                    amount.setError("Amount is required");
                    return;
                }

                int outamountint = Integer.parseInt(tmAmount);

                if (tmNote.isEmpty()) {
                    note.setError("Note is required");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String mDate = sdf.format(new Date());

                Data data = new Data(outamountint, tmType, tmNote, id, mDate);
                mIncomeDatabase.child(id).setValue(data);

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initAdapter() {
        Query query;

        switch (currentSortField) {
            case "amount":
                query = mIncomeDatabase.orderByChild("amount");
                break;
            case "type":
                query = mIncomeDatabase.orderByChild("type");
                break;
            case "date":
            default:
                query = mIncomeDatabase.orderByChild("date");
                break;
        }

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, final Data model) {
                // Berechne "echte" Position je nach Sortierrichtung
                int realPosition = sortAscending ? position : getItemCount() - 1 - position;
                Data item = getItem(realPosition);

                holder.setType(item.getType());
                holder.setNote(item.getNote());
                holder.setDate(item.getDate());
                holder.setAmount(item.getAmount());

                holder.mView.setOnClickListener(v -> {
                    post_key = getRef(realPosition).getKey();
                    amount = item.getAmount();
                    type = item.getType();
                    note = item.getNote();
                    updateDataItem();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadDataSorted(String sortBy) {
        if (sortBy.equals(currentSortField)) {
            // Selbes Feld → Richtung umkehren
            sortAscending = !sortAscending;
        } else {
            // Neues Feld → Standardrichtung aufsteigend
            currentSortField = sortBy;
            sortAscending = true;
        }

        adapter.stopListening();
        initAdapter();
    }
}