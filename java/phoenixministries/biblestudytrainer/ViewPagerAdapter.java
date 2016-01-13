package phoenixministries.biblestudytrainer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Drew.Sartorius on 5/14/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[];
    int NumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int numOfTabs){
        super(fm);

        this.Titles = mTitles;
        this.NumOfTabs = numOfTabs;
    }

    public Fragment getItem(int position){
        if(position == 0){
            BookTab bookTab = new BookTab();
            return bookTab;
        }
        else if(position == 1){
            ChapterTab chapterTab = new ChapterTab();
            return chapterTab;
        }
        else {
            VerseTab verseTab = new VerseTab();
            return verseTab;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        return Titles[position];
    }

    @Override
    public int getCount(){
        return NumOfTabs;
    }
}
