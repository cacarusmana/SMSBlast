package com.eeng.sms.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.eeng.sms.R;
import com.eeng.sms.model.Customer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Caca Rusmana on 05/03/2017.
 */

public class CustomerSummaryActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.table_layout) TableLayout tableLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_summary_page);
        ButterKnife.bind(this);

        initComponent();
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     onBackPressed();
                                                 }
                                             }
        );


//        Type listOfTestObject = new TypeToken<List<Customer>>(){}.getType();
//        List<Customer> customerList = new Gson().fromJson(getIntent().getExtras().getString("json"), listOfTestObject);
//
//        for (int i = 0; i < customerList.size(); i++) {
//            tableLayout.addView(addTableRow(i, customerList.get(i)));
//        }


        Thread t = new Thread("Thread1") {
            @Override
            public void run() {
                // some code #2
                runOnUiThread(new Runnable() {
                    public void run() {
                        Type listOfTestObject = new TypeToken<List<Customer>>() {
                        }.getType();
                        List<Customer> customerList = new Gson().fromJson(getIntent().getExtras().getString("json"), listOfTestObject);


                        for (int i = 0; i < customerList.size(); i++) {
                            tableLayout.addView(addTableRow(i, customerList.get(i)));
                        }
                    }
                });

            }
        };

        t.start();

    }


    private TableRow addTableRow(int index, Customer customer) {
        TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row, null);

        TextView field1 = (TextView) tableRow.findViewById(R.id.field1);
        field1.setText(customer.getCustomerName());

        TextView field2 = (TextView) tableRow.findViewById(R.id.field2);
        field2.setText(customer.getPhoneNumber());

        TextView field3 = (TextView) tableRow.findViewById(R.id.field3);
        field3.setText(customer.getGender());

        if (index % 2 == 0) {
            tableRow.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            tableRow.setBackgroundColor(getResources().getColor(R.color.light_gray));
        }

        return tableRow;
    }


}
