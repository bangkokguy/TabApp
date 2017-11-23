package bangkokguy.development.android.tabapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
class DummyContent {

    // An array of sample (dummy) items.
    private List<DummyItem> items = new ArrayList<>();

    private static final int COUNT = 25;

    DummyContent () {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            items.add (new DummyItem(Integer.toString(i), "content", "details"));
        }
    }

    List<DummyItem> getItems () {
        return items;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public class DummyItem {
        private String id;
        private String content;
        private String details;

        DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return id + " " + content + " " + details;
        }

        public String getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        public String getId() {
            return id;
        }
    }
}
