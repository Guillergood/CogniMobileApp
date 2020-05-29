package ugr.gbv.cognimobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder>{
    private Cursor cursor;
    final private TestClickHandler onClickHandler;

    public TestsAdapter(Cursor pCursor, TestClickHandler testClickHandler) {
        cursor = pCursor;
        onClickHandler = testClickHandler;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);

            if (adapterPosition >= 0 && adapterPosition < cursor.getCount()) {
                onClickHandler.onClick(cursor.getInt(cursor.getColumnIndex(Provider.Cognimobile_Data._ID)));
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
