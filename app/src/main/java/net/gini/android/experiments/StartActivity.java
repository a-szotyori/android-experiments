package net.gini.android.experiments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @Bind(R.id.list_experiments)
    ListView mListExperiments;

    private Class[] mExperiments = new Class[]{
            OrientationActivity.class,
            DeferredActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mListExperiments.setAdapter(new ExperimentsListAdapter(mExperiments, getLayoutInflater()));
        mListExperiments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class activityClass = mExperiments[position];
                startActivity(new Intent(StartActivity.this, activityClass));
            }
        });
    }

    class ExperimentsListAdapter extends BaseAdapter {

        private final Class[] mExperiments;
        private final LayoutInflater mLayoutInflater;

        private ExperimentsListAdapter(Class[] experiments, LayoutInflater layoutInflater) {
            mExperiments = experiments;
            mLayoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return mExperiments.length;
        }

        @Override
        public Object getItem(int position) {
            return mExperiments[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.textView.setText(mExperiments[position].getSimpleName());

            return convertView;
        }

        class ViewHolder {
            @Bind(android.R.id.text1)
            TextView textView;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
