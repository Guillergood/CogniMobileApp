package ugr.gbv.cognimobile.interfaces;

/**
 * Interface that gives the activity the ability to handle Text-to-Speech functionality
 */
public interface TTSHandler {
    /**
     * Instantiate the Text-to-Speech functionality
     */
    void startTTS();

    /**
     * Tells the parent that the Text-to-Speech functionality has ended speaking.
     */
    void TTSEnded();

    /**
     * Sets the index of the element of array that it is being enumerated
     *
     * @param index of the element of array
     */
    void setIndex(int index);

    /**
     * Tells the parent that the Text-to-Speech is initialized.
     */
    void TTSisInitialized();

    /**
     * Registers the current timestamp.
     */
    void registerTimeStamp();
}
