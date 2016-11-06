package joao.splitride.app.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joao.splitride.R;
import joao.splitride.app.main.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    //private LoginButton fb_button;
    private Button fb_button;
    private ParseUser parseUser;
    private String name, email;
    private Profile profile;
    private Button login, register, cancel;
    private TextInputLayout usernameWrapper, emailWrapper, passwordWrapper;
    private LinearLayout parentLayout;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Facebook Stuff
        fb_button = (Button) findViewById(R.id.btn_fb_login);
        fb_button.setOnClickListener(this);

        profile = Profile.getCurrentProfile();

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        login = (Button) findViewById(R.id.button_login);
        register = (Button) findViewById(R.id.register_button);
        cancel = (Button) findViewById(R.id.register_cancel);


        login.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        String username, password;

        switch (v.getId()) {

            case R.id.button_login:
                hideKeyboard();
                username = usernameWrapper.getEditText().getText().toString();
                password = passwordWrapper.getEditText().getText().toString();
                doLogin(username, password);

                break;

            case R.id.register_button:
                if (register.getText().toString().equals("Registar")) {
                    emailWrapper.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

                    register.setText("OK");
                } else {
                    username = usernameWrapper.getEditText().getText().toString();
                    password = passwordWrapper.getEditText().getText().toString();
                    email = emailWrapper.getEditText().getText().toString();

                    doRegister(username, password, email);

                }

                break;

            case R.id.register_cancel:
                login.setVisibility(View.VISIBLE);
                register.setText("Registar");
                emailWrapper.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                break;

            case R.id.btn_fb_login:

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {

                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailsFromFB();

                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailsFromParse();
                        }
                    }
                });
                break;
        }
    }

    // Facebook Stuff
    private void saveNewUser() {
        parseUser = new ParseUser();
        parseUser.put("username", name);
        parseUser.put("email", email);
        parseUser.put("password", getSaltString());

//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = null;
        //((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] data = stream.toByteArray();
            String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    parseUser.put("profileThumb", parseFile);

                    //Finally save all the user details
                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                // Show the error message
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                                        .show(); // Don’t forget to show!
                            } else {
                                Toast.makeText(LoginActivity.this, "New user:" + parseUser.getUsername() + " Signed up", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }

        /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", parseUser.getObjectId());
        editor.commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("image", bitmap);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent); */


    }

    private void getUserDetailsFromFB() {

        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        try {

                            Log.d("Response", response.getRawResponse());

                            email = response.getJSONObject().getString("email");
                            //mEmailID.setText(email);

                            name = response.getJSONObject().getString("name");
                            //mUsername.setText(name);

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");

                            Log.d("Profile pic", "url: " + pictureUrl);

                            saveNewUser();

                            //new ProfilePhotoAsync(pictureUrl).execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

        Bitmap bitmap = null;
        //Fetch profile photo
        // try {
        //     ParseFile parseFile = parseUser.getParseFile("profileThumb");
        //     byte[] data = parseFile.getData();
        //     bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        //mProfileImage.setImageBitmap(bitmap);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        Toast.makeText(LoginActivity.this, "Welcome back " + parseUser.getUsername(), Toast.LENGTH_SHORT).show();

        /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", parseUser.getObjectId());
        editor.commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("image", bitmap);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    // NORMAL LOGIN & REGISTER

    private void doLogin(String username, String password) {

        if (username.isEmpty()) {
            usernameWrapper.setError("Introduza um username.");
        } else if (password.isEmpty()) {
            passwordWrapper.setError("Introduza uma password.");
        } else if (!validatePassword(password)) {
            passwordWrapper.setError("Not a valid password!");
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

            //Set progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Por favor espere");
            progressDialog.setMessage("A realizar o login.");
            progressDialog.show();

            ParseUser user = new ParseUser();

            user.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_LONG)
                                .show(); // Don’t forget to show!
                    } else {
                        // Start an intent for the dispatch activity
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userID", user.getObjectId());
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    private void doRegister(String username, String password, String email) {

        // Validating the form

        if (username.isEmpty()) {
            usernameWrapper.setError("Introduza um username.");
        } else if (password.isEmpty()) {
            passwordWrapper.setError("Introduza uma password.");
        } else if (email.isEmpty()) {
            emailWrapper.setError("Introduza um email.");
        } else if (!validateEmail(email)) {
            emailWrapper.setError("Not a valid email address!");
        } else if (!validatePassword(password)) {
            passwordWrapper.setError("Not a valid password!");
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);
        }

        //Set progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A realizar o registo.");
        progressDialog.show();


        final ParseUser user = new ParseUser();
        user.put("username", username);
        user.put("password", password);
        user.put("email", email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_LONG)
                            .show(); // Don’t forget to show!
                } else {
                    // Start an intent for the dispatch activity
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userID", user.getObjectId());
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();

        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }


}
