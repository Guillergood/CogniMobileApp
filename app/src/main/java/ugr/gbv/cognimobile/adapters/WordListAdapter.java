package ugr.gbv.cognimobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.TextTaskCallback;

/**
 * Adapter for the {@link ugr.gbv.cognimobile.fragments.TextTask}
 * that allows the user to interact with the list of words only on some tasks.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.MyViewHolder> {
    private ArrayList<String> dataReversed;
    final TextTaskCallback taskCallback;


    /**
     * Constructor
     *
     * @param taskCallback callback to send to the parent the click events.
     */
    public WordListAdapter(TextTaskCallback taskCallback) {
        dataReversed = new ArrayList<>();
        this.taskCallback = taskCallback;
    }

    /**
     * Method to create every view item, for more information see: {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    @NonNull
    public WordListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.word_view;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MyViewHolder(view);
    }

    /**
     * Method to bind every view item, for more information see: {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        View.OnClickListener editBehaviour = v -> {
            if (holder.isBeingEdited) {
                editWord(holder.word.getText().toString(), holder.editableWord.getText().toString());
                holder.displayTextView();
            } else {
                //holder.displayEditable();
                taskCallback.editWord(holder.word.getText().toString());
            }
        };


        if (dataReversed.get(position) != null && !dataReversed.get(position).isEmpty()) {
            holder.word.setText(dataReversed.get(position));
            holder.editButton.setOnClickListener(editBehaviour);
            holder.deleteButton.setOnClickListener(v -> removeWord(dataReversed.get(position)));
        }

    }

    /**
     * Overrides {@link RecyclerView.LayoutManager#getItemCount()} method.
     *
     * @return The number of items currently available
     */
    @Override
    public int getItemCount() {
        return dataReversed.size();
    }

    /**
     * Add a word on the data structure
     *
     * @param word string to be inserted
     */
    public void addWord(String word) {
        if (!dataReversed.contains(word)) {
            dataReversed.add(0, word);
            notifyItemInserted(0);
        }
    }

    /**
     * Add a word on the data structure
     *
     * @param word  string to be inserted
     * @param index position where should be
     */
    private void addWordInIndex(String word, int index) {
        if (!dataReversed.contains(word)) {
            dataReversed.add(index, word);
            notifyItemInserted(index);
        }
    }

    /**
     * Removes a word on the data structure
     *
     * @param word to be removed.
     */
    private void removeWord(String word) {
        int position = dataReversed.indexOf(word);
        if (position >= 0) {
            dataReversed.remove(position);
            notifyItemRemoved(position);
        }
        notifyDataSetChanged();
    }

    /**
     * Replaces one item with another.
     *
     * @param original first item to be replace.
     * @param replace  item which will replace the first item.
     */
    public void editWord(String original, String replace) {
        int position = dataReversed.indexOf(original);
        removeWord(original);
        addWordInIndex(replace, position);
        notifyItemChanged(position);
    }

    /**
     * Removes all items on the data structure
     */
    public void removeAllWords() {
        dataReversed = new ArrayList<>();
    }

    /**
     * Gets all the information from the data structure.
     *
     * @return all the words inserted by the user
     */
    public ArrayList<String> getAllWords() {
        return dataReversed;
    }

    /**
     * Class to display every word.
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView word;
        final EditText editableWord;
        final ImageButton editButton;
        final ImageButton deleteButton;
        boolean isBeingEdited;


        /**
         * Constructor
         *
         * @param itemView The view of the item to be displayed.
         */
        MyViewHolder(View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word);
            editableWord = itemView.findViewById(R.id.editWord);
            editButton = itemView.findViewById(R.id.icon_edit);
            deleteButton = itemView.findViewById(R.id.icon_delete);
            isBeingEdited = false;

        }

        /**
         * Changes the UI to display the text to be not editable
         */
        void displayTextView() {
            word.setVisibility(View.VISIBLE);
            editableWord.setVisibility(View.GONE);
            isBeingEdited = false;
        }

        /**
         * Changes the UI to display the text to be editable
         */
        void displayEditable() {
            word.setVisibility(View.GONE);
            editableWord.setVisibility(View.VISIBLE);
            editableWord.setText(word.getText());
            isBeingEdited = true;
        }
    }
}