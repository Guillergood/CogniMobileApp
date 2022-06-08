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
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
/**
 * Fragment to display the Study section in {@link ugr.gbv.cognimobile.activities.MainActivity}
 */
public class StudyFragment extends Fragment {

    private ImageView noStudyTest;
    private Context context;
    private final ServerLinkRetrieval callBack;
    private RecyclerView recyclerView;
    private TextView noStudyText;

    /**
     * Constructor
     *
     * @param callBack to call {@link ugr.gbv.cognimobile.qr_reader.ReadQR} from {@link ugr.gbv.cognimobile.activities.MainActivity}
     */
    public StudyFragment(ServerLinkRetrieval callBack) {
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

        View view = inflater.inflate(R.layout.layout_study_fragment, container, false);
        recyclerView = view.findViewById(R.id.studies_recyclerview);
        noStudyTest = view.findViewById(R.id.noStudyImage);
        noStudyText = view.findViewById(R.id.noStudyLabel);

        context = getContext();

        if (emptyStudies()) {
            showNoStudies();
        } else {
            fetchStudies();
        }


        return view;
    }

    /**
     * Makes the Study components visible.
     */
    private void showStudies() {
        recyclerView.setVisibility(View.VISIBLE);
        noStudyText.setVisibility(View.INVISIBLE);
        noStudyTest.setVisibility(View.INVISIBLE);
    }

    /**
     * Retrieves the Studies information.
     */

    private void fetchStudies() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String[] projection = new String[]{Provider.Cognimobile_Data._ID, Provider.Cognimobile_Data.NAME};
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_STUDIES, projection, null, null, Provider.Cognimobile_Data._ID);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        TestsAdapter studiesAdapter = new TestsAdapter(cursor, null);
        recyclerView.setAdapter(studiesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);

        recyclerView.addItemDecoration(dividerItemDecoration);

        showStudies();
    }

    /**
     * Makes the UI components invisible.
     */
    private void showNoStudies() {
        recyclerView.setVisibility(View.INVISIBLE);
        noStudyText.setVisibility(View.VISIBLE);
        noStudyTest.setVisibility(View.VISIBLE);
    }

    /**
     * Checks if there is a study enrolled.
     *
     * @return true if there is a study enrolled, false if not.
     */
    private boolean emptyStudies() {
        String[] projection = new String[]{Provider.Cognimobile_Data._ID};
        Cursor tempCursor = context.getContentResolver().query(Provider.CONTENT_URI_STUDIES, projection, null, null,
                Provider.Cognimobile_Data._ID);
        int count = 0;
        if (tempCursor != null) {
            count = tempCursor.getCount();
            tempCursor.close();
        }
        return count == 0;
    }

}
