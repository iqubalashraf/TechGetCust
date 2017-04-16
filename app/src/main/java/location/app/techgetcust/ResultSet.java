package location.app.techgetcust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by ashrafiqubal on 17/05/16.
 */
public class ResultSet extends AppCompatActivity {
    String address,mobileNo,pincode;
    double latitude=25.61823450;
    double longitude=85.17223456;
    TextView techDetails;
    String serverResponse;
    String latitudeTech,longitudeTech,email,cEmail,status;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_set);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        String customerName = intent.getStringExtra(SplashScreen.CUSTOMERNAME);
        String price = intent.getStringExtra("PRICE_TECH");
        String distance = intent.getStringExtra("DISTANCE_TECH");
        address = intent.getStringExtra(SplashScreen.ADDRESS);
        mobileNo = intent.getStringExtra(SplashScreen.MOBILENO);
        cEmail = sharedpreferences.getString(SplashScreen.EMAIL,null);
        status = intent.getStringExtra("STATUS_TECH");
        //pincode = intent.getStringExtra(SplashScreen.PINCODE);
        latitudeTech = intent.getStringExtra(SplashScreen.LATITUDE);
        longitudeTech = intent.getStringExtra(SplashScreen.LONGITUDE);
        email = intent.getStringExtra("EMAIL_TECH");
        techDetails = (TextView)findViewById(R.id.techDetails);
        String string = "Name: "+customerName+"\n"+address+"\n"+"Mobile: "+mobileNo+"    Min-Charge: "+price+"\nDistance: "+distance;
        techDetails.setText(string);
    }
    public void bookTechnician(View view){
        if(status.equals("0")){
            Toast.makeText(getApplicationContext(),"Technician is Busy now. Try after some time",Toast.LENGTH_SHORT).show();
        }else if(status.equals("2")){
            Toast.makeText(getApplicationContext(),"Technician is Not-Available now. Try after some time",Toast.LENGTH_SHORT).show();
        }
        if(status.equals("1")){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(MainActivity.SERVER_IP_ADDRESS)
                    .appendPath("techGet")
                    .appendPath("technicianBooking.jsp")
                    .appendQueryParameter("temail", email)
                    .appendQueryParameter("cemail",cEmail);
            String myUrl = builder.build().toString();
            myUrl = myUrl.replace("%3A",":");
            BookTechnician bookTechnician = new BookTechnician();
            bookTechnician.execute(myUrl);
            Log.d("URl://", myUrl);
        }
    }
    public class BookTechnician extends AsyncTask<String, Void, Boolean> {
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
                    serverResponse = str;
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
            serverResponse = serverResponse.replace(" ","");
            int check = Integer.parseInt(serverResponse);
            if (check == 1) {
                Toast.makeText(getApplicationContext(),"Technician Book Successfully",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_LONG).show();
            }
            //Log.d("CustomerRegistartn:// ", "onPostExecution ");
            //setContentView(R.layout.layout_registration_successful);
        }
    }
    public void showOnMap(View view){
        try{
            Log.d("ShowOnMap", "Line Executed");
            //Uri.parse("http://maps.google.com/maps?daddr="+Double.parseDouble(latitudeTech)+","+Double.parseDouble(longitudeTech));
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", latitude, longitude, Double.parseDouble(latitudeTech), Double.parseDouble(longitudeTech));
            Log.d("ShowOnMap", "Line Executed "+ uri);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }catch (Exception e){
            Log.d("ShowONMap://",e.getMessage());
        }

    }
    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=23");
        return urlString.toString();
    }
}
