package bangkokguy.development.android.tabapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class PagerAdapter extends FragmentPagerAdapter {


    private final static int FRAGMENT_LOG = 0;
    private final static int FRAGMENT_WIFI_LIST = 1;
    private final static int FRAGMENT_WIFI_DETAILS = 2;
    private final static int FRAGMENT_NETWORK_DETAILS = 3;
    private final static int FRAGMENT_I_DO_NOT_KNOW = 4;

     PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    private NetworkDetailsFragment networkDetailsFragment = null;

    NetworkDetailsFragment getNetworkDetailsFragment () {return networkDetailsFragment;}

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
            /*ItemFragment extends Fragment*/
        switch (position) {
            case FRAGMENT_LOG:
                return ItemFragment.newInstance(position + 1);
            case FRAGMENT_WIFI_LIST:
                return ItemFragment.newInstance(position + 1);
            case FRAGMENT_WIFI_DETAILS:
                return WifiDetailsFragment.newInstance(position + 1);
            case FRAGMENT_NETWORK_DETAILS:
                networkDetailsFragment = NetworkDetailsFragment.newInstance(position + 1);
                return networkDetailsFragment;
            case FRAGMENT_I_DO_NOT_KNOW:
                return ItemFragment.newInstance(position + 1);

            default:
                throw new NullPointerException("Fragment type out of range");
        }
    }

    final static private String[] NUMBERS = {"Egy", "Kettő", "Három", "Négy", "Öt"};
    @Override
    public CharSequence getPageTitle(int position) {
        return NUMBERS[position]; //CONTENT[position % CONTENT.length].toUpperCase();
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }
}
