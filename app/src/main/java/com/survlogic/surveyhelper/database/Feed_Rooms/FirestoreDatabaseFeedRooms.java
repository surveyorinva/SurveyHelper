package com.survlogic.surveyhelper.database.Feed_Rooms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedRooms;

import java.util.ArrayList;
import java.util.List;

public class FirestoreDatabaseFeedRooms {

    private static final String TAG = "FirestoreDatabaseFeedRo";

    public interface FeedRoomListener{
        void fetchPublicHall(FeedRooms publicHall);
        void fetchPrivateRoomsMember(ArrayList<FeedRooms> privateRoomList);
        void fetchFeedRoomGetError(boolean isError);
    }

    private Context mContext;
    private FeedRoomListener mListenerRoom;

    private DocumentSnapshot mLastQueriedRoomList;

    public FirestoreDatabaseFeedRooms(Context context, FeedRoomListener listener) {
        this.mContext = context;
        this.mListenerRoom = listener;

    }

    public void getRoomListFromFirestore(){
        final ArrayList<FeedRooms> mListRoomsFromFirestore = new ArrayList<>();
        final ArrayList<FeedRooms> mListPrivateRooms = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_rooms");

        Query query = null;

        if(mLastQueriedRoomList !=null){
            query = ref
                    .startAfter(mLastQueriedRoomList)
                    .whereEqualTo("room_available",true);
        }else{
            query = ref
                    .whereEqualTo("room_available",true);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedRooms room = document.toObject(FeedRooms.class);
                        mListRoomsFromFirestore.add(room);
                    }

                    if(task.getResult().size() !=0){

                        boolean isPublicHallFound = false;

                        for(int j = 0; j <mListRoomsFromFirestore.size(); j++){
                            FeedRooms room = mListRoomsFromFirestore.get(j);

                            if(room.isRoom_public_hall() && !isPublicHallFound){
                                isPublicHallFound = true;
                                mListenerRoom.fetchPublicHall(room);
                            }

                        }
                    }
                    mListenerRoom.fetchPrivateRoomsMember(mListPrivateRooms);

                }else{
                    mListenerRoom.fetchFeedRoomGetError(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerRoom.fetchFeedRoomGetError(true);
            }
        });

    }




    public void getRoomListFromFirestore(final List<String> listUserRooms){
        final ArrayList<FeedRooms> mListRoomsFromFirestore = new ArrayList<>();
        final ArrayList<FeedRooms> mListPrivateRooms = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("feed_rooms");

        Query query = null;

        if(mLastQueriedRoomList !=null){
            query = ref
                    .startAfter(mLastQueriedRoomList)
                    .whereEqualTo("room_available",true);
        }else{
            query = ref
                    .whereEqualTo("room_available",true);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document:task.getResult()){
                        FeedRooms room = document.toObject(FeedRooms.class);
                        mListRoomsFromFirestore.add(room);
                    }

                    if(task.getResult().size() !=0){

                        boolean isPublicHallFound = false;

                        for(int i = 0; i  < listUserRooms.size(); i++){
                            String roomUser = listUserRooms.get(i);

                            for(int j = 0; j <mListRoomsFromFirestore.size(); j++){
                                FeedRooms room = mListRoomsFromFirestore.get(j);
                                String roomServer = room.getRoom_id();

                                if(room.isRoom_public_hall() && !isPublicHallFound){
                                    isPublicHallFound = true;
                                    mListenerRoom.fetchPublicHall(room);
                                }

                                if(roomUser.equals(roomServer)){
                                    mListPrivateRooms.add(room);
                                }

                            }
                        }
                    }
                    mListenerRoom.fetchPrivateRoomsMember(mListPrivateRooms);

                }else{
                    mListenerRoom.fetchFeedRoomGetError(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListenerRoom.fetchFeedRoomGetError(true);
            }
        });

    }


}
