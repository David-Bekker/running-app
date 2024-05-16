/**

 This class provides helper methods for interacting with Firebase Firestore database.
 */
package helpers;
import android.location.Location;

import androidx.annotation.NonNull;

import classes.Data;
import classes.Guest;
import classes.Session;
import classes.User;
import kotlin.jvm.internal.TypeReference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firestore.collection("users");

    /**
     * Adds a User to the Firestore database.
     * @param user the User object to add
     */
    public void addUser(User user){
        String sesJson = new Gson().toJson(user.getSes());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", user.getUsername());
        userMap.put("lvl", user.getLvl());
        userMap.put("password", user.getPassword());
        userMap.put("phone", user.getPhone());
        userMap.put("step_size", user.getStep_size());
        userMap.put("weight", user.getWeight());
        userMap.put("xp", user.getXp());
        userMap.put("ses", sesJson);

        collectionReference.document(user.getUsername()).set(userMap);
    }

    /**
     * Adds a Guest to the Firestore database.
     * @param guest the Guest object to add
     */
    public void addGuest(Guest guest){
        String sesJson = new Gson().toJson(guest.getSes());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", guest.getUsername());
        userMap.put("lvl", guest.getLvl());
        userMap.put("step_size", guest.getStep_size());
        userMap.put("weight", guest.getWeight());
        userMap.put("xp", guest.getXp());
        userMap.put("ses", sesJson);

        collectionReference.document(guest.getUsername()).set(userMap);
    }


    /**
     * Checks if a user exists in the Firestore database.
     * @param user the username to check
     * @param callback the callback function to handle the result
     */
    public void userExists(String user, final Callback callback) {
        if (user == null) {
            callback.onError();
            return;
        }
        // Create a reference to the document you want to check
        DocumentReference docRef = firestore.collection("users").document(user);

        // Check if the document exists
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists
                        callback.onSuccess(true);
                    } else {
                        // Document does not exist
                        callback.onSuccess(false);
                    }
                } else {
                    // Failed to get the document
                    callback.onError();
                }
            }
        });
    }

    /**
     * A callback interface to handle the result of userExists method.
     */
    public interface Callback {
        void onSuccess(boolean exists);
        void onError();
    }

    /**
     * Retrieves a user from the database by their username.
     * If the username is null, calls the onError() method of the callback.
     * If the user does not exist in the database, calls the onError() method of the callback.
     * If the user exists in the database, creates a Data object representing the user and calls the onSuccess() method of the callback with that object.
     * @param user The username of the user to retrieve.
     * @param callback The callback to invoke with the results.
     */
    public void getUser(String user, ReturnCall<Data> callback) {
        if (user == null) {
            callback.onError();
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("users");
        DocumentReference docRef = collectionRef.document(user);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Data user;
                    Gson gson = new Gson();
                    Type dataType = new TypeToken<List<Session>>(){}.getType();
                    if (documentSnapshot.contains("password")) {
                        ArrayList<Session> sessions = gson.fromJson(documentSnapshot.getString("ses"), dataType);
                        user = new User(documentSnapshot.getDouble("step_size"),
                                documentSnapshot.getDouble("weight"),
                                documentSnapshot.getString("username"),
                                sessions,
                                documentSnapshot.get("lvl",int.class),
                                documentSnapshot.get("xp",int.class),
                                documentSnapshot.getString("password"),
                                documentSnapshot.getString("phone"));
                    } else {
                        ArrayList<Session> sessions = gson.fromJson(documentSnapshot.getString("ses"), dataType);
                        user = new Guest(documentSnapshot.getDouble("step_size"),
                                documentSnapshot.getDouble("weight"),
                                documentSnapshot.getString("username"),
                                sessions,
                                documentSnapshot.get("lvl",int.class),
                                documentSnapshot.get("xp",int.class));
                    }
                    callback.onSuccess(user);
                } else {
                    // the document does not exist
                    callback.onError();
                }
            }
        });
    }


    public interface ReturnCall<T> {
        void onSuccess(T result);
        void onError();
    }

    /**
     * Retrieves all users from the database.
     * If the retrieval is successful, creates a list of Data objects representing the users and calls the onSuccess() method of the callback with that list.
     * If the retrieval is not successful, calls the onError() method of the callback.
     * @param callback The callback to invoke with the results.
     */
    public void getAllUsers(final ListCall<Data> callback) {
        CollectionReference collectionRef = firestore.collection("users");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Data> users = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Data user;
                        Gson gson = new Gson();
                        Type dataType = new TypeToken<List<Session>>(){}.getType();
                        if (document.contains("password")) {
                            ArrayList<Session> sessions = gson.fromJson(document.getString("ses"), dataType);
                            user = new User(document.getDouble("step_size"),
                                    document.getDouble("weight"),
                                    document.getString("username"),
                                    sessions,
                                    document.get("lvl",int.class),
                                    document.get("xp",int.class),
                                    document.getString("password"),
                                    document.getString("phone"));
                        } else {
                            ArrayList<Session> sessions = gson.fromJson(document.getString("ses"), dataType);
                            user = new Guest(document.getDouble("step_size"),
                                    document.getDouble("weight"),
                                    document.getString("username"),
                                    sessions,
                                    document.get("lvl",int.class),
                                    document.get("xp",int.class));
                        }
                        users.add(user);
                    }
                    callback.onSuccess(users);
                } else {
                    // Failed to get users
                    callback.onError();
                }
            }
        });
    }


    public interface ListCall<T> {
        void onSuccess(List<T> result);
        void onError();
    }

}