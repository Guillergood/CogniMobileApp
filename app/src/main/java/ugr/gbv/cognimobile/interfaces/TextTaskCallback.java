package ugr.gbv.cognimobile.interfaces;

/**
 * Interface that gives the activity the ability to edit words on the words list, in some tasks.
 */
public interface TextTaskCallback {
    /**
     * Tells the adapter to edit the word.
     *
     * @param word to be edited.
     */
    void editWord(String word);
}
