package autyzmsoft.pl.sylaby;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends Activity implements View.OnClickListener, OnLongClickListener,
        SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;

    TextView v01,v02,v03,v04,v05,v06,v07,v08,v09;
    Intent pelnyObrazek;      //do rozdmuchania obrazka na caly ekran
    Intent wypelniaczZielony; //kosmetyka - na chwilowy 'buforek' pomiedzy przesuwanymi ekranami
    String wybrany_zestaw;    //jaki zestaw wybrano z menu (globalna, bo sprawdzam czy nie null po uruchomieniu - istotne przy obracaniu urzadzenia) - nie dziala.....
    String aktZestaw = "zestaw01"; //aktualnie wyswietlany zestaw 9-tu sylab
    MenuItem wybrItem;           //aktualnie wybrany Item z Menu (zeby wiedziec co gasic, a co pozostawic nie zgaszone w menu)
    Boolean wielkieLitery = false;
    Boolean pierwszeWejscie = true;
    SharedPreferences sharedPreferences;
    int nrZestSharPref = 3;  //ktory zestaw (if any) wybrano z SharedPreferences
    View Obszar;      //caly obszar okna z 9-ma sylabami
    private float initialSize; //rozmiarZadany textu okreslony w layoucie w DesignTime (bo trzeba dynamicznie czasami zmienic - wielkie litery nie zawsze sie mieszcza...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wielkieLitery = false; //przełącznik wielkie/male litery (startujemy od malych)
        //Ukrywamy 'pasek narzedziowy' : - uwaga - jesli to zrobimy, nie bedzie Menu... No i dobrze - menu pokazujemy na swipeDown
        getActionBar().hide(); //uwaga = API dependent - minimum 11
        setContentView(R.layout.activity_main);
        detector = new SimpleGestureFilter(this, this); //na wykrywanie Swip'ów
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //na zapisanie ustawien na next. sesję
        pelnyObrazek = new Intent("autyzmsoft.pl.sylaby.PelnyObrazek");       //do rozdmuchania obrazka na caly ekran
        //pokazanie splash screena :
        Intent splashKlasa = new Intent("autyzmsoft.pl.sylaby.SplashKlasa");
        startActivity(splashKlasa);
        //odblokowanie calego obszru (rowniez miedzy 'pudełkami') na Longtouch'a :
        Obszar = findViewById(R.id.CalyEkran);
        Obszar.setOnLongClickListener(this);
        //Ustawienie zmiennych i listenerow (poczatkowe obrazki pobieram z drawable (juz nie) - nie umiem z assets...; pozniej -> (w PelnyObrazek) -> umiem... :) :
        v01 = (TextView) findViewById(R.id.v01);
        v01.setOnClickListener(this);
        v01.setOnLongClickListener(this);
        v01.setSoundEffectsEnabled(false); //zeby nie bylo dzwieku 'klik' - niepedagogiczne...nie ;)
        initialSize = ((TextView) v01).getTextSize(); //dowiadujemy sie tego na pdst.v01 - inne v0n-taki sam rozmiarZadany tekstu, wiec dalej nie trzeba tego robic ;)

        v02 = (TextView) findViewById(R.id.v02);
        v02.setOnClickListener(this);
        v02.setOnLongClickListener(this);
        v02.setSoundEffectsEnabled(false);

        v03 = (TextView) findViewById(R.id.v03);
        v03.setOnClickListener(this);
        v03.setOnLongClickListener(this);
        v03.setSoundEffectsEnabled(false);

        v04 = (TextView) findViewById(R.id.v04);
        v04.setOnClickListener(this);
        v04.setOnLongClickListener(this);
        v04.setSoundEffectsEnabled(false);

        v05 = (TextView) findViewById(R.id.v05);
        v05.setOnClickListener(this);
        v05.setOnLongClickListener(this);
        v05.setSoundEffectsEnabled(false);

        v06 = (TextView) findViewById(R.id.v06);
        v06.setOnClickListener(this);
        v06.setOnLongClickListener(this);
        v06.setSoundEffectsEnabled(false);

        v07 = (TextView) findViewById(R.id.v07);
        v07.setOnClickListener(this);
        v07.setOnLongClickListener(this);
        v07.setSoundEffectsEnabled(false);

        v08 = (TextView) findViewById(R.id.v08);
        v08.setOnClickListener(this);
        v08.setOnLongClickListener(this);
        v08.setSoundEffectsEnabled(false);

        v09 = (TextView) findViewById(R.id.v09);
        v09.setOnClickListener(this);
        v09.setOnLongClickListener(this);
        v09.setSoundEffectsEnabled(false);

        //Rozpoczynamy aplikacje od wyswietleni 'startowego' zestawu (korzystam z ewentualnych ustawien z ostatniej sesji) :
        aktZestaw = sharedPreferences.getString("zestaw", "zestaw01"); //2-gi parametr na wypadek, gdyby w SharedPref. nic jeszcze nie bylo
        wielkieLitery = sharedPreferences.getBoolean("wielkieLitery",false);

        if (aktZestaw.contains("**")) { //gdyby byly jakies zaszlosci PelnaWersja vs. DarmowaWersja (uzytkownik zainstalowal pelna wersje, po tym jak mial darmowo - MOGA wejsc ** do SharedPref (!)
            aktZestaw = "zestaw01";
        }

        //policzenie, ktorej pozycji w menu odpowiada wybrany w SharedPref zestaw - zeby go 'zaczekowac' za pomocą onCreateOptionsMenu
        String robstr = aktZestaw.substring(6,aktZestaw.length());
        nrZestSharPref = Integer.valueOf(robstr)-1;  //potrzebny w menu startowym
        //pokazanie startowego zestawu:
        wyswietlZestaw(aktZestaw);
        wybrany_zestaw = aktZestaw; //na przyszlosc
        //jezeli 'uspilismy' z wielkiemi literami, to niech tak samo sie obudzi :
        wielkieLitery = !wielkieLitery;
        onLongClick(v01); //v01 - parametr ze wzgledow formalnych
    } //onCreate

    @Override
    protected void onDestroy() {
	/* Zapisanie ustawienia w SharedPreferences na przyszła sesję */
        super.onDestroy();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("zestaw", wybrany_zestaw);
        edit.putBoolean("wielkieLitery", wielkieLitery);
        edit.apply();
    }

    public String dajZestawNaPdstMenu(String tytulzMenu) {
        //Na pdst opcji wybranej z menu daje stringa (ktory jest jednoczesnie nazwa katalogu z zestawem w assets)
        //Przyklad : Zestaw nr 01 --> zestaw01

        //Toast.makeText(this, tytulzMenu, Toast.LENGTH_LONG).show();

        String numerZestawu = tytulzMenu.substring(10, tytulzMenu.length()); // "Zestaw nr 01" --> "01"
        String wynik = "zestaw" + numerZestawu;

        return wynik;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (pierwszeWejscie) {
            pierwszeWejscie = false;
            return;
        }
        if (ZmienneGlobalne.getInstance().cbTlo_str == "ZMIANA") {
            Obszar.setBackgroundResource(R.drawable.pistacjowy);
        } else {
            Obszar.setBackgroundResource(R.drawable.maroon);
        }
    }


    public static String dajSylabe(String ciagZnakow) {
	/* **************************************************************************************************** */
	/* Na podstawie ciagZnakow(=nazwa pliku mp3) oddekodowuję sylabe (do polozenia na aktualnym kwadraciku) */
	/* static - bo bedzie rowniez wywolywana w PelnyObrazek (przy pomniejszaniu sylaby)                     */
	/* **************************************************************************************************** */
        //(troche gimnastyki, bo assets nie przyjmuja polskich znakow w nazwach plikow - polskie znaki uzyskuję nadajac plikom nazwy wedlug reguly : ę -> -e ; gęś.mp3 -> g-e-s.mp3
        //Trzeba to teraz "odkodować' i na TextView umiescic napis "gęś" :
        String sylaba="";
        Integer pozycjaKropki = ciagZnakow.indexOf(".");
        String plikNazwa = ciagZnakow.substring(0, pozycjaKropki); // "kra.mp3" --> "kra"

        //usuniecie ewentualnych znakow 01_,02_..09_ decydujacych o kolejnosci(=polozeniu) sylaby w kwadracikach (rozwiazanie żeby że-rze, łó-łu... występowały blisko siebie...)
        if (ciagZnakow.startsWith("0")) {
            plikNazwa = plikNazwa.substring(3, plikNazwa.length()); //wlasciwa nazwa zaczyna sie od pozycji 3 np. 01_rze = rze
        };
        //
        for (int j = 0; j < plikNazwa.length(); j++) {
            if (!plikNazwa.substring(j, j + 1).equals("-")) {   //'normalny' znak
                sylaba = sylaba + plikNazwa.substring(j, j + 1);
            }
            else {   //znak '-' - sygnal, ze mamy polski znak w nazwie pliku
                j++; //przesuwamy sie o 1 znak w prawo i patrzymy co za znak stoi za znakiem "-", i w zaleznosci od tego, co tam jest - reagujemy :
                if (plikNazwa.substring(j, j + 1).equals("a")) {
                    sylaba = sylaba + "ą";
                } else if (plikNazwa.substring(j, j + 1).equals("c")) {
                    sylaba = sylaba + "ć";
                } else if (plikNazwa.substring(j, j + 1).equals("e")) {
                    sylaba = sylaba + "ę";
                } else if (plikNazwa.substring(j, j + 1).equals("l")) {
                    sylaba = sylaba + "ł";
                } else if (plikNazwa.substring(j, j + 1).equals("n")) {
                    sylaba = sylaba + "ń";
                } else if (plikNazwa.substring(j, j + 1).equals("o")) {
                    sylaba = sylaba + "ó";
                } else if (plikNazwa.substring(j, j + 1).equals("s")) {
                    sylaba = sylaba + "ś";
                } else if (plikNazwa.substring(j, j + 1).equals("z")) {
                    sylaba = sylaba + "ż";
                } else if (plikNazwa.substring(j, j + 1).equals("x")) {
                    sylaba = sylaba + "ź";
                }
            }
        }
        return sylaba;
    }  //dajSylabe

    public boolean wyswietlZestaw(String wybrany_zestaw) {
		 /* *****************************************************************************************************
		 /* Na ekranie wyswietlany jest wybrany z menu zestaw obrazkow.
		 /* Istotne jest rowniez, ze do kazdego takiego obrazka przypisywany jest tag='nazwa_obrazka', zeby
		 /* mozna go bylo potem identyfikowac na klik
		 /* W przypadku wersji demo, gdy wybrano zestaw, prezentowany jest on w wersji Toast/Alert i wyjscie z proc. (marketing)
		 /* **************************************************************************************************** */
        String sylaba;
        String zestaw   = wybrany_zestaw; //zeby nie naruszyc zmiennej wybrany_zestaw (bo bedzie jeszcze potrzebna jej oryginalna wartosc)
        String zestawik = "";

        //przygotowania na wypadek wybrania zestawu opatrzonego '**' (nie dozwolony w wersji demo) - zaprezentujemy go w Toast'cie
        if (wybrany_zestaw.endsWith("**")) {
            Integer pozycjaGwiazdki = wybrany_zestaw.indexOf("*");
            zestawik = wybrany_zestaw.substring(0,pozycjaGwiazdki-1);  //'Zestaw04 **' -> 'Zestaw04'
            zestaw   = zestawik;
        }
        //Uzyskanie listy plikow .mp3 wchodzacych w sklad zestawu 'wybrany_zestaw' przekazanego w parametrze niniejszej funkcji :
        //Pobieramy liste plikow w wybranym zestawie (katalogu w assets) :
        String lista[] = null;
        AssetManager mgr = getAssets();
        try {
            lista = mgr.list(zestaw);
        } catch (IOException e) {
            Toast.makeText(this, "Problem ze znalezieniem plików", Toast.LENGTH_LONG).show();
        }

        //Jesli wersja demo i user wybral niedozwolony zestaw - informacja i koniec procedury (marketing) :
        if (wybrany_zestaw.endsWith("**")) {
            //Kompletujemy liste sylab w zestawie, pakujemy ja do stringa s0; sylaby odzielamy spacją; nast3epnie wyswietlamy na ekranie - marketing :
            String s0 = " ";
            String slb = "";
            int dlug = 0;
            for (int i = 0; i < 9; i++) {  //formatowanie -> 3 wiersze x 3 kolumny :
                slb = dajSylabe(lista[i]);
                s0  = s0 + slb; //a zaraz ponizej dopelniam spacjami..
                if ((i!=2)&(i!=5)&(i!=8)) { //dopelniam spacjami (ale nie stawiam spacji na koncach linii)
                    dlug = slb.length();
                    for (int j = dlug; j < 7; j++) { s0 = s0 + " ";	}
                }
                if ((i==2)||(i==5)) {s0 = s0 + (char)10 + (char)13; } //zlamanie 1-szej i 2-giej linii
            }

            //s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);

            SpannableString wykazSylab = new SpannableString(s0); //inicjujemy "tlustego" stringa wartoscia 9-ciu sylab z zestawu **
            wykazSylab.setSpan(new RelativeSizeSpan(2.5f), 0, wykazSylab.length(), 0);
            wykazSylab.setSpan(new StyleSpan(Typeface.BOLD), 0, wykazSylab.length(), 0);

            SpannableString infoDemo = new SpannableString("Zestaw dostępny w pełnej wersji aplikacji.");
            infoDemo.setSpan(new RelativeSizeSpan(2.5f),   0, infoDemo.length(), 0);
            infoDemo.setSpan(new StyleSpan(Typeface.BOLD), 0, infoDemo.length(), 0);

            //Toast.makeText(MainSylaby.this, wykazSylab, Toast.LENGTH_LONG).show();
            String message = s0;
            AlertDialog.Builder okienkoDialog = new AlertDialog.Builder(MainActivity.this);
            okienkoDialog.setTitle("Zestaw dostępny w pełnej wersji aplikacji");
            okienkoDialog.setMessage(message);
            okienkoDialog.setPositiveButton("OK", null);
            okienkoDialog.show();

            //Toast.makeText(MainSylaby.this, infoDemo, Toast.LENGTH_LONG).show();
            return true;  //wychodzimy z funkcji "na skróty" (bo dalej isc nie trzeba i nie wolno...)
        }


        String plik_path = "";            //na nazwe kolejnego pliku w zestawie
        TextView robView = null;          //inicjacja zm., zeby sie kompilator nie czepial, za chwile bedzie i tak zmieniana...

        //Rozpoczynamy przypisanie pliku (mp3/ogg) i wyswietlenie jego nazwy(=sylaba) na view'wach :
        for (int i = 0; i < 9; i++) {
            //Na pdst. indeksu 'i' petli for, przygotowujemy nazwe pliku :
            plik_path = wybrany_zestaw + "/" + lista[i];
            //wybieramy view, na ktorym bedziemy operowac (=przypisywac obrazek z zasobow i tag(=mp3 file)):
            switch (i) {
                case 0:	robView = (TextView) findViewById(R.id.v01); break;
                case 1:	robView = (TextView) findViewById(R.id.v02); break;
                case 2:	robView = (TextView) findViewById(R.id.v03); break;
                case 3:	robView = (TextView) findViewById(R.id.v04); break;
                case 4:	robView = (TextView) findViewById(R.id.v05); break;
                case 5:	robView = (TextView) findViewById(R.id.v06); break;
                case 6:	robView = (TextView) findViewById(R.id.v07); break;
                case 7:	robView = (TextView) findViewById(R.id.v08); break;
                case 8:	robView = (TextView) findViewById(R.id.v09); break;
                default: break;
            }
            robView.setTag(plik_path);     //wazne - ustawiamy sciezke do pliku .mp3 w tagu (dla pozniejszego odegrania na OnClik'a)
            robView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            //wypisanie sylaby na srodku vie'wa tekstowego :
            sylaba = dajSylabe(lista[i]); //na podstawie nazwy pliku z zasoby/Zestaw-nn.mp3/ogg okresla sylabe
            robView.setText(sylaba);
        }
        return true;
    }/* Koniec funkcji wyswietlZestaw */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /******************************************************************************************/
	/*Wypelnienie listy opcji. Wypelniam nieco inaczej, w zaleznosci od zmiennej PELNA_WERSJA */
	/*                                                                                        */
        /******************************************************************************************/
        super.onCreateOptionsMenu(menu);
        MenuInflater wypelniacz = getMenuInflater();
        wypelniacz.inflate(R.menu.activity_main, menu);
        //
        //usuwam ten 1-szy (obowiazkowy?) wziety z xml - potem tworze sam na pdst struktury katalogow z assets:
        menu.removeItem(menu.getItem(0).getItemId());

		/* ************************************************************************/
        //na podst. zawartosci assets tworzone jest menu (do menu wchodzo pozycje (m.inn.) "zestaw01,...zestawNN"):
        String lista[] = null;
        AssetManager mgr = getAssets();
        try {
            lista = mgr.list("");
        } catch (IOException e) {
            Toast.makeText(this, "Problem ze znalezieniem plików", Toast.LENGTH_LONG).show();
        }
		/* ************************************************************************/

        //Wypelnienie menu :
        //szukam miejsca, gdzie rozpoczynają sie sekwencje "zestaw01 zestaw02...." (zalezy od s.o.) :
        int j = 0;
        while (!lista[j].contains("zestaw")) {
            j++;
        }
        final int delta = j; //znalezione przesuniecie 'delta' (to zalezy od wersji s.o. ANDROID - np. na tablecie delta=5, na telefonach delta=4 itp - dlatego szukam...)
        for (int k = delta; k < lista.length; k++) {
            String numerZestawu = lista[k].substring(6, lista[k].length()); // "zestaw01" --> "01"
            String tekscikMenu = "Zestaw nr " + numerZestawu;
            if (!ZmienneGlobalne.PELNA_WERSJA) {  //w wersji demo tylko 3 pierwsze pozycje menu (zestawy) dostepne - inne oznaczone zostaja **
                if (k > delta + 2) {
                    if ((k != delta + 5)&&(k != delta + 24)&&(k != delta + 27)&&(k != delta + 41)) { //z czasem 'uwalniam' rowniez inne pozycje... :)
                        tekscikMenu = tekscikMenu + " **";
                    } //pozycje menu, ktore NIE wchodza do wersji demo oznaczam dwoma '**'
                }
            }
            menu.add(tekscikMenu);
            menu.getItem(k - delta).setCheckable(true); //zeby mial pólko na checkbox'a
        }
        //oznaczenie itemu (wybranego z SharPref.) jako wybranego (czekbox) w menu :
        menu.getItem(nrZestSharPref).setChecked(true);
        wybrItem = menu.getItem(nrZestSharPref);

        //dodanie "ogona" na koncu (zeby latwiej bylo wybierac palcem ostatni item):
        menu.add("--------------");
        menu.getItem(menu.size() - 1).setCheckable(false);
        menu.getItem(menu.size() - 1).setEnabled(false);
        //
        return true;
    } //Koniec metody onCreateOptionsMenu

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	/* Gaszę wszystkie checkboxy OPRÓCZ aktualnie obowiazujacego - zeby nie pokazywal wiecej niz Jednego */
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i) != wybrItem) {            //chodzi o to, zeby nie zgasic akt. wybranego
                menu.getItem(i).setChecked(false);
            }
        }
        return true;
    } //onPrepareOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	/* ****************************************************************************************************
	 * Po wyborze zestawu z menu, wszystkie 9 sylab wchodzacych w jego sklad wyswietlane sa na ekranie;
	 * Poczynione tez kroki w przypadku wybrania niedozwolonego (z pelnej wersji) zestawu gdy jestesmy w wersji demo
	 * *****************************************************************************************************/
        boolean savedLettersSize = wielkieLitery; //zapamietujemy, bo moze byc 'dziabniete' - patrz nizej

        MenuItem robItem = wybrItem; //zapamietuję 'obowiazujacy' item, na wypadek wybrania niedozwolonego (z pelnej wersji przy demo)
        item.setChecked(true);
        wybrItem = item; //przekazanie identyfikatora wybranego itemu do zmiennej glob. - zeby go NIE ZGASIC przy nastepnym pokazaniu menu (kosmetics..)
        wybrany_zestaw = dajZestawNaPdstMenu((String) item.getTitle());
        wyswietlZestaw(wybrany_zestaw);
        wielkieLitery = false; //"dziabniecie" - jak wybieramy nowy zestaw, to zawsze jest on fizycznie na malych literach; trzeba ten fakt rowniez odwzorowac logicznie (bo onLongTouch by wariowal...)
        //jezeli wybralismy niedozwolony (z pelnej wersji), to powrot to poprzedniego (zeby check byl w starym, wlasciwym miejscu) :
        if (wybrany_zestaw.endsWith("**")) {
            wybrItem = robItem;
            wielkieLitery = savedLettersSize;
        }
        getActionBar().hide();
        //powrot do normalnych rozmiarZadanyow liter (bo mogly byc zmiany na Upper przez uprzedniego LongClick'a i sa mini problemy estetyczne przy wyborze innego zestawu):
        aplikujRozmiarFontow(initialSize);
        //
        return true;
    } /* onOptionsItemSelected */

    void aplikujRozmiarFontow(float rozmiarZadany)
	/* Wszystkie teksty z vn ustala na tensam rozmiarZadany fontow */
	/* Uzywana przy powiekszaniu/zmniejszaniu, bo w tych     */
	/* opercjach moga byc zmieniane rozmiarZadanyych niektorych    */
	/* dluzszych sylab (np. GLOWA) i jesli nie zmieni sie    */
	/* wszystkich pozostalych, to ekran wyglada "niefajnie"  */
    {
        v01.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v02.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v03.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v04.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v05.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v06.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v07.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v08.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
        v09.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 1.0 * rozmiarZadany);
    } //PrzywrocrozmiarZadany



    public boolean zmienWielkieMale(View arg0)
    {/* Zamiana malych liter na wielka (lub odwrotnie) na wszystkich kwadratach */
        TextView robView = null;   //dowiadujemy sie ktory kliknielismy i jego bedziemy
        String zawartosc = null;
        float minSize = initialSize; //na wyliczenie najmnieszego rozmiaru jesli bylo zmniejszanie na toUpper, zeby potem aplikowac;
        float modifiedSize; //robocza
        //wlaczenie efektu dzwiekowego - 'click' na powieksznie liter - lepszy efekt :
        arg0.setSoundEffectsEnabled(true);  //bo arg0 jest de facto jednym z wjów - v01..v09 - a tam jest dzwiek wylaczony
        arg0.playSoundEffect(android.view.SoundEffectConstants.CLICK); //dzieki temu bedzie dzwięk na longClick'u (normalnie nie ma)
        arg0.setSoundEffectsEnabled(false); //bo arg0 jest de facto jednym z wjówów - v01..v09 - a tam dzwiek trzeba na powrot WYLACZYC...
        if (!wielkieLitery) {
            //na kazdym kwadracie powiekszam/zmniejszam litery:
            for (int i = 0; i < 9; i++) {
                switch (i) {
                    case 0:
                        robView = (TextView) findViewById(R.id.v01);
                        break;
                    case 1:
                        robView = (TextView) findViewById(R.id.v02);
                        break;
                    case 2:
                        robView = (TextView) findViewById(R.id.v03);
                        break;
                    case 3:
                        robView = (TextView) findViewById(R.id.v04);
                        break;
                    case 4:
                        robView = (TextView) findViewById(R.id.v05);
                        break;
                    case 5:
                        robView = (TextView) findViewById(R.id.v06);
                        break;
                    case 6:
                        robView = (TextView) findViewById(R.id.v07);
                        break;
                    case 7:
                        robView = (TextView) findViewById(R.id.v08);
                        break;
                    case 8:
                        robView = (TextView) findViewById(R.id.v09);
                        break;
                    default:
                        break;
                }
                zawartosc = (String) robView.getText();
                zawartosc = zawartosc.toUpperCase(Locale.getDefault()); //podobno samo .tuUpperCase() bez parametru 'locale' jest niebezpieczne...
                //jezeli dlugi napis, to jest niebezpieczenstwo, ze duze litery nie zmieszcza sie w "okienku" (np. "DŹWIA"), zmniejszam :
                if (zawartosc.length()>4) {
                    modifiedSize = (float) 0.90 * initialSize; //robocza
                    if (!ZmienneGlobalne.rozmiarDevice.equals("large")) { //na duzych urzadzeniach nie ma tego problemu
                        robView.setTextSize(TypedValue.COMPLEX_UNIT_PX, modifiedSize);
                        if (modifiedSize < minSize) minSize = modifiedSize; //klasyczne szukanie najmniejszej wartosci
                    }
                }
                //te szczegolne slowa "mama", "włosz", "woda", "głowa" sprawiają trudnosci, a powinny zostac, wiec je pomniejszam... :
                if (zawartosc.equals("MAMA")||zawartosc.equals("WŁOSZ")||zawartosc.equals("WODA")||zawartosc.equals("GŁOWA")) {
                    modifiedSize = (float) 0.87 * initialSize; //robocza //poczatkowo bylo 0.87
                    robView.setTextSize(TypedValue.COMPLEX_UNIT_PX, modifiedSize);
                    if (modifiedSize < minSize) minSize = modifiedSize; //klasyczne szukanie najmniejszej wartosci
                }
                if (zawartosc.equals("CHODŹ")||zawartosc.equals("WSTAŃ")) { //jeszcze inne wredne wyrazy....
                    modifiedSize = (float) 0.85 * initialSize; //robocza
                    robView.setTextSize(TypedValue.COMPLEX_UNIT_PX, modifiedSize);
                    if (modifiedSize < minSize) minSize = modifiedSize; //klasyczne szukanie najmniejszej wartosci
                }
                robView.setText(zawartosc);
            } //for
            aplikujRozmiarFontow(minSize); //zeby, jesli bylo pomniejszanie, wszystko na ekranie wygladalo rowno (w tym samym rozmiarze)
            wielkieLitery = true;
            return true; //UWAGA !!! UWAGA !!! jak tutaj bedzie false, to natychmiast po zdarzeniu LongClick wywola sie OnClick, a jak true - to nie ... :)
        } // If

        if (wielkieLitery) { //zmieniamy na male litery :
            //Wyswietlenie zestawu jak podano w assets (czyli male litery (uwaga-Imiona); troche ten proces
            //pokomplikowany, bo trzeba uwzglednic sytuację, kiedy z wersji demo wybrano niedozwolony zestaw -
            //wtedy nie wolno wyswietlac 'wybrany_zestaw', ale ten, ktory jest okreslony przez obowiazujacy item menu (wybrItem),
            //bo wybrItem zawsze pokazuje na dozwolony zestaw :
            if (wybrItem == null) {
                wyswietlZestaw(wybrany_zestaw);  //jak null, to program swiezo po uruchomieniu i w ten sposob sobbie z tym radzimy - patrz SharedPreferences
            }
            else {
                String currentZestaw = dajZestawNaPdstMenu(wybrItem.getTitle().toString());
                wyswietlZestaw(currentZestaw);
            }
            //
            aplikujRozmiarFontow(initialSize); //na wszelki wypadek, bo algorym powyzej mogl zmienic rozmiarZadany fontow pomniejszajac np. "MAMA',"WLOSZ","WODA" i inne > 4 znaki
            wielkieLitery = false;
            return true; //UWAGA !!! UWAGA !!! jak tutaj bedzie false, to natychmiast po zdarzeniu LongClick wywola sie OnClick, a jak true - to nie ... :)
        }
        return true; //wymog formalny, wyjscie bylo wczesniej //UWAGA !!! UWAGA !!! jak tutaj bedzie false, to natychmiast po zdarzeniu LongClick wywola sie OnClick, a jak true - to nie ... :)
    }  // koniec fukcji zmienWielkieMale


    @Override
    public boolean onLongClick(View arg0) {
        zmienWielkieMale(arg0);
        return true;
    }

    public void demonstrujPiktogram(View arg0) {
	/* Pokazanie kliknietej sylaby w nowym, pelnym oknie (=activity) */

        //Dowiadujemy sie jaka jest nazwa pliku z obrazkiem podwiazana w kliknietym elemencie (poprzez Tag dopisany w wyswietlZestaw) :
        String nazwaPliku = (String) arg0.getTag();
        //pobieramy napis z kliknietego kwadratu (trzeba kastowac do TextView, bo arg0 jest typu View (OnClickListener tak wymaga�)) :
        String napisSylaba;
        TextView robTextView = (TextView) arg0;
        napisSylaba = (String) robTextView.getText();

        //Toast.makeText(this, nazwaPliku, Toast.LENGTH_LONG).show();

        //Przygotowanie danych do przekazania do innego activity :

        pelnyObrazek.putExtra("sciezka_do_pliku", nazwaPliku);    //plik zostanie odnaleziony i odegrany
        pelnyObrazek.putExtra("napisSylaba", napisSylaba);        //napis zostanie wyswietlony na srodku okna nowego activity pelnyObrazek

        //Rozdmuchukemy wybrany obrazek na pelny ekran :
        startActivity(pelnyObrazek);

    } //demonstrujPiktogram

    @Override
    public void onClick(View arg0)
    {
        if (!arg0.getTag().toString().endsWith("_.ogg")) //jezeli pusty kwadracik, to nie demonstrujemy

            demonstrujPiktogram(arg0);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                this.openOptionsMenu();   //this oznacza tutaj Applications object
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                this.openOptionsMenu();   //this oznacza tutaj Applications object
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                this.openOptionsMenu();   //this oznacza tutaj Applications object
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                this.openOptionsMenu();   //this oznacza tutaj Applications object
                break;
        }
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    } //onSwipe


    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
        //this.openOptionsMenu();
        Intent splashKlasa;
        splashKlasa = new Intent("autyzmsoft.pl.sylaby.SplashKlasa");       //do rozdmuchania obrazka na caly ekran
        startActivity(splashKlasa);
    }

}

