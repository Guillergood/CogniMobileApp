package ugr.gbv.cognimobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.activities.LoginActivity;
import ugr.gbv.cognimobile.activities.Test;
import ugr.gbv.cognimobile.callbacks.CredentialsCallback;
import ugr.gbv.cognimobile.callbacks.StudyCallback;
import ugr.gbv.cognimobile.callbacks.TestCallback;
import ugr.gbv.cognimobile.dto.Study;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.utilities.DataSender;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpertTestFragment extends Fragment implements StudyCallback, CredentialsCallback {

    private List<Study> studies;
    private AutoCompleteTextView studyPicker;
    private AutoCompleteTextView testPicker;
    private AutoCompleteTextView subjectPicker;
    private FloatingActionButton doTestButton;
    private String study;
    private String test;
    private String subject;

    private final CredentialsCallback callback;

    public ExpertTestFragment(CredentialsCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_expert_fragment, container, false);
        studyPicker = view.findViewById(R.id.studies_dropdown);
        testPicker = view.findViewById(R.id.tests_dropdown);
        subjectPicker = view.findViewById(R.id.users_dropdown);
        doTestButton = view.findViewById(R.id.doTestButton);
        doTestButton.setEnabled(false);
        doTestButton.setOnClickListener(buttonView -> {
            Intent intent = new Intent(getContext(), Test.class);
            intent.putExtra("testName", test);
            intent.putExtra("subjectName", subject);
            intent.putExtra("studyName", study);
            startActivity(intent);
        });
        DataSender.getInstance().getAllStudies(getContext(),this, this);
        return view;
    }

    private void enableStudyList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_dropdown_item, studies.stream().map(Study::getName).collect(Collectors.toList()));
        studyPicker.setEnabled(true);
        studyPicker.setAdapter(adapter);
        studyPicker.setOnItemClickListener((adapterView, view, position, id) -> {
            study = (String) adapterView.getItemAtPosition(position);
            enableOtherPickers();
        });
    }

    private void disableOtherPickers() {
        testPicker.clearListSelection();
        testPicker.setEnabled(false);
        subjectPicker.clearListSelection();
        subjectPicker.setEnabled(false);
    }

    private void enableOtherPickers() {
        Study selectedStudy = studies.stream()
                .filter(theStudy -> theStudy.getName().equals(study)).findFirst().orElse(null);
        if(selectedStudy != null){
            ArrayAdapter<String> adapterTest = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_dropdown_item, selectedStudy.getTests());
            ArrayAdapter<String> adapterSubject = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_dropdown_item, selectedStudy.getSubjects());
            testPicker.setEnabled(true);
            testPicker.setAdapter(adapterTest);
            subjectPicker.setEnabled(true);
            subjectPicker.setAdapter(adapterSubject);

            testPicker.setOnItemClickListener((adapterView, view, position, id) -> {
                test = (String) adapterView.getItemAtPosition(position);
                if(!TextUtils.isEmpty(subject)){
                    doTestButton.setEnabled(true);
                }
            });
            subjectPicker.setOnItemClickListener((adapterView, view, position, id) -> {
                subject = (String) adapterView.getItemAtPosition(position);
                if(!TextUtils.isEmpty(test)){
                    doTestButton.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void getStudies(List<Study> studies) {
        this.studies = studies;
        enableStudyList();
    }

    @Override
    public void doLogout() {
        callback.doLogout();
    }
}
