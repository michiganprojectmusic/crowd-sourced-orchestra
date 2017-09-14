import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;

import java.io.FileInputStream;

/**
 * Created by kiran on 9/14/2017.
 */
public class main {
    public static void main(String[] args) throws Exception  {
        FileInputStream serviceAccount =
                new FileInputStream("src/private-key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://crowd-sourced-orchestra.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("restricted_access/secret_document");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                System.out.println(document);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
