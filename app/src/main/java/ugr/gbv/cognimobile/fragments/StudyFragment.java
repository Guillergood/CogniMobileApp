package ugr.gbv.cognimobile.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
/**
 * Fragment to display the Study section in {@link ugr.gbv.cognimobile.activities.MainActivity}
 */
public class StudyFragment extends Fragment {

    private ImageView noStudyTest;
    private Context context;
    private final ServerLinkRetrieval callBack;
    private Cursor cursor;
    private FloatingActionButton joinStudyButton;
    private CardView studyCard;
    private TextView animationLabel;

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
        noStudyTest = view.findViewById(R.id.noStudyImage);
        animationLabel = view.findViewById(R.id.noStudyLabel);

        context = getContext();

        joinStudyButton = view.findViewById(R.id.joinStudyButton);
        studyCard = view.findViewById(R.id.studyCard);

        LinearLayout quitStudyButtonContainer = view.findViewById(R.id.quitStudyButtonContainer);
        ImageButton quitStudyButton = view.findViewById(R.id.quitStudyButton);
        TextView quitStudyButtonLabel = view.findViewById(R.id.quitStudyButtonLabel);

        View.OnClickListener quitStudyClickListener = v -> quitStudy();

        quitStudyButtonContainer.setOnClickListener(quitStudyClickListener);
        quitStudyButton.setOnClickListener(quitStudyClickListener);
        quitStudyButtonLabel.setOnClickListener(quitStudyClickListener);

        joinStudyButton.setOnClickListener(v -> callBack.goToChooseQrOrTextActivity());

        checkNewStudy();

        return view;
    }


    /**
     * Checks if there is a new study to be displayed or not.
     */
    private void checkNewStudy() {
        if (emptyStudy() || !CognimobilePreferences.getHasUserJoinedStudy(context)) {
            showNoStudy();
        } else {
            fetchStudy();
            showStudy();
        }
    }

    /**
     * Makes the Study components visible.
     */
    private void showStudy() {
        studyCard.setVisibility(View.VISIBLE);
        animationLabel.setVisibility(View.INVISIBLE);
        noStudyTest.setVisibility(View.INVISIBLE);
        joinStudyButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Retrieves the Study information.
     */
    private void fetchStudy() {
        //TODO CHANGE DATABASE
//        cursor = Aware.getStudy(context, "");
//        cursor.moveToFirst();
//        int columnName = cursor.getColumnIndex(Aware_Provider.Aware_Studies.STUDY_TITLE);
//        int columnDeviceId = cursor.getColumnIndex(Aware_Provider.Aware_Studies.STUDY_DEVICE_ID);
//        if(columnName > 0 && columnDeviceId > 0){
//            TextView name = studyCard.findViewById(R.id.studyNameViewholder);
//            TextView description = studyCard.findViewById(R.id.studyDescriptionViewholder);
//            name.setText(cursor.getString(columnName));
//            String descriptionText = getString(R.string.device_id) + cursor.getString(columnDeviceId);
//            description.setText(descriptionText);
//        }
    }

    /**
     * Makes the UI components invisible.
     */
    private void showNoStudy() {
        studyCard.setVisibility(View.INVISIBLE);
        animationLabel.setVisibility(View.VISIBLE);
        joinStudyButton.setVisibility(View.VISIBLE);
        noStudyTest.setVisibility(View.VISIBLE);
    }

    /**
     * Checks if there is a study enrolled.
     *
     * @return true if there is a study enrolled, false if not.
     */
    private boolean emptyStudy() {
//        cursor = Aware.getStudy(context, "");
//        cursor.moveToFirst();
//        return cursor.getCount() == 0;
        return true;
    }

    /**
     * Quits the enrolled study.
     */
    private void quitStudy() {
//        Aware.reset(context);
//        context.getContentResolver().delete(
//                Provider.Cognimobile_Data.CONTENT_URI_TESTS,
//                null,
//                null
//        );
//        CognimobilePreferences.setHasUserJoinedStudy(context, false);
//        showNoStudy();
    }
}
