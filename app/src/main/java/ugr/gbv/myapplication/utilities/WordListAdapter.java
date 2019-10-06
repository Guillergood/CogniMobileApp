package ugr.gbv.myapplication.utilities;

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

import ugr.gbv.myapplication.R;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView word;
        EditText editableWord;
        ImageButton editButton;
        ImageButton deleteButton;
        boolean isBeingEdited;



        MyViewHolder(View itemView) {
            super(itemView);

            word =  itemView.findViewById(R.id.word);
            editableWord =  itemView.findViewById(R.id.editWord);
            editButton =  itemView.findViewById(R.id.icon_edit);
            deleteButton =  itemView.findViewById(R.id.icon_delete);
            isBeingEdited = false;

        }

        void displayTextView(){
            word.setVisibility(View.VISIBLE);
            editableWord.setVisibility(View.GONE);
            isBeingEdited = false;
        }

        void displayEditable(){
            word.setVisibility(View.GONE);
            editableWord.setVisibility(View.VISIBLE);
            editableWord.setText(word.getText());
            isBeingEdited = true;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WordListAdapter() {
        mDataset = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WordListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.word_view;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        View.OnClickListener editBehaviour = v -> {
            if(holder.isBeingEdited){
                editWord(holder.word.getText().toString(), holder.editableWord.getText().toString());
                holder.displayTextView();
            }
            else{
                holder.displayEditable();
            }
        };

        View.OnClickListener deleteBehaviour = v -> {
            removeWord(position);
        };

        if(mDataset.get(position) != null && !mDataset.get(position).isEmpty()){
            holder.word.setText(mDataset.get(position));
            holder.editButton.setOnClickListener(editBehaviour);
            holder.deleteButton.setOnClickListener(deleteBehaviour);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addWord(String word){
        if(!mDataset.contains(word)){
            mDataset.add(word);
            notifyItemInserted(mDataset.size() - 1);
        }
    }

    public void addWordInIndex(String word, int index){
        mDataset.add(index, word);
        notifyItemInserted(index);
    }

    public void removeWord(int position){
        if(mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
        else {
            notifyDataSetChanged();
        }
    }
    public void editWord(String original, String replace){
        int position = mDataset.indexOf(original);
        removeWord(position);
        addWordInIndex(replace,position);
        notifyItemChanged(position);
    }

    public void removeAllWords() {
        mDataset = new ArrayList<>();
    }
}