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
import ugr.gbv.cognimobile.utilities.ErrorHandler;

/**
 * Adapter for the {@link ugr.gbv.cognimobile.fragments.TestsFragment}
 * that allows the user to interact with the list of Tests.
 */
public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder> {
    private Cursor cursor;
    final private TestClickHandler onClickHandler;

    /**
     * Constructor
     *
     * @param pCursor          data to be displayed
     * @param testClickHandler callback to send to the parent the click events.
     */
    public TestsAdapter(Cursor pCursor, TestClickHandler testClickHandler) {
        cursor = pCursor;
        onClickHandler = testClickHandler;
    }

    /**
     * Method to create every view item, for more information see: {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
     *                  an adapter position.
     * @param viewType  The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.test_view_holder;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Method to bind every view item, for more information see: {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView testName = holder.itemView.findViewById(R.id.testNameViewholder);
        cursor.moveToPosition(position);
        int columnIndex = cursor.getColumnIndex(Provider.Cognimobile_Data.NAME);
        if(columnIndex >= 0) {
            testName.setText(cursor.getString(columnIndex));
        }
        else{
            ErrorHandler.displayError("Some error happened displaying the tests");
        }
    }

    /**
     * Overrides {@link RecyclerView.LayoutManager#getItemCount()} method.
     *
     * @return The number of items currently available
     */
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * Updates the adapter with new data
     *
     * @param newCursor the new data updated.
     */
    public void updateCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Class to display every test in the database.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Constructor
         *
         * @param itemView The view of the item to be displayed.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Method to catch the clicks on the UI
         *
         * @param v The view that has been clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);

            int testPosition = cursor.getColumnIndex(Provider.Cognimobile_Data._ID);

            if (onClickHandler != null
                    && adapterPosition >= 0
                    && adapterPosition < cursor.getCount()
                    && testPosition >= 0) {
                onClickHandler.onClick(cursor.getInt(testPosition));
            }

        }


    }


}
