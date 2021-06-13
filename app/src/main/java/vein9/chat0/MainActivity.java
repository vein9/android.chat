package vein9.chat0;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import vein9.chat0.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private EditText edtMessage;
    private Button btnSend;
    private ArrayList<String> messages;
    private ArrayAdapter<String> messagesAdapter;
    private ListView lvMessages;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Like Start() in Unity?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        messages = new ArrayList<>();
        messagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        lvMessages.setAdapter(messagesAdapter);

        setUpListViewMessagesListener();


        // Get all firestore messages

        db.collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        messagesAdapter.clear();

                        for (QueryDocumentSnapshot document : value) {
                            Log.d("chat", document.getId() + " => " + document.getData());
                            messagesAdapter.add ( document.getString( "text"));
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }
                });


    }

    public void handleSendMessage (View v){
        Log.d("chat", "handleSendMessage (View v)");
        addMessage(v);
    }

    private void addMessage(View v){
        String inputMessageText = edtMessage.getText().toString();
        edtMessage.setText("");
        if(!inputMessageText.equals("")){
            // Send data to firebase
            Map<String, Object> message = new HashMap<>();
            message.put("text", inputMessageText);
            db.collection("messages")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("chat", "DocumentSnapshot added with ID: " + documentReference.getId());
                            // Sau khi da gui tin nhan xong
//                            messagesAdapter.add(inputMessageText);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("chat", "Error adding document", e);
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Message can not be empty!", Toast.LENGTH_LONG).show();
        }
    }

    public void setUpListViewMessagesListener(){
        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Message removed", Toast.LENGTH_SHORT).show();
                messages.remove(position); // neu sai ty test thanh id
                messagesAdapter.notifyDataSetChanged();
            }
        });
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