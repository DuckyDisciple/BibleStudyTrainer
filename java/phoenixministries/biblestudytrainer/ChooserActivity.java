package phoenixministries.biblestudytrainer;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
//import android.widget.Toolbar;
import de.greenrobot.event.EventBus;

import phoenixministries.biblestudytrainer.view.SlidingTabLayout;


public class ChooserActivity extends ActionBarActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    //SharedPreferences sharedPreferences = getSharedPreferences(
    //        getString(R.string.preference_file_key), Context.MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        setTitle("Choose a passage...");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //get saved preferences
        int bookId = sharedPreferences.getInt("selected_book_id",-1);
        int chapterNumber = sharedPreferences.getInt("selected_chapter_id",-1) + 1;
        int verseNumber = sharedPreferences.getInt("selected_verse_id",-1) + 1;

        //Double check saved preferences
        if(bookId < 0){
            chapterNumber = -1;
            verseNumber = -1;
            editor.putInt("selected_chapter_id",-1);
            editor.putInt("selected_verse_id",-1);
            editor.commit();
        }else if(chapterNumber < 1){
            verseNumber = -1;
            editor.putInt("selected_verse_id",-1);
            editor.commit();
        }

        if(verseNumber > 0){
            //TODO - Change this to navigate to reading activity
            CharSequence bookTitle = BibleStructure.getBooks()[bookId];
            goToReading(bookTitle,chapterNumber,verseNumber);
        }else if(chapterNumber > 0){
            CharSequence bookTitle = BibleStructure.getBooks()[bookId];
            addVerseTab(bookTitle, chapterNumber, true);
        }else if(bookId >= 0){
            CharSequence bookTitle = BibleStructure.getBooks()[bookId];
            addChapterTab(bookTitle,true);
        }else{
            CharSequence justBooks[] = {"Books"};
            createChooser(justBooks, 1);
        }
    }

    public void createChooser(CharSequence Titles[], int NumOfTabs){
        //Set adapter
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);
        pager = (ViewPager) findViewById(R.id.pager);
        //Set tabs
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        //Set tab color
        /*tabs.setCustomTabColorizer(new SlidingTabLayout().TabColorizer(){
            @Override
            public int getIndicatorColor(int position){
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });*/

        //Set ViewPager
        pager.setAdapter(adapter);

        //Set ViewPager
        tabs.setViewPager(pager);
    }

    public void addChapterTab(CharSequence bookTitle, boolean navToChapterTab){
        //Setup new layout
        CharSequence Titles2[] = {"Book","Chapter"};
        int NumOfTabs2 = 2;
        createChooser(Titles2,NumOfTabs2);

        //Set title bar to Book name
        setTitle(bookTitle);

        //Auto-navigate TODO - Slower tab scroll by extending ViewPager and set custom velocity
        if(navToChapterTab){
            //pager.setCurrentItem(0);
            pager.setCurrentItem(1,true);        //Navigate to Chapters
        }
    }

    public void addVerseTab(CharSequence bookTitle, int chapterNumber, boolean navToVerseTab){
        //Setup new layout
        CharSequence Titles3[] = {"Book","Chapter","Verse"};
        int NumOfTabs3 = 3;
        createChooser(Titles3,NumOfTabs3);

        //Set title bar to Book and Chapter
        CharSequence newTitle = bookTitle + " " + chapterNumber;
        setTitle(newTitle);

        //Auto-navigate
        if(navToVerseTab){
            //pager.setCurrentItem(1);        //Navigate to Verse tab
            pager.setCurrentItem(2,true);
        }
    }

    public void goToReading(CharSequence bookTitle, int chapterNumber, int verseNumber){
        //Format title
        String passageReference = bookTitle + " " + chapterNumber;
        //Changed from book chap:num  -- Shows full chapter at a time
        Intent intent = new Intent(this,ReadingActivity.class);
        Bundle b = new Bundle();
        b.putString("passage",passageReference);
        b.putString("book",bookTitle.toString());
        b.putInt("chapter", chapterNumber);
        b.putInt("verse", verseNumber);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
