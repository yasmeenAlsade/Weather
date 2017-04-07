package com.example.yasmeen.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    ListView weatherListView ;
    SwipeRefreshLayout swipeRefreshLayout ;
    ImageView bigPictureImageView ;
    TextView tempTextView , statusTextView , dayTextView , countryTextView;


    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherListView = (ListView)findViewById(R.id.list) ;
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh) ;
        bigPictureImageView = (ImageView)findViewById(R.id.bigPicture) ;
        tempTextView =(TextView)findViewById(R.id.TemperatureTextView) ;
        statusTextView = (TextView)findViewById(R.id.StatusTextView) ;
        dayTextView = (TextView)findViewById(R.id.dayTextView) ;
        countryTextView = (TextView)findViewById(R.id.countryTextView) ;


        //using picasso library to get image from the internet and resize the image
        //Picasso.with(getApplicationContext()).load("http://openweathermap.org/img/w/10d.png").resize(200 ,200).into(bigPictureImageView) ;


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                SharedPreferences sharedPreferences = getSharedPreferences("Country" , MODE_PRIVATE) ;
                String name =  sharedPreferences.getString("CountryName" ,"palestine" ) ;

                FetchWeatherTask fetchWeatherTask = new FetchWeatherTask() ;
                fetchWeatherTask.execute(name) ;

                Toast.makeText(getApplicationContext(), "Successfully Updated" , Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        //ArrayList<String> arrayList = new ArrayList<String>() ;

        //arrayList.add("yasmeen");
        //arrayList.add("hisham");
        //arrayList.add("alsade");



        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask() ;
        fetchWeatherTask.execute("palestine") ;


       // http://api.openweathermap.org/data/2.5/forecast/daily?q=palestine&units=metric&APPID=6927a83c28b2936a157a9574f3a2c2a4

    }

    public void Country(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.BLACK);
        edittext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        edittext.setHint("Type City Name");
        edittext.setHintTextColor(Color.GRAY);
        alert.setTitle("Set Location");

        alert.setView(edittext);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String country = edittext.getText().toString();
                FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
                fetchWeatherTask.execute(country) ;
                countryTextView.setText(country.substring(0,1).toUpperCase() + country.substring(1));

                //OR
                //String YouEditTextValue = edittext.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }



    private class FetchWeatherTask extends AsyncTask<String , Void , String>

    {
        ArrayList<Data> arrayList = new ArrayList<Data>();
        @Override
        protected String doInBackground(String... params) {
            BufferedReader bufferedReader ;
            StringBuilder result = new StringBuilder() ;
            String line ;
            URL url ;

            SharedPreferences sharedPreferences = getSharedPreferences("Country" , MODE_PRIVATE) ;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("CountryName" , params[0]) ;
            editor.commit() ;


            try{
                if(params[0] == "palestine")
                {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=palestine&units=metric&APPID=6927a83c28b2936a157a9574f3a2c2a4") ;
                }
                else
                {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + params[0]+"&units=metric&APPID=6927a83c28b2936a157a9574f3a2c2a4") ;
                }
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=palestine&units=metric&APPID=6927a83c28b2936a157a9574f3a2c2a4") ;
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection() ;
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())) ;
                while((line=bufferedReader.readLine())!=null){
                    result.append(line) ;
                }


                Log.d("AsyncTask" , result.toString()) ;
                return result.toString() ;


            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(getApplicationContext() , s.toString() , Toast.LENGTH_LONG).show();

            if(s != null)
            {
                try
                {
                    String day , min , max , desc , icon ;
                    JSONObject weatherObject = new JSONObject(s) ;
                    JSONArray listArray = weatherObject.getJSONArray("list") ;

                    for(int i = 0 ; i < listArray.length() ; i++)
                    {
                        JSONObject listObject = listArray.getJSONObject(i) ;

                        JSONObject tempObject = listObject.getJSONObject("temp") ;
                        min = tempObject.getString("min") ;
                        max = tempObject.getString("max") ;

                        JSONArray weatherArray = listObject.getJSONArray("weather") ;
                        desc = weatherArray.getJSONObject(0).getString("main") ;
                        icon = weatherArray.getJSONObject(0).getString("icon") ;

                        //create a Gregorian Calendar, which is in current date
                        GregorianCalendar gc = new GregorianCalendar();
                        //add i dates to current date of calendar
                        gc.add(GregorianCalendar.DATE, i);
                        //get that date, format it, and "save" it on variable day
                        Date time = gc.getTime();
                        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                        day = shortenedDateFormat.format(time);

                        if(i==0)
                        {
                            statusTextView.setText(desc);
                            tempTextView.setText(max);
                            dayTextView.setText(day);
                            Picasso.with(getApplicationContext()).load("http://openweathermap.org/img/w/" + icon +".png").resize(200 ,200).into(bigPictureImageView) ;
                        }

                        Data data = new Data(min , max , icon , day , desc) ;
                        arrayList.add(data);

                        //Toast.makeText(getApplicationContext() , min + " " + max + " " + desc + " " + icon +" " + day , Toast.LENGTH_LONG).show(); ;
                    }

                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext() , arrayList) ;

                    weatherListView.setAdapter(customAdapter);




                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }





    private class CustomAdapter extends ArrayAdapter<Data>
    {
        ArrayList<Data> list ;
        Context context ;

        public CustomAdapter(Context context, ArrayList<Data> list) {
            super(context, R.layout.list_item , list);

            this.list = list ;
            this.context = context ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null)
            {
                LayoutInflater mInflator = LayoutInflater.from(context) ;
                convertView = mInflator.inflate(R.layout.list_item , parent , false) ;
            }

            TextView listItemDateView = (TextView)convertView.findViewById(R.id.list_item_date_textview) ;
            TextView listItemforecastView = (TextView)convertView.findViewById(R.id.list_item_forecast_textview) ;
            TextView listItemMaxView = (TextView)convertView.findViewById(R.id.list_item_high_textview);
            TextView listItemMinView = (TextView)convertView.findViewById(R.id.list_item_low_textview);

            ImageView listItemImageView = (ImageView)convertView.findViewById(R.id.list_item_icon) ;

            listItemDateView.setText(list.get(position).getDay());

            listItemforecastView.setText(list.get(position).getDesc());

            listItemMaxView.setText(list.get(position).getMax());

            listItemMinView.setText(list.get(position).getMin());

            Picasso.with(getApplicationContext()).load("http://openweathermap.org/img/w/" + list.get(position).getIcon()+ ".png").resize(150,150).into(listItemImageView) ;

            return convertView ;

        }
    }
}
