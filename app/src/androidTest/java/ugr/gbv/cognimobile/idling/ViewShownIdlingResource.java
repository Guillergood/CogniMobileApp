package ugr.gbv.cognimobile.idling;

import android.view.View;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewFinder;
import androidx.test.espresso.ViewInteraction;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;

import static androidx.test.espresso.Espresso.onView;

public class ViewShownIdlingResource implements IdlingResource {

    private static final String TAG = ViewShownIdlingResource.class.getSimpleName();

    private final Matcher<View> viewMatcher;
    private ResourceCallback resourceCallback;

    public ViewShownIdlingResource(final Matcher<View> viewMatcher) {
        this.viewMatcher = viewMatcher;
    }

    @Override
    public boolean isIdleNow() {
        View view = getView(viewMatcher);
        boolean idle = view == null || view.isShown();

        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    @Override
    public String getName() {
        return this + viewMatcher.toString();
    }

    private static View getView(Matcher<View> viewMatcher) {
        try {
            ViewInteraction viewInteraction = onView(viewMatcher);
            Field finderField = viewInteraction.getClass().getDeclaredField("viewFinder");
            finderField.setAccessible(true);
            ViewFinder finder = (ViewFinder) finderField.get(viewInteraction);
            assert finder != null;
            return finder.getView();
        } catch (Exception e) {
            return null;
        }
    }
}
