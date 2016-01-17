package phoenixministries.biblestudytrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * Created by Drew.Sartorius on 5/14/2015.
 */

public class BookTab extends Fragment {
    private TextView[] textViewBooks = new TextView[66];
    private TextView[] textViewChapters = new TextView[66];
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int selectedBookId = -1;
    private int defaultBackgroundColor = Color.parseColor("#fcfbe3");
    private int selectedBackgroundColor = Color.parseColor("#42a5f5");

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.tab_book, container, false);

        Context context = v.findViewById(R.id.bookList).getContext();
        sharedPreferences = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        for(int i=0; i < BibleStructure.getBooks().length; i++) {
            //setup layout for Book tab
            textViewBooks[i] = new TextView(context);
            textViewBooks[i].setTextAppearance(context, R.style.chooser_item_style);
            textViewBooks[i].setGravity(Gravity.CENTER_HORIZONTAL);
            textViewBooks[i].setText(BibleStructure.getBooks()[i]);

            //set id for Book
            textViewBooks[i].setId(i);

            //highlight book if selected value
            if(sharedPreferences.getInt("selected_book_id", -1)==textViewBooks[i].getId()){
                textViewBooks[i].setBackgroundColor(selectedBackgroundColor);
            }
            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.bookList);
            linearLayout.addView(textViewBooks[i]);
        }

        //setup onClick handlers
        for(final TextView textView : textViewBooks){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save selected book
                    editor.putInt("selected_book_id", textView.getId());
                    editor.putInt("selected_chapter_id", -1);
                    editor.putInt("selected_verse_id", -1);
                    editor.putString("selected_book", textView.getText().toString());
                    editor.commit();

                    //Create chapter tab and navigate to it
                    ((ChooserActivity)getActivity()).addChapterTab(textView.getText(), true);
                }
            });
        }

        return v;
    }
}
