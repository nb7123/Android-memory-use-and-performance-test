package com.michael.android.performancetest;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.michael.android.performancetest.databinding.ActivityMainBinding;
import com.michael.android.performancetest.viewmodel.Toggles;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
{
    public static final int COUNT = 100000;

    int originInt;
    String originString;

    Object obj;

    Toggles toggles;

    public void setOriginInt(int originInt)
    {
        this.originInt = originInt;
    }

    public void setOriginString(String originString)
    {
        this.originString = originString;
    }

    public void setObj(Object obj)
    {
        this.obj = obj;
    }

    Snackbar testingSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toggles = new Toggles();
        toggles.setMainActivityWorking(false);

        binding.setToggles(toggles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                testingSnackbar = Snackbar.make(view, "Testing...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null);
                testingSnackbar.show();

                test();
            }
        });

        initAdapter();

        initViews();
    }

    public void test()
    {
        testResults.clear();

        if (toggles.isMainActivityWorking())
        {
            return;
        }

        AsyncTask<Void, Integer, Void> testTask = new AsyncTask<Void, Integer, Void>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                toggles.setMainActivityWorking(true);
            }

            @Override
            protected Void doInBackground(Void... params)
            {
                testMultiAndShift();

                setMethodPerformanceTest();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                toggles.setMainActivityWorking(false);

                if (testingSnackbar != null && testingSnackbar.isShown())
                    testingSnackbar.dismiss();

                adapter.clear();
                for (SetterPerfomanceItem item : testResults)
                {
                    adapter.add(item);
                }
            }
        };

        testTask.execute();
    }

    @WorkerThread
    protected void testMultiAndShift()
    {
        long start = System.currentTimeMillis();

        int a = 1;

        for (int i = 0; i < COUNT; i++)
        {
            a <<= 1;
        }

        Log.d(getClass().getName(), "Shift " + COUNT + " times spend: " + (System.currentTimeMillis() - start));

        for (int i = 0; i < COUNT; i++)
        {
            a *= 2;
        }

        Log.d(getClass().getName(), "Mult " + COUNT + " times spend: " + (System.currentTimeMillis() - start));
    }

    ListView listView;
    TextView text1;

    protected void initViews()
    {
        listView = (ListView) findViewById(android.R.id.list);
        text1 = (TextView) findViewById(R.id.text1);

        text1.setText("Test count: " + String.valueOf(COUNT));

        listView.setAdapter(adapter);
    }

    LinkedList<SetterPerfomanceItem> testResults = new LinkedList<>();

    /**
     * test performance use set method to set self property and direct set self property
     * <p/>
     * like this: this.setXXX(xxx); and this.XXX = xxx;
     */
    @WorkerThread
    protected void setMethodPerformanceTest()
    {
        long start = System.currentTimeMillis();
        long spend = 0;

        int ai = 15;
        String s = "ABCD";
        Object object = getSystemService(ACTIVITY_SERVICE);

        // test origin
        for (int i = 0; i < COUNT; i++) {
            this.setOriginInt(ai);
        }

        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Set origin int " + COUNT + "times spend: " + spend + "milliseconds");

        testResults.add(new SetterPerfomanceItem(spend, "setOriginInt", "Int"));

        start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            this.setOriginString(s);
        }

        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Set origin String " + COUNT + "times spend: " + spend + "milliseconds");
        testResults.add(new SetterPerfomanceItem(spend, "setOriginString", "String"));

        // test set ui component a complex object
        start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            this.setObj(object);
        }

        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Set complex object View " + COUNT + "times spend: " + spend + "milliseconds");
        testResults.add(new SetterPerfomanceItem(spend, "SetUiObj", "View"));

        // direct set value test

        start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            this.originInt = ai;
        }
        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Direct set origin int " + COUNT + "times spend: " + spend + "milliseconds");
        testResults.add(new SetterPerfomanceItem(spend, "NULL", "Int"));


        start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            this.originString = s;
        }
        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Direct set origin String " + COUNT + "times spend: " + spend + "milliseconds");
        testResults.add(new SetterPerfomanceItem(spend, "NULL", "String"));

        start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            this.originString = s;
        }
        spend = System.currentTimeMillis() - start;
        Log.d(getClass().getName(), "Direct set complex object View " + COUNT + "times spend: " + spend + "milliseconds");
        testResults.add(new SetterPerfomanceItem(spend, "NULL", "View"));
    }

    ArrayAdapter<SetterPerfomanceItem> adapter;

    protected void initAdapter()
    {
        adapter = new ArrayAdapter<SetterPerfomanceItem>(this,
                R.layout.setter_item, R.id.spendTime,
                new ArrayList<SetterPerfomanceItem>())
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if (null == convertView) {
                    convertView = super.getView(position, null, parent);
                }

                TextView spendTime = (TextView) convertView.findViewById(R.id.spendTime);
                TextView methodName = (TextView) convertView.findViewById(R.id.methodName);
                TextView type = (TextView) convertView.findViewById(R.id.type);

                SetterPerfomanceItem item = getItem(position);

                spendTime.setText(String.valueOf(item.spendTime));
                methodName.setText(item.methodName);
                type.setText(item.type);

                return convertView;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
