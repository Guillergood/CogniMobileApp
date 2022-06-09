package ugr.gbv.cognimobile.fragments;

import android.content.ContentValues;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.adapters.TestsAdapter;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
/**
 * Fragment to display the Tests section in {@link ugr.gbv.cognimobile.activities.MainActivity}
 */
public class TestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView noTestView;
    private TextView noTestText;
    private Context context;
    private TestClickHandler callBack;

    public TestsFragment() {}

    /**
     * Constructor
     *
     * @param callBack to handle the clicks from {@link ugr.gbv.cognimobile.activities.MainActivity}
     */
    public TestsFragment(TestClickHandler callBack) {
        this.callBack = callBack;
    }

    /**
     * Overrides {@link androidx.fragment.app.Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_tests_fragment, container, false);
        recyclerView = view.findViewById(R.id.tests_recyclerview);
        noTestView = view.findViewById(R.id.noTestImage);
        noTestText = view.findViewById(R.id.noTestLabel);

        context = getContext();

        if (emptyTests()) {
            showNoTests();
        } else {
            fetchTests();
        }

        return view;
    }

    /**
     * Shows that there are no tests available
     */
    private void showNoTests() {
        recyclerView.setVisibility(View.INVISIBLE);
        noTestView.setVisibility(View.VISIBLE);
        noTestText.setVisibility(View.VISIBLE);
    }

    /**
     * Shows all the tests available
     */
    private void showTests() {
        recyclerView.setVisibility(View.VISIBLE);
        noTestView.setVisibility(View.INVISIBLE);
        noTestText.setVisibility(View.INVISIBLE);
    }


    /**
     * Retrieves from the local database the tests available
     */
    private void fetchTests() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Cursor cursor = getValidTests();
        if (cursor != null) {
            cursor.moveToFirst();
        }
        TestsAdapter testsAdapter = new TestsAdapter(cursor, callBack);
        recyclerView.setAdapter(testsAdapter);

        showTests();
    }

    private Cursor getValidTests() {
        String where = Provider.Cognimobile_Data.REDO_TIMESTAMP + " < ?";
        String[] selectionArgs = {Long.toString(System.currentTimeMillis(), 10)};
        String[] projection = new String[]{Provider.Cognimobile_Data._ID, Provider.Cognimobile_Data.NAME};
        return context.getContentResolver()
                .query(Provider.CONTENT_URI_TESTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);
    }

    /**
     * Checks if there are tests to be displayed or not.
     */
    private boolean emptyTests() {
        Cursor tempCursor = getValidTests();
        int count = 0;
        if (tempCursor != null) {
            count = tempCursor.getCount();
            tempCursor.close();
        }
        return count == 0;
    }

    /**
     * Deletes the test with the name given.
     */
    public void updateTestDone(String name) {
        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {name};
        String[] projection = new String[]{
                Provider.Cognimobile_Data._ID,
                Provider.Cognimobile_Data.NAME,
                Provider.Cognimobile_Data.REDO_TIMESTAMP,
                Provider.Cognimobile_Data.DAYS_TO_DO,
                Provider.Cognimobile_Data.DATA
        };
        Cursor testCursor = context.getContentResolver()
                .query(Provider.CONTENT_URI_TESTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);
        if (testCursor != null) {
            testCursor.moveToFirst();
            int days = testCursor.getInt(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.DAYS_TO_DO));
            long timestampToReDo = 86400000L * days;
            ContentValues values = new ContentValues();
            values.put(Provider.Cognimobile_Data._ID, testCursor.getInt(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data._ID)));
            values.put(Provider.Cognimobile_Data.NAME, testCursor.getString(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.NAME)));
            values.put(Provider.Cognimobile_Data.REDO_TIMESTAMP, timestampToReDo);
            values.put(Provider.Cognimobile_Data._ID, testCursor.getInt(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data._ID)));
            values.put(Provider.Cognimobile_Data.DAYS_TO_DO, testCursor.getInt(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.DAYS_TO_DO)));
            values.put(Provider.Cognimobile_Data.DATA, testCursor.getInt(
                    testCursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.DATA)));
            context.getContentResolver().update(Provider.CONTENT_URI_TESTS,values, where, selectionArgs);
            testCursor.close();
        }

        if (emptyTests()) {
            showNoTests();
        } else {
            fetchTests();
        }
    }


}
