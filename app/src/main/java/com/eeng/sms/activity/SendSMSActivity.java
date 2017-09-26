package com.eeng.sms.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eeng.sms.R;
import com.eeng.sms.model.Customer;
import com.eeng.sms.util.FileUtil;
import com.eeng.sms.util.SMSUtil;
import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendSMSActivity extends AppCompatActivity {
    private static final int PERMISSION_ALL = 10;
    private static final int SEND_SMS_PERMISSION = 11;
    private String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] SMS_PERMISSIONS =  {Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS};


    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.file_name) TextView fileNameLabel;
    @Bind(R.id.greeting_edit) EditText greetingEdit;
    @Bind(R.id.message_edit) EditText messageEdit;

    private List<Customer> customerList;
    private String greeting;
    private String contentMessage;

    private File fileCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sms_page);

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
    }

    private String validateField() {
        String msg = "";

        if (fileNameLabel.getText().toString().equals(getString(R.string.label_file_not_uploaded)))
            msg = getString(R.string.message_file_not_uploaded);
        else if (greetingEdit.getText().toString().equals(""))
            msg = getString(R.string.message_greeting_is_blank);
        else if (messageEdit.getText().toString().equals(""))
            msg = getString(R.string.message_message_is_blank);

        return msg;
    }

    @OnClick({R.id.upload_button, R.id.send_button, R.id.view_data})
    public void onClick(View view) {
        if (view.getId() == R.id.upload_button) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select .xls File"), 1);
            }

        } else if (view.getId() == R.id.send_button) {
            String msg = validateField();

            if (!msg.equals("")) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                greeting = greetingEdit.getText().toString();
                contentMessage = messageEdit.getText().toString();

                if (!hasPermissions(this, SMS_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, SMS_PERMISSIONS, SEND_SMS_PERMISSION);
                } else {
                    new SendSMSTask().execute();
                }
            }
        } else if (view.getId() == R.id.view_data) {
            if (!fileNameLabel.getText().toString().equals(getString(R.string.label_file_not_uploaded))) {
                Intent intent = new Intent(this, CustomerSummaryActivity.class);
                intent.putExtra("json", new Gson().toJson(customerList));
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.label_file_not_uploaded), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {

                    try {
                        Uri uri = data.getData();
                        String path = FileUtil.getAbsolutePath(this, uri);
                        fileCopy = new File(path);
                        String fileName = path.substring(path.lastIndexOf("/") + 1);

                        System.out.println("==PATH : " + path);

                        if (!fileName.toLowerCase().endsWith("xls")) {
                            Toast.makeText(this, getString(R.string.message_file_not_supported), Toast.LENGTH_SHORT).show();
                        }

                        FileUtil.copyFile(fileCopy.getParent() + "/", fileName);

//                        readExcelXLSFile(fileCopy);

                        fileNameLabel.setText(fileName);

                        new ReadFile().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void readExcelXLSFile(File file) {
        try {
            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /**
             * We now need something to iterate through the cells.*
             */
            Iterator rowIter = mySheet.rowIterator();

            customerList = new ArrayList<>();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (myRow.getRowNum() == 0) {
                    continue;
                }
                Iterator cellIter = myRow.cellIterator();
                int indexCell = 0;
                Customer customer = new Customer();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();

                    if (indexCell == 0) {
                        customer.setCustomerName(myCell.toString());
                    } else if (indexCell == 1) {
                        customer.setPhoneNumber(myCell.toString());
                    } else if (indexCell == 2) {
                        customer.setGender(myCell.toString());
                    }
                    indexCell += 1;

                }

                if (customer.getCustomerName() == null || customer.getCustomerName().equals(""))
                    continue;

                customer.setTitle(customer.getGender().equalsIgnoreCase("L") ? " Pak" : "Bu");

                customerList.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select .xls File"), 1);
                }

                break;
            } case SEND_SMS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    new SendSMSTask().execute();

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private class ReadFile extends AsyncTask<Object, String, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(SendSMSActivity.this, "", "Loading ...", true, false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {

            readExcelXLSFile(fileCopy);

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if (dialog != null && dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    private class SendSMSTask extends AsyncTask<Object, String, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(SendSMSActivity.this, "", "Kirim SMS ...", true, false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {

            for (Customer customer : customerList) {
                SMSUtil.sendLongSMS(SendSMSActivity.this, customer, greeting, contentMessage);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if (dialog != null && dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(SendSMSActivity.this, getString(R.string.message_sms_sent), Toast.LENGTH_SHORT).show();
        }
    }



}
