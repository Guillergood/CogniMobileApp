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

import com.aware.Aware;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.interfaces.QRCallback;

public class StudyFragment extends Fragment {

    private ImageView noStudyTest;
    private Context context;
    private QRCallback callBack;
    private Cursor cursor;
    private FloatingActionButton joinStudyButton;
    private CardView studyCard;
    private TextView animationLabel;

    public StudyFragment(QRCallback callBack) {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_study_fragment,container, false);
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

        joinStudyButton.setOnClickListener(v -> callBack.goToQRActivity());

        checkNewStudy();

        return view;
    }

    private void checkNewStudy() {
        if (emptyStudy() || !CognimobilePreferences.getHasUserJoinedStudy(context)) {
            showNoStudy();
        }
        else{
            fetchStudy();
            showStudy();
        }
    }

    private void showStudy() {
        studyCard.setVisibility(View.VISIBLE);
        animationLabel.setVisibility(View.INVISIBLE);
        noStudyTest.setVisibility(View.INVISIBLE);
        joinStudyButton.setVisibility(View.INVISIBLE);
    }

    private void fetchStudy() {
        cursor = Aware.getStudy(context,"");
        cursor.moveToFirst();
    }


    private void showNoStudy() {
        studyCard.setVisibility(View.INVISIBLE);
        animationLabel.setVisibility(View.VISIBLE);
        joinStudyButton.setVisibility(View.VISIBLE);
        noStudyTest.setVisibility(View.VISIBLE);
    }

    private boolean emptyStudy() {
        cursor = Aware.getStudy(context,"");
        cursor.moveToFirst();
        return cursor.getCount() == 0;
    }

    private void quitStudy() {
        Aware.reset(context);
        context.getContentResolver().delete(
                Provider.Cognimobile_Data.CONTENT_URI_TESTS,
                null,
                null
        );
        CognimobilePreferences.setHasUserJoinedStudy(context, false);
        showNoStudy();
    }
}
