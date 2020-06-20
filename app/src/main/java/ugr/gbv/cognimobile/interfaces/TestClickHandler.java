package ugr.gbv.cognimobile.interfaces;

/**
 * Interface that gives the activity the ability to handle the tests clicks
 */
public interface TestClickHandler {
    /**
     * Handle the click of the test.
     *
     * @param id of the clicked test.
     */
    void onClick(int id);

    /**
     * Tells the parent to reload the fragment.
     */
    void reloadFragment();
}
