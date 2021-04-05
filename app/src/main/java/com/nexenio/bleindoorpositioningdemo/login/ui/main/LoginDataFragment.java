package com.nexenio.bleindoorpositioningdemo.login.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.nexenio.bleindoorpositioningdemo.HomeActivity;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.RegisterUser;
import com.nexenio.bleindoorpositioningdemo.UrlLinks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginDataFragment extends Fragment {
    public static String usernameglobal="";
    public static String userreview="";
    private MainViewModel mViewModel;

    public static LoginDataFragment newInstance() {
        return new LoginDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_login, container, false);
        Context context = root.getContext();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
       Boolean Registered = sharedPref.getBoolean("Registered", false);

        if (!Registered) {

            Intent i = new Intent(getActivity(), RegisterUser.class);
            startActivity(i);
            // If the user is registered already.
        } else{


        }


        final EditText usernames = root.findViewById(R.id.tv_user_name);
        final EditText passwords = root.findViewById(R.id.tv_password);
        final Button b1=root.findViewById(R.id.btn_login);

        final Button register=root.findViewById(R.id.btn_forgot_register);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usernames.getText().toString();
                String password=passwords.getText().toString();

                usernames.setText("");
                passwords.setText("");
                if(username.equals("")||password.equals("")){
                    Toast.makeText(getActivity(), "Please neter username or password", Toast.LENGTH_SHORT).show();

                }else {


                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String url = UrlLinks.checklogin;

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                    nameValuePairs.add(new BasicNameValuePair("username", username));
                    nameValuePairs.add(new BasicNameValuePair("password", password));


//                    JSONObject result = null;
//                    try {
//                        result = jSOnClassforData.forCallingServer(url, nameValuePairs);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

//        JSONArray jArray = new JSONArray(result.toString());
//
//        for(int i=0;i<jArray.length();i++) {
//            String alldata = jArray.get(i).toString();
//
//            String datasplit[] = alldata.split("_");
//            latilongidata.add(alldata);
//
//
//
//
//        }


//                JSONArray jArray = null;
//                try {
//                    jArray = result.getJSONArray("jsonarrayval");
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                System.out.println("*****JARRAY*****" + jArray.length());
//
//                for (int i = 0; i < jArray.length(); i++) {


//                    JSONArray jArray = null;
////                try {
//                    try {
//                        jArray = result.getJSONArray("jsonarrayval");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                System.out.println("*****JARRAY*****" + jArray.length());
//
//                for (int i = 0; i < jArray.length(); i++) {


                    JSONObject json_data;
//
//                    try {
//                        json_data = jArray.getJSONObject(0);
                      //  String bookName = "1";//json_data.getString("bookName");
//                        String author = json_data.getString("author");
//                        String publisher = json_data.getString("publisher");
                  //  final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                  //  SharedPreferences sp = sharedPref(SHARED_PREF_NAME, MODE_PRIVATE);
                    String name = sharedPref.getString("Username", null);
                    String pass = sharedPref.getString("Password", null);
                    Boolean bookName=false;
if(username.equals(name)&&pass.equals(password)){
    bookName=true;
}


                        if (bookName) {
                            usernameglobal=username;
                            Intent io = new Intent(getActivity(), HomeActivity.class);

                            startActivity(io);
                            // NavController navController= Navigation.findNavController(view);
                            // navController.navigate(R.id.nav_slideshow);


                        } else {

                            Toast.makeText(getActivity(), "Wrong username or password", Toast.LENGTH_SHORT).show();

                        }


                        //  SplittingBooktime=bookName.split(",");

//							 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//					        		 R.layout.textview, SplittingBooktime);


                        //  Toast.makeText(SelectingLcoation.this,"Doctor Available at "+ bookName, Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }


                }


            }
        });
//        forgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent io = new Intent(getActivity(), ForgotPasswordEmailreset.class);
//
//                startActivity(io);
//            }
//        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), RegisterUser.class);
                startActivity(i);

                String usernamev=usernames.getText().toString();
                String passwordv=passwords.getText().toString();
                Toast.makeText(getContext(), usernamev+""+passwordv, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}