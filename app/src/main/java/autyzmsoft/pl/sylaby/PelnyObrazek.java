package autyzmsoft.pl.sylaby;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class PelnyObrazek extends Activity implements View.OnClickListener, OnLongClickListener {
    MediaPlayer mp = null;
    //przekazywane z MainSylaba :
    String sciezka_do_pliku;
    String napisSylaba; //to co wyswietlamy (np. 'bies')

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Wyswietlenie kliknietego obrazka/sylaby na calym ekranie.
        super.onCreate(savedInstanceState);

        //Ukrywamy 'pasek narzedziowy' :
        getActionBar().hide(); //uwaga = API dependent - minimum 11

        setContentView(R.layout.pelny_obrazek);

        View ObszarCaly = findViewById(R.id.imageViewObrazek);

        //ewentualna zmiana tla (jesli ustawiono na SplashKlasa (ekran powitalny) :
        if (ZmienneGlobalne.getInstance().cbTlo_str == "ZMIANA") {
            ObszarCaly.setBackgroundResource(R.drawable.pistacjowy);
        }

        //Jesli zostawiamy sylabe, to pozwalamy tez klikac w tym oknie :
        if (ZmienneGlobalne.cbZnikacz_str == "SYLABA NIE ZNIKA") {
            if (ZmienneGlobalne.cbNoSound_str == "Z GLOSEM") {
                ObszarCaly.setOnClickListener(this);
            }
            ObszarCaly.setSoundEffectsEnabled(false); //zeby nie bylo dzwieku 'klik' - niepedagogiczne...nie ;)
            ObszarCaly.setOnLongClickListener(this);
        }
        //pobieram nazwe obrazka przekazana przez macierzyste activity (MainKomunikator):
        sciezka_do_pliku = getIntent().getStringExtra("sciezka_do_pliku");
        napisSylaba      = getIntent().getStringExtra("napisSylaba");

        //Wyswietlenie sylaby :

        ((TextView) ObszarCaly).setGravity(Gravity.CENTER);

        //ewentualne zmniejszenie o 25% sylab "groźnych", łamiących się na niektórych urządzenach (szczegolnie tych najpopularniejszych - Samsung S4 itp.) :
        if (!ZmienneGlobalne.rozmiarDevice.equals("large")) {
            if (napisSylaba.length() > 1) { //ski - pierwotnie bylo ...length() > 3 , ale wtedy litery y,j,g (z ogonkami) byly za nisko i dlatego 'ucinane' od dolu ...
                float currentSize = ((TextView) ObszarCaly).getTextSize();
                ((TextView) ObszarCaly).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.65 * currentSize);   //setTextSize(currentSize/4);
            }
        }

        ((TextView) ObszarCaly).setText(napisSylaba);					//wypisanie sylaby na srodku vie'wa tekxtowego

		/* ****************************** mierzę rozdzielczosc zeby dostosowac wielkosc napisu do ekranu urzadzenia *********************** */
		/*
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
// since SDK_INT = 1;
		int widthPixels  = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
			}


		Toast.makeText(this,Integer.toString(widthPixels), Toast.LENGTH_LONG).show();
		Toast.makeText(this,Integer.toString(heightPixels), Toast.LENGTH_LONG).show();
		*/


        //po wyswietleniu sylaby, po chwili odtwarzamy dzwiek :
        if (ZmienneGlobalne.getInstance().cbNoSound_str=="Z GLOSEM") {   //ale gramy tylko wtedy, kiedy wybrano opcje 'z glosem' (default zreszta...)
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    Graj(sciezka_do_pliku);//(nazwa_obrazka, nazwa_zestawu);
                }
            }, 600);
        }

        //Jesli z ustawien wynika, ze sylaba ma zniknac samodzielnie, to "znikamy" ją :
        if (ZmienneGlobalne.cbZnikacz_str == "sylaba znika") {
            //Opozniamy zamkniecie :
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 3800);
        }

    } //onCreate



    public void Graj(String plik) {
        //Odegranie dzwieku; parametr wejsciowy 'plik' to nazwa obrazka (png) wraz ze sciezka w assets - zeby odegrac dzwiek,
        //trzeba pozbawic ja nazwy zestawu i rozszerzenia

        //Toast.makeText(this,plik, Toast.LENGTH_LONG).show();
        //Toast.makeText(this,ZmienneGlobalne.getInstance().cbNoSound_str,Toast.LENGTH_LONG).show();

        if (mp != null) { //gdyby mp juz istnial, to trzeba go bezwzglednie ubic - problemy przy wielokrotnym (onClick) odgrywaniu dzwieku
            mp.release();
            mp = new MediaPlayer();
        }

        if (mp == null) { //jesli 1-szy raz wchodzimy do aktywnosci
            mp = new MediaPlayer();
        }

        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd(plik);
            mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mp.prepare();
            mp.setVolume(1f, 1f);
            mp.setLooping(false);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(this, "Nie można odegrać pliku z dźwiękiem.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }  //Graj

    @Override
    public void onClick(View arg0) {
        if (!mp.isPlaying()) {  //zeby nie klikal jak wsciekly... ;)
            Graj(sciezka_do_pliku);
        }
    }

    @Override
    public boolean onLongClick(View arg0) {
	/* ******************* */
	/* Wielkie/Male litery */
	/* ******************* */
        TextView robView = null;
        robView   = (TextView) arg0;
        String zawartosc = null;
        zawartosc = (String) robView.getText(); //dowiadujemy sie, co na ekranie (od razu czy wielkie/male litery - patrz nizej - toUpper/Lower)

        //wlaczenie efektu dzwiekowego - 'click' na powieksznie liter - lepszy efekt :
        arg0.setSoundEffectsEnabled(true);  //bo arg0 jest de facto jednym z wjów - v01..v09 - a tam jest dzwiek wylaczony
        arg0.playSoundEffect(android.view.SoundEffectConstants.CLICK); //dzieki temu bedzie dzwięk na longClick'u (normalnie nie ma)
        arg0.setSoundEffectsEnabled(false); //bo arg0 jest de facto obszarem glownego view - a tam dzwiek trzeba na powrot WYLACZYC...

        //zamiana na wielkie/male litery w zaleznosci od tego, co jest aktualnie na ekranie :
        if (zawartosc.toUpperCase(Locale.getDefault()) != zawartosc) { //to oznacza, ze na ekranie byly male litery
            zawartosc = zawartosc.toUpperCase(Locale.getDefault());
        } else { //zmiana na małe litery :
            // a to oznacza, ze na ekranie byly wielkie litery
            //Pokazujemy pierwotną zawartość pola (nie wystarczy po prostu zmniejszyc poprzez toLowerCase - bo imiona wyjda z malej litery....
            //zeby osiagnac dobry efekt posluguję sie metoda z MainSylaby - jednak zeby byla widoczna z child activity, przedefiniowalem ja na statyczna :
            zawartosc = MainActivity.dajSylabe(sciezka_do_pliku.substring(9,sciezka_do_pliku.length()));
            //zawartosc = zawartosc.toLowerCase(Locale.getDefault());->tak bylo przed wprowadzeniem imion - nie do utrzymania.... (patrz opis powyzej)
        }
        robView.setText(zawartosc);
        return true;  //UWAGA !!! UWAGA !!! jak tutaj bedzie false, to natychmiast po zdarzeniu LongClick wywola sie OnClick, a jak true - to nie ... :)
    }

} //koniec Klasy PelnyObrazek

