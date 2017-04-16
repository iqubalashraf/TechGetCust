package location.app.techgetcust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    TextView userName,userAddress;
    Spinner customerDropDownMenu;
    List<String> techDetailsList = new ArrayList<String>();
    ListView lv;
    public static final String SERVER_IP_ADDRESS = "192.168.43.47:8080";
    String address,mobileNo,pincode;
    String latitude= null;
    String longitude= null;
    String latitudeTec = null;
    String longitudeTec = null;
    String serverResponse;
    int check = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        String customerName = intent.getStringExtra(SplashScreen.CUSTOMERNAME);
        address = intent.getStringExtra(SplashScreen.ADDRESS);
        mobileNo = intent.getStringExtra(SplashScreen.MOBILENO);
        pincode = intent.getStringExtra(SplashScreen.PINCODE);
        latitude = intent.getStringExtra(SplashScreen.LATITUDE);
        longitude = intent.getStringExtra(SplashScreen.LONGITUDE);
        userName = (TextView)findViewById(R.id.userName);
        userName.setText("Hello "+customerName+"\n");
        userAddress= (TextView)findViewById(R.id.userAddress);
        userAddress.setText("Your current address is: \n"+address+"\nPhone No: "+mobileNo+" \nPincode: "+pincode);
        customerDropDownMenu = (Spinner)findViewById(R.id.customerDropDownMenu);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.customerDropDownList, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        customerDropDownMenu.setAdapter(adapter);
    }
    public void getDataForUser(View view){
        String selectedItem = customerDropDownMenu.getSelectedItem().toString();
        if(check ==1){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(SERVER_IP_ADDRESS)
                    .appendPath("techGet")
                    .appendPath("viewByDistance.jsp")
                    .appendQueryParameter("pincode", pincode)
                    .appendQueryParameter("latitude", latitude)
                    .appendQueryParameter("longitude", longitude)
                    .appendQueryParameter("skill", selectedItem);
            String myUrl = builder.build().toString();
            myUrl = myUrl.replace("%3A",":");
            GetData getData = new GetData();
            getData.execute(myUrl);
            Log.d("URL://", myUrl);
            Log.d("URl://",myUrl);
        }
        if (check ==2){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(SERVER_IP_ADDRESS)
                    .appendPath("techGet")
                    .appendPath("viewByRate.jsp")
                    .appendQueryParameter("pincode", pincode)
                    .appendQueryParameter("latitude", latitude)
                    .appendQueryParameter("longitude", longitude)
                    .appendQueryParameter("skill", selectedItem);
            String myUrl = builder.build().toString();
            myUrl = myUrl.replace("%3A",":");
            GetData getData = new GetData();
            getData.execute(myUrl);
            Log.d("URl://", myUrl);
        }
        if (check ==3){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(SERVER_IP_ADDRESS)
                    .appendPath("techGet")
                    .appendPath("viewByWeight.jsp")
                    .appendQueryParameter("pincode", pincode)
                    .appendQueryParameter("latitude", latitude)
                    .appendQueryParameter("longitude", longitude)
                    .appendQueryParameter("skill", selectedItem);
            String myUrl = builder.build().toString();
            myUrl = myUrl.replace("%3A",":");
            GetData getData = new GetData();
            getData.execute(myUrl);
            Log.d("URl://", myUrl);
        }
    }
    public class GetData extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {
                String str;
                HttpClient myClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse myResponse = myClient.execute(get);
                BufferedReader br = new BufferedReader(new InputStreamReader(myResponse.getEntity().getContent()));
                while ((str = br.readLine()) != null) {
                    Log.d("Technician Booking: ", str);
                    serverResponse = str;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            prepared = true;
            return prepared;
        }
        @Override
        protected void onPostExecute(final Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            setContentView(R.layout.customer_display_data);
            Object obj = JSONValue.parse(serverResponse);
            JSONObject jsonObject = (JSONObject)obj;
            Long max = (Long)jsonObject.get("MAX");
            int max1 = ((int)(long)max);
            //int max1=5;
            Log.d("Line Executed", "ok");
            for(int i=0;i<max1;i++){
                Log.d("Value i",Integer.toString(i));
                String name = (String)jsonObject.get("NAME"+Integer.toString(i));
                //name = "Ashraf Iqubal";
                Log.d("Name: ",name);
                String price = (String)jsonObject.get("PRICE"+Integer.toString(i));
                //price = "10";
                Log.d("Price: ",price);
                String phone = (String)jsonObject.get("PHONE"+Integer.toString(i));
                //phone = "9876543211";
                Log.d("Phone: ",phone);
                String address = (String)jsonObject.get("ADDRESS"+Integer.toString(i));
                //address = "Mahendru, Patna Bihar";
                Log.d("Address: ",address);
                String distance = (String)jsonObject.get("DISTANCE"+Integer.toString(i));
                //distance = "130";
                Log.d("Distance: ",distance);
                String status = (String)jsonObject.get("STATUS"+Integer.toString(i));
                String situation = null;
                String rateValue = (String)jsonObject.get("RATE"+Integer.toString(i));
                if(status.equals("0")){
                    situation = "Busy";
                }
                if(status.equals("1")){
                    situation = "Available";
                }
                if (status.equals("2")){
                    situation = "Not Available";
                }
                String string = "Name: "+name+"\n"+address+"\n"+"Mobile: "+phone+"    Min-Charge: "+price+"\nDistance: "+distance+"\nStatus: "+situation+"  Rating: "+rateValue;
                techDetailsList.add(string);
                Log.d("Array:", techDetailsList.toString());
            }
            lv = (ListView) findViewById(R.id.label);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,techDetailsList);
            lv.setAdapter(arrayAdapter);
            Log.d("PostExecution: ", "");
            Log.d("Tech Booking:// ", "onPostExecution ");
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int i = position;
                    Object obj = JSONValue.parse(serverResponse);
                    JSONObject jsonObject = (JSONObject)obj;
                    Log.d("Value i",Integer.toString(i));
                    String name = (String)jsonObject.get("NAME"+Integer.toString(i));
                    //name = "Ashraf Iqubal";
                    Log.d("Name: ",name);
                    String price = (String)jsonObject.get("PRICE"+Integer.toString(i));
                    //price = "10";
                    Log.d("Price: ",price);
                    String phone = (String)jsonObject.get("PHONE"+Integer.toString(i));
                    //phone = "9876543211";
                    Log.d("Phone: ",phone);
                    String address = (String)jsonObject.get("ADDRESS"+Integer.toString(i));
                    //address = "Mahendru, Patna Bihar";
                    Log.d("Address: ",address);
                    String distance = (String)jsonObject.get("DISTANCE"+Integer.toString(i));
                    //distance = "130";
                    Log.d("Distance: ",distance);
                    //String string = "Name: "+name+"\n"+address+"\n"+"Mobile: "+phone+"    Min-Charge: "+price+"\nDistance: "+distance;
                    String latitudeTec = (String)jsonObject.get("LATITUDE"+Integer.toString(i));
                    String longitudeTec = (String)jsonObject.get("LONGITUDE"+Integer.toString(i));
                    String email = (String)jsonObject.get("EMAIL"+Integer.toString(i));
                    String status = (String)jsonObject.get("STATUS"+Integer.toString(i));
                    Intent resultSet = new Intent(MainActivity.this,ResultSet.class);
                    resultSet.putExtra(SplashScreen.CUSTOMERNAME,name);
                    resultSet.putExtra("PRICE_TECH",price);
                    resultSet.putExtra("DISTANCE_TECH",distance);
                    resultSet.putExtra(SplashScreen.ADDRESS,address);
                    resultSet.putExtra(SplashScreen.MOBILENO,phone);
                    resultSet.putExtra(SplashScreen.LATITUDE,latitudeTec);
                    resultSet.putExtra(SplashScreen.LONGITUDE,longitudeTec);
                    resultSet.putExtra("EMAIL_TECH",email);
                    resultSet.putExtra("STATUS_TECH",status);
                    startActivity(resultSet);
                    finish();
                }
            });
        }
    }
    public void rateTechnician(View view){
        RatingBar rattingTech = (RatingBar)findViewById(R.id.ratingTech);
        EditText emailTech = (EditText)findViewById(R.id.emailTechnicianfinal);
        Float rated = rattingTech.getRating();
        String emailTechString = emailTech.getText().toString();
        if(emailTechString.equals("")){
            Toast.makeText(getApplicationContext(),"Please Enter ID",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Float.floatToIntBits(rated)==0){
            Toast.makeText(getApplicationContext(),"Please Enter Rating",Toast.LENGTH_SHORT).show();
            return;
        }
        //int rate = (int)rated;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(MainActivity.SERVER_IP_ADDRESS)
                .appendPath("techGet")
                .appendPath("rate.jsp")
                .appendQueryParameter("rate", Integer.toString(Float.floatToIntBits(rated)))
                .appendQueryParameter("email", emailTechString);
        String myUrl = builder.build().toString();
        myUrl = myUrl.replace("%3A",":");
        RateTech rateTech = new RateTech();
        rateTech.execute(myUrl);
        Log.d("URl://", myUrl);
    }
    public class RateTech extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {
                String str;
                HttpClient myClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse myResponse = myClient.execute(get);
                BufferedReader br = new BufferedReader(new InputStreamReader(myResponse.getEntity().getContent()));
                while ((str = br.readLine()) != null) {
                    //serverResponse1 = str;
                    Log.d("CustomerRegistration: ", str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            prepared = true;
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            RatingBar rattingTech = (RatingBar)findViewById(R.id.ratingTech);
            EditText emailTech = (EditText)findViewById(R.id.emailTechnicianfinal);
            rattingTech.setProgress(0);
            emailTech.setText("");
            Log.d("CustomerRegistartn:// ", "onPostExecution ");
            Toast.makeText(getApplicationContext(),"Rated Successfully",Toast.LENGTH_SHORT).show();
        }
    }
    public void distance(View view){
        check = 1;
    }
    public void mean(View view){
        check = 3;
    }
    public void rating(View view){
        check = 2;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
