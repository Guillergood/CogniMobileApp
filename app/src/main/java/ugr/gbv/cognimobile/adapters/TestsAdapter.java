package ugr.gbv.cognimobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
import ugr.gbv.cognimobile.interfaces.TestsAdapterClickHandler;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder>{
    private Cursor cursor;
    final private TestsAdapterClickHandler quitTestHandler;
    final private TestClickHandler onClickHandler;

    public TestsAdapter(Cursor pCursor, TestsAdapterClickHandler pHandler, TestClickHandler testClickHandler) {
        cursor = pCursor;
        quitTestHandler = pHandler;
        onClickHandler = testClickHandler;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageButton quitTestButton;
        TextView quitTestButtonLabel;
        LinearLayout quitTestButtonContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            quitTestButton = itemView.findViewById(R.id.quitTestButton);
            quitTestButtonLabel = itemView.findViewById(R.id.quitTestButtonLabel);
            quitTestButtonContainer = itemView.findViewById(R.id.quitTestButtonContainer);
            quitTestButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int id = v.getId();
            int otros = quitTestButton.getId();
            int otros1 = quitTestButtonLabel.getId();
            int otros2 = quitTestButtonContainer.getId();

            if(v.getId() == quitTestButton.getId() ||
                v.getId() == quitTestButtonLabel.getId() ||
                v.getId() == quitTestButtonContainer.getId()
            ){
                cursor.moveToPosition(adapterPosition);
                quitTestHandler.quitTest(cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.NAME)));
            }
            else{
                if(adapterPosition >= 0 && adapterPosition < cursor.getCount()) {
                    cursor.moveToPosition(adapterPosition);
                    //TODO Pasar DATA y que se desenvuelva los datos
                    onClickHandler.onClick(adapterPosition);
                }
            }
        }



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.test_view_holder;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView studyName = holder.itemView.findViewById(R.id.testNameViewholder);

        cursor.moveToPosition(position);

        studyName.setText(cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.NAME)));

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void updateCursor(Cursor newCursor){
        cursor = newCursor;
        notifyDataSetChanged();
    }


}
