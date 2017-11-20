package autyzmsoft.pl.sylaby;

import android.app.Application;

/**
 * Created by developer on 2015-10-21.
 * Sluzy jako mechanizm/obiekt do przekazania do PelnyObrazek stanu ustawien (checkbox'ow) okna SplashKlasa
 * (Bo SplashKlasa przestaje istniec z chwila rozpoczecia innych aktywnosci).
 *  Stan tego obiektu jest ustawiany na OnPause w SplashKlasa.
 * `2015.11.21 - dokladam jeszcze zmienna PELNA_WERSJA
 */
public class ZmienneGlobalne extends Application {
    public static String cbZnikacz_str = "pusty";
    public static String cbNoSound_str = "pusty";
    public static String cbTlo_str     = "pusty";
    public static String rozmiarDevice = "pusty";
    public static Boolean PELNA_WERSJA = false;
    private static ZmienneGlobalne singleton;
    public static ZmienneGlobalne getInstance() {
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        cbTlo_str="";
    }
}
