import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;

@SpringBootApplication
public class main {
    public static void main(String[] args) throws Exception  {
        FileInputStream serviceAccount = new FileInputStream("src/private-key.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://crowd-sourced-orchestra.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("restricted_access/secret_document");

        // Attach a listener to read the data at our posts reference
        while(true) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object document = dataSnapshot.getValue();
                    if (document != null) {
                        System.out.println("DOCUMENT" + document);
                        return;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }


}
