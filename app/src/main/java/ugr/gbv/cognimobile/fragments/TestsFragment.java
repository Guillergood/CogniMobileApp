package ugr.gbv.cognimobile.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.adapters.TestsAdapter;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
import ugr.gbv.cognimobile.interfaces.TestsAdapterClickHandler;

public class TestsFragment extends Fragment implements TestsAdapterClickHandler {

    private RecyclerView recyclerView;
    private ImageView noTestView;
    private TestsAdapter testsAdapter;
    private TextView noTestText;
    private Context context;
    private TestClickHandler callBack;

    public TestsFragment(TestClickHandler callBack) {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_tests_fragment,container, false);
        recyclerView = view.findViewById(R.id.tests_recyclerview);
        noTestView = view.findViewById(R.id.noTestImage);
        noTestText = view.findViewById(R.id.noTestLabel);

        context = getContext();

        if(emptyTests()) {
            showNoTests();
        }
        else{
            fetchTests();
        }

        return view;
    }

    private void showNoTests() {
        recyclerView.setVisibility(View.INVISIBLE);
        noTestView.setVisibility(View.VISIBLE);
        noTestText.setVisibility(View.VISIBLE);
    }

    private void showTests() {
        recyclerView.setVisibility(View.VISIBLE);
        noTestView.setVisibility(View.INVISIBLE);
        noTestText.setVisibility(View.INVISIBLE);
    }


    private void fetchTests() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String[] projection = new String[]{Provider.Cognimobile_Data._ID, Provider.Cognimobile_Data.NAME, Provider.Cognimobile_Data.DATA};
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS,projection,null,null,Provider.Cognimobile_Data._ID);
        if(cursor != null)  {
            cursor.moveToFirst();
        }
        testsAdapter = new TestsAdapter(cursor, this, callBack);
        recyclerView.setAdapter(testsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);

        recyclerView.addItemDecoration(dividerItemDecoration);

        showTests();
    }


    private boolean emptyTests(){
        String[] projection = new String[]{Provider.Cognimobile_Data._ID};
        Cursor tempCursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS,projection,null,null,Provider.Cognimobile_Data._ID);
        int count = 0;
        if(tempCursor != null){
            count = tempCursor.getCount();
            tempCursor.close();
        }
        return count == 0;
    }

    private void setTestsOnAdapter(){
        String[] projection = new String[]{Provider.Cognimobile_Data._ID,Provider.Cognimobile_Data.NAME};
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS,projection,null,null,Provider.Cognimobile_Data._ID);
        if(cursor != null) {
            cursor.moveToFirst();
            testsAdapter.updateCursor(cursor);
        }
        else{
            throw new RuntimeException("PETO TESTSFRAGMENT.java:106");
        }
    }

    @Override
    public void quitTest(String name) {

        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {name};
        context.getContentResolver().delete(Provider.CONTENT_URI_TESTS,where,selectionArgs);

        if(emptyTests()){
            showNoTests();
        }
        else{
            setTestsOnAdapter();
            showTests();
        }



    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////




}
