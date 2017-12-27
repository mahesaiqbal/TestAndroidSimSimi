package com.mahesaiqbal.testandroidsimsimi;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.mahesaiqbal.testandroidsimsimi.adapter.CustomAdapter;
import com.mahesaiqbal.testandroidsimsimi.helper.HttpDataHandler;
import com.mahesaiqbal.testandroidsimsimi.model.ChatModel;
import com.mahesaiqbal.testandroidsimsimi.model.SimSimiModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editText;
    List<ChatModel> list_chat = new ArrayList<>();
    FloatingActionButton btn_send_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list_of_message);
        editText = (EditText)findViewById(R.id.user_message);
        btn_send_message = (FloatingActionButton)findViewById(R.id.fab_send);

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                ChatModel model = new ChatModel(text,true); // user send message
                list_chat.add(model);
                new SimSimiAPI().execute(list_chat);

                //remove user message
                editText.setText("");
            }
        });
    }

    private class SimSimiAPI extends AsyncTask<List<ChatModel>,Void,String> {
        String stream = null;
        List<ChatModel> models;
        String text = editText.getText().toString();

        @Override
        protected String doInBackground(List<ChatModel>... params) {
            String url =
                    String.format("http://sandbox.api.simsimi.com/request.p?key=%s&lc=id&ft=1.0&text=%s",
                            getString(R.string.simsimi_api),text);
            models = params[0];
            HttpDataHandler httpDataHandler = new HttpDataHandler();
            stream = httpDataHandler.GetHTTPData(url);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            SimSimiModel response = gson.fromJson(s,SimSimiModel.class);

            ChatModel chatModel = new ChatModel(response.getResponse(),false); // get response from simsimi
            models.add(chatModel);
            CustomAdapter adapter = new CustomAdapter(models,getApplicationContext());
            listView.setAdapter(adapter);
        }
    }
}
