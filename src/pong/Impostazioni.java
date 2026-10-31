// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    // Gli estremi degli intervalli (compresi): scritti una volta sola.
    private static final int MIN_PUNTI = 1;
    private static final int MAX_PUNTI = 99;
    private static final int MIN_SCARTO = 1;
    private static final int MAX_SCARTO = 10;
    private static final int MIN_VELOCITA = 1;
    private static final int MAX_VELOCITA = 20;

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (!inIntervallo(puntiPerVincere, MIN_PUNTI, MAX_PUNTI)) {
            throw new IllegalArgumentException("puntiPerVincere vale " + puntiPerVincere
                    + ": deve essere fra " + MIN_PUNTI + " e " + MAX_PUNTI);
        }
        if (!inIntervallo(scartoMinimo, MIN_SCARTO, MAX_SCARTO)) {
            throw new IllegalArgumentException("scartoMinimo vale " + scartoMinimo
                    + ": deve essere fra " + MIN_SCARTO + " e " + MAX_SCARTO);
        }
        if (!inIntervallo(velocitaPalla, MIN_VELOCITA, MAX_VELOCITA)) {
            throw new IllegalArgumentException("velocitaPalla vale " + velocitaPalla
                    + ": deve essere fra " + MIN_VELOCITA + " e " + MAX_VELOCITA);
        }
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int puntiPerVincere = STANDARD.puntiPerVincere();
        int scartoMinimo = STANDARD.scartoMinimo();
        int velocitaPalla = STANDARD.velocitaPalla();
        boolean vistaPunti = false;
        boolean vistoScarto = false;
        boolean vistaVelocita = false;

        for (int i = 0; i < righe.size(); i++) {
            int n = i + 1;                       // il numero di riga conta da 1
            String riga = righe.get(i).trim();

            if (riga.isEmpty() || riga.startsWith("#")) {
                continue;                        // vuota o commento: si ignora
            }

            // errore 1: manca il segno =
            int posizioneUguale = riga.indexOf('=');
            if (posizioneUguale < 0) {
                throw errore(n, "manca il segno =");
            }
            String chiave = riga.substring(0, posizioneUguale).trim();
            String testoValore = riga.substring(posizioneUguale + 1).trim();

            // errore 2: la chiave non è una delle tre
            if (!chiave.equals("puntiPerVincere") && !chiave.equals("scartoMinimo")
                    && !chiave.equals("velocitaPalla")) {
                throw errore(n, "chiave sconosciuta «" + chiave + "»");
            }

            // errore 3: il valore non è un numero intero
            int valore;
            try {
                valore = Integer.parseInt(testoValore);
            } catch (NumberFormatException e) {
                throw errore(n, "il valore di " + chiave + " non è un numero intero: «"
                        + testoValore + "»");
            }

            // errori 4 e 5: chiave già vista, valore fuori intervallo
            switch (chiave) {
                case "puntiPerVincere":
                    if (vistaPunti) {
                        throw errore(n, "puntiPerVincere è già stata scritta");
                    }
                    if (!inIntervallo(valore, MIN_PUNTI, MAX_PUNTI)) {
                        throw errore(n, "puntiPerVincere vale " + valore + ": deve essere fra "
                                + MIN_PUNTI + " e " + MAX_PUNTI);
                    }
                    puntiPerVincere = valore;
                    vistaPunti = true;
                    break;
                case "scartoMinimo":
                    if (vistoScarto) {
                        throw errore(n, "scartoMinimo è già stato scritto");
                    }
                    if (!inIntervallo(valore, MIN_SCARTO, MAX_SCARTO)) {
                        throw errore(n, "scartoMinimo vale " + valore + ": deve essere fra "
                                + MIN_SCARTO + " e " + MAX_SCARTO);
                    }
                    scartoMinimo = valore;
                    vistoScarto = true;
                    break;
                default: // velocitaPalla: le altre chiavi sono già state escluse
                    if (vistaVelocita) {
                        throw errore(n, "velocitaPalla è già stata scritta");
                    }
                    if (!inIntervallo(valore, MIN_VELOCITA, MAX_VELOCITA)) {
                        throw errore(n, "velocitaPalla vale " + valore + ": deve essere fra "
                                + MIN_VELOCITA + " e " + MAX_VELOCITA);
                    }
                    velocitaPalla = valore;
                    vistaVelocita = true;
                    break;
            }
        }

        return new Impostazioni(puntiPerVincere, scartoMinimo, velocitaPalla);
    }

    /** Dice se valore sta fra minimo e massimo, estremi compresi. */
    private static boolean inIntervallo(int valore, int minimo, int massimo) {
        return valore >= minimo && valore <= massimo;
    }

    /** Crea l'eccezione con il messaggio che comincia con «riga N: ». */
    private static IllegalArgumentException errore(int numeroRiga, String messaggio) {
        return new IllegalArgumentException("riga " + numeroRiga + ": " + messaggio);
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
