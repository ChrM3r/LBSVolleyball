package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

;import com.google.gson.Gson;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class KontoAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Spieler> spielerList = new ArrayList<Spieler>();
    private ArrayList<String> spielerNamen = new ArrayList<String>();
    private Context context;
    private ListView spielerListView;



    public KontoAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, Context context) {
        this.spielerList = spielerList;
        this.spielerNamen = spielerNamen;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spielerNamen.size();
    }

    @Override
    public Object getItem(int pos) {
        return spielerNamen.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.konto_list_view, null);
        }

        TextView listSpielerName = (TextView) view.findViewById(R.id.list_item_string);
        listSpielerName.setText(spielerNamen.get(position));

        Button addBtn = (Button) view.findViewById(R.id.add_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spieler spieler = (Spieler) spielerList.get(position);

                Intent data = new Intent(context, SpielerKontoActivity.class);
                data.putExtra("buchungSpieler", new Gson().toJson(spieler));

                System.out.println(new Gson().toJson(spieler));

                context.startActivity(data);


                notifyDataSetChanged();
            }
        });

        return view;
    }


}
