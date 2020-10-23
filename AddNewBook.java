package com.example.sofe4640bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddNewBook extends AppCompatActivity {

    Button btnParse = null;
    HttpURLConnection conn= null;
    ImageView imgFile = null;
    TextView jsonView;
    Button btnImage = null;
    Uri imgUri;

    private static final int IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);


        Intent recMsg = getIntent();
        String tempMsg = recMsg.getStringExtra("msg1");

        TextView txtview = (TextView) findViewById(R.id.lblMessage);
        txtview.setText(tempMsg);
        btnParse = (Button) findViewById(R.id.btnParse);
        jsonView = (TextView) findViewById(R.id.lblMessage);
        btnImage = (Button) findViewById(R.id.btnFile);
        imgFile = (ImageView) findViewById(R.id.img);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                chooseImage();
            }
        });


        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //"https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesDemoItem.txt"
                JSONTask myTask = new JSONTask();
                myTask.execute("https://www.googleapis.com/books/v1/volumes?q=isbn");

            }
        });
    }

    public void chooseImage(){
        Intent bookimage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(bookimage, IMAGE);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {

        super.onActivityResult(request, result, data);

        if (request == RESULT_OK && request == IMAGE){
            imgUri = data.getData();
            imgFile.setImageURI(imgUri);
        }
    }


    class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000); //5 sec.
                conn.connect();
                if (conn.getResponseCode()== HttpURLConnection.HTTP_OK){

                    InputStream stream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader( new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line= "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }


                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }finally {
                conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //jsonView.setText(result.toString());


            String jsonString = result.toString();

            TextView Book_ID = (EditText) findViewById(R.id.txtBookID);
            TextView name = (TextView) findViewById(R.id.txtBookName);
            TextView publisher_name = (TextView) findViewById(R.id.pub);
            TextView description_summary = (TextView) findViewById(R.id.txtSummary);
            TextView price_book = (TextView) findViewById(R.id.txtPrice);
            TextView authors_name = (TextView) findViewById(R.id.txtAuthorName);
            EditText q = (EditText) findViewById(R.id.editQuantity);

            try {

                /* ADD NEW BOOK VALUES NEEDED

                ISBN INTEGER NOT NULL, " +
                " title  TEXT NOT NULL, " +
                " publisher  TEXT, " +
                " price  INTEGER, " +
                " quantity INTEGER, " +
                " edition TEXT, " +
                " pages INTEGER, " +
                " user TEXT , " + Not sure what this one means

                 */
                String search_id = Book_ID.getText().toString();

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++){

                    JSONObject firstObj = jsonArray.getJSONObject(i);
                    String isbn = firstObj.getString("id");

                    if (search_id.equals(isbn)){ // Checked Book ID entered to the JSON Book items

                        // Gets the title and sale status
                        String title = firstObj.getJSONObject("volumeInfo").getString("title");
                        String sales = firstObj.getJSONObject("saleInfo").getString("saleability");

                        // Default values
                        String publish = "Not available";
                        String description = "No Summary is available for the Selected book";
                        String price_string = null;
                        String author = null;
                        int pageCount = 0;
                        String version = null;

                        // Gets publisher information is it exists and stores it in publish
                        if(firstObj.getJSONObject("volumeInfo").has("publisher")){
                            publish = firstObj.getJSONObject("volumeInfo").getString("publisher");
                        }

                        // Gets Price information it it exists
                        if(sales.equals("FOR_SALE")){
                            price_string = firstObj.getJSONObject("saleInfo").getJSONObject("listPrice").getString("amount");
                        } else {
                            price_string = "0";
                            price_book.setText("Not For Sale");
                        }

                        // Gets Description if exists
                        if (firstObj.getJSONObject("volumeInfo").has("description")){
                            description = firstObj.getJSONObject("volumeInfo").getString("description");
                        }

                        // Gets authors name if exists
                        if(firstObj.getJSONObject("volumeInfo").has("authors")){
                            author = firstObj.getJSONObject("volumeInfo").getString("authors");
                        }

                        // Gets page count
                        if(firstObj.getJSONObject("volumeInfo").has("pageCount")){
                            pageCount = firstObj.getJSONObject("volumeInfo").getInt("pageCount");
                        }

                        // Gets Version/Edition
                        if(firstObj.getJSONObject("volumeInfo").has("contentVersion")){
                            version = firstObj.getJSONObject("volumeInfo").getString("contentVersion");
                        }

                        //Sets the text feilds to the JSON values
                        name.setText(title);
                        authors_name.setText(author);
                        publisher_name.setText(publish);
                        description_summary.setText(description);

                        double convert = Double.parseDouble(price_string);
                        int price = (int) convert;

                        String quan = q.getText().toString();
                        double convert_quantity = Double.parseDouble(quan);
                        int quantity = (int) convert_quantity;

                        // These values are the ones needed to go into the database,
                        System.out.println("Quanitity: " + quantity);
                        System.out.println("Price is: " + price);
                        System.out.println("Page count is: " + pageCount);
                        System.out.println("Edition is: " + version);
                        System.out.println("Publisher: " + publish);
                        System.out.println("Title: " + title);
                        String user = "Walid";

                        //jsonView.setText(search_id);

                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}