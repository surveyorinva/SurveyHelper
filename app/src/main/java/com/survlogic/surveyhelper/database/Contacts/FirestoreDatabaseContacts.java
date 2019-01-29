package com.survlogic.surveyhelper.database.Contacts;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.activity.staffCompany.model.Contact;
import com.survlogic.surveyhelper.database.Users.FirestoreDatabaseUserListener;

import java.util.ArrayList;

public class FirestoreDatabaseContacts {

    public interface fetchList{
        void fetchContactListSuccess(ArrayList<Contact> contacts);
        void fetchContactListFail(String error);
    }

    public interface addContact{
        void addContactSuccess();
        void addContactFail(String error);
    }

    private Context mContext;
    private Activity mActivity;

    private fetchList mListenerFetchList;
    private addContact mListenerAddContact;

    private DocumentSnapshot mLastQueried;

    public FirestoreDatabaseContacts(Context context) {
        this.mContext = context;
        this.mActivity = (Activity) context;

    }

    public void setInterfaceFetchList(fetchList activityListener){
        this.mListenerFetchList = activityListener;
    }

    public void setInterfaceAddContact(addContact activityListener) {
        this.mListenerAddContact = activityListener;
    }

    public void fetchContactListAll(){
        final ArrayList<Contact> mListContacts = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("contacts");

        Query query = null;

        if(mLastQueried !=null){
            query = ref
                    .startAfter(mLastQueried)
                    .orderBy("first_name",Query.Direction.ASCENDING);
        }else{
            query = ref
                    .orderBy("first_name",Query.Direction.ASCENDING);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document:task.getResult()){
                        Contact contact = document.toObject(Contact.class);
                        mListContacts.add(contact);
                    }
                    mListenerFetchList.fetchContactListSuccess(mListContacts);
                }else{
                    mListenerFetchList.fetchContactListFail("On Complete Error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               mListenerFetchList.fetchContactListFail(e.getMessage());
            }
        });

    }

    public void addContactToServer(Contact contact) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newRef = db
                .collection("contacts")
                .document();

        contact.setContact_id(newRef.getId());

        newRef.set(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mListenerAddContact.addContactSuccess();
                } else {
                    mListenerAddContact.addContactFail("On Complete Error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerAddContact.addContactFail(e.getMessage());
            }
        });
    }


}
