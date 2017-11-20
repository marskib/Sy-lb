package autyzmsoft.pl.sylaby;

import android.view.View;

        import android.app.Activity;
        import android.graphics.Color;
        import android.graphics.Typeface;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.TextView;

/*

 Pokazanie splash screena, ale tak, zeby nie zaslanial - zasluga THEME w manifescie
 Spalsh'a gasimy Buttonem 'klawisz' (choc nie koniecznie tylko nim...)

 */

public class SplashKlasa extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        // usunalem klawisz ponizej, bo malo miejsca na ekranie SplasjKlasy... :
        // Button klawisz = (Button) findViewById(R.id.button1);
        // klawisz.setOnClickListener(this);

        //Zeby klikniecie na SplaszScreenie zamykalo go :
        View Obraz = findViewById(R.id.SplashEkran);
        Obraz.setOnClickListener(this);

        //zeby mozna bylo zmieniac tło :

        CheckBox cbTlo = (CheckBox) findViewById(R.id.cb_tlo);
        cbTlo.setOnCheckedChangeListener(this);

        //Jesli Pelna_Wersja, to informacja o tym (jednoczesnie skazuje informacje o znaczeniu ** (gwiazdek) w wersji demo:
        if (ZmienneGlobalne.PELNA_WERSJA) {
            TextView robView = (TextView) findViewById(R.id.gwiazdki_info);
            robView.setGravity(Gravity.CENTER_HORIZONTAL);
            robView.setText("WERSJA PEŁNA");
            robView.setTextColor(Color.GREEN);
            robView.setTypeface(null, Typeface.BOLD);
        }
    }  //onCreate

    @Override
    protected void onResume() {
    /* wywolywana (niebezposrednio, ale jako skutek) na podwójny tap w MainSylaby (wtedy przywolywana jest SplashKlasa - patrz MAinSylaby.onDoubleTap */
        super.onResume();
        if (ZmienneGlobalne.cbTlo_str.equals("ZMIANA"))
            ((CheckBox) findViewById(R.id.cb_tlo)).setChecked(true);
        if (ZmienneGlobalne.cbZnikacz_str.equals("SYLABA NIE ZNIKA"))
            ((CheckBox) findViewById(R.id.cb_znikacz)).setChecked(false);
        if (ZmienneGlobalne.cbNoSound_str.equals("bez glosu"))
            ((CheckBox) findViewById(R.id.cb_nosound)).setChecked(true);
    } //onResume

    @Override
    protected void onPause() {
        //Sprawdzam checkbox'y i daje znac o ich stanie w zmiennych globalnych,
        //bo obiekt zwiazany ze SplashKlasa przestanie za chwile istniec ...
        //Okreslam tez rozmiar urzadzenia (na pdst. tagu w layout.xml)
        super.onPause();
        ZmienneGlobalne.getInstance().rozmiarDevice = (String) ((View) findViewById(R.id.SplashEkran)).getTag(); //okreslenie rozmiaru ekranu-urządzenia

        if ( ((CheckBox)(findViewById(R.id.cb_nosound))).isChecked()) {
            ZmienneGlobalne.cbNoSound_str = "bez glosu";
        } else {
            ZmienneGlobalne.cbNoSound_str = "Z GLOSEM";
        }

        if ( ((CheckBox) (findViewById(R.id.cb_znikacz))).isChecked() ) {
            ZmienneGlobalne.getInstance().cbZnikacz_str = "sylaba znika";
        } else {
            ZmienneGlobalne.getInstance().cbZnikacz_str = "SYLABA NIE ZNIKA";
        }


        if ( ((CheckBox) (findViewById(R.id.cb_tlo))).isChecked() ) {
            ZmienneGlobalne.getInstance().cbTlo_str = "ZMIANA";
        } else {
            ZmienneGlobalne.getInstance().cbTlo_str = "bez zmian";
        }
    }

    public void onClick(View arg0) {
        finish(); //zamyka aktywnosc i wraca do aktywnosci macierzystej
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Toast.makeText(this, "Zmiana", Toast.LENGTH_LONG).show();

        //LinearLayout calyEkran = (LinearLayout) findViewById(R.id.CalyEkran);
        //calyEkran.setBackgroundColor(Color.BLUE);

        //View v1 = (View) findViewById(R.id.v01);
        //v1.setBackgroundColor(Color.BLUE);
    }

} //SplashKlasa
